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

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.events.CreditRedemptionEvent;
import games.cultivate.mcmmocredits.events.CreditTransactionEvent;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.Console;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserService;
import io.papermc.paper.event.player.AsyncChatEvent;
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

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

/**
 * Event Handlers used to manage the database, and input storage.
 */
public class Listeners implements Listener {
    private final ChatQueue queue;
    private final UserService service;
    private final MainConfig config;

    @Inject
    public Listeners(final MainConfig config, final ChatQueue queue, final UserService service) {
        this.config = config;
        this.queue = queue;
        this.service = service;
    }

    /**
     * Add users to MCMMO Credits database, and updates username for existing records.
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
        Optional<User> user = this.service.getUser(uuid);
        if (user.isPresent()) {
            this.service.setUsername(uuid, username);
            return;
        }
        this.service.addUser(uuid, username);
        if (this.config.bool("settings", "add-user-message")) {
            Text.forOneUser(Console.INSTANCE, this.config.string("add-user")).send();
        }
    }

    /**
     * Sends login message to user if enable in configuration.
     *
     * @param e The event.
     */
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        if (this.config.bool("settings", "send-login-message")) {
            User user = this.service.forceUser(e.getPlayer().getUniqueId());
            Text.forOneUser(user, this.config.string("login-message")).send();
        }
    }

    /**
     * Captures chat of a user if their {@link UUID} is in the {@link ChatQueue}.
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
                User user = this.service.forceUser(uuid);
                Text.forOneUser(user, this.config.string("cancel-prompt")).send();
            }
            this.queue.complete(uuid, completion);
            e.setCancelled(true);
        }
    }

    /**
     * Removes all users from the {@link ChatQueue} if present.
     *
     * @param e The event.
     */
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        String username = e.getPlayer().getName();
        this.service.removeFromCache(uuid, username);
        this.queue.remove(uuid);
    }

    /**
     * Performs Credit Transaction with information derived from the event.
     *
     * @param e The event.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void performTransaction(final CreditTransactionEvent e) {
        CommandExecutor executor = this.service.fromSender(e.sender());
        User target = this.service.forceUser(e.uuid());
        int amount = e.amount();
        CreditOperation operation = e.operation();
        Resolver resolver = Resolver.ofTransaction(executor, target, amount);
        if (!this.service.modifyCredits(target.uuid(), operation, amount)) {
            Text.fromString(executor, this.config.string("not-enough-credits"), resolver).send();
            return;
        }
        String content = "credits-" + operation;
        if (!e.silentForSender()) {
            Text.fromString(executor, this.config.string(content), resolver).send();
        }
        if (target.player() != null && !e.silentForUser()) {
            Text.fromString(target, this.config.string(content + "-user"), resolver).send();
        }
    }

    /**
     * Performs Credit Redemption with information derived from the event.
     *
     * @param e The event.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void performRedemption(final CreditRedemptionEvent e) {
        CommandExecutor executor = this.service.fromSender(e.sender());
        User target = this.service.forceUser(e.uuid());
        int amount = e.amount();
        PrimarySkillType skill = e.skill();
        Resolver resolver = Resolver.ofRedemption(executor, target, skill, amount);
        if (target.credits() < amount) {
            Text.fromString(executor, this.config.string("not-enough-credits"), resolver).send();
            return;
        }
        Optional<PlayerProfile> optionalProfile = Util.getMCMMOProfile(e.uuid());
        if (optionalProfile.isEmpty()) {
            Text.fromString(executor, this.config.string("mcmmo-profile-fail"), resolver).send();
            return;
        }
        PlayerProfile profile = optionalProfile.get();
        if (Util.exceedsSkillCap(profile, skill, amount)) {
            Text.fromString(executor, this.config.string("mcmmo-skill-cap"), resolver).send();
            return;
        }
        if (this.service.redeemCredits(target.uuid(), amount)) {
            profile.addLevels(skill, amount);
            profile.save(true);
            //TODO: fix
            //make new user to avoid hitting database. If we implement a User Cache this will not be necessary.
            target = new User(target.uuid(), target.username(), target.credits() - amount, target.redeemed() + amount);
            resolver = Resolver.ofRedemption(executor, target, skill, amount);
            if (!e.silentForSender()) {
                String key = e.isSelfRedemption() ? "redeem" : "redeem-sudo";
                Text.fromString(executor, this.config.string(key), resolver).send();
            }
            if (!e.silentForUser() && target.player() != null) {
                Text.fromString(target, this.config.string("redeem-sudo-user"), resolver).send();
            }
        }
    }

    /**
     * Cancels shift-clicking inside of Interfaces due to a bug in the library.
     * Can be removed if <a href="https://github.com/Incendo/interfaces/issues/69">this</a> is patched.
     * <p></p>
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
     * <p></p>
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
