package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class ItemStackSerializer implements TypeSerializer<ItemStack> {
    public static final ItemStackSerializer INSTANCE = new ItemStackSerializer();

    public ItemStack deserializePlayer(Type type, ConfigurationNode node, Player player) {
        ItemStack item = this.deserialize(type, node);
        item.editMeta(meta -> {
            meta.displayName(Util.parse(Objects.requireNonNull(meta.displayName()), player));
            meta.lore(Objects.requireNonNull(meta.lore()).stream().map(i -> Util.parse(i, player)).toList());
        });
        return item;
    }

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) {
        ItemStack item = new ItemStack(Material.valueOf(node.node("material").getString()));
        item.setAmount(node.node("amount").getInt());
        item.setDurability((short) node.node("durability").getInt());
        item.editMeta(meta -> {
            try {
                meta.displayName(Component.text(node.node("name").getString("")));
                meta.lore(Objects.requireNonNull(node.node("lore").getList(String.class)).stream().map(i -> Component.text(i).asComponent()).toList());
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
                    node.node("glow").set(!obj.getEnchantments().isEmpty());
                    if (obj.getItemMeta().hasLore()) {
                        node.node("lore").set(Objects.requireNonNull(obj.lore()).stream().map(i -> MiniMessage.miniMessage().serialize(i)).toList());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
