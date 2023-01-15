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
package games.cultivate.mcmmocredits.placeholders;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.data.UserDAO;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.PreProcess;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
     * @param dao An injected {@link UserDAO} instance used to obtain player data for parsing.
     * @return The {@link Builder}
     */
    public static Builder builder(final UserDAO dao) {
        return new Builder(dao);
    }

    /**
     * Builder used to create {@link TagResolver} instances.
     */
    public static final class Builder {
        private final UserDAO dao;
        private final Map<String, PreProcess> placeholders;

        public Builder(final UserDAO dao) {
            this.dao = dao;
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
        //TODO: fix
        public Builder users(final CommandSender sender, final String target) {
            Map<String, String> tags = new ConcurrentHashMap<>();
            this.dao.getUser(target).ifPresentOrElse(user -> {
                tags.put("target", user.username());
                tags.put("target_credits", user.credits() + "");
            }, () -> tags.put("target", target));
            if (sender instanceof Player player) {
                this.dao.getUser(player.getName()).ifPresent(user -> {
                    tags.put("sender", user.username());
                    tags.put("sender_credits", user.credits() + "");
                });
                return this.tags(tags);
            }
            tags.put("sender", sender.getName());
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
