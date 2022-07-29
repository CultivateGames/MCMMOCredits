package games.cultivate.mcmmocredits.placeholders;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Resolver {
    public static TagResolver fromSender(CommandSender sender) {
        return Resolver.builder().sender(sender).build();
    }

    public static TagResolver fromPlayer(Player player) {
        return Resolver.builder().player(player).build();
    }

    public static TagResolver fromTransaction(CommandSender sender, String username, int amount) {
        return Resolver.builder().sender(sender).player(username).transaction(amount).build();
    }

    public static TagResolver fromRedemption(CommandSender sender, Player player, PrimarySkillType skill, int amount) {
        return Resolver.builder().sender(sender).player(player).skill(skill).transaction(amount).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<String, Tag> placeholders;

        public Builder() {
            this.placeholders = new HashMap<>();
        }

        public Builder tag(String key, String value) {
            this.placeholders.putIfAbsent(key, Tag.preProcessParsed(value));
            return this;
        }

        public Builder sender(CommandSender sender) {
            return this.tag("sender", sender.getName());
        }

        public Builder player(String username) {
            return this.tag("player", username);
        }

        public Builder player(Player player) {
            return this.player(player.getName());
        }

        public Builder transaction(int amount) {
            return this.tag("amount", amount + "");
        }

        @SuppressWarnings("deprecation")
        public Builder skill(PrimarySkillType skill) {
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
