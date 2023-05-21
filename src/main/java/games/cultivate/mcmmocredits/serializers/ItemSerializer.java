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
package games.cultivate.mcmmocredits.serializers;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.ui.item.BaseItem;
import games.cultivate.mcmmocredits.ui.item.CommandItem;
import games.cultivate.mcmmocredits.ui.item.ConfigItem;
import games.cultivate.mcmmocredits.ui.item.Item;
import games.cultivate.mcmmocredits.ui.item.RedeemItem;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles serialization/deserialization of an Item.
 */
public final class ItemSerializer implements TypeSerializer<Item> {
    public static final ItemSerializer INSTANCE = new ItemSerializer();

    /**
     * {@inheritDoc}
     */
    @Override
    public Item deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        Material material = node.node("material").get(Material.class, Material.STONE);
        int amount = node.node("amount").getInt(1);
        String name = node.node("name").getString("");
        List<String> lore = node.node("lore").getList(String.class);
        int slot = node.node("slot").getInt();
        String texture = node.node("texture").getString("");
        int customModelData = node.node("custom-model-data").getInt(0);
        ItemStack stack = new ItemStack(material, amount);
        ItemMeta meta = stack.getItemMeta();
        if (node.node("glow").getBoolean(false)) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.ARROW_INFINITE, 10, true);
        }
        meta.setCustomModelData(customModelData);
        stack.setItemMeta(texture.isEmpty() ? meta : this.createSkullMeta(meta, texture));
        Item item = BaseItem.of(stack, name, lore, slot);
        String key = node.key().toString();
        if (key.equals("messages") || key.equals("settings")) {
            return ConfigItem.of(node.path(), item);
        }
        if (!node.node("command").virtual()) {
            return CommandItem.of(node.node("command").getString(), item);
        }
        if (Util.getSkillNames().contains(key)) {
            return RedeemItem.of(PrimarySkillType.valueOf(key.toUpperCase()), item);
        }
        return item;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final Type type, final Item item, final ConfigurationNode node) throws SerializationException {
        ItemStack stack = item.stack();
        node.node("name").set(item.name());
        node.node("lore").setList(String.class, item.lore());
        node.node("slot").set(item.slot());
        node.node("material").set(stack.getType());
        node.node("amount").set(stack.getAmount());
        ItemMeta meta = stack.getItemMeta();
        node.node("texture").set(meta instanceof SkullMeta skullMeta ? this.getTexture(skullMeta) : "");
        node.node("custom-model-data").set(meta.hasCustomModelData() ? meta.getCustomModelData() : 0);
        node.node("glow").set(!stack.getEnchantments().isEmpty());
        if (item instanceof CommandItem citem) {
            node.node("command").set(citem.command());
        }
    }

    /**
     * Creates SkullMeta from provided ItemMeta and texture string.
     *
     * @param meta    The current ItemMeta.
     * @param texture The texture string.
     * @return SkullMeta with texture string applied.
     */
    private SkullMeta createSkullMeta(final ItemMeta meta, final String texture) {
        SkullMeta skullMeta = (SkullMeta) meta;
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        profile.setProperty(new ProfileProperty("textures", texture));
        skullMeta.setPlayerProfile(profile);
        return skullMeta;
    }

    /**
     * Gets String texture from provided SkullMeta.
     *
     * @param meta The item meta.
     * @return The texture string.
     */
    private String getTexture(final SkullMeta meta) {
        Optional<ProfileProperty> property = meta.getPlayerProfile().getProperties().stream().filter(x -> x.getName().equals("texture")).findAny();
        return property.map(ProfileProperty::getValue).orElse("");
    }
}
