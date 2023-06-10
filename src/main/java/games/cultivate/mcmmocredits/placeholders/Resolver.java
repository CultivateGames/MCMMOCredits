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
import org.jetbrains.annotations.Nullable;

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
        return new Resolver().addUser(sender, "sender").addUser(target, "target");
    }

    /**
     * Creates a Resolver with the provided user.
     *
     * @param sender A User.
     * @return The Resolver.
     */
    public static Resolver ofUser(final CommandExecutor sender) {
        return new Resolver().addUser(sender, "sender");
    }

    /**
     * Creates a Resolver with the provided transaction properties.
     *
     * @param transaction The transaction to parse.
     * @return The Resolver.
     */
    public static Resolver ofTransaction(final Transaction transaction) {
        PrimarySkillType skill = transaction instanceof RedeemTransaction tr ? tr.skill() : null;
        return Resolver.ofUsers(transaction.executor(), transaction.target()).addAmount(transaction.amount()).addSkill(skill);
    }

    /**
     * Creates a Resolver with the provided transaction result.
     *
     * @param result The result of a transaction to parse.
     * @return The Resolver.
     */
    public static Resolver ofTransactionResult(final TransactionResult result) {
        Transaction transaction = result.transaction();
        PrimarySkillType skill = transaction instanceof RedeemTransaction tr ? tr.skill() : null;
        return Resolver.ofUsers(result.executor(), result.target()).addAmount(transaction.amount()).addSkill(skill);
    }

    /**
     * Adds a tag to the Resolver.
     *
     * @param key   Placeholder key of the tag.
     * @param value Value of the tag.
     * @param <T>   Type of the value.
     */
    public <T> Resolver addTag(final String key, final T value) {
        this.placeholders.put(key, value.toString());
        return this;
    }

    /**
     * Adds user info to the Resolver with a specified prefix.
     *
     * @param user   A user.
     * @param prefix Prefix to apply to the placeholder keys.
     */
    public Resolver addUser(final CommandExecutor user, final String prefix) {
        return this.addTag(prefix, user.username())
                .addTag(prefix + "_uuid", user.uuid())
                .addTag(prefix + "_credits", user.credits())
                .addTag(prefix + "_redeemed", user.redeemed());
    }

    /**
     * Adds a PrimarySkillType to the Resolver.
     * If null is passed, the unmodified resolver is returned.
     *
     * @param skill The skill.
     */
    public Resolver addSkill(@Nullable final PrimarySkillType skill) {
        return skill == null ? this : this.addTag("skill", Util.capitalizeWord(skill.name())).addTag("cap", mcMMO.p.getGeneralConfig().getLevelCap(skill));
    }

    /**
     * Adds transaction amount to the Resolver.
     *
     * @param amount The amount.
     */
    public Resolver addAmount(final int amount) {
        return this.addTag("amount", amount);
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
