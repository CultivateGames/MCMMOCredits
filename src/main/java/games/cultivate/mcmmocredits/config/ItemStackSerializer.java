package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.MCMMOCredits;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) {
        ItemStack item = new ItemStack(Material.valueOf(node.node("material").getString()));
        item.setAmount(node.node("amount").getInt());
        item.setDurability((short) node.node("durability").getInt());
        item.editMeta(meta -> {
            try {
                meta.displayName(MiniMessage.miniMessage().deserialize(Objects.requireNonNull(node.node("name").getString())));
                meta.lore(Objects.requireNonNull(node.node("lore").getList(String.class)).stream().map(i -> MiniMessage.miniMessage().deserialize(i)).toList());
                if (node.node("glow").getBoolean()) {
                    meta.addEnchant(Enchantment.ARROW_INFINITE, 10, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                meta.getPersistentDataContainer().set(MCMMOCredits.key, PersistentDataType.INTEGER, node.node("inventory-slot").getInt());
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
                    node.node("inventory-slot").set(1);
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
