package games.cultivate.mcmmocredits.text;

import games.cultivate.mcmmocredits.placeholders.Resolver;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class Text {
    public static final Style DEFAULT_STYLE = Style.style().decoration(TextDecoration.ITALIC, false).build();
    private final Audience audience;
    private String content;
    private final TagResolver resolver;

    private Text(Audience audience, String content, TagResolver resolver) {
        this.audience = audience;
        this.content = content;
        this.resolver = resolver;
    }

    public Component toComponent() {
        if (this.audience instanceof Player player) {
            this.content = PlaceholderAPI.setPlaceholders(player, this.content);
        }
        return MiniMessage.miniMessage().deserialize(this.content, this.resolver);
    }

    /**
     * Creates a Text object from an existing String.
     *
     * @param audience recipient of the message.
     * @param content content of the message.
     * @param resolver TagResolver to use for parsing.
     * @return a populated Text object.
     */
    public static Text fromString(Audience audience, String content, TagResolver resolver) {
        return new Text(audience, content, resolver);
    }

    public static Text fromString(Audience audience, String content) {
        return new Text(audience, content, createResolver(audience));
    }

    public void send() {
        this.audience.sendMessage(this.toComponent());
    }

    public static Component parseComponent(Component comp, Player player) {
        Pattern p = PlaceholderAPI.getPlaceholderPattern();
        comp = comp.replaceText(i -> i.match(p).replacement((m, b) -> b.content(PlaceholderAPI.setPlaceholders(player, m.group()))));
        return Component.empty().style(DEFAULT_STYLE).append(MiniMessage.miniMessage().deserialize(MiniMessage.miniMessage().serialize(comp), Resolver.fromPlayer(player)));
    }

    private static TagResolver createResolver(Audience audience) {
        return audience instanceof Player p ? Resolver.fromPlayer(p) : Resolver.fromSender((CommandSender) audience);
    }
}
