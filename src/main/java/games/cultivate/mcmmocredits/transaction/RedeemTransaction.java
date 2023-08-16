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

import java.util.List;
import java.util.Optional;

/**
 * Represents a transaction in which a user exchanges credits for levels in the provided skill.
 *
 * @param executor The executor of the transaction.
 * @param targets  The targets of the transaction.
 * @param skill    The skill to add levels to.
 * @param amount   The amount of credits to remove from the user.
 */
public record RedeemTransaction(CommandExecutor executor, List<User> targets, PrimarySkillType skill, int amount) implements Transaction {
    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionResult execute() {
        List<User> mapped = this.targets.stream().map(x -> {
            PlayerProfile profile = this.getPlayerProfile(x);
            if (profile == null) {
                return x;
            }
            profile.addLevels(this.skill, this.amount);
            profile.save(true);
            return new User(x.uuid(), x.username(), x.credits() - this.amount, x.redeemed() + this.amount);
        }).toList();
        return TransactionResult.of(this, this.executor, mapped);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> validate(final User user) {
        if (user.credits() < this.amount) {
            return Optional.of(this.type().notEnoughCredits());
        }
        PlayerProfile profile = this.getPlayerProfile(user);
        if (profile == null || !profile.isLoaded()) {
            return Optional.of("mcmmo-profile-fail");
        }
        if (profile.getSkillLevel(this.skill) + this.amount > mcMMO.p.getGeneralConfig().getLevelCap(this.skill)) {
            return Optional.of("mcmmo-skill-cap");
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionType type() {
        return this.targets.size() > 1 ? TransactionType.REDEEMALL : TransactionType.REDEEM;
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
