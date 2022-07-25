package games.cultivate.mcmmocredits.serializers;

import com.destroystokyo.paper.profile.ProfileProperty;
import dev.dbassett.skullcreator.SkullCreator;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.text.Text;
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
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("deprecation")
public class ItemStackSerializer implements TypeSerializer<ItemStack> {
    public static final ItemStackSerializer INSTANCE = new ItemStackSerializer();

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) {
        ItemStack item = new ItemStack(Material.valueOf(node.node("material").getString()));
        if (!node.node("skull").virtual()) {
            item = SkullCreator.itemWithBase64(item, node.node("skull").getString(""));
        }
        item.setAmount(node.node("amount").getInt());
        item.setDurability((short) node.node("durability").getInt());
        item.editMeta(meta -> {
            meta.displayName(Component.text(node.node("name").getString("")));
            try {
                List<String> loreList = node.node("lore").getList(String.class);
                meta.lore(loreList.stream().map(i -> Component.text(i).asComponent()).toList());
            } catch (SerializationException e) {
                e.printStackTrace();
            }
            if (node.node("glow").getBoolean(false)) {
                meta.addEnchant(Enchantment.ARROW_INFINITE, 10, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            ConfigurationNode slotNode = node.node("slot");
            meta.getPersistentDataContainer().set(MCMMOCredits.NAMESPACED_KEY, PersistentDataType.INTEGER, slotNode.getInt(0));
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
                if (obj.hasItemMeta()) {
                    ItemMeta meta = obj.getItemMeta();
                    node.node("glow").set(!obj.getEnchantments().isEmpty());
                    if (meta.hasLore()) {
                        node.node("lore").set(obj.lore().stream().map(i -> MiniMessage.miniMessage().serialize(i)).toList());
                    }
                    if (meta instanceof SkullMeta skullMeta) {
                        this.setSkullMeta(skullMeta, (CommentedConfigurationNode) node);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ItemStack deserializePlayer(CommentedConfigurationNode node, Player player) {
        ItemStack item = this.deserialize(ItemStack.class, node);
        item.editMeta(meta -> {
            meta.displayName(Text.parseComponent(meta.displayName(), player));
            if (meta.hasLore()) {
                meta.lore(meta.lore().stream().map(i -> Text.parseComponent(i, player)).toList());
            }
        });
        return item;
    }

    private void setSkullMeta(SkullMeta skullMeta, CommentedConfigurationNode node) throws SerializationException {
        Optional<ProfileProperty> opt = skullMeta.getPlayerProfile().getProperties().stream().filter(i -> i.getName().equals("textures")).findAny();
        if (opt.isPresent()) {
            node.node("skull").set(opt.get().getValue());
        }
    }
}
