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
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents a transaction in which a user exchanges credits for levels in the provided skill.
 *
 * @param executor The executor of the transaction.
 * @param targets  The targets of the transaction.
 * @param skill    The skill to add levels to.
 * @param amount   The amount of credits to remove from the user.
 */
public record RedeemTransaction(CommandExecutor executor, User[] targets, PrimarySkillType skill,
                                int amount) implements Transaction {
    private static final String MESSAGE_KEY = "redeem";
    private static final String SUDO_REDEEM_KEY = "redeem-sudo";
    private static final String USER_MESSAGE_KEY = "redeem-sudo-user";

    /**
     * {@inheritDoc}
     */
    @Override
    public String userMessageKey() {
        return USER_MESSAGE_KEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String messageKey() {
        return this.isSelfTransaction() ? MESSAGE_KEY : SUDO_REDEEM_KEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionResult execute() {
        PlayerProfile profile = this.getPlayerProfile(this.targets[0]);
        profile.addLevels(this.skill, this.amount);
        profile.save(true);
        User updated = this.targets[0].takeCredits(this.amount).addRedeemed(this.amount);
        return this.isSelfTransaction() ? TransactionResult.of(this, updated) : TransactionResult.of(this, this.executor, updated);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> valid() {
        if (this.targets[0].credits() < this.amount) {
            return Optional.of("not-enough-credits");
        }
        PlayerProfile profile = this.getPlayerProfile(this.targets[0]);
        if (profile == null || !profile.isLoaded()) {
            return Optional.of("mcmmo-profile-fail");
        }
        if (profile.getSkillLevel(this.skill) + this.amount > mcMMO.p.getGeneralConfig().getLevelCap(this.skill)) {
            return Optional.of("mcmmo-skill-cap");
        }
        return Optional.empty();
    }

    /**
     * Gets the target's MCMMO profile.
     *
     * @param user The user to fetch a profile for.
     * @return The profile.
     */
    private @Nullable PlayerProfile getPlayerProfile(final User user) {
        Player player = user.player();
        if (player == null) {
            return mcMMO.getDatabaseManager().loadPlayerProfile(user.uuid());
        }
        if (player.getLastLogin() + 5000 < System.currentTimeMillis()) {
            return UserManager.getPlayer(player).getProfile();
        }
        return null;
    }
}
