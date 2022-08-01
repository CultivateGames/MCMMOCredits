package games.cultivate.mcmmocredits.serializers;

import broccolai.corn.paper.item.AbstractPaperItemBuilder;
import broccolai.corn.paper.item.PaperItemBuilder;
import broccolai.corn.paper.item.special.SkullBuilder;
import games.cultivate.mcmmocredits.text.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class ItemSerializer implements TypeSerializer<ItemStack> {
    public static final ItemSerializer INSTANCE = new ItemSerializer();

    @Override
    public ItemStack deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        AbstractPaperItemBuilder<?, ?> builder = this.generateBuilder(node)
                .amount(node.node("amount").getInt(1))
                .name(Component.text(node.node("name").getString("")))
                .lore(node.node("lore").getList(String.class).stream().map(i -> Component.text(i).asComponent()).toList());
        if (node.node("glow").getBoolean(false)) {
            builder = builder.addEnchant(Enchantment.ARROW_INFINITE, 10).addFlag(ItemFlag.HIDE_ENCHANTS);
        }
        return builder.build();
    }

    @Override
    public void serialize(final Type type, final ItemStack item, final ConfigurationNode node) throws SerializationException {
        AbstractPaperItemBuilder<?, ?> builder = this.generateBuilder(node);
        if (builder instanceof SkullBuilder sb) {
            node.node("skull").set(sb.textures().get(0).getValue());
        }
        node.node("material").set(builder.material());
        node.node("amount").set(builder.amount());
        node.node("name").set(MiniMessage.miniMessage().serialize(builder.name()));
        node.node("lore").setList(String.class, builder.lore().stream().map(i -> MiniMessage.miniMessage().serialize(i)).toList());
        node.node("glow").set(!builder.enchants().isEmpty());
    }

    private AbstractPaperItemBuilder<?, ?> generateBuilder(final ConfigurationNode node) throws SerializationException {
        if (!node.node("skull").virtual()) {
            return SkullBuilder.ofPlayerHead().textures(node.node("skull").getString(""));
        }
        return PaperItemBuilder.ofType(node.node("material").get(Material.class, Material.AIR));
    }

    public ItemStack deserializePlayer(final CommentedConfigurationNode node, final Player player) throws SerializationException {
        ItemStack result = this.deserialize(ItemStack.class, node);
        return PaperItemBuilder.of(result)
                .name(Text.parseComponent(Component.text(node.node("name").getString("")), player))
                .loreModifier(i -> i.forEach(x -> Text.parseComponent(x, player))).build();
    }
}
