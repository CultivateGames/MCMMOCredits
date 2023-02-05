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
import games.cultivate.mcmmocredits.user.CommandExecutor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.PreProcess;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang.WordUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Object used to build a {@link TagResolver} to parse placeholders within {@link Component}s.
 */
public final class Resolver {
    //The Tag Resolver is mutable because we want to attach other Resolvers to it.
    private TagResolver tagResolver;
    private final Map<String, PreProcess> placeholders;

    private Resolver(final Map<String, PreProcess> placeholders) {
        this.placeholders = placeholders;
        TagResolver.Builder builder = TagResolver.builder();
        for (Map.Entry<String, PreProcess> entry : this.placeholders.entrySet()) {
            builder = builder.tag(entry.getKey(), entry.getValue());
        }
        this.tagResolver = builder.build();
    }

    /**
     * Generates new {@link Builder}s.
     *
     * @return The {@link Builder}
     */
    public static Builder builder() {
        return new Builder(new HashMap<>());
    }

    public static Resolver fromRedeem(final CommandExecutor user, final PrimarySkillType skill, final int amount) {
        return user.resolver().toBuilder().skill(skill).transaction(amount).build();
    }

    public static Resolver fromRedeemSudo(final CommandExecutor sender, final CommandExecutor target, final PrimarySkillType skillType, final int amount) {
        return Resolver.builder().users(sender, target).skill(skillType).transaction(amount).build();
    }

    public Resolver.Builder toBuilder() {
        return new Builder(this.placeholders);
    }

    public TagResolver resolver() {
        return this.tagResolver;
    }

    public Resolver with(final CommandExecutor user) {
        return this.toBuilder().user(user, "target").build();
    }

    public void addResolver(final String key, final String value) {
        this.tagResolver = TagResolver.resolver(this.tagResolver, TagResolver.resolver(key, Tag.preProcessParsed(value)));
    }

    /**
     * Builder used to create {@link TagResolver} instances.
     */
    public static final class Builder {
        private final Map<String, PreProcess> placeholders;

        private Builder(final Map<String, PreProcess> placeholders) {
            this.placeholders = placeholders;
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
        public Builder tags(final Map<String, PreProcess> tags) {
            this.placeholders.putAll(tags);
            return this;
        }

        /**
         * Used to create {@link Placeholder} for users involved within an action.
         * <p>
         * Typically used for command-based transactions.
         *
         * @param sender A {@link CommandExecutor} to parse.
         * @param target A recipient {@link CommandExecutor} to parse.
         * @return The {@link Builder}
         */
        public Builder users(final CommandExecutor sender, final CommandExecutor target) {
            return this.tags(sender.placeholders("sender")).tags(target.placeholders("target"));
        }

        public Builder user(final CommandExecutor user, final String prefix) {
            return this.tags(user.placeholders(prefix));
        }

        public Builder username(final String username) {
            return this.tag("target_username", username);
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
            String formattedSkill = WordUtils.capitalizeFully(skill.name());
            return this.tag("skill", formattedSkill).tag("cap", skill.getMaxLevel() + "");
        }

        /**
         * Builds a {@link Resolver} based on information stored within this {@link Builder}
         *
         * @return A {@link Resolver} containing a {@link TagResolver} derived from the construction.
         */
        public Resolver build() {
            return new Resolver(this.placeholders);
        }
    }
}
