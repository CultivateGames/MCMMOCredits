//
// MIT License
//
// Copyright (c) 2022 Cultivate Games
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
package games.cultivate.mcmmocredits.events;

import games.cultivate.mcmmocredits.util.CreditOperation;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * {@link Event} which represents a transaction involving MCMMO Credits. Can be used by external plugins to modify credit balances via a Listener.
 */
public final class CreditTransactionEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final CommandSender initiator;
    private final UUID uuid;
    private final CreditOperation operation;
    private final int amount;
    private final boolean silent;

    /**
     * Initializes the event.
     *
     * @param initiator The {@link CommandSender} who initiated the transaction.
     * @param uuid {@link UUID} of the user who is
     * @param operation {@link CreditOperation} of the transaction we are about to execute.
     * @param amount amount of Credits to use in the transaction.
     * @param silent Whether the transaction should produce a message to be sent to the user.
     */
    public CreditTransactionEvent(final CommandSender initiator, final UUID uuid, final CreditOperation operation, final int amount, final boolean silent) {
        this.initiator = initiator;
        this.uuid = uuid;
        this.operation = operation;
        this.amount = amount;
        this.silent = silent;
    }

    public CommandSender initiator() {
        return this.initiator;
    }

    public UUID uuid() {
        return this.uuid;
    }

    public CreditOperation operation() {
        return this.operation;
    }

    public int amount() {
        return this.amount;
    }

    public boolean isSilent() {
        return this.silent;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
