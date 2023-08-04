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
 * Represents a transaction in which an amount is removed from a user's balance.
 *
 * @param executor The executor of the transaction.
 * @param targets  The targets of the transaction.
 * @param amount   The amount of credits to remove from targets.
 */
public record TakeTransaction(CommandExecutor executor, User[] targets, int amount) implements Transaction {
    private static final String MESSAGE_KEY = "credits-take";
    private static final String USER_MESSAGE_KEY = "credits-take-user";

    /**
     * {@inheritDoc}
     */
    @Override
    public String userMessageKey() {
        return USER_MESSAGE_KEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String messageKey() {
        return MESSAGE_KEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionResult execute() {
        User updated = this.targets[0].takeCredits(this.amount);
        return this.isSelfTransaction() ? TransactionResult.of(this, updated) : TransactionResult.of(this, this.executor, updated);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> valid() {
        return this.targets[0].credits() - this.amount >= 0 ? Optional.empty() : Optional.of("not-enough-credits");
    }
}
