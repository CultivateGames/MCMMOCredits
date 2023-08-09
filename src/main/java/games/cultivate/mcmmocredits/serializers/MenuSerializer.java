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

import games.cultivate.mcmmocredits.menu.Item;
import games.cultivate.mcmmocredits.menu.RedeemMenu;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Handles serialization/deserialization of a RedeemMenu.
 */
public final class MenuSerializer implements TypeSerializer<RedeemMenu> {
    public static final MenuSerializer INSTANCE = new MenuSerializer();

    /**
     * {@inheritDoc}
     */
    @Override
    public RedeemMenu deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        String title = node.node("title").getString();
        int slots = node.node("slots").getInt();
        boolean fill = node.node("fill").getBoolean();
        boolean navigation = node.node("navigation").getBoolean();
        Map<String, Item> items = new HashMap<>();
        BitSet occupiedSlots = new BitSet(slots);
        for (ConfigurationNode entry : node.node("items").childrenMap().values()) {
            Item item = entry.get(Item.class);
            items.put(Objects.requireNonNull(entry.key()).toString(), item);
            occupiedSlots.set(item.slot());
        }
        Item filler = items.remove("fill");
        if (!navigation) {
            int nav = items.remove("navigation").slot();
            items.put("fill" + nav, filler.slot(nav));
        }
        if (fill) {
            for (int i = occupiedSlots.nextClearBit(0); i < slots; i = occupiedSlots.nextClearBit(++i)) {
                items.put("fill" + i, filler.slot(i));
                occupiedSlots.set(i);
            }
        }
        return new RedeemMenu(items, title, slots, fill, navigation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final Type type, final RedeemMenu menu, final ConfigurationNode node) throws SerializationException {
        node.node("title").set(menu.title());
        node.node("slots").set(menu.slots());
        node.node("fill").set(menu.fill());
        node.node("navigation").set(menu.navigation());
        for (Map.Entry<String, Item> entry : menu.items().entrySet()) {
            node.node("items", entry.getKey()).set(Item.class, entry.getValue());
        }
    }
}
