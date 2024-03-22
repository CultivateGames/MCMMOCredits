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

import games.cultivate.mcmmocredits.user.User;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A fixed size collection of items.
 */
public interface Menu extends InventoryHolder {
    /**
     * Returns a map of items and their keys.
     *
     * @return A map of items and their keys.
     */
    Map<String, Item> items();

    /**
     * Opens the inventory for the provided User.
     *
     * @param plugin plugin used to open the inventory.
     * @param user   The user.
     */
    void open(JavaPlugin plugin, User user);

    /**
     * The size of the inventory. Will never exceed 54.
     *
     * @return The size of the inventory.
     */
    int size();

    /**
     * Performs actions required when the inventory is closed.
     */
    void close();

    default @Nullable Map.Entry<String, Item> getItemEntry(final int slot) {
        return this.items().entrySet().stream().filter(entry -> entry.getValue().slot() == slot).findFirst().orElse(null);
    }
}
