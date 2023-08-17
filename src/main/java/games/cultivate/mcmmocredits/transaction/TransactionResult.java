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

import java.util.HashSet;
import java.util.List;

/**
 * Represents the result of the transaction.
 *
 * @param transaction The transaction.
 * @param executor    The updated executor of the transaction.
 * @param targets     The updated users for the transaction.
 */
public record TransactionResult(Transaction transaction, CommandExecutor executor, List<User> targets) {
    /**
     * Constructs a TransactionResult.
     *
     * @param transaction The transaction.
     * @param executor    The updated executor of the transaction.
     * @param targets     The updated users for the transaction.
     * @return The result of the provided transaction.
     */
    public static TransactionResult of(final Transaction transaction, final CommandExecutor executor, final List<User> targets) {
        return new TransactionResult(transaction, executor, targets);
    }

    /**
     * Returns if the executor was updated by the transaction.
     *
     * @return if the executor was updated by the transaction.
     */
    public boolean updatedExecutor() {
        return this.transaction.executor() != this.executor;
    }

    /**
     * Returns if the targets are updated by the transaction.
     *
     * @return if the targets are updated by the transaction.
     */
    public boolean updatedTargets() {
        return !new HashSet<>(this.transaction.targets()).containsAll(this.targets);
    }
}
