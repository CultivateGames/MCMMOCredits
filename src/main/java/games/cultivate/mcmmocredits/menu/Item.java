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

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.base.Suppliers;
import games.cultivate.mcmmocredits.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@ConfigSerializable
public final class Item {
    private final Material material;
    private final int amount;
    private final String name;
    private final List<String> lore;
    private final int customModelData;
    private final boolean glowing;
    private final String texture;
    private final int slot;
    private final transient Supplier<ItemStack> itemSupplier;

    private Item(final Builder builder) {
        this.material = builder.material;
        this.amount = builder.amount;
        this.name = builder.name;
        this.lore = builder.lore;
        this.customModelData = builder.customModelData;
        this.glowing = builder.glowing;
        this.texture = builder.texture;
        this.slot = builder.slot;
        this.itemSupplier = Suppliers.memoize(this::getItem);
    }

    private Item() {
        this(Item.builder());
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public Item withSlot(final int slot) {
        return this.toBuilder().slot(slot).build();
    }

    public int slot() {
        return this.slot;
    }

    public ItemStack getItemFor(final User user) {
        ItemStack item = this.itemSupplier.get();
        item.editMeta(meta -> {
            //TODO: re-impl with Text refactor.
//            TextFormatter<User> formatter = new TextFormatter<>();
//            if (!this.name.isEmpty()) {
//                meta.displayName(formatter.format(user, this.name));
//            }
//            if (!this.lore.stream().allMatch(String::isEmpty)) {
//                meta.lore(this.lore.stream().map(x -> formatter.format(user, x)).toList());
//            }
        });
        return item;
    }

    private ItemStack getItem() {
        ItemStack stack = new ItemStack(this.material, this.amount);
        ItemMeta meta = stack.getItemMeta();
        if (this.glowing) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.ARROW_INFINITE, 10, true);
        }
        meta.setCustomModelData(this.customModelData);
        if (!this.texture.isEmpty()) {
            SkullMeta skullMeta = (SkullMeta) meta;
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", this.texture));
            skullMeta.setPlayerProfile(profile);
            stack.setItemMeta(skullMeta);
        } else {
            stack.setItemMeta(meta);
        }
        return stack;
    }

    public static final class Builder {
        private Material material;
        private int amount;
        private String name;
        private List<String> lore;
        private int customModelData;
        private boolean glowing;
        private String texture;
        private int slot;

        private Builder() {
            this.material = Material.STONE;
            this.amount = 1;
            this.name = "";
            this.lore = List.of();
            this.customModelData = 0;
            this.glowing = false;
            this.texture = "";
            this.slot = -1;
        }

        private Builder(final Item item) {
            this.material = item.material;
            this.amount = item.amount;
            this.name = item.name;
            this.lore = item.lore;
            this.customModelData = item.customModelData;
            this.glowing = item.glowing;
            this.texture = item.texture;
            this.slot = item.slot;
        }

        public Builder material(final Material material) {
            this.material = material;
            return this;
        }

        public Builder amount(final int amount) {
            this.amount = amount;
            return this;
        }

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder lore(final List<String> lore) {
            this.lore = lore;
            return this;
        }

        public Builder customModelData(final int customModelData) {
            this.customModelData = customModelData;
            return this;
        }

        public Builder glowing(final boolean glowing) {
            this.glowing = glowing;
            return this;
        }

        public Builder texture(final String texture) {
            this.texture = texture;
            return this;
        }

        public Builder slot(final int slot) {
            this.slot = slot;
            return this;
        }

        public Item build() {
            return new Item(this);
        }
    }
}
