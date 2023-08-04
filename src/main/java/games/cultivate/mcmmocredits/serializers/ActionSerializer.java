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

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.menu.Action;
import games.cultivate.mcmmocredits.menu.Action.Command;
import games.cultivate.mcmmocredits.util.Util;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * (De)serializes all Action types based on traits of the underlying item configuration.
 */
public final class ActionSerializer implements TypeSerializer<Action> {
    public static final ActionSerializer INSTANCE = new ActionSerializer();

    /**
     * {@inheritDoc}
     */
    @Override
    public Action deserialize(final Type type, final ConfigurationNode node) {
        String key = node.key().toString();
        if (key.equals("messages") || key.equals("settings")) {
            return Action.editConfig(node.path());
        }
        if (!node.node("command").virtual()) {
            return new Command(node.node("command").getString());
        }
        if (Util.getSkillNames().contains(key)) {
            return Action.redeem(PrimarySkillType.valueOf(key.toUpperCase()));
        }
        return Action.nothing();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final Type type, @Nullable final Action obj, final ConfigurationNode node) {
        throw new UnsupportedOperationException("Actions cannot be independently serialized! (currently)");
    }
}
