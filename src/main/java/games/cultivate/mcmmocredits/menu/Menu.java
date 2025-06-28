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

import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A fixed size collection of items.
 */
public final class Menu implements InventoryHolder {
    private final MenuProperties properties;
    private final Map<String, Item> items;
    private final Map<Integer, Item> slotted;
    private Inventory inventory;
    private ScheduledTask task;

    public Menu(final MenuProperties properties) {
        this.properties = properties;
        Map<String, Item> items = new HashMap<>(properties.items());
        Item filler = items.remove("fill");
        if (!properties.navigation()) {
            items.remove("navigation");
        }
        this.slotted = items.values().stream().collect(Collectors.toMap(Item::slot, item -> item));
        if (properties.fill() && filler != null) {
            IntStream.range(0, properties.size()).forEach(i -> this.slotted.putIfAbsent(i, filler.withSlot(i)));
        }
        this.items = items;
        this.inventory = null;
        this.task = null;
    }

    /**
     * Returns a map of items and their keys.
     *
     * @return A map of items and their keys.
     */
    public Map<Integer, Item> items() {
        return this.slotted;
    }

    /**
     * Opens the inventory for the provided User.
     *
     * @param plugin plugin used to open the inventory.
     * @param player The menu viewer.
     */
    public void open(final JavaPlugin plugin, final Player player) {
        this.inventory = Bukkit.createInventory(this, this.size(), this.properties.parseTitle(player));
        this.draw(player);
        EntityScheduler scheduler = player.getScheduler();
        scheduler.run(plugin, x -> player.openInventory(this.inventory), player::closeInventory);
        this.task = scheduler.runAtFixedRate(plugin, x -> this.draw(player), player::closeInventory, 1, 20);
    }

    /**
     * The size of the inventory. Will never exceed 54.
     *
     * @return The size of the inventory.
     */
    public int size() {
        return this.properties.size();
    }

    /**
     * Performs actions required when the inventory is closed.
     */
    public void close() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }

    /**
     * Returns the ScheduledTask associated with this inventory if it is open.
     *
     * @return The ScheduledTask.
     */
    public @Nullable ScheduledTask scheduledTask() {
        return this.task;
    }

    public @Nullable Item item(final int slot) {
        return this.slotted.get(slot);
    }

    public @Nullable String itemKey(final int slot) {
        Item item = this.item(slot);
        return this.items.entrySet().stream().filter(e -> e.getValue().equals(item)).findFirst().map(Map.Entry::getKey).orElse(null);
    }

    public boolean isOpen() {
        return this.task != null;
    }

    public @NotNull Inventory getInventory() {
        if (this.inventory == null) {
            this.inventory = Bukkit.createInventory(this, this.size(), this.properties.parseTitle(null));
        }
        return this.inventory;
    }

    /**
     * Refreshes the items displayed in the inventory for the provided player.
     *
     * @param player The player to re-draw items for.
     */
    private void draw(final Player player) {
        this.items().values().forEach(x -> this.inventory.setItem(x.slot(), x.getItemFor(player)));
    }
}
