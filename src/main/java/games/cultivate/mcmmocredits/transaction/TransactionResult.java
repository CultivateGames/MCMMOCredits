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

import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.User;

/**
 * Represents the result of the transaction.
 *
 * @param transaction The transaction.
 * @param executor    The updated executor of the transaction.
 * @param target      The updated user for the transaction.
 */
public record TransactionResult(Transaction transaction, CommandExecutor executor, User target) {
    /**
     * @param transaction The transaction.
     * @param executor    The updated executor of the transaction.
     * @param target      The updated user for the transaction.
     * @return The result of the provided transaction.
     */
    public static TransactionResult of(final Transaction transaction, final CommandExecutor executor, final User target) {
        return new TransactionResult(transaction, executor, target);
    }

    /**
     * Sends feedback for the transaction.
     *
     * @param config       MainConfig used to extract messages.
     * @param userSilent   If process sends feedback to the user. True will silence feedback.
     * @param senderSilent If process sends feedback to the executor. True will silence feedback except for errors.
     */
    public void sendFeedback(final MainConfig config, final boolean senderSilent, final boolean userSilent) {
        Resolver resolver = Resolver.ofTransactionResult(this);
        if (!senderSilent) {
            this.executor.sendText(config.getMessage(this.transaction.getMessageKey()), resolver);
        }
        if (!userSilent && this.target.player() != null) {
            this.target.sendText(config.getMessage(this.transaction.getUserMessageKey(), resolver));
        }
    }
}
