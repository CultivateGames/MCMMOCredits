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
import com.gmail.nossr50.mcMMO;
import games.cultivate.mcmmocredits.transaction.RedeemTransaction;
import games.cultivate.mcmmocredits.transaction.Transaction;
import games.cultivate.mcmmocredits.transaction.TransactionResult;
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
     * @param transaction The transaction to parse.
     * @return The Resolver.
     */
    public static Resolver ofTransaction(final Transaction transaction) {
        Resolver resolver = Resolver.ofUsers(transaction.executor(), transaction.target());
        resolver.addAmount(transaction.amount());
        if (transaction instanceof RedeemTransaction tr) {
            resolver.addSkill(tr.skill());
        }
        return resolver;
    }
    /**
     * Creates a Resolver with the provided transaction result.
     *
     * @param result The result of a transaction to parse.
     * @return The Resolver.
     */
    public static Resolver ofTransactionResult(final TransactionResult result) {
        Resolver resolver = Resolver.ofUsers(result.executor(), result.target());
        Transaction transaction = result.transaction();
        resolver.addAmount(transaction.amount());
        if (transaction instanceof RedeemTransaction tr) {
            resolver.addSkill(tr.skill());
        }
        return resolver;
    }

    public <T> void addTag(final String key, final T value) {
        this.placeholders.put(key, value.toString());
    }

    /**
     * Adds user info to the Resolver with a specified prefix.
     *
     * @param user   A user.
     * @param prefix Prefix to apply to the placeholder keys.
     */
    public void addUser(final CommandExecutor user, final String prefix) {
        this.addTag(prefix, user.username());
        this.addTag(prefix + "_uuid", user.uuid().toString());
        this.addTag(prefix + "_credits", user.credits());
        this.addTag(prefix + "_redeemed", user.redeemed());
    }

    /**
     * Adds a PrimarySkillType to the Resolver.
     *
     * @param skill The skill.
     */
    public void addSkill(final PrimarySkillType skill) {
        this.addTag("skill", Util.capitalizeWord(skill.name()));
        this.addTag("cap", mcMMO.p.getGeneralConfig().getLevelCap(skill));
    }

    /**
     * Adds transaction amount to the Resolver.
     *
     * @param amount The amount.
     */
    public void addAmount(final int amount) {
        this.addTag("amount", amount);
    }

    /**
     * Adds a username to the Resolver.
     *
     * @param username The username.
     */
    public void addUsername(final String username) {
        this.addTag("target", username);
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
