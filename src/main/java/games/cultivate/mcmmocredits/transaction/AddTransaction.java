//
// MIT License
//
// Copyright (c) 2024 Cultivate Games
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
import java.util.Optional;

/**
 * Represents a transaction in which an amount is added to a user's balance.
 *
 * @param executor The executor of the transaction.
 * @param targets  The targets of the transaction.
 * @param amount   The amount of credits to add to targets.
 */
public record AddTransaction(CommandExecutor executor, List<User> targets, int amount) implements Transaction {

    @Override
    public TransactionResult execute() {
        List<User> mapped = this.targets.stream().map(x -> x.addCredits(this.amount)).toList();
        return new TransactionResult(this, this.executor, mapped);
    }


    @Override
    public Optional<String> validate(final User user) {
        return this.amount + user.credits() >= 0 ? Optional.empty() : Optional.of(this.type().notEnoughCredits());
    }


    @Override
    public TransactionType type() {
        return this.targets.size() > 1 ? TransactionType.ADDALL : TransactionType.ADD;
    }
}
