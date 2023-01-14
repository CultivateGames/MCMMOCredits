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

import games.cultivate.mcmmocredits.placeholders.ResolverFactory;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

/**
 * Object which represents any messaging we send to users.
 */
public final class Text {
    private final Audience audience;
    private final TagResolver resolver;
    private String content;

    private Text(final Audience audience, final String content, final TagResolver resolver) {
        this.audience = audience;
        this.content = content;
        this.resolver = resolver;
    }

    /**
     * Creates a Text object from an existing String.
     *
     * @param audience recipient of the message.
     * @param content  content of the message.
     * @param resolver TagResolver to use for parsing.
     * @return a populated Text object.
     */
    public static Text fromString(final Audience audience, final String content, final TagResolver resolver) {
        return new Text(audience, content, resolver);
    }

    /**
     * Utility method to parse a {@link Component} for {@link PlaceholderAPI} placeholders.
     *
     * @param player    {@link Player} to parse placeholders against.
     * @param component Existing {@link Component} to scan for parseable placeholders.
     * @param factory   An injected {@link ResolverFactory} to parse local placeholders.
     * @return The modified {@link Component}.
     */
    public static Component parseComponent(final Player player, final Component component, final ResolverFactory factory) {
        return Text.fromString(player, PlainTextComponentSerializer.plainText().serialize(component), factory.fromUsers(player)).toComponent();
    }

    /**
     * Method that mutates the current {@link Text} object into a {@link Component}. Parses external placeholders, filters unwanted italics from the text and deserializes {@link MiniMessage} tags.
     *
     * @return A {@link Component} that is ready to send to the {@link Audience}
     */
    public Component toComponent() {
        if (this.audience instanceof Player player) {
            this.content = PlaceholderAPI.setPlaceholders(player, this.content);
        }
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(this.content, this.resolver));
    }

    /**
     * Sends the {@link Text} to the attached {@link Audience}. Transforms the object into a {@link Component} and then sends it.
     */
    public void send() {
        this.audience.sendMessage(this.toComponent());
    }
}
