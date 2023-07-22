//
// MIT License
//
// Copyright (c) 2023 Cultivate Games
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package games.cultivate.mcmmocredits.util;

import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.events.CreditTransactionEvent;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.transaction.Transaction;
import games.cultivate.mcmmocredits.transaction.TransactionResult;
import games.cultivate.mcmmocredits.user.Console;
import games.cultivate.mcmmocredits.user.UserService;
import io.papermc.paper.event.player.AsyncChatEvent;
import jakarta.inject.Inject;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.incendo.interfaces.paper.view.ChestView;

import java.util.Optional;
import java.util.UUID;

/**
 * Event Handlers used to manage the Database and ChatQueue.
 */
public class Listeners implements Listener {
    private final ChatQueue queue;
    private final UserService service;
    private final MainConfig config;

    /**
     * Constructs the object.
     *
     * @param config  MainConfig, used to read properties for event handlers.
     * @param queue   ChatQueue, used to listen for relevant chat messages.
     * @param service UserService, required to modify users.
     */
    @Inject
    public Listeners(final MainConfig config, final ChatQueue queue, final UserService service) {
        this.config = config;
        this.queue = queue;
        this.service = service;
    }

    /**
     * Updates usernames for existing users, and add new users if they do not exist.
     *
     * @param e The event.
     */
    @EventHandler
    public void onPlayerPreLogin(final AsyncPlayerPreLoginEvent e) {
        if (!e.getLoginResult().equals(Result.ALLOWED)) {
            return;
        }
        var profile = e.getPlayerProfile();
        UUID uuid = profile.getId();
        String username = profile.getName();
        if (this.service.getUser(uuid).isPresent()) {
            this.service.setUsername(uuid, username);
            return;
        }
        this.service.addUser(uuid, username);
        if (this.config.getBoolean("settings", "add-user-message")) {
            Console.INSTANCE.sendText(this.config.getMessage("add-user"), r -> r.addTag("username", username));
        }
    }

    /**
     * Sends login message to a user if enabled in configuration.
     *
     * @param e The event.
     */
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        if (this.config.getBoolean("settings", "send-login-message")) {
            this.service.getUser(e.getPlayer().getUniqueId()).orElseThrow().sendText(this.config.getMessage("login-message"));
        }
    }

    /**
     * Captures chat messages of a user if their UUID is in the ChatQueue.
     *
     * @param e The event.
     */
    @EventHandler
    public void onPlayerChat(final AsyncChatEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (this.queue.contains(uuid)) {
            String completion = PlainTextComponentSerializer.plainText().serialize(e.message());
            if (completion.equalsIgnoreCase("cancel")) {
                this.queue.remove(uuid);
                this.service.getUser(uuid).orElseThrow().sendText(this.config.getMessage("cancel-prompt"));
            }
            this.queue.complete(uuid, completion);
            e.setCancelled(true);
        }
    }

    /**
     * Removes logged-out users from the UserCache and ChatQueue.
     *
     * @param e The event.
     */
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        this.service.removeFromCache(uuid);
        this.queue.remove(uuid);
    }

    /**
     * Performs a transaction with event supplied information.
     *
     * @param e The event.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTransaction(final CreditTransactionEvent e) {
        Transaction transaction = e.transaction();
        Optional<String> failure = transaction.valid();
        if (failure.isPresent()) {
            transaction.executor().sendText(this.config.getMessage(failure.get()), Resolver.ofTransaction(transaction));
            return;
        }
        TransactionResult result = transaction.execute();
        this.service.processTransaction(result);
        Resolver resolver = Resolver.ofTransactionResult(result);
        if (!e.senderFeedback()) {
            result.executor().sendText(this.config.getMessage(transaction.messageKey()), resolver);
        }
        if (!e.userFeedback() && result.target().player() != null) {
            result.target().sendText(this.config.getMessage(transaction.userMessageKey(), resolver));
        }
    }

    /**
     * Cancels shift-clicking inside of Interfaces due to a bug in the library.
     * Can be removed if <a href="https://github.com/Incendo/interfaces/issues/69">this</a> is patched.
     * <br><br>
     * Note: The current solution will likely affect any Inventory that is a {@link ChestView}
     *
     * @param e The event.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof ChestView && e.isShiftClick()) {
            e.setCancelled(true);
        }
    }

    /**
     * Cancels dragging inside of Interfaces due to a bug in the library.
     * Can be removed if <a href="https://github.com/Incendo/interfaces/issues/69">this</a> is patched.
     * <br><br>
     * Note: The current solution will likely affect any Inventory that is a {@link ChestView}
     *
     * @param e The event.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryDrag(final InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof ChestView) {
            e.setCancelled(true);
        }
    }
}
