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

import cloud.commandframework.exceptions.ArgumentParseException;
import cloud.commandframework.exceptions.CommandExecutionException;
import cloud.commandframework.exceptions.InvalidCommandSenderException;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.exceptions.NoPermissionException;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.util.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.PreProcess;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang.WordUtils;

import java.util.HashMap;
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
     * @return The {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder used to create {@link TagResolver} instances.
     */
    public static final class Builder {
        private final Map<String, PreProcess> placeholders;

        public Builder() {
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
         * Used to create {@link Placeholder} for users involved within an action.
         * <p>
         * Typically used for command-based transactions.
         *
         * @param sender A {@link User} to parse.
         * @param target A recipient {@link User} to parse.
         * @return The {@link Builder}
         */
        public Builder users(final User sender, final User target) {
            Map<String, String> map = new HashMap<>();
            map.putAll(sender.placeholders("sender"));
            map.putAll(target.placeholders("target"));
            return this.tags(map);
        }

        /**
         * Used to create {@link Placeholder} for users involved within an action.
         * <p>
         * Typically used for command-based transactions.
         *
         * @param sender A {@link User} to parse.
         * @return The {@link Builder}
         */
        public Builder sender(final User sender) {
            return this.tags(sender.placeholders("sender"));
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
            Map<String, String> map = new HashMap<>();
            map.put("skill", WordUtils.capitalizeFully(skill.name()));
            map.put("cap", skill.getMaxLevel() + "");
            return this.tags(map);
        }

        public Builder exception(final ArgumentParseException ex) {
            return this.tag("argument_error", ex.getCause().getMessage());
        }

        public Builder exception(final InvalidSyntaxException ex) {
            return this.tag("correct_syntax", "/" + ex.getCorrectSyntax());
        }

        public Builder exception(final InvalidCommandSenderException ex) {
            return this.tag("correct_sender", ex.getRequiredSender().getSimpleName());
        }

        public Builder exception(final NoPermissionException ex) {
            return this.tag("required_permission", ex.getMissingPermission());
        }

        public Builder exception(final CommandExecutionException ex) {
            return this.tag("command_context", String.valueOf(ex.getCommandContext()));
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
