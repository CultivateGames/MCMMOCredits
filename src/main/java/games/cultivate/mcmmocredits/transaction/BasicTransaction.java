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

import java.util.Optional;

/**
 * Represents a basic transaction (add/set/take).
 *
 * @param executor The executor of the transaction.
 * @param target   The user to apply the transaction to.
 * @param type     The type of the transaction.
 * @param amount   The amount to use.
 */
public record BasicTransaction(CommandExecutor executor, User target, BasicTransactionType type, int amount) implements Transaction {
    /**
     * Creates a solo transaction using the provided information.
     *
     * @param target The user to apply the transaction to.
     * @param type   The type of the transaction.
     * @param amount The amount to use.
     * @return The transaction.
     */
    public static BasicTransaction of(final User target, final BasicTransactionType type, final int amount) {
        return new BasicTransaction(target, target, type, amount);
    }

    /**
     * Creates a transaction using the provided information.
     *
     * @param executor The executor of the transaction.
     * @param target   The user to apply the transaction to.
     * @param type     The type of the transaction.
     * @param amount   The amount to use.
     * @return The transaction.
     */
    public static BasicTransaction of(final CommandExecutor executor, final User target, final BasicTransactionType type, final int amount) {
        return new BasicTransaction(executor, target, type, amount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionResult execute() {
        int result = this.type.apply(this.target.credits(), this.amount);
        return TransactionResult.of(this, this.executor, this.target.withCredits(result));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<FailureReason> executable() {
        try {
            int result = this.type.apply(this.target.credits(), this.amount);
            return result < 0 ? Optional.of(FailureReason.NOT_ENOUGH_CREDITS) : Optional.empty();
        } catch (ArithmeticException e) {
            return Optional.of(FailureReason.NOT_ENOUGH_CREDITS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSelfTransaction() {
        return this.executor.equals(this.target);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageKey() {
        return this.type.getMessageKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserMessageKey() {
        return this.type.getUserMessageKey();
    }
}
