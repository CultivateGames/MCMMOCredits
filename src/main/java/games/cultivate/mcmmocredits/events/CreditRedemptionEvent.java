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
import games.cultivate.mcmmocredits.util.User;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class CreditRedemptionEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final CommandSender sender;
    private final User user;
    private final int amount;
    private final boolean silent;
    private final PrimarySkillType skill;

    /**
     * Initializes the event.
     *
     * @param sender The {@link CommandSender} who initiated the transaction.
     * @param user      {@link User} being impacted by the event.
     * @param skill     {@link PrimarySkillType} that is being used for credit redemption.
     * @param amount    amount of Credits to use in the transaction.
     * @param silent    Whether the transaction should produce a message to be sent to the user.
     */
    public CreditRedemptionEvent(final CommandSender sender, final User user, final PrimarySkillType skill, final int amount, final boolean silent) {
        this.sender = sender;
        this.user = user;
        this.skill = skill;
        this.amount = amount;
        this.silent = silent;
    }

    public CommandSender sender() {
        return this.sender;
    }

    public User user() {
        return this.user;
    }

    public int amount() {
        return this.amount;
    }

    public boolean isSilent() {
        return this.silent;
    }

    public PrimarySkillType skill() {
        return this.skill;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
