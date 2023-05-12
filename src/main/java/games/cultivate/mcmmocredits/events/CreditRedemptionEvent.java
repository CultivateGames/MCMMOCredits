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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Event that is fired when a redemption is triggered. Does not capture results.
 */
public final class CreditRedemptionEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final CommandSender sender;
    private final UUID uuid;
    private final int amount;
    private final boolean userSilent;
    private final boolean senderSilent;
    private final PrimarySkillType skill;

    /**
     * Constructs the object.
     *
     * @param sender       Executor of the transaction. Can be Console.
     * @param uuid         UUID of the transaction target.
     * @param skill        The affected skill.
     * @param amount       Amount of credits to apply to skill.
     * @param userSilent   If process sends feedback to the user. True will silence feedback.
     * @param senderSilent If process sends feedback to the executor. True will silence feedback except for errors.
     */
    public CreditRedemptionEvent(final CommandSender sender, final UUID uuid, final PrimarySkillType skill, final int amount, final boolean userSilent, final boolean senderSilent) {
        this.sender = sender;
        this.uuid = uuid;
        this.skill = skill;
        this.amount = amount;
        this.userSilent = userSilent;
        this.senderSilent = senderSilent;
    }

    /**
     * Constructs the object as a self-redemption using a Bukkit player.
     *
     * @param player     CommandExecutor. Must be an online player.
     * @param skill      The affected skill.
     * @param amount     Amount of credits to apply to skill.
     * @param userSilent If the process should send feedback to the user. True will silence feedback.
     */
    @SuppressWarnings("unused")
    public CreditRedemptionEvent(final Player player, final PrimarySkillType skill, final int amount, final boolean userSilent) {
        this(player, player.getUniqueId(), skill, amount, userSilent, true);
    }

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
     * Gets the Bukkit CommandSender.
     *
     * @return The CommandSender.
     */
    public CommandSender sender() {
        return this.sender;
    }

    /**
     * Gets the target's UUID.
     *
     * @return The UUID.
     */
    public UUID uuid() {
        return this.uuid;
    }

    /**
     * Gets the amount of credits used in the transaction.
     *
     * @return The amount of credits.
     */
    public int amount() {
        return this.amount;
    }

    /**
     * Gets if the transaction is silent for the recipient.
     *
     * @return if the transaction is silent.
     */
    public boolean silentForUser() {
        return this.userSilent;
    }

    /**
     * Gets if the transaction is silent for the sender.
     *
     * @return if the transaction is silent.
     */
    public boolean silentForSender() {
        return this.senderSilent;
    }

    /**
     * Gets the skill being affected by the transaction.
     *
     * @return The Skill.
     */
    public PrimarySkillType skill() {
        return this.skill;
    }

    /**
     * Gets if the executor and target of the transaction are the same.
     *
     * @return True if entity is the same, false otherwise.
     */
    public boolean isSelfRedemption() {
        return this.sender instanceof Player p && p.getUniqueId().equals(this.uuid);
    }
}
