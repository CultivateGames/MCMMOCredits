package games.cultivate.mcmmocredits.serializers;

import com.destroystokyo.paper.profile.ProfileProperty;
import dev.dbassett.skullcreator.SkullCreator;
import games.cultivate.mcmmocredits.item.Item;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class ItemSerializer implements TypeSerializer<ItemStack> {
    public static final ItemSerializer INSTANCE = new ItemSerializer();

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) {
        Item item = new Item();
        item.material(Material.valueOf(node.node("material").getString()));
        item.amount(node.node("amount").getInt(1));
        item.durability((short) node.node("durability").getInt(0));
        item.name(Component.text(node.node("name").getString("")));
        item.glow(node.node("glow").getBoolean(false));
        List<Component> lore = new ArrayList<>();
        try {
            node.node("lore").getList(String.class).forEach(i -> lore.add(Component.text(i)));
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        item.lore(lore);
        ItemStack result = item.toStack();
        ConfigurationNode skull = node.node("skull");
        return skull.virtual() ? result : SkullCreator.itemWithBase64(result, skull.getString(""));
    }

    @Override
    public void serialize(Type type, ItemStack item, ConfigurationNode node) {
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            List<String> list = new ArrayList<>();
            meta.lore().forEach(i -> list.add(MiniMessage.miniMessage().serialize(i)));
            try {
                node.node("material").set(item.getType().name());
                node.node("amount").set(item.getAmount());
                node.node("durability").set(item.getDurability());
                node.node("name").set(MiniMessage.miniMessage().serialize(meta.displayName()));
                node.node("glow").set(!meta.getEnchants().isEmpty());
                node.node("lore").setList(String.class, list);
                if (meta instanceof SkullMeta skullMeta) {
                    for (ProfileProperty pp : skullMeta.getPlayerProfile().getProperties()) {
                        if (pp.getName().equals("textures")) {
                            node.node("skull").set(pp.getValue());
                        }
                    }
                }
            } catch (SerializationException e) {
                e.printStackTrace();
            }
        }
    }

    public ItemStack deserializePlayer(CommentedConfigurationNode node, Player player) {
        return new Item(this.deserialize(ItemStack.class, node)).toStack(player);
    }
}
