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

import games.cultivate.mcmmocredits.config.Captions;
import games.cultivate.mcmmocredits.user.CommandSourceData;
import org.incendo.cloud.caption.Caption;

import java.util.function.BiFunction;

public enum TransactionType implements TransactionStrategy {
    TAKE(CommandSourceData::takeCredits, Captions.TAKE_SENDER, Captions.TAKE_TARGET, Captions.TAKE_ALL),
    ADD(CommandSourceData::addCredits, Captions.ADD_SENDER, Captions.ADD_TARGET, Captions.ADD_ALL),
    SET(CommandSourceData::withCredits, Captions.SET_SENDER, Captions.SET_TARGET, Captions.SET_ALL),
    REDEEM(CommandSourceData::redeemCredits, Captions.REDEEM_SENDER, Captions.REDEEM_TARGET, Captions.REDEEM_ALL),
    PAY(CommandSourceData::takeCredits, CommandSourceData::addCredits, Captions.PAY_SENDER, Captions.PAY_TARGET, Captions.PAY_ALL);

    private final BiFunction<CommandSourceData, Integer, CommandSourceData> senderStrategy;
    private final BiFunction<CommandSourceData, Integer, CommandSourceData> strategy;
    private final Caption senderCaption;
    private final Caption targetCaption;
    private final Caption allCaption;

    TransactionType(final BiFunction<CommandSourceData, Integer, CommandSourceData> senderStrategy, final BiFunction<CommandSourceData, Integer, CommandSourceData> strategy, final Caption senderCaption, final Caption targetCaption, final Caption allCaption) {
        this.senderStrategy = senderStrategy;
        this.strategy = strategy;
        this.senderCaption = senderCaption;
        this.targetCaption = targetCaption;
        this.allCaption = allCaption;
    }

    TransactionType(final BiFunction<CommandSourceData, Integer, CommandSourceData> strategy, final Caption senderCaption, final Caption targetCaption, final Caption allCaption) {
        this(null, strategy, senderCaption, targetCaption, allCaption);
    }

    @Override
    public BiFunction<CommandSourceData, Integer, CommandSourceData> senderStrategy() {
        return this.senderStrategy;
    }

    @Override
    public BiFunction<CommandSourceData, Integer, CommandSourceData> strategy() {
        return this.strategy;
    }

    @Override
    public Caption senderCaption() {
        return this.senderCaption;
    }

    @Override
    public Caption targetCaption() {
        return this.targetCaption;
    }

    @Override
    public Caption allCaption() {
        return this.allCaption;
    }
}
