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
package games.cultivate.mcmmocredits.transaction;

import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents an interaction in which a user's credit balance is modified.
 */
public interface Transaction {
    /**
     * Returns a new instance of the Transaction builder.
     *
     * @return new instance of the Transaction builder.
     */
    static TransactionBuilder builder(final CommandExecutor executor, final TransactionType type, final int amount) {
        return new TransactionBuilder(executor, type, amount);
    }

    /**
     * Returns a new instance of the Transaction builder.
     *
     * @return new instance of the Transaction builder.
     */
    static Transaction of(final CommandExecutor executor, final TransactionType type, final int amount) {
        return new TransactionBuilder(executor, type, amount).build();
    }

    /**
     * Executes the transaction.
     *
     * @return An updated User from execution of the transaction.
     */
    TransactionResult execute();

    /**
     * Gets if the transaction is valid. The optional is empty if the transaction can be executed.
     *
     * @return An optional indicating execution status of the transaction.
     */
    Optional<String> validate(final User user);

    /**
     * Gets the executor of the transaction.
     *
     * @return The CommandExecutor.
     */
    CommandExecutor executor();

    /**
     * Gets the targets of the transaction.
     *
     * @return The targets.
     */
    List<User> targets();

    /**
     * Gets the amount of credits applied in transaction.
     *
     * @return The amount
     */
    int amount();

    /**
     * Gets the type of the transaction.
     *
     * @return Type of the transaction.
     */
    TransactionType type();

    /**
     * Returns if the executor and target are the same entity.
     *
     * @return True if the executor and target are the same entity, otherwise false.
     */
    default boolean isSelfTransaction() {
        if (!this.executor().isPlayer()) {
            return false;
        }
        return this.targets().size() == 1 && this.targets().contains((User) this.executor());
    }

    /**
     * Returns a map of users and failure messages, if they fail validation.
     *
     * @return Map of users and failure messages, if they fail validation.
     */
    default Map<User, Optional<String>> validateTransaction() {
        return this.targets().stream().collect(Collectors.toMap(Function.identity(), this::validate));
    }

    /**
     * Gets the config key for feedback sent to the user during this transaction.
     *
     * @return The config key.
     */
    default String userMessageKey() {
        return this.type().userMessageKey();
    }

    /**
     * Gets the config key for feedback sent to the executor during this transaction.
     *
     * @return The config key.
     */
    default String messageKey() {
        return this.type().messageKey(this);
    }
}
