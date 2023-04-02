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
package games.cultivate.mcmmocredits.text;

import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

/**
 * Represents a Message, a tuple of Audience, Resolver and the content as a String.
 */
public final class Text {
    private static final Component NO_ITALICS = Component.empty().decoration(TextDecoration.ITALIC, false);
    private final Audience audience;
    private final Resolver resolver;
    private String content;

    /**
     * Constructs the object.
     *
     * @param audience recipient of the message.
     * @param content  content of the message.
     * @param resolver Resolver to use for parsing.
     */
    private Text(final Audience audience, final String content, final Resolver resolver) {
        this.audience = audience;
        this.content = content;
        this.resolver = resolver;
    }

    /**
     * Static factory to create a Text.
     *
     * @param audience recipient of the message.
     * @param content  content of the message.
     * @param resolver Resolver to use for parsing.
     * @return a new Text.
     */
    public static Text fromString(final Audience audience, final String content, final Resolver resolver) {
        return new Text(audience, content, resolver);
    }

    /**
     * Static factory to create a Text using a {@link CommandExecutor}.
     *
     * @param executor recipient of the message.
     * @param content  content of the message.
     * @param resolver Resolver to use for parsing.
     * @return a new Text.
     */
    public static Text fromString(final CommandExecutor executor, final String content, final Resolver resolver) {
        return Text.fromString(executor.sender(), content, resolver);
    }

    /**
     * Static factory where the Resolver for the object is derived from the CommandExecutor.
     *
     * @param executor recipient of the message.
     * @param content  content of the message.
     * @return a new Text.
     */
    public static Text forOneUser(final CommandExecutor executor, final String content) {
        return Text.fromString(executor, content, Resolver.ofUser(executor));
    }

    /**
     * Converts a Text to a Component. Placeholders are parsed and italics are removed in this stage.
     *
     * @return A finished Component.
     */
    public Component toComponent() {
        if (this.audience instanceof Player player) {
            this.content = PlaceholderAPI.setPlaceholders(player, this.content);
        }
        return NO_ITALICS.append(MiniMessage.miniMessage().deserialize(this.content, this.resolver.toTagResolver()));
    }

    /**
     * Sends the Text to the audience. Converts the Text to {@link Component} and sends it to the {@link Audience}.
     */
    public void send() {
        this.audience.sendMessage(this.toComponent());
    }
}
