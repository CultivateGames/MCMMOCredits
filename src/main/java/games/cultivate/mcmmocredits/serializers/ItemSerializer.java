//
// MIT License
//
// Copyright (c) 2022 Cultivate Games
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

import broccolai.corn.paper.item.AbstractPaperItemBuilder;
import broccolai.corn.paper.item.PaperItemBuilder;
import broccolai.corn.paper.item.special.SkullBuilder;
import games.cultivate.mcmmocredits.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * Class responsible for serializing and deserializing {@link ItemStack} from {@link Config}
 */
public final class ItemSerializer implements TypeSerializer<ItemStack> {
    public static final ItemSerializer INSTANCE = new ItemSerializer();

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * Creates a {@link AbstractPaperItemBuilder}, based on whether the item might have {@link SkullMeta}
     *
     * @param node The {@link ConfigurationNode} to inspect.
     * @return The correct {@link AbstractPaperItemBuilder}
     * @throws SerializationException Thrown if checking {@link ConfigurationNode} for a {@link Material} fails.
     */
    private AbstractPaperItemBuilder<?, ?> generateBuilder(final ConfigurationNode node) throws SerializationException {
        if (!node.node("skull").virtual()) {
            return SkullBuilder.ofPlayerHead().textures(node.node("skull").getString(""));
        }
        return PaperItemBuilder.ofType(node.node("material").get(Material.class, Material.AIR));
    }
}
