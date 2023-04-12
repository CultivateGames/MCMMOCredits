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

import games.cultivate.mcmmocredits.menu.ClickTypes;
import games.cultivate.mcmmocredits.util.Util;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class ClickTypeSerializer implements TypeSerializer<ClickTypes> {
    public static final ClickTypeSerializer INSTANCE = new ClickTypeSerializer();

    /**
     * Deserializes the ClickType by checking the node's key.
     *
     * @param type the type of return value required
     * @param node the node containing serialized data
     * @return The ClickType.
     */
    @Override
    public ClickTypes deserialize(final Type type, final ConfigurationNode node) {
        String data = (String) node.key();
        if (Util.getSkillNames().contains(data)) {
            return ClickTypes.REDEEM;
        }
        return switch (data) {
            case "messages" -> ClickTypes.EDIT_MESSAGE;
            case "settings" -> ClickTypes.EDIT_SETTING;
            case "navigation", "config", "redeem" -> ClickTypes.COMMAND;
            case "fill" -> ClickTypes.FILL;
            default -> throw new IllegalStateException("Invalid data was passed to the ClickTypeSerializer! value:" + data);
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final Type type, @Nullable final ClickTypes obj, final ConfigurationNode node) {
        throw new UnsupportedOperationException("Operation not supported!");
    }
}
