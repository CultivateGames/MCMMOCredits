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
package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.messages.Text;
import games.cultivate.mcmmocredits.user.User;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Represents a Bukkit Inventory with populated item slots.
 */
public final class RedeemMenu implements InventoryHolder {
    private final Map<String, Item> items;
    private final String title;
    private final int slots;
    private final boolean fill;
    private final boolean navigation;
    private Inventory inventory;
    private ScheduledTask task;

    /**
     * Constructs the object.
     *
     * @param items      Map of items and their internal names.
     * @param title      Unparsed title of the inventory.
     * @param slots      Size of the inventory.
     * @param fill       If the menu will be filled.
     * @param navigation If the menu will have a navigation item.
     */
    public RedeemMenu(final Map<String, Item> items, final String title, final int slots, final boolean fill, final boolean navigation) {
        this.items = items;
        this.title = title;
        this.slots = slots;
        this.fill = fill;
        this.navigation = navigation;
        this.inventory = null;
        this.task = null;
    }

    /**
     * Opens the inventory for the provided User. Executed on the following tick.
     *
     * @param plugin Plugin to execute the opening later.
     * @param user   The user to open the inventory on.
     */
    public void openInventory(final MCMMOCredits plugin, final User user) {
        this.inventory = Bukkit.getServer().createInventory(this, this.slots, Text.forOneUser(user, this.title).toComponent());
        this.setItems(user);
        Bukkit.getGlobalRegionScheduler().run(plugin, x -> ((Player) user.sender()).openInventory(this.inventory));
        this.task = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, x -> this.setItems(user), 50, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * Cancels the task which redraws items within the inventory.
     */
    public void cancelRefresh() {
        if (this.task != null) {
            this.task.cancel();
        }
        this.task = null;
    }

    /**
     * Gets an item map entry at the provided slot if it exists.
     *
     * @param slot The slot.
     * @return The item map entry.
     */
    public Optional<Map.Entry<String, Item>> getItem(final int slot) {
        return this.items.entrySet().stream().filter(x -> x.getValue().slot() == slot).findAny();
    }

    /**
     * Redraws items to the inventory. Parses placeholders against provided user.
     *
     * @param user The user.
     */
    public void setItems(final User user) {
        this.items.forEach((k, v) -> this.inventory.setItem(v.slot(), v.parseUser(user)));
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (this.inventory == null) {
            this.inventory = Bukkit.createInventory(this, this.slots);
        }
        return this.inventory;
    }

    /**
     * Returns the item map.
     *
     * @return The item map.
     */
    public Map<String, Item> items() {
        return this.items;
    }

    /**
     * Returns the unparsed title.
     *
     * @return The unparsed title.
     */
    public String title() {
        return this.title;
    }

    /**
     * Returns the size of the inventory.
     *
     * @return The size of the inventory.
     */
    public int slots() {
        return this.slots;
    }

    /**
     * Returns if the inventory is filled.
     *
     * @return If the inventory is filled.
     */
    public boolean fill() {
        return this.fill;
    }

    /**
     * Returns if the inventory has a navigation button.
     *
     * @return If there is a navigation button.
     */
    public boolean navigation() {
        return this.navigation;
    }
}
