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
package games.cultivate.mcmmocredits.ui.item;

import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.ui.ContextFactory;
import games.cultivate.mcmmocredits.user.User;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.pane.ChestPane;

import java.util.List;

/**
 * Represents a basic Item.
 */
public class BaseItem implements Item {
    private final ItemStack stack;
    private final String name;
    private final List<String> lore;
    private final int slot;

    /**
     * Constructs the object.
     *
     * @param stack The representative ItemStack. Updated with refreshing name/lore.
     * @param name  Raw name of the item. Always parsed.
     * @param lore  Raw lore of the item. Always parsed.
     * @param slot  Location of item in a Menu.
     */
    BaseItem(final ItemStack stack, final String name, final List<String> lore, final int slot) {
        this.stack = stack;
        this.name = name;
        this.lore = lore;
        this.slot = slot;
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
    public static BaseItem of(final ItemStack stack, final String name, final List<String> lore, final int slot) {
        return new BaseItem(stack, name, lore, slot);
    }

    /**
     * Constructs the object with sane defaults.
     *
     * @param material The type of the Item.
     * @return The item.
     */
    public static BaseItem of(final Material material) {
        return new BaseItem(new ItemStack(material, 1), "", List.of(), -1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeClick(final User user, final ContextFactory factory, final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> ctx) {
        ctx.status(ClickContext.ClickStatus.DENY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ItemStack stack() {
        return this.stack;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> lore() {
        return this.lore;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int slot() {
        return this.slot;
    }
}
