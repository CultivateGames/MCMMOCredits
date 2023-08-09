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
package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.user.User;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a Bukkit ItemStack.
 *
 * @param stack  The Bukkit ItemStack.
 * @param name   Unparsed item name.
 * @param lore   Unparsed item lore.
 * @param slot   Slot of the item in the Menu.
 * @param action Action to execute when the item is clicked in a menu.
 */
@ConfigSerializable
public record Item(ItemStack stack, String name, List<String> lore, int slot, ItemAction action) {
    /**
     * Constructs the object.
     *
     * @param material The material of the ItemStack. Updated with refreshing name/lore.
     * @param name     Raw name of the item. Always parsed.
     * @param lore     Raw lore of the item. Always parsed.
     * @param slot     Location of item in a Menu.
     * @return The item.
     */
    public static Item of(final Material material, final String name, final List<String> lore, final int slot) {
        return new Item(new ItemStack(material), name, lore, slot, ItemAction.CANCEL);
    }

    /**
     * Constructs the object.
     *
     * @param stack The representative ItemStack. Updated with refreshing name/lore.
     * @param name  Raw name of the item. Always parsed.
     * @param lore  Raw lore of the item. Always parsed.
     * @param slot  Location of item in a Menu.
     * @return The item.
     */
    public static Item of(final ItemStack stack, final String name, final List<String> lore, final int slot) {
        return new Item(stack, name, lore, slot, ItemAction.CANCEL);
    }

    /**
     * Constructs the object with sane defaults.
     *
     * @param material The type of the Item.
     * @return The item.
     */
    public static Item of(final Material material) {
        return new Item(new ItemStack(material), "", new ArrayList<>(), 0, ItemAction.CANCEL);
    }

    /**
     * Returns a copy of the item with a new action.
     *
     * @param action The action.
     * @return The new item.
     */
    public Item action(final ItemAction action) {
        return new Item(this.stack, this.name, this.lore, this.slot, action);
    }

    /**
     * Returns a copy of the item with a new slot.
     *
     * @param slot The slot.
     * @return The new item.
     */
    public Item slot(final int slot) {
        return new Item(this.stack, this.name, this.lore, slot, this.action);
    }

    /**
     * Updates the name and lore based on the User.
     *
     * @param user The user to parse against.
     * @return A Bukkit ItemStack with updated properties.
     */
    public ItemStack parseUser(final User user) {
        ItemMeta meta = this.stack.getItemMeta();
        if (!this.name.isEmpty()) {
            Component itemName = Text.forOneUser(user, this.name).toComponent();
            meta.displayName(itemName);
        }
        if (!this.lore.stream().allMatch(String::isEmpty)) {
            List<Component> itemLore = this.lore.stream().map(x -> Text.forOneUser(user, x).toComponent()).toList();
            meta.lore(itemLore);
        }
        ItemStack copy = this.stack.clone();
        copy.setItemMeta(meta);
        return copy;
    }
}
