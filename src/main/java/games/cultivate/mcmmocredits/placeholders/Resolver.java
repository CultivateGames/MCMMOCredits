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
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class for resolving placeholders with key-value pairs. It is designed to be used with
 * {@link TagResolver} to process and replace placeholders in messages.
 * <p>
 * The class provides methods to add placeholders, users, skills, and transaction amounts.
 * It also offers static factory methods to create instances with pre-populated placeholders.
 * </p>
 */
public final class Resolver {
    private final Map<String, String> placeholders;

    /**
     * Constructs a new Resolver with an empty placeholder map.
     */
    public Resolver() {
        this.placeholders = new ConcurrentHashMap<>();
    }

    /**
     * Creates a new Resolver instance with the given sender and target users.
     *
     * @param sender The sender user.
     * @param target The target user.
     * @return A new Resolver instance.
     */
    public static Resolver ofUsers(final CommandExecutor sender, final CommandExecutor target) {
        Resolver resolver = new Resolver();
        resolver.addUserPair(sender, target);
        return resolver;
    }

    /**
     * Creates a new Resolver instance with the given sender user.
     *
     * @param sender The sender user.
     * @return A new Resolver instance.
     */
    public static Resolver ofUser(final CommandExecutor sender) {
        Resolver resolver = new Resolver();
        resolver.addUser(sender, "sender");
        return resolver;
    }

    /**
     * Creates a new Resolver instance for a transaction with the given sender, target users, and amount.
     *
     * @param sender The sender user.
     * @param target The target user.
     * @param amount The amount of the transaction.
     * @return A new Resolver instance.
     */
    public static Resolver ofTransaction(final CommandExecutor sender, final CommandExecutor target, final int amount) {
        Resolver resolver = Resolver.ofUsers(sender, target);
        resolver.addAmount(amount);
        return resolver;
    }

    /**
     * Creates a new Resolver instance for a redemption with the given sender, target users, skill, and amount.
     *
     * @param sender The sender user.
     * @param target The target user.
     * @param skill  The skill for the redemption.
     * @param amount The amount of the redemption.
     * @return A new Resolver instance.
     */
    public static Resolver ofRedemption(final CommandExecutor sender, final CommandExecutor target, final PrimarySkillType skill, final int amount) {
        Resolver resolver = Resolver.ofTransaction(sender, target, amount);
        resolver.addSkill(skill);
        return resolver;
    }

    /**
     * Adds a String tag to the placeholder map.
     *
     * @param key   The key of the placeholder.
     * @param value The value of the placeholder.
     */
    public void addStringTag(final String key, final String value) {
        this.placeholders.put(key, value);
    }

    /**
     * Adds an integer tag to the placeholder map.
     *
     * @param key   The key of the placeholder.
     * @param value The value of the placeholder.
     */
    public void addIntTag(final String key, final int value) {
        this.placeholders.put(key, String.valueOf(value));
    }

    /**
     * Adds a CommandExecutor user's information to the placeholder map with the specified prefix.
     *
     * @param user   The user whose information is to be added.
     * @param prefix The prefix for the user's placeholders.
     */
    public void addUser(final CommandExecutor user, final String prefix) {
        this.addStringTag(prefix, user.username());
        this.addStringTag(prefix + "_uuid", user.uuid().toString());
        this.addIntTag(prefix + "_credits", user.credits());
        this.addIntTag(prefix + "_credits", user.redeemed());
    }

    /**
     * Adds a pair of CommandExecutor users to the placeholder map with the "sender" and "target" prefixes.
     *
     * @param sender The sender user.
     * @param target The target user.
     */
    public void addUserPair(final CommandExecutor sender, final CommandExecutor target) {
        this.addUser(sender, "sender");
        this.addUser(target, "target");
    }

    /**
     * Adds a PrimarySkillType skill to the placeholder map.
     *
     * @param skill The skill to be added.
     */
    @SuppressWarnings("deprecation")
    public void addSkill(final PrimarySkillType skill) {
        this.addStringTag("skill", this.capitalize(skill.name()));
        this.addIntTag("cap", skill.getMaxLevel());
    }

    /**
     * Adds a skill using its name to the placeholder map.
     *
     * @param data The name of the skill to be added.
     */
    public void addSkill(final String data) {
        this.addSkill(PrimarySkillType.valueOf(data));
    }

    /**
     * Adds an amount to the placeholder map.
     *
     * @param amount The amount to be added.
     */
    public void addAmount(final int amount) {
        this.addIntTag("amount", amount);
    }

    /**
     * Adds a username to the placeholder map.
     *
     * @param username The username to be added.
     */
    public void addUsername(final String username) {
        this.addStringTag("target_username", username);
    }

    /**
     * Converts the current Resolver instance to a TagResolver.
     *
     * @return A new TagResolver with the current Resolver's placeholders.
     */
    public TagResolver toTagResolver() {
        TagResolver.Builder builder = TagResolver.builder();
        for (Map.Entry<String, String> entry : this.placeholders.entrySet()) {
            builder = builder.tag(entry.getKey(), Tag.preProcessParsed(entry.getValue()));
        }
        return builder.build();
    }

    /**
     * Capitalizes the first letter of the input string and sets the remaining characters to lowercase.
     *
     * @param string The input string to be capitalized.
     * @return The capitalized string.
     */
    private String capitalize(final String string) {
        if (string == null || string.isEmpty()) {
            return string;
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }
}
