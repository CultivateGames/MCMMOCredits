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
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.core.util.Vector2;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;

import java.util.List;

/**
 * An {@link ItemStack} used within a {@link Menu}.
 */
public final class Item {
    private final ClickTypes type;
    private final ItemStack stack;
    private final ItemProperties properties;
    private final int slot;
    private final String data;

    /**
     * Constructs the object.
     *
     * @param stack      The representative ItemStack. Updated with refreshing name/lore.
     * @param properties Updatable properties of the item.
     * @param type       Item ClickTypes that determines what type of click is applied.
     * @param slot       Location of item in the inventory.
     * @param data       String data used to construct the click. For example, could represent a command or skill type.
     */
    private Item(final ItemStack stack, final ItemProperties properties, final ClickTypes type, final String data, final int slot) {
        this.stack = stack;
        this.properties = properties;
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
        return Item.builder().item(new ItemStack(material, 1)).build();
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
     * Converts the Item into a builder.
     *
     * @return New instance of the Item Builder with preserved properties.
     */
    public Item.Builder toBuilder() {
        return Item.builder().item(this.stack).properties(this.properties).type(this.type).slot(this.slot).data(this.data);
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
        return this.properties.name();
    }

    /**
     * Gets the raw item lore.
     *
     * @return The Item's lore.
     */
    public List<String> lore() {
        return this.properties.lore();
    }

    /**
     * Get the ItemProperties.
     *
     * @return The ItemProperties.
     */
    public ItemProperties properties() {
        return this.properties;
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

    public TransformContext<ChestPane, PlayerViewer> context(final ClickFactory clickFactory, final Resolver resolver) {
        String info = this.type.name().startsWith("EDIT_") ? this.name() : this.data;
        var handler = clickFactory.getClick(this.type, info, resolver);
        return TransformContext.of(0, ((pane, view) -> {
            ItemStack menuItem = this.properties.apply(this.stack, view.viewer().player(), resolver);
            return pane.element(ItemStackElement.of(menuItem, handler), this.slot % 9, this.slot / 9);
        }));
    }

    /**
     * Builder class for {@link Item}.
     */
    public static final class Builder {
        private ClickTypes type;
        private ItemStack item;
        private ItemProperties properties;
        private int slot;
        private String data;

        /**
         * Constructs the Builder with sane defaults.
         */
        private Builder() {
            this.type = ClickTypes.FILL;
            this.item = new ItemStack(Material.STONE, 1);
            this.properties = ItemProperties.empty();
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
            this.properties = new ItemProperties(name, this.properties.lore());
            return this;
        }

        /**
         * Sets the raw Item lore. This will update alongside a GUI.
         *
         * @param lore The item's raw lore.
         * @return The updated Builder.
         */
        public Builder lore(final List<String> lore) {
            this.properties = new ItemProperties(this.properties.name(), lore);
            return this;
        }

        public Builder properties(final ItemProperties properties) {
            this.properties = properties;
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
            return new Item(this.item, this.properties, this.type, this.data, this.slot);
        }
    }
}
