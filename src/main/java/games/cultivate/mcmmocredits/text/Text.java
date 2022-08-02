package games.cultivate.mcmmocredits.text;

import games.cultivate.mcmmocredits.placeholders.Resolver;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

    public static Text fromString(final Audience audience, final String content) {
        if (audience instanceof Player p) {
            return new Text(audience, content, Resolver.fromPlayer(p));
        }
        return new Text(audience, content, Resolver.fromSender((CommandSender) audience));
    }

    public static Component parseComponent(final Component comp, final Player player) {
        String content = PlainTextComponentSerializer.plainText().serialize(comp);
        //TODO find better way to delegate local placeholder to PAPI. In the meantime this simplifies the resolver.
        content = content.replace("<credits>", "%mcmmocredits_credits%");
        content = PlaceholderAPI.setPlaceholders(player, content);
        return MiniMessage.miniMessage().deserialize(content, Resolver.fromPlayer(player));
    }

    public Component toComponent() {
        if (this.audience instanceof Player player) {
            //TODO find better way to delegate local placeholder to PAPI. In the meantime this simplifies the resolver.
            this.content = this.content.replace("<credits>", "%mcmmocredits_credits%");
            this.content = PlaceholderAPI.setPlaceholders(player, this.content);
        }
        return MiniMessage.miniMessage().deserialize(this.content, this.resolver);
    }

    public void send() {
        this.audience.sendMessage(Component.empty().decoration(TextDecoration.ITALIC, false).append(this.toComponent()));
    }
}
