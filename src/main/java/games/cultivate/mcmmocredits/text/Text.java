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

import java.util.function.UnaryOperator;

/**
 * Represents a chat message that will be parsed and sent to a user.
 */
public final class Text {
    private static final Component NO_ITALICS = Component.empty().decoration(TextDecoration.ITALIC, false);
    private final Audience audience;
    private final Resolver resolver;
    private String content;

    /**
     * Constructs the object.
     *
     * @param audience The message's target.
     * @param content  The message's content.
     * @param resolver The message's resolver.
     */
    private Text(final Audience audience, final String content, final Resolver resolver) {
        this.audience = audience;
        this.content = content;
        this.resolver = resolver;
    }

    /**
     * Constructs the object.
     *
     * @param audience The message's target.
     * @param content  The message's content.
     * @param resolver The message's resolver.
     * @return the Text.
     */
    public static Text fromString(final Audience audience, final String content, final Resolver resolver) {
        return new Text(audience, content, resolver);
    }

    /**
     * Constructs the object using a CommandExecutor.
     *
     * @param executor The message's target as a CommandExecutor.
     * @param content  The message's content.
     * @param resolver The message's resolver.
     * @return the Text.
     */
    public static Text fromString(final CommandExecutor executor, final String content, final Resolver resolver) {
        return Text.fromString(executor.sender(), content, addMessageViewer(executor, resolver));
    }

    /**
     * Constructs the object using a CommandExecutor, a generic Resolver.
     *
     * @param executor The message's target as a CommandExecutor.
     * @param content  The message's content.
     * @return the Text.
     */
    public static Text forOneUser(final CommandExecutor executor, final String content) {
        return Text.fromString(executor, content, Resolver.ofUser(executor));
    }

    /**
     * Constructs the object using a CommandExecutor, and a customized Resolver.
     *
     * @param executor The message's target as a CommandExecutor.
     * @param content  The message's content.
     * @param operator Function to apply to resolver.
     * @return the Text.
     */
    public static Text forOneUser(final CommandExecutor executor, final String content, final UnaryOperator<Resolver> operator) {
        return Text.fromString(executor, content, operator.apply(Resolver.ofUser(executor)));
    }

    /**
     * Adds Resolver tags for the viewer of the Text.
     *
     * @param executor The message viewer.
     * @param resolver The current resolver.
     * @return modified or existing resolver, depending on if the user is a player.
     */
    private static Resolver addMessageViewer(final CommandExecutor executor, final Resolver resolver) {
        return executor.isPlayer() ? resolver.addUser(executor.toUser(), "viewer") : resolver;
    }

    /**
     * Converts the object to a Component. The object is stripped of leading italics and parsed for placeholders.
     *
     * @return The converted Component.
     */
    public Component toComponent() {
        if (this.audience instanceof Player player) {
            this.content = PlaceholderAPI.setPlaceholders(player, this.content);
        }
        return NO_ITALICS.append(MiniMessage.miniMessage().deserialize(this.content, this.resolver.toTagResolver()));
    }

    /**
     * Sends the Text to the audience.
     */
    public void send() {
        this.audience.sendMessage(this.toComponent());
    }
}
