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

import games.cultivate.mcmmocredits.user.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a transaction in which an amount is added to a user's balance and taken from another user's balance.
 *
 * @param executor The executor of the transaction.
 * @param targets  The target of the transaction.
 * @param amount   The amount of credits to add to targets.
 */
public record PayTransaction(User executor, List<User> targets, int amount) implements Transaction {

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionResult execute() {
        List<User> mapped = List.of(this.targets.get(0).addCredits(this.amount));
        return new TransactionResult(this, this.executor.takeCredits(this.amount), mapped);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> validate(final User user) {
        if (this.isSelfTransaction()) {
            return Optional.of("credits-pay-same-user");
        }
        if (this.executor.credits() - this.amount < 0 || user.credits() + this.amount < 0 || this.amount < 1) {
            return Optional.of(this.type().notEnoughCredits());
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionType type() {
        return TransactionType.PAY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSelfTransaction() {
        return this.executor.equals(this.targets.get(0));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<User, Optional<String>> validateTransaction() {
        User user = this.targets.get(0);
        return Map.of(user, this.validate(user));
    }
}
