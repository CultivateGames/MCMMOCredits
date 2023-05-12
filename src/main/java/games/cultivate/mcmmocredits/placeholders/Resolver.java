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
import games.cultivate.mcmmocredits.util.Util;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a map of key-value pairs.
 */
public final class Resolver {
    private final Map<String, String> placeholders;

    /**
     * Constructs the object.
     */
    public Resolver() {
        this.placeholders = new ConcurrentHashMap<>();
    }

    /**
     * Creates a Resolver with placeholders for the provided users.
     *
     * @param sender A user.
     * @param target Another user.
     * @return The Resolver.
     */
    public static Resolver ofUsers(final CommandExecutor sender, final CommandExecutor target) {
        Resolver resolver = new Resolver();
        resolver.addUser(sender, "sender");
        resolver.addUser(target, "target");
        return resolver;
    }

    /**
     * Creates a Resolver with the provided user.
     *
     * @param sender A User.
     * @return The Resolver.
     */
    public static Resolver ofUser(final CommandExecutor sender) {
        Resolver resolver = new Resolver();
        resolver.addUser(sender, "sender");
        return resolver;
    }

    /**
     * Creates a Resolver with the provided transaction properties.
     *
     * @param sender A user.
     * @param target Another user.
     * @param amount Transaction amount.
     * @return The Resolver.
     */
    public static Resolver ofTransaction(final CommandExecutor sender, final CommandExecutor target, final int amount) {
        Resolver resolver = Resolver.ofUsers(sender, target);
        resolver.addAmount(amount);
        return resolver;
    }

    /**
     * Creates a Resolver with the provided redemption properties.
     *
     * @param sender A user.
     * @param target Another user.
     * @param skill  The affected skill.
     * @param amount Transaction amount.
     * @return The Resolver.
     */
    public static Resolver ofRedemption(final CommandExecutor sender, final CommandExecutor target, final PrimarySkillType skill, final int amount) {
        Resolver resolver = Resolver.ofTransaction(sender, target, amount);
        resolver.addSkill(skill);
        return resolver;
    }

    /**
     * Adds a key-value pair with a String-based value to the Resolver.
     *
     * @param key   The key.
     * @param value The value.
     */
    public void addStringTag(final String key, final String value) {
        this.placeholders.put(key, value);
    }

    /**
     * Adds a key-value pair with a integer-based value to the Resolver.
     *
     * @param key   The key.
     * @param value The value.
     */
    public void addIntTag(final String key, final int value) {
        this.placeholders.put(key, String.valueOf(value));
    }

    /**
     * Adds user info to the Resolver with a specified prefix.
     *
     * @param user   A user.
     * @param prefix Prefix to apply to the placeholder keys.
     */
    public void addUser(final CommandExecutor user, final String prefix) {
        this.addStringTag(prefix, user.username());
        this.addStringTag(prefix + "_uuid", user.uuid().toString());
        this.addIntTag(prefix + "_credits", user.credits());
        this.addIntTag(prefix + "_redeemed", user.redeemed());
    }

    /**
     * Adds a PrimarySkillType to the Resolver.
     *
     * @param skill The skill.
     */
    @SuppressWarnings("deprecation")
    public void addSkill(final PrimarySkillType skill) {
        this.addStringTag("skill", Util.capitalizeWord(skill.name()));
        this.addIntTag("cap", skill.getMaxLevel());
    }

    /**
     * Adds transaction amount to the Resolver.
     *
     * @param amount The amount.
     */
    public void addAmount(final int amount) {
        this.addIntTag("amount", amount);
    }

    /**
     * Adds a username to the Resolver.
     *
     * @param username The username.
     */
    public void addUsername(final String username) {
        this.addStringTag("target", username);
    }

    /**
     * Converts the Resolver to a TagResolver.
     *
     * @return The built TagResolver.
     */
    public TagResolver toTagResolver() {
        TagResolver.Builder builder = TagResolver.builder();
        for (Map.Entry<String, String> entry : this.placeholders.entrySet()) {
            builder = builder.tag(entry.getKey(), Tag.preProcessParsed(entry.getValue()));
        }
        return builder.build();
    }
}
