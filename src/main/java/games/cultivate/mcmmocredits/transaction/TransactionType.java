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

import cloud.commandframework.context.CommandContext;
import games.cultivate.mcmmocredits.user.CommandExecutor;

import java.util.function.Function;

/**
 * Represents different types of transactions.
 */
public enum TransactionType {
    ADD("credits-add", "credits-add-user"),
    SET("credits-set", "credits-set-user"),
    TAKE("credits-take", "credits-take-user"),
    PAY("credits-pay", "credits-pay-user"),
    REDEEM(t -> t.isSelfTransaction() ? "credits-redeem" : "credits-redeem-sudo", "credits-redeem-user"),
    ADDALL("credits-add-all", "credits-add-user"),
    TAKEALL("credits-take-all", "credits-take-user"),
    SETALL("credits-set-all", "credits-set-user"),
    REDEEMALL("credits-redeem-all", "credits-redeem-user");

    private final Function<Transaction, String> function;
    private final String key;

    TransactionType(final Function<Transaction, String> function, final String key) {
        this.function = function;
        this.key = key;
    }

    TransactionType(final String messageKey, final String key) {
        this.function = t -> messageKey;
        this.key = key;
    }

    public static TransactionType fromArgs(final CommandContext<CommandExecutor> args, int i) {
        return TransactionType.valueOf(args.getRawInput().get(i).toUpperCase());
    }

    public String messageKey(final Transaction transaction) {
        return this.function.apply(transaction);
    }

    public String userMessageKey() {
        return this.key;
    }

    public String notEnoughCredits() {
        return this.name().contains("ALL") ? "not-enough-credits-other" : "not-enough-credits";
    }
}
