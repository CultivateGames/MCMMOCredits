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
package games.cultivate.mcmmocredits.events;

import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.util.CreditOperation;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Event that is fired when a {@link CreditOperation} is triggered. Does not capture results.
 * Plugins that want to modify this event will have to use EventPriority.MONITOR.
 */
public final class CreditTransactionEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final CommandSender sender;
    private final UUID uuid;
    private final CreditOperation operation;
    private final int amount;
    private final boolean silent;

    /**
     * Constructs the event.
     *
     * @param sender    Command executor. Can be from Console.
     * @param uuid      UUID of the {@link User} being actioned.
     * @param operation The type of transaction taking place.
     * @param amount    Amount of credits affected by transaction.
     * @param silent    If command should be "silent". User will not get feedback upon completion if true.
     */
    public CreditTransactionEvent(final CommandSender sender, final UUID uuid, final CreditOperation operation, final int amount, final boolean silent) {
        this.sender = sender;
        this.uuid = uuid;
        this.operation = operation;
        this.amount = amount;
        this.silent = silent;
    }

    /**
     * Constructs the event using a {@link User} instead of the Bukkit equivalents.
     *
     * @param user      Command Executor. Must be an online player.
     * @param operation The type of transaction occurring.
     * @param amount    Amount of credits affected by transaction.
     * @param silent    If command should be "silent". User will not get feedback upon completion if true.
     */
    public CreditTransactionEvent(final User user, final CreditOperation operation, final int amount, final boolean silent) {
        this(user.player(), user.uuid(), operation, amount, silent);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Returns the Bukkit CommandSender.
     *
     * @return The CommandSender.
     */
    public CommandSender sender() {
        return this.sender;
    }

    /**
     * Returns the target's UUID.
     *
     * @return The UUID.
     */
    public UUID uuid() {
        return this.uuid;
    }

    /**
     * Returns the type of transaction occurring.
     *
     * @return The transaction type.
     */
    public CreditOperation operation() {
        return this.operation;
    }

    /**
     * Returns the amount of credits used in the transaction.
     *
     * @return The amount of credits.
     */
    public int amount() {
        return this.amount;
    }

    /**
     * Returns if the transaction is silent.
     *
     * @return Silence of the transaction.
     */
    public boolean isSilent() {
        return this.silent;
    }
}
