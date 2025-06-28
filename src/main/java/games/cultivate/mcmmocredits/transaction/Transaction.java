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

import com.google.inject.assistedinject.Assisted;
import games.cultivate.mcmmocredits.config.Captions;
import games.cultivate.mcmmocredits.messages.MessageService;
import games.cultivate.mcmmocredits.user.CommandSource;
import games.cultivate.mcmmocredits.user.CommandSourceData;
import games.cultivate.mcmmocredits.user.MalformedUserException;
import games.cultivate.mcmmocredits.user.UserService;
import jakarta.inject.Inject;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.type.Either;

import java.util.function.BiFunction;

public final class Transaction {
    private final CommandSource sender;
    private final CommandSourceData target;
    private final int amount;
    private final BiFunction<CommandSourceData, Integer, CommandSourceData> strategy;
    private final Caption senderCaption;
    private final Caption targetCaption;
    private final MessageService messageService;
    private final UserService userService;

    @Inject
    public Transaction(final MessageService messageService, final UserService userService, @Assisted("context") final TransactionContext context) {
        this.sender = context.sender();
        this.target = context.target();
        this.amount = context.amount();
        this.strategy = context.strategy();
        this.senderCaption = context.senderCaption();
        this.targetCaption = context.targetCaption();
        this.messageService = messageService;
        this.userService = userService;
    }

    public void runForSender() {
        this.validate().mapEither(this::forSender, this::forFail);
    }

    public void runForTarget(final boolean silent) {
        this.validate().mapEither(d -> this.forTarget(d, silent), this::forFail);
    }

    private Either<CommandSourceData, Caption> validate() {
        try {
            CommandSourceData updated = this.strategy.apply(this.target, this.amount);
            return Either.ofPrimary(updated);
        } catch (MalformedUserException e) {
            return Either.ofFallback(Captions.NOT_ENOUGH_CREDITS);
        }
    }

    private Void forSender(final CommandSourceData updated) {
        if (this.userService.updateUser(updated)) {
            this.messageService.forTransaction(this.senderCaption, updated, this.amount).send(this.sender);
        }
        return null;
    }

    private Void forTarget(final CommandSourceData updated, final boolean silent) {
        if (this.userService.updateUser(updated)) {
            this.messageService.forTransaction(this.senderCaption, this.sender.data(), updated, this.amount).send(this.sender);
            if (!silent) {
                var m = this.messageService.forTransaction(this.targetCaption, this.sender.data(), updated, this.amount);
                this.userService.asPlayer(updated).ifPresent(m::send);
            }
        }
        return null;
    }

    private Void forFail(final Caption caption) {
        this.messageService.forTransaction(caption, this.sender.data(), this.target, this.amount).send(this.sender);
        return null;
    }
}
