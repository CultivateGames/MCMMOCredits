package games.cultivate.mcmmocredits.placeholders;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.data.Database;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.PreProcess;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.common.returnsreceiver.qual.This;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Object used to build a {@link TagResolver} to parse placeholders within {@link Component}s.
 */
public final class Resolver {
    private Resolver() {
    }

    /**
     * Generates new {@link Builder}s.
     *
     * @param database An injected {@link Database} instance used to obtain player data for parsing.
     * @return The {@link Builder}
     */
    public static Builder builder(final Database database) {
        return new Builder(database);
    }

    /**
     * Builder used to create {@link TagResolver} instances.
     */
    public static final class Builder {
        private final Database database;
        private final Map<String, PreProcess> placeholders;

        public Builder(final Database database) {
            this.database = database;
            this.placeholders = new ConcurrentHashMap<>();
        }

        /**
         * Used to add a tag to the resulting {@link TagResolver}.
         *
         * @param key   name of the {@link Placeholder}. If the tag is {@code <sender>}, then the name is "sender".
         * @param value The value used to parse the {@link Placeholder}.
         * @return the {@link Builder}
         */
        public Builder tag(final String key, final String value) {
            this.placeholders.put(key, Tag.preProcessParsed(value));
            return this;
        }

        /**
         * Used to add a Map of String pairs to the resulting {@link TagResolver}.
         *
         * @param tags map containing strings used to create {@link Tag}.
         * @return the {@link Builder}
         * @see #tag(String, String)
         */
        public Builder tags(final Map<String, String> tags) {
            tags.forEach((k, v) -> this.placeholders.put(k, Tag.preProcessParsed(v)));
            return this;
        }

        /**
         * Used to create {@link Placeholder}s for users involved within an action. Typically used for command-based transactions.
         *
         * @param sender A {@link CommandSender} to parse for.
         * @param target Username of an action target to parse for.
         * @return The {@link Builder}
         */
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

        /**
         * Used to parse for a singular user when there is no second party to parse for. This to ensure no ambiguity between sender and target tags.
         *
         * @param sender A {@link CommandSender} to generate tags for.
         * @return The {@link Builder}
         */
        public Builder users(final CommandSender sender) {
            return this.users(sender, sender.getName());
        }

        /**
         * Used to parse for transaction specific information.
         *
         * @param amount Amount of credits used for a transaction.
         * @return The {@link Builder}
         */
        public Builder transaction(final int amount) {
            return this.tag("amount", amount + "");
        }

        /**
         * Used to parse information about a {@link PrimarySkillType} related to a transaction.
         *
         * @param skill The {@link PrimarySkillType}
         * @return The {@link Builder}
         */
        @This
        @SuppressWarnings("deprecation")
        public Builder skill(final PrimarySkillType skill) {
            return this.tags(Map.of("skill", WordUtils.capitalizeFully(skill.name()), "cap", skill.getMaxLevel() + ""));
        }

        /**
         * Builds a {@link TagResolver} based on information stored within this {@link Builder}
         *
         * @return A {@link TagResolver} containing {@link Tag}s derived from the internal placeholder map.
         */
        public TagResolver build() {
            TagResolver.Builder builder = TagResolver.builder();
            for (Map.Entry<String, PreProcess> entry : this.placeholders.entrySet()) {
                builder = builder.tag(entry.getKey(), entry.getValue());
            }
            return builder.build();
        }
    }
}
