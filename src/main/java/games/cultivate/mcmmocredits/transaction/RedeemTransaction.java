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

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.User;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Represents a transaction in which a user will exchange credits for levels in MCMMO Skills.
 *
 * @param executor The executor of the transaction.
 * @param target   The user to apply the transaction to.
 * @param skill    The affected skill.
 * @param amount   The amount to use.
 */
public record RedeemTransaction(CommandExecutor executor, User target, PrimarySkillType skill, int amount) implements Transaction {
    private static final String MESSAGE_KEY = "redeem";
    private static final String SUDO_REDEEM_KEY = "redeem-sudo";
    private static final String USER_MESSAGE_KEY = "redeem-sudo-user";

    /**
     * Creates a solo transaction using the provided information.
     *
     * @param target The user to apply the transaction to.
     * @param skill  The affected skill.
     * @param amount The amount to use.
     * @return The transaction.
     */
    public static RedeemTransaction of(final User target, final PrimarySkillType skill, final int amount) {
        return new RedeemTransaction(target, target, skill, amount);
    }

    /**
     * Creates a transaction using the provided information.
     *
     * @param executor The executor of the transaction.
     * @param target   The user to apply the transaction to.
     * @param skill    The affected skill.
     * @param amount   The amount to use.
     * @return The transaction.
     */
    public static RedeemTransaction of(final CommandExecutor executor, final User target, final PrimarySkillType skill, final int amount) {
        return new RedeemTransaction(executor, target, skill, amount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionResult execute() {
        int credits = this.target.credits();
        int redeemed = this.target.redeemed();
        User updatedUser = this.target.withCredits(credits - this.amount).withRedeemed(redeemed + this.amount);
        PlayerProfile profile = this.getPlayerProfile();
        profile.addLevels(this.skill, this.amount);
        profile.save(true);
        return TransactionResult.of(this, this.executor, updatedUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<FailureReason> executable() {
        if (this.target.credits() < this.amount) {
            return Optional.of(FailureReason.NOT_ENOUGH_CREDITS);
        }
        PlayerProfile profile = this.getPlayerProfile();
        if (profile == null || !profile.isLoaded()) {
            return Optional.of(FailureReason.MCMMO_PROFILE_FAIL);
        }
        if (profile.getSkillLevel(this.skill) + this.amount > mcMMO.p.getGeneralConfig().getLevelCap(this.skill)) {
            return Optional.of(FailureReason.MCMMO_SKILL_CAP);
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSelfTransaction() {
        return this.executor.equals(this.target);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageKey() {
        return this.isSelfTransaction() ? MESSAGE_KEY : SUDO_REDEEM_KEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserMessageKey() {
        return USER_MESSAGE_KEY;
    }

    /**
     * Gets the target's MCMMO profile.
     *
     * @return The profile.
     */
    private @Nullable PlayerProfile getPlayerProfile() {
        Player player = this.target.player();
        return player == null ? mcMMO.getDatabaseManager().loadPlayerProfile(this.target.uuid()) : UserManager.getPlayer(player).getProfile();
    }
}
