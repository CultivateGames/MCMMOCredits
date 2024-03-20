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
package games.cultivate.mcmmocredits.events;

import games.cultivate.mcmmocredits.transaction.Transaction;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event that is fired when a credit transaction is created.
 */
public class CreditTransactionEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Transaction transaction;
    private final boolean userFeedback;
    private final boolean senderFeedback;
    private boolean cancelled = false;

    /**
     * Constructs the object.
     *
     * @param transaction    The transaction that will be executed.
     * @param userFeedback   If process sends feedback to the user. True will silence feedback.
     * @param senderFeedback If process sends feedback to the executor. True will silence feedback except for errors.
     */
    public CreditTransactionEvent(final Transaction transaction, final boolean userFeedback, final boolean senderFeedback) {
        super(!Bukkit.isPrimaryThread());
        this.transaction = transaction;
        this.userFeedback = userFeedback;
        this.senderFeedback = senderFeedback;
    }

    /**
     * Required method for Paper event classes.
     *
     * @return The HandlerList of the event.
     */
    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    @SuppressWarnings("java:S4144")
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Gets the provided transaction.
     *
     * @return The transaction.
     */
    public Transaction transaction() {
        return this.transaction;
    }

    /**
     * Gets if the transaction is silent for the recipient.
     *
     * @return if the transaction is silent.
     */
    public boolean userFeedback() {
        return this.userFeedback;
    }

    /**
     * Gets if the transaction is silent for the sender.
     *
     * @return if the transaction is silent.
     */
    public boolean senderFeedback() {
        return this.senderFeedback;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }
}
