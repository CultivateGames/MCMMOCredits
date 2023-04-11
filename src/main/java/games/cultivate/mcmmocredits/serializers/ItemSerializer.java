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
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.menu.ClickTypes;
import games.cultivate.mcmmocredits.menu.Item;
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
import java.util.UUID;

/**
 * Handles serialization/deserialization of {@link Item} from {@link Config}
 */
public final class ItemSerializer implements TypeSerializer<Item> {
    public static final ItemSerializer INSTANCE = new ItemSerializer();

    @Override
    public Item deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        Material material = node.node("material").get(Material.class, Material.STONE);
        int amount = node.node("amount").getInt(1);
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (node.node("glow").getBoolean(false)) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.ARROW_INFINITE, 10, true);
        }
        String texture = node.node("texture").getString("");
        if (material != Material.PLAYER_HEAD || texture.isEmpty()) {
            item.setItemMeta(meta);
        } else {
            SkullMeta skullMeta = (SkullMeta) meta;
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", texture));
            skullMeta.setPlayerProfile(profile);
            item.setItemMeta(skullMeta);
        }
        ClickTypes clickType = node.get(ClickTypes.class, ClickTypes.FILL);
        String data = switch (clickType) {
            case COMMAND -> node.node("command").getString("");
            case EDIT_MESSAGE -> "messages";
            case EDIT_SETTING -> "settings";
            case REDEEM -> ((String) node.key()).toUpperCase();
            case FILL -> "fill";
        };
        String name = node.node("name").getString("");
        List<String> lore = node.node("lore").getList(String.class);
        int slot = node.node("slot").getInt();
        return Item.builder()
                .name(name)
                .lore(lore)
                .slot(slot)
                .item(item)
                .type(clickType)
                .data(data)
                .build();
    }

    @Override
    public void serialize(final Type type, final Item item, final ConfigurationNode node) throws SerializationException {
        node.node("name").set(item.name());
        node.node("lore").setList(String.class, item.lore());
        node.node("slot").set(item.slot());
        ItemStack stack = item.stack();
        node.node("material").set(stack.getType());
        node.node("amount").set(stack.getAmount());
        String texture = "";
        if (stack.getItemMeta() instanceof SkullMeta skullMeta) {
            PlayerProfile profile = skullMeta.getPlayerProfile();
            for (ProfileProperty x : profile.getProperties()) {
                if (x.getName().equals("textures")) {
                    texture = x.getValue();
                    break;
                }
            }
        }
        node.node("texture").set(texture);
        node.node("glow").set(!stack.getEnchantments().isEmpty());
        ClickTypes clickType = item.clickType();
        if (clickType == ClickTypes.COMMAND) {
            node.node("command").set(item.data());
        }
    }
}
