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

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Event that is fired when a redemption is triggered. Does not capture results.
 * Plugins that want to modify this event will have to use EventPriority.MONITOR.
 */
public final class CreditRedemptionEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final CommandSender sender;
    private final UUID uuid;
    private final int amount;
    private final boolean silent;
    private final PrimarySkillType skill;

    /**
     * Constructs the event
     *
     * @param sender Command executor. Can be from Console.
     * @param uuid   UUID of the {@link User} being actioned.
     * @param skill  The skill being actioned.
     * @param amount Amount of credits affected by transaction.
     * @param silent If command should be "silent". User will not get feedback upon completion if true.
     */
    public CreditRedemptionEvent(final CommandSender sender, final UUID uuid, final PrimarySkillType skill, final int amount, final boolean silent) {
        this.sender = sender;
        this.uuid = uuid;
        this.skill = skill;
        this.amount = amount;
        this.silent = silent;
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

    /**
     * The skill being affected by the transaction.
     *
     * @return The Skill.
     */
    public PrimarySkillType skill() {
        return this.skill;
    }
}