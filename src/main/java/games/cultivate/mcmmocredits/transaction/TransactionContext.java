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

import games.cultivate.mcmmocredits.user.CommandSource;
import games.cultivate.mcmmocredits.user.CommandSourceData;
import games.cultivate.mcmmocredits.user.UserCommandSource;
import org.incendo.cloud.caption.Caption;

import java.util.function.BiFunction;

public record TransactionContext(TransactionInfo info, TransactionStrategy transactionStrategy) {
    public static TransactionContext create(final CommandSource sender, final CommandSourceData data, final int amount, final TransactionStrategy type) {
        return new TransactionContext(TransactionInfo.of(sender, data, amount), type);
    }

    public static TransactionContext create(final UserCommandSource sender, final int amount, final TransactionStrategy type) {
        return new TransactionContext(TransactionInfo.of(sender, amount), type);
    }

    public TransactionContext replacePair(final CommandSource sender, final CommandSourceData data) {
        var info = TransactionInfo.of(sender, data, this.info.amount());
        return new TransactionContext(info, this.transactionStrategy);
    }

    public TransactionContext withData(final CommandSourceData data) {
        var info = TransactionInfo.of(this.info.sender(), data, this.info.amount());
        return new TransactionContext(info, this.transactionStrategy);
    }

    public CommandSource sender() {
        return this.info.sender();
    }

    public int amount() {
        return this.info.amount();
    }

    public CommandSourceData target() {
        return this.info.target();
    }

    public BiFunction<CommandSourceData, Integer, CommandSourceData> strategy() {
        return this.transactionStrategy.strategy();
    }

    public Caption senderCaption() {
        return this.transactionStrategy.senderCaption();
    }

    public Caption targetCaption() {
        return this.transactionStrategy.targetCaption();
    }

    public Caption allCaption() {
        return this.transactionStrategy.allCaption();
    }
}
