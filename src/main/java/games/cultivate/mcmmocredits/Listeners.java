//
// MIT License
//
// Copyright (c) 2024 Cultivate Games
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
package games.cultivate.mcmmocredits;

import games.cultivate.mcmmocredits.config.Settings;
import games.cultivate.mcmmocredits.menu.Menu;
import games.cultivate.mcmmocredits.menu.MenuService;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserService;
import io.papermc.paper.event.player.AsyncChatEvent;
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.PlayerInventory;

/**
 * Event Handlers for required plugin functionality.
 */
@SuppressWarnings("unused")
//TODO: finalize with further refactoring.
public final class Listeners implements Listener {
    private final UserService userService;
    private final MenuService menuService;
    private final Settings settings;

    @Inject
    public Listeners(final UserService userService, final MenuService menuService, final Settings settings) {
        this.userService = userService;
        this.menuService = menuService;
        this.settings = settings;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        if (this.settings.sendLoginMessage()) {
            //this.userService.getOrCreate(e.getPlayer()).sendText(this.captions.caption(Captions.LOGIN_MESSAGE));
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        Player player = e.getPlayer();
        this.userService.onLogout(player);
        this.menuService.unregisterUser(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerChat(final AsyncChatEvent e) {
        Player player = e.getPlayer();
        if (this.menuService.isRegistered(player.getUniqueId())) {
            e.setCancelled(true);
            User user = this.userService.getOrCreate(e.getPlayer());
            this.menuService.completeChat(user, e.message());
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent e) {
        if (e.getInventory().getHolder(true) instanceof Menu) {
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
        if (e.getInventory().getHolder(true) instanceof Menu && e.getPlayer() instanceof Player player) {
            PlayerInventory inv = player.getInventory();
            inv.setItemInOffHand(inv.getItemInOffHand());
            this.menuService.close(player.getUniqueId());
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        InventoryView view = e.getView();
        Inventory inv = view.getInventory(e.getRawSlot());
        if (inv == null || inv.equals(view.getBottomInventory())) {
            return;
        }
        if (e.getClickedInventory() == null) {
            return;
        }
        if (view.getTopInventory().getHolder(true) instanceof Menu menu && view.getPlayer() instanceof Player player) {
            e.setCancelled(true);
            this.menuService.handleClick(this.userService.getOrCreate(player), e.getSlot());
        }
    }
}
