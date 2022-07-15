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
import org.jetbrains.annotations.Nullable;

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
        if (this.resolver != null) {
            return MiniMessage.miniMessage().deserialize(this.content, this.resolver);
        }
        return MiniMessage.miniMessage().deserializeOr(this.content, Component.empty());
    }

    public static Builder builder() {
        return new Builder();
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
        return new Builder().audience(audience).content(content).resolver(null).build();
    }

    public void send() {
        audience.sendMessage(this.toComponent());
    }

    @SuppressWarnings("unused")
    public static Component parseComponent(Component comp, Player player) {
        Pattern p = PlaceholderAPI.getPlaceholderPattern();
        comp = comp.replaceText(i -> i.match(p).replacement((m, b) -> b.content(PlaceholderAPI.setPlaceholders(player, m.group()))));
        return Component.empty().style(Text.DEFAULT_STYLE).append(MiniMessage.miniMessage().deserialize(MiniMessage.miniMessage().serialize(comp), Resolver.fromPlayer(player)));
    }


    @SuppressWarnings("unused")
    public Builder toBuilder() {
        return new Builder(this.audience, this.content, this.resolver);
    }

    public static class Builder {
        private final Audience audience;
        private final String content;
        private TagResolver resolver;

        private Builder(Audience audience, String content, @Nullable TagResolver resolver) {
            this.audience = audience;
            this.content = content;
            this.resolver = resolver;
        }

        public Builder() {
            this(Audience.empty(), null, null);
        }

        public Builder audience(Audience audience) {
            return new Builder(audience, this.content, this.resolver);
        }

        public Builder content(String content) {
            return new Builder(this.audience, content, this.resolver);
        }

        public Builder resolver(TagResolver resolver) {
            return new Builder(this.audience, this.content, resolver);
        }

        private static TagResolver createResolver(Audience audience) {
            return audience instanceof Player p ? Resolver.fromPlayer(p) : Resolver.fromSender((CommandSender) audience);
        }

        public Text build() {
            if (this.resolver == null) {
                this.resolver = createResolver(this.audience);
            }
            return new Text(this.audience, this.content, this.resolver);
        }
    }
}
