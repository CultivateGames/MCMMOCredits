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
import org.incendo.interfaces.core.util.Vector2;
import org.incendo.interfaces.paper.utils.PaperUtils;

import java.util.List;

/**
 * An {@link ItemStack} used within a {@link Menu}.
 */
public final class Item {
    private final ItemType type;
    private final ItemStack stack;
    private final String name;
    private final List<String> lore;
    private final Vector2 vector;
    private final String data;

    /**
     * Constructs the object.
     *
     * @param stack  The representative ItemStack. Updated with refreshing name/lore.
     * @param name   Item name string. Refreshes to parse placeholders.
     * @param lore   Item lore as strings. Refreshes to parse placeholders.
     * @param type   Item Type that determines what type of {@link MenuTransform} is applied.
     * @param vector Location data. Converts to slot.
     * @param data   Optional String data. Can carry information needed in other parts of Menu construction.
     */
    private Item(final ItemStack stack, final String name, final List<String> lore, final ItemType type, final Vector2 vector, final String data) {
        this.stack = stack;
        this.name = name;
        this.lore = lore;
        this.type = type;
        this.vector = vector;
        this.data = data;
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
     * Returns a new instance of the Item Builder.
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
        return Item.builder().item(this.stack).name(this.name).lore(this.lore).type(this.type).vector(this.vector);
    }

    /**
     * Gets the ItemType.
     *
     * @return The ItemType.
     */
    public ItemType type() {
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
        return PaperUtils.gridToSlot(this.vector);
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
     * Gets the horizontal location of the {@link Vector2}.
     *
     * @return The X location within a {@link Menu} grid.
     */
    public int getX() {
        return this.vector.x();
    }

    /**
     * Gets the vertical location of the {@link Vector2}.
     *
     * @return The Y location within a {@link Menu} grid.
     */
    public int getY() {
        return this.vector.y();
    }

    /**
     * Updates the item's name and lore with the provided Player and Resolver.
     *
     * @param player   The user to update the item against.
     * @param resolver The resolver to update the item against.
     * @return The updated Item as a Bukkit ItemStack.
     */
    public ItemStack update(final Player player, final Resolver resolver) {
        Component display = Text.fromString(player, this.name, resolver).toComponent();
        List<Component> displayLore = this.lore.stream().map(x -> Text.fromString(player, x, resolver).toComponent()).toList();
        ItemStack stackCopy = new ItemStack(this.stack);
        stackCopy.editMeta(meta -> {
            if (!this.name.isEmpty()) {
                meta.displayName(display);
            }
            if (this.lore.stream().noneMatch(String::isEmpty)) {
                meta.lore(displayLore);
            }
        });
        return stackCopy;
    }

    /**
     * Builder class for {@link Item}.
     */
    public static final class Builder {
        private ItemType type;
        private ItemStack item;
        private String name;
        private List<String> lore;
        private Vector2 vector;
        private String data;

        /**
         * Constructs the Builder with sane defaults.
         */
        private Builder() {
            this.type = ItemType.FILL;
            this.item = new ItemStack(Material.STONE, 1);
            this.name = "";
            this.lore = List.of("");
            this.vector = Vector2.at(0, 0);
            this.data = "";
        }

        /**
         * Sets the ItemType.
         *
         * @param type The ItemType.
         * @return The updated Builder.
         */
        public Builder type(final ItemType type) {
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
            this.vector = PaperUtils.slotToGrid(slot);
            return this;
        }

        /**
         * Sets the Item's location data.
         *
         * @param vector The location data.
         * @return The updated Builder.
         */
        public Builder vector(final Vector2 vector) {
            this.vector = vector;
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
            return new Item(this.item, this.name, this.lore, this.type, this.vector, this.data);
        }
    }
}
