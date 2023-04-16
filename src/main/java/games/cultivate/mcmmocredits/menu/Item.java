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

import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.core.util.Vector2;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;

import java.util.List;
import java.util.Objects;

/**
 * An {@link ItemStack} used within a {@link Menu}.
 */
public final class Item {
    private final ClickTypes type;
    private final ItemStack stack;
    private final String name;
    private final List<String> lore;
    private final int slot;
    private final String data;

    /**
     * Constructs the object.
     *
     * @param stack The representative ItemStack. Updated with refreshing name/lore.
     * @param name  Mutable name of the item.
     * @param lore  Mutable lore of the item.
     * @param type  Item ClickTypes that determines what type of click is applied.
     * @param slot  Location of item in the inventory.
     * @param data  String data used to construct the click. For example, could represent a command or skill type.
     */
    private Item(final ItemStack stack, final String name, final List<String> lore, final ClickTypes type, final String data, final int slot) {
        this.stack = stack;
        this.name = name;
        this.lore = lore;
        this.type = type;
        this.data = data;
        this.slot = slot;
    }

    /**
     * Static factory to make a new Item instance.
     *
     * @param material Material type to apply to new Item.
     * @return The Item.
     */
    public static Item of(final Material material) {
        return Item.builder()
                .item(new ItemStack(material, 1))
                .build();
    }

    /**
     * Creates a new instance of the Item Builder.
     *
     * @return New Instance of the Item Builder.
     */
    public static Item.Builder builder() {
        return new Item.Builder();
    }

    /**
     * Provides copy of existing item with updated ClickType
     *
     * @param type ClickType to apply.
     * @return An updated copy of the item.
     */
    public Item withClickType(final ClickTypes type) {
        return new Item(this.stack, this.name, this.lore, type, this.data, this.slot);
    }

    /**
     * Provides copy of existing item with updated ItemStack.
     *
     * @param stack ItemStack to apply.
     * @return An updated copy of the item.
     */
    public Item withStack(final ItemStack stack) {
        return new Item(stack, this.name, this.lore, this.type, this.data, this.slot);
    }

    /**
     * Provides copy of existing item with updated name.
     *
     * @param name name to apply.
     * @return An updated copy of the item.
     */
    public Item withName(final String name) {
        return new Item(this.stack, name, this.lore, this.type, this.data, this.slot);
    }

    /**
     * Provides copy of existing item with updated lore.
     *
     * @param lore lore to apply.
     * @return An updated copy of the item.
     */
    public Item withLore(final List<String> lore) {
        return new Item(this.stack, this.name, lore, this.type, this.data, this.slot);
    }

    /**
     * Provides copy of existing item with updated data
     *
     * @param data data to apply.
     * @return An updated copy of the item.
     */
    public Item withData(final String data) {
        return new Item(this.stack, this.name, this.lore, this.type, data, this.slot);
    }

    /**
     * Provides copy of existing item with updated slot.
     *
     * @param slot slot to apply.
     * @return An updated copy of the item.
     */
    public Item withSlot(final int slot) {
        return new Item(this.stack, this.name, this.lore, this.type, this.data, slot);
    }

    /**
     * Gets the ItemType.
     *
     * @return The ItemType.
     */
    public ClickTypes clickType() {
        return this.type;
    }

    /**
     * Gets the base ItemStack.
     *
     * @return The ItemStack.
     */
    public ItemStack stack() {
        return this.stack;
    }

    /**
     * Gets the raw item name.
     *
     * @return The Item's name.
     */
    public String name() {
        return this.name;
    }

    /**
     * Gets the raw item lore.
     *
     * @return The Item's lore.
     */
    public List<String> lore() {
        return this.lore;
    }

    /**
     * Gets the Item's slot converted from a {@link Vector2}.
     *
     * @return The Item's slot
     */
    public int slot() {
        return this.slot;
    }

    /**
     * Gets the Item's data. Usually empty.
     *
     * @return The data.
     */
    public String data() {
        return this.data;
    }

    /**
     * Updates the name and lore based on the passed in Player and resolver.
     *
     * @param player   The viewer of the item.
     * @param resolver The resolver used to parse the item.
     * @return A Bukkit ItemStack with updated properties.
     */
    public ItemStack applyProperties(final Player player, final Resolver resolver) {
        Component display = Text.fromString(player, this.name, resolver).toComponent();
        var ilore = this.lore.stream().map(x -> Text.fromString(player, x, resolver).toComponent()).toList();
        ItemStack stackCopy = new ItemStack(this.stack);
        stackCopy.editMeta(meta -> {
            if (!this.name.isEmpty()) {
                meta.displayName(display);
            }
            if (this.lore.stream().noneMatch(String::isEmpty)) {
                meta.lore(ilore);
            }
        });
        return stackCopy;
    }

    /**
     * Provides a TransformContext object for the item where
     * it's name and lore are updated, and a click handler is attached to it.
     *
     * @param clickFactory ClickFactory to obtain the click for the item.
     * @param resolver     Resolver to update the item's name/lore.
     * @return The TransformContext.
     */
    public TransformContext<ChestPane, PlayerViewer> context(final ClickFactory clickFactory, final Resolver resolver) {
        String info = this.type.name().startsWith("EDIT_") ? this.name() : this.data;
        var handler = clickFactory.getClick(this.type, info, resolver);
        return TransformContext.of(0, ((pane, view) -> {
            ItemStack menuItem = this.applyProperties(view.viewer().player(), resolver);
            return pane.element(ItemStackElement.of(menuItem, handler), this.slot % 9, this.slot / 9);
        }));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;
        if (stack.getAmount() != item.stack.getAmount()) return false;
        if (stack.getType() != item.stack.getType()) return false;
        if (slot != item.slot) return false;
        if (type != item.type) return false;
        if (!Objects.equals(name, item.name)) return false;
        if (!Objects.equals(lore, item.lore)) return false;
        return Objects.equals(data, item.data);
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (lore != null ? lore.hashCode() : 0);
        result = 31 * result + slot;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    /**
     * Builder class for {@link Item}.
     */
    public static final class Builder {
        private ClickTypes type;
        private ItemStack item;
        private String name;
        private List<String> lore;
        private int slot;
        private String data;

        /**
         * Constructs the Builder with sane defaults.
         */
        private Builder() {
            this.type = ClickTypes.FILL;
            this.item = new ItemStack(Material.STONE, 1);
            this.name = "";
            this.lore = List.of();
            this.slot = 0;
            this.data = "";
        }

        /**
         * Sets the ItemType.
         *
         * @param type The ItemType.
         * @return The updated Builder.
         */
        public Builder type(final ClickTypes type) {
            this.type = type;
            return this;
        }

        /**
         * Sets the base ItemStack.
         *
         * @param item The ItemStack.
         * @return The updated Builder.
         */
        public Builder item(final ItemStack item) {
            this.item = item;
            return this;
        }

        /**
         * Sets the raw Item name. This will update alongside a GUI.
         *
         * @param name The item's raw name.
         * @return The updated Builder.
         */
        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the raw Item lore. This will update alongside a GUI.
         *
         * @param lore The item's raw lore.
         * @return The updated Builder.
         */
        public Builder lore(final List<String> lore) {
            this.lore = lore;
            return this;
        }

        /**
         * Sets the Item's slot. Converts to a {@link Vector2}
         *
         * @param slot The desired slot.
         * @return The updated Builder.
         */
        public Builder slot(final int slot) {
            this.slot = slot;
            return this;
        }

        /**
         * Sets the Item's data.
         *
         * @param data The data.
         * @return The updated Builder.
         */
        public Builder data(final String data) {
            this.data = data;
            return this;
        }

        /**
         * Builds the item.
         *
         * @return A new Item instance.
         */
        public Item build() {
            return new Item(this.item, this.name, this.lore, this.type, this.data, this.slot);
        }
    }
}
