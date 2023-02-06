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
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.PreProcess;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang.WordUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * A resolver for internal and external placeholders.
 *
 * @see PlaceholderAPI
 * @see MiniMessage
 */
public final class Resolver {
    private final Map<String, PreProcess> placeholders;
    //The Tag Resolver is mutable because we want to attach other Resolvers to it.
    private TagResolver tagResolver;

    /**
     * Constructs the object. Stores an underlying {@link TagResolver} and the placeholder map.
     *
     * @param placeholders placeholder map of String keys and {@link PreProcess} values.
     */
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

    /**
     * Utility method to build a Resolver used in a Credit Redemption.
     *
     * @param sender Command executor. Can be from Console.
     * @param target Target of the credit redemption.
     * @param skill  Skill used in the credit redemption.
     * @param amount Amount of credits used in the transaction.
     * @return A new Resolver.
     */
    public static Resolver fromRedemption(final CommandExecutor sender, final CommandExecutor target, final PrimarySkillType skill, final int amount) {
        return Resolver.builder().users(sender, target).skill(skill).transaction(amount).build();
    }

    /**
     * Constructs a new Resolver Builder while preserving properties.
     *
     * @return A new Resolver Builder.
     */
    public Resolver.Builder toBuilder() {
        return new Builder(this.placeholders);
    }

    /**
     * Gets the underlying TagResolver.
     *
     * @return The TagResolver.
     */
    public TagResolver resolver() {
        return this.tagResolver;
    }

    /**
     * Adds another CommandExecutor to the Resolver as the target.
     *
     * @param user The target.
     * @return An updated Resolver.
     */
    public Resolver with(final CommandExecutor user) {
        return this.toBuilder().user(user, "target").build();
    }

    /**
     * Adds a new {@link TagResolver} to the underlying TagResolver without building a new Resolver.
     *
     * @param key   Key of the placeholder. Example: "target_username"
     * @param value Value of the placeholder. Example: Notch
     */
    public void addResolver(final String key, final String value) {
        this.toBuilder().tag(key, value).build();
        this.tagResolver = TagResolver.resolver(this.tagResolver, TagResolver.resolver(key, Tag.preProcessParsed(value)));
    }

    /**
     * Builder for Resolver objects.
     */
    public static final class Builder {
        private final Map<String, PreProcess> placeholders;

        /**
         * Constructs the object using the provided placeholder map.
         *
         * @param placeholders The placeholder map.
         * @see Resolver#Resolver(Map)
         */
        private Builder(final Map<String, PreProcess> placeholders) {
            this.placeholders = placeholders;
        }

        /**
         * Adds a placeholder to the Builder.
         *
         * @param key   Key of the placeholder.
         * @param value Value of the placeholder.
         * @return The updated Builder.
         */
        public Builder tag(final String key, final String value) {
            this.placeholders.put(key, Tag.preProcessParsed(value));
            return this;
        }

        /**
         * Adds multiple placeholders to the Builder.
         *
         * @param tags The placeholder map.
         * @return The updated Builder.
         */
        public Builder tags(final Map<String, PreProcess> tags) {
            this.placeholders.putAll(tags);
            return this;
        }

        /**
         * Adds a user to the placeholder map.
         *
         * @param user   The user to add.
         * @param prefix The prefix of the user's placeholders.
         * @return The updated Builder.
         * @see CommandExecutor#placeholders(String)
         */
        public Builder user(final CommandExecutor user, final String prefix) {
            return this.tags(user.placeholders(prefix));
        }

        /**
         * Adds a "sender" and "target" user to the placeholder map.
         *
         * @param sender Command executor. May be Console.
         * @param target The target user.
         * @return The updated Builder.
         */
        public Builder users(final CommandExecutor sender, final CommandExecutor target) {
            return this.tags(sender.placeholders("sender")).tags(target.placeholders("target"));
        }

        /**
         * Adds just a username to the placeholder map. Used when a User doesn't exist.
         *
         * @param username The username.
         * @return The updated Builder.
         */
        public Builder username(final String username) {
            return this.tag("target_username", username);
        }

        /**
         * Adds the amount of credits used in a transaction to the placeholder map.
         *
         * @param amount Amount of credits.
         * @return The updated Builder.
         */
        public Builder transaction(final int amount) {
            return this.tag("amount", amount + "");
        }

        /**
         * Adds a MCMMO Skill used in a credit redemption to the placeholder map.
         *
         * @param skill The MCMMO Skill.
         * @return The updated Builder.
         */
        @SuppressWarnings("deprecation")
        public Builder skill(final PrimarySkillType skill) {
            String formattedSkill = WordUtils.capitalizeFully(skill.name());
            return this.tag("skill", formattedSkill).tag("cap", skill.getMaxLevel() + "");
        }

        /**
         * Builds the Resolver with the current placeholder map.
         *
         * @return A new Resolver.
         */
        public Resolver build() {
            return new Resolver(this.placeholders);
        }
    }
}
