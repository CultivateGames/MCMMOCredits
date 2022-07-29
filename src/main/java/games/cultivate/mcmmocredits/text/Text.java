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

public class Text {
    private final Audience audience;
    private final TagResolver resolver;
    private String content;

    private Text(Audience audience, String content, TagResolver resolver) {
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
    public static Text fromString(Audience audience, String content, TagResolver resolver) {
        return new Text(audience, content, resolver);
    }

    public static Text fromString(Audience audience, String content) {
        return new Text(audience, content, createResolver(audience));
    }

    public static Component parseComponent(Component comp, Player player) {
        String content = PlainTextComponentSerializer.plainText().serialize(comp);
        //TODO find better way to delegate local placeholder to PAPI. In the meantime this simplifies the resolver.
        content = content.replace("<credits>", "%mcmmocredits_credits%");
        content = PlaceholderAPI.setPlaceholders(player, content);
        return Text.removeItalics(MiniMessage.miniMessage().deserialize(content, Resolver.fromPlayer(player)));
    }

    public static Component removeItalics(Component component) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(component);
    }

    private static TagResolver createResolver(Audience audience) {
        return audience instanceof Player p ? Resolver.fromPlayer(p) : Resolver.fromSender((CommandSender) audience);
    }

    public Component toComponent() {
        if (this.audience instanceof Player player) {
            this.content = PlaceholderAPI.setPlaceholders(player, this.content);
        }
        return MiniMessage.miniMessage().deserialize(this.content, this.resolver);
    }

    public void send() {
        this.audience.sendMessage(removeItalics(this.toComponent()));
    }
}
