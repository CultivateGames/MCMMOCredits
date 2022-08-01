package games.cultivate.mcmmocredits.placeholders;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class Resolver {
    private Resolver() {
    }

    public static TagResolver fromSender(final CommandSender sender) {
        return Resolver.builder().sender(sender).build();
    }

    public static TagResolver fromPlayer(final Player player) {
        return Resolver.builder().player(player).build();
    }

    public static TagResolver fromTransaction(final CommandSender sender, final String username, final int amount) {
        return Resolver.builder().sender(sender).transaction(username, amount).build();
    }

    public static TagResolver fromRedemption(final CommandSender sender, final Player player, final PrimarySkillType skill, final int amount) {
        return Resolver.builder().sender(sender).skill(skill).transaction(player.getName(), amount).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<String, Tag> placeholders;

        public Builder() {
            this.placeholders = new HashMap<>();
        }

        public Builder tag(final String key, final String value) {
            this.placeholders.putIfAbsent(key, Tag.preProcessParsed(value));
            return this;
        }

        public Builder sender(final CommandSender sender) {
            return this.tag("sender", sender.getName());
        }

        public Builder player(final String username) {
            return this.tag("player", username);
        }

        public Builder player(final Player player) {
            return this.player(player.getName());
        }

        public Builder transaction(final String username, final int amount) {
            return this.tag("amount", amount + "").player(username);
        }

        @SuppressWarnings("deprecation")
        public Builder skill(final PrimarySkillType skill) {
            return this.tag("skill", WordUtils.capitalizeFully(skill.name())).tag("cap", skill.getMaxLevel() + "");
        }

        public TagResolver build() {
            TagResolver.Builder builder = TagResolver.builder();
            for (Map.Entry<String, Tag> entry : this.placeholders.entrySet()) {
                builder = builder.tag(entry.getKey(), entry.getValue());
            }
            return builder.build();
        }
    }
}
