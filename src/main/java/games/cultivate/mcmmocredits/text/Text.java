package games.cultivate.mcmmocredits.text;

import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.keys.StringKey;
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

public class Text {
    public static final Style DEFAULT_STYLE = Style.style().decoration(TextDecoration.ITALIC, false).build();
    private final Audience audience;
    private final StringKey key;
    private final TagResolver resolver;
    private final boolean withPrefix;
    private String content;

    private Text(Audience audience, StringKey key, TagResolver resolver, boolean withPrefix) {
        this.audience = audience;
        this.key = key;
        this.resolver = resolver;
        this.withPrefix = withPrefix;
        if (this.key != null) {
            this.content = this.key.get(this.withPrefix);
        }
    }

    private Text(Audience audience, String content, TagResolver resolver, boolean withPrefix) {
        this.audience = audience;
        this.key = null;
        this.resolver = resolver;
        this.withPrefix = withPrefix;
        this.content = content;
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
     * Creates a Text object from a StringKey when resolver logic is still required.
     *
     * @param audience recipient of the message.
     * @param key StringKey to derive the message from.
     * @param resolver TagResolver to use for parsing.
     * @return a populated Text object.
     */
    public static Text fromKey(Audience audience, StringKey key, TagResolver resolver) {
        return new Text(audience, key, resolver, true);
    }

    /**
     * Creates a Text object from a StringKey where the resolver is inferred from the provided audience.
     *
     * @param audience recipient of the message.
     * @param key StringKey to derive the message from.
     * @return a populated Text object.
     */
    public static Text fromKey(Audience audience, StringKey key) {
        return fromKey(audience, key, createResolver(audience));
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
        return new Text(audience, content, resolver, true);
    }

    public static Text fromPath(Audience audience, Config<?> config, String pathPart, TagResolver resolver) {
        return new Text(audience, config.string(pathPart, "") ,resolver,true);
    }

    public static Text fromComponent(Audience audience, Component component) {
        return Text.fromString(audience, MiniMessage.miniMessage().serialize(component), createResolver(audience));
    }

    public void send() {
        audience.sendMessage(this.toComponent());
    }

    @Override
    public String toString() {
        return withPrefix ? StringKey.PREFIX.get() + this.key.get() : this.key.get();
    }

    private static TagResolver createResolver(Audience audience) {
       return audience instanceof Player p ? Resolver.fromPlayer(p) : Resolver.fromSender((CommandSender) audience);
    }

    public Builder toBuilder() {
        return new Builder(this.audience, this.key, this.resolver, this.withPrefix);
    }

    public static class Builder {
        private final Audience audience;
        private final StringKey key;
        private final TagResolver resolver;
        private final boolean withPrefix;

        private Builder(Audience audience, StringKey key, TagResolver resolver, boolean withPrefix) {
            this.audience = audience;
            this.key = key;
            this.resolver = resolver;
            this.withPrefix = withPrefix;
        }

        public Builder() {
            this(Audience.empty(), null, null, true);
        }

        public Builder audience(Audience audience) {
            return new Builder(audience, this.key, this.resolver, this.withPrefix);
        }

        public Builder key(StringKey key) {
            return new Builder(this.audience, key, this.resolver, this.withPrefix);
        }

        public Builder resolver(TagResolver resolver) {
            return new Builder(this.audience, this.key, resolver, this.withPrefix);
        }

        public Builder withPrefix(boolean withPrefix) {
            return new Builder(this.audience, this.key, this.resolver, withPrefix);
        }

        public Text build() {
            return new Text(this.audience, this.key, this.resolver, this.withPrefix);
        }
    }
}
