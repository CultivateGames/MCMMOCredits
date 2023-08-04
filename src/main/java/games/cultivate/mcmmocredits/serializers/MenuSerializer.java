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
import games.cultivate.mcmmocredits.menu.Menu;
import games.cultivate.mcmmocredits.menu.RegularMenu;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Handles serialization/deserialization of a Menu.
 */
public final class MenuSerializer implements TypeSerializer<Menu> {
    public static final MenuSerializer INSTANCE = new MenuSerializer();

    /**
     * {@inheritDoc}
     */
    @Override
    public Menu deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        Map<String, Item> items = new HashMap<>();
        for (ConfigurationNode entry : node.node("items").childrenMap().values()) {
            items.put(Objects.requireNonNull(entry.key()).toString(), entry.get(Item.class));
        }
        String title = node.node("title").getString();
        int slots = node.node("slots").getInt();
        boolean fill = node.node("fill").getBoolean();
        boolean navigation = node.node("navigation").getBoolean();
        return new RegularMenu(items, title, slots, fill, navigation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final Type type, final Menu menu, final ConfigurationNode node) throws SerializationException {
        node.node("title").set(menu.title());
        node.node("slots").set(menu.slots());
        node.node("fill").set(menu.fill());
        node.node("navigation").set(menu.navigation());
        for (Map.Entry<String, Item> entry : menu.items().entrySet()) {
            node.node("items", entry.getKey()).set(Item.class, entry.getValue());
        }
    }
}
