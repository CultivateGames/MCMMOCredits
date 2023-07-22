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

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.User;

import java.util.List;
import java.util.Optional;

/**
 * Represents an interaction in which a user's credit balance is modified.
 */
public interface Transaction {
    /**
     * Returns a new instance of the Transaction builder.
     *
     * @return new instance of the Transaction builder.
     */
    static Transaction.Builder builder() {
        return new Transaction.Builder();
    }

    /**
     * Gets the config key for feedback sent to the user during this transaction.
     *
     * @return The config key.
     */
    String userMessageKey();

    /**
     * Gets the config key for feedback sent to the executor during this transaction.
     *
     * @return The config key.
     */
    String messageKey();

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
    Optional<String> valid();

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
    User[] targets();

    /**
     * Gets the amount of credits applied in transaction.
     *
     * @return The amount
     */
    int amount();

    /**
     * Returns if the executor and target are the same entity.
     *
     * @return True if the executor and target are the same entity, otherwise false.
     */
    default boolean isSelfTransaction() {
        return this.executor().equals(this.targets()[0]);
    }

    /**
     * Builder class for Transactions.
     */
    class Builder {
        private int amount;
        private CommandExecutor executor;
        private User[] targets;
        private PrimarySkillType skill;
        private TransactionType type;

        /**
         * Sets PrimarySkillType of the transaction, and also sets the transaction type to REDEEM.
         *
         * @param skill The skill.
         * @return The builder.
         */
        public Builder skill(final PrimarySkillType skill) {
            this.skill = skill;
            this.type = TransactionType.REDEEM;
            return this;
        }

        /**
         * Sets the type of the transaction.
         *
         * @param type The type.
         * @return The builder.
         */
        public Builder type(final TransactionType type) {
            this.type = type;
            return this;
        }

        /**
         * Sets the amount of the transaction.
         *
         * @param amount The amount.
         * @return The builder.
         */
        public Builder amount(final int amount) {
            this.amount = amount;
            return this;
        }

        /**
         * Sets the executor and target to the provided user.
         *
         * @param executor The user.
         * @return The builder.
         */
        public Builder self(final User executor) {
            this.executor = executor;
            this.targets = new User[]{executor};
            return this;
        }

        /**
         * Sets the users of the transaction.
         *
         * @param executor The executor.
         * @param targets  The targets.
         * @return The builder.
         */
        public Builder users(final CommandExecutor executor, final User... targets) {
            this.executor = executor;
            this.targets = targets;
            return this;
        }

        /**
         * Sets the users of the transaction.
         *
         * @param executor The executor.
         * @param targets  The targets.
         * @return The builder.
         */
        public Builder users(final CommandExecutor executor, final List<User> targets) {
            this.executor = executor;
            this.targets = targets.toArray(new User[0]);
            return this;
        }

        /**
         * Builds the transaction.
         *
         * @return The transaction.
         */
        public Transaction build() {
            return switch (this.type) {
                case ADD -> new AddTransaction(this.executor, this.targets, this.amount);
                case SET -> new SetTransaction(this.executor, this.targets, this.amount);
                case TAKE -> new TakeTransaction(this.executor, this.targets, this.amount);
                case PAY -> new PayTransaction(this.executor, this.targets, this.amount);
                case REDEEM -> new RedeemTransaction(this.executor, this.targets, this.skill, this.amount);
            };
        }
    }
}
