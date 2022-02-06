package games.cultivate.mcmmocredits.config;

import com.destroystokyo.paper.profile.ProfileProperty;
import dev.dbassett.skullcreator.SkullCreator;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class ItemStackSerializer implements TypeSerializer<ItemStack> {
    public static final ItemStackSerializer INSTANCE = new ItemStackSerializer();

    public ItemStack deserializePlayer(ConfigurationNode node, Player player) {
        ItemStack item = this.deserialize(ItemStack.class, node);
        item.editMeta(meta -> {
            meta.displayName(Util.parse(Objects.requireNonNull(meta.displayName()), player));
            meta.lore(Objects.requireNonNull(meta.lore()).stream().map(i -> Util.parse(i, player)).toList());
        });
        return item;
    }

    public ItemStack deserializeConfig(ConfigurationNode node) {
        ItemStack item = node.node("skull").virtual() ?  new ItemStack(Material.valueOf(node.node("material").getString())) : SkullCreator.itemWithBase64(new ItemStack(Material.PLAYER_HEAD), Objects.requireNonNull(node.node("skull").getString()));
        item.setDurability((short) node.node("durability").getInt());
        item.editMeta(meta -> {
            try {
                meta.lore(node.node("lore").getList(String.class, List.of()).stream().map(i -> Component.empty().style(Util.DEFAULT_STYLE).append(MiniMessage.miniMessage().deserialize(i)).asComponent()).toList());
                if (node.node("glow").getBoolean()) {
                    meta.addEnchant(Enchantment.ARROW_INFINITE, 10, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return item;
    }

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) {
            ItemStack item = node.node("skull").virtual() ?  new ItemStack(Material.valueOf(node.node("material").getString())) : SkullCreator.itemWithBase64(new ItemStack(Material.PLAYER_HEAD), Objects.requireNonNull(node.node("skull").getString()));
            item.setAmount(node.node("amount").getInt());
            item.setDurability((short) node.node("durability").getInt());
            item.editMeta(meta -> {
                try {
                    meta.displayName(Component.text(node.node("name").getString("")));
                    meta.lore(node.node("lore").getList(String.class, List.of()).stream().map(i -> Component.text(i).asComponent()).toList());
                    if (node.node("glow").getBoolean()) {
                        meta.addEnchant(Enchantment.ARROW_INFINITE, 10, true);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    }
                    int slot = node.node("inventory-slot").virtual() ? 0 : node.node("inventory-slot").getInt();
                    meta.getPersistentDataContainer().set(MCMMOCredits.key, PersistentDataType.INTEGER, slot);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return item;
        }

    @Override
    public void serialize(Type type, @Nullable ItemStack obj, ConfigurationNode node) {
        if (obj != null) {
            try {
                node.node("material").set(obj.getType().name());
                node.node("name").set(MiniMessage.miniMessage().serialize(obj.displayName()));
                node.node("amount").set(obj.getAmount());
                node.node("durability").set((int) obj.getDurability());
                if (obj.hasItemMeta() && obj.getItemMeta() != null) {
                    ItemMeta meta = obj.getItemMeta();
                    node.node("glow").set(!obj.getEnchantments().isEmpty());
                    if (meta.hasLore()) {
                        node.node("lore").set(Objects.requireNonNull(obj.lore()).stream().map(i -> MiniMessage.miniMessage().serialize(i)).toList());
                    }
                    if (meta instanceof SkullMeta skullMeta) {
                        for (ProfileProperty property : Objects.requireNonNull(skullMeta.getPlayerProfile()).getProperties()) {
                            if (property.getName().equalsIgnoreCase("textures")) {
                                node.node("skull").set(property.getValue());
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
