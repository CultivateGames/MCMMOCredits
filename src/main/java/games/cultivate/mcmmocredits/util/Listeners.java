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

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.ConfigService;
import games.cultivate.mcmmocredits.events.CreditTransactionEvent;
import games.cultivate.mcmmocredits.menu.RedeemMenu;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.transaction.Transaction;
import games.cultivate.mcmmocredits.transaction.TransactionResult;
import games.cultivate.mcmmocredits.transaction.TransactionType;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.Console;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserService;
import io.papermc.paper.event.player.AsyncChatEvent;
import jakarta.inject.Inject;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

/**
 * Event Handlers used to manage the Database and ChatQueue.
 */
public class Listeners implements Listener {
    private final ChatQueue queue;
    private final UserService service;
    private final ConfigService configs;
    private final MCMMOCredits plugin;

    /**
     * Constructs the object.
     *
     * @param configs ConfigService, used to get configs.
     * @param queue   ChatQueue, used to listen for relevant chat messages.
     * @param service UserService, required to modify users.
     * @param plugin  Instance of the plugin to help with inventory management.
     */
    @Inject
    public Listeners(final ConfigService configs, final ChatQueue queue, final UserService service, final MCMMOCredits plugin) {
        this.configs = configs;
        this.queue = queue;
        this.service = service;
        this.plugin = plugin;
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
        //joining solves a race condition in which new user are not available during command registration
        //when using a "slow" filesystem or MYSQL.
        this.service.getUser(uuid).thenAccept(u -> {
            if (u.isPresent()) {
                this.service.setUsername(uuid, username);
                return;
            }
            this.service.addUser(new User(uuid, username, 0, 0)).join();
            if (this.configs.mainConfig().getBoolean("settings", "add-user-message")) {
                Console.INSTANCE.sendText(this.configs.getMessage("add-user"), r -> r.addTag("target", username));
            }
        }).join();
    }

    /**
     * Sends login message to a user if enabled in configuration.
     *
     * @param e The event.
     */
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        if (this.configs.mainConfig().getBoolean("settings", "send-login-message")) {
            this.service.getUser(e.getPlayer()).thenAccept(x -> x.orElseThrow().sendText(this.configs.getMessage("login-message")));
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
                this.service.getUser(uuid).thenAccept(x -> x.orElseThrow().sendText(this.configs.getMessage("cancel-prompt")));
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
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTransaction(final CreditTransactionEvent e) {
        if (e.isCancelled()) {
            return;
        }
        Transaction tr = e.transaction();
        tr.validateTransaction().forEach((k, v) -> v.ifPresent(y -> {
            tr.executor().sendText(this.configs.getMessage(y), r -> r.addTransaction(tr).addUser(k, "target"));
            tr.targets().remove(k);
        }));
        if (tr.targets().isEmpty()) {
            return;
        }
        TransactionResult result = tr.execute();
        this.service.processTransaction(result).thenRun(() -> {
            if (!e.senderFeedback()) {
                CommandExecutor executor = result.targetExecutor().isPresent() ? result.targetExecutor().get() : result.executor();
                executor.sendText(this.configs.getMessage(tr.messageKey()), Resolver.ofResult(result));
            }
            if (!e.userFeedback()) {
                result.targets().stream().filter(x -> x.player().isOnline()).forEach(x -> x.sendText(this.configs.getMessage(tr.userMessageKey()), Resolver.ofResult(result, x)));
            }
        });
    }

    /**
     * Cancels drag events for our holder.
     *
     * @param e The event.
     */
    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent e) {
        if (e.getInventory().getHolder(true) instanceof RedeemMenu) {
            e.setCancelled(true);
        }
    }

    /**
     * Cancel item refresh task when our inventory is closed.
     * Set item in offhand to avoid client desync issue.
     *
     * @param e The event.
     */
    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        if (e.getInventory().getHolder(true) instanceof RedeemMenu menu) {
            menu.cancelRefresh();
            PlayerInventory inv = e.getPlayer().getInventory();
            inv.setItemInOffHand(inv.getItemInOffHand());
        }
    }

    /**
     * Manages inventory clicks for our inventories.
     *
     * @param e The event.
     */
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        InventoryView view = e.getView();
        Inventory inv = view.getInventory(e.getRawSlot());
        if (inv == null) {
            return;
        }
        if (view.getTopInventory().getHolder(true) instanceof RedeemMenu menu) {
            e.setCancelled(true);
            if (inv.equals(view.getBottomInventory())) {
                return;
            }
            menu.getItem(e.getSlot()).ifPresent(x -> {
                switch (x.getValue().action()) {
                    case COMMAND -> this.doCommand(e, x.getKey());
                    case REDEEM -> this.doRedeem(e, x.getKey());
                    default -> { /* do nothing */ }
                }
            });
        }
    }

    /**
     * Executes a command when specific items are clicked in the RedeemMenu.
     *
     * @param event The inventory event.
     * @param key   The key of the item in the inventory's item map.
     */
    private void doCommand(final InventoryClickEvent event, final String key) {
        event.getClickedInventory().close();
        String command = this.configs.menuConfig().getString("items", key, "command");
        Bukkit.dispatchCommand(event.getWhoClicked(), command);
    }

    /**
     * Executes a redemption when specific items are clicked in the RedeemMenu.
     *
     * @param event The inventory event.
     * @param key   The key of the item in the inventory's item map.
     */
    private void doRedeem(final InventoryClickEvent event, final String key) {
        event.getClickedInventory().close();
        this.service.getUser(event.getWhoClicked().getUniqueId()).thenAccept(opt -> {
            User user = opt.orElseThrow();
            PrimarySkillType skill = PrimarySkillType.valueOf(key.toUpperCase());
            user.sendText(this.configs.getMessage("credits-redeem-prompt"), r -> r.addSkill(skill));
            this.queue.act(user.uuid(), i -> Bukkit.getGlobalRegionScheduler().runDelayed(this.plugin, t -> {
                if (i != null) {
                    Transaction transaction = Transaction.builder(user, TransactionType.REDEEM, Integer.parseInt(i)).skill(skill).build();
                    Bukkit.getPluginManager().callEvent(new CreditTransactionEvent(transaction, true, false));
                }
            }, 1L));
        });
    }
}
