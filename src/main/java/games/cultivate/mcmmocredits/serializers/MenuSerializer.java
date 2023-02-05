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

import games.cultivate.mcmmocredits.config.MenuConfig.MenuProperties;
import games.cultivate.mcmmocredits.menu.Item;
import games.cultivate.mcmmocredits.menu.ItemType;
import games.cultivate.mcmmocredits.menu.Menu;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class MenuSerializer implements TypeSerializer<Menu> {
    public static final MenuSerializer INSTANCE = new MenuSerializer();

    @Override
    public Menu deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        MenuProperties properties = node.node("properties").get(MenuProperties.class);
        List<Item> items = new ArrayList<>();
        for (var entry : node.node("items").childrenMap().entrySet()) {
            Item item = entry.getValue().get(Item.class);
            ItemType itemType = item.type();
            if (itemType == ItemType.FILL || itemType == ItemType.MAIN_MENU) {
                continue;
            }
            items.add(item);
        }
        if (properties.navigation()) {
            items.add(node.node("items", "navigation").get(Item.class));
        }
        return new Menu(properties, items);
    }

    @Override
    public void serialize(final Type type, @Nullable final Menu obj, final ConfigurationNode node) {
        throw new UnsupportedOperationException("Cannot serialize Menu");
    }
}
