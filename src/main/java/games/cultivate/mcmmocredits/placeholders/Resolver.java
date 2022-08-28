package games.cultivate.mcmmocredits.placeholders;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.data.Database;
import net.kyori.adventure.text.minimessage.tag.PreProcess;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Resolver {
    private Resolver() {
    }

    public static Builder builder(Database database) {
        return new Builder(database);
    }

    public static final class Builder {
        private final Database database;
        private final Map<String, PreProcess> placeholders;

        public Builder(final Database database) {
            this.database = database;
            this.placeholders = new ConcurrentHashMap<>();
        }

        public Builder tag(final String key, final String value) {
            this.placeholders.put(key, Tag.preProcessParsed(value));
            return this;
        }

        public Builder tags(final Map<String, String> tags) {
            tags.forEach((k, v) -> this.placeholders.put(k, Tag.preProcessParsed(v)));
            return this;
        }

        public Builder users(final CommandSender sender, final String target) {
            Map<String, String> tags = new ConcurrentHashMap<>();
            tags.put("sender", sender.getName());
            tags.put("target", target);
            if (!target.equalsIgnoreCase("CONSOLE")) {
                tags.put("target_credits", this.database.getCredits(this.database.getUUID(target).join()) + "");
            }
            String senderCredits = sender instanceof Player p ? this.database.getCredits(this.database.getUUID(p.getName()).join()) + "" : "0";
            tags.put("sender_credits", senderCredits);
            return this.tags(tags);
        }

        public Builder users(final CommandSender sender) {
            return this.users(sender, sender.getName());
        }

        public Builder transaction(final int amount) {
            return this.tag("amount", amount + "");
        }

        @SuppressWarnings("deprecation")
        public Builder skill(final PrimarySkillType skill) {
            return this.tags(Map.of("skill", WordUtils.capitalizeFully(skill.name()), "cap", skill.getMaxLevel() + ""));
        }

        public TagResolver build() {
            TagResolver.Builder builder = TagResolver.builder();
            for (Map.Entry<String, PreProcess> entry : this.placeholders.entrySet()) {
                builder = builder.tag(entry.getKey(), entry.getValue());
            }
            return builder.build();
        }
    }
}
