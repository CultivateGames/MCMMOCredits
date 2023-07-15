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

public final class Transaction {
    private int amount;
    private CommandExecutor executor;
    private User[] targets;
    private String userMessageKey;
    private String messageKey;
    private TransactionType type;
    private PrimarySkillType skill;

    public static Transaction builder() {
        return new Transaction();
    }

    public PrimarySkillType skill() {
        return this.skill;
    }

    public Transaction skill(final PrimarySkillType skill) {
        this.type = TransactionType.REDEEM;
        this.skill = skill;
        return this;
    }

    public TransactionType type() {
        return this.type;
    }

    public Transaction type(final TransactionType type) {
        this.type = type;
        this.messageKey = this.type.messageKey();
        this.userMessageKey = this.type.userMessageKey();
        return this;
    }

    public int amount() {
        return this.amount;
    }

    public Transaction amount(final int amount) {
        this.amount = amount;
        return this;
    }

    public CommandExecutor executor() {
        return this.executor;
    }

    public User[] targets() {
        return this.targets;
    }

    public Transaction self(final User executor) {
        this.executor = executor;
        this.targets = new User[]{executor};
        return this;
    }

    public Transaction users(final CommandExecutor executor, final User... targets) {
        this.executor = executor;
        this.targets = targets;
        return this;
    }

    public Transaction users(final User executor, final List<User> targets) {
        this.executor = executor;
        this.targets = targets.toArray(new User[0]);
        return this;
    }

    public String userMessageKey() {
        return this.userMessageKey;
    }

    public String messageKey() {
        return this.messageKey;
    }

    public TransactionResult execute() {
        User primary = this.targets[0];
        int balance = primary.credits();
        return switch (this.type) {
            case ADD -> TransactionResult.of(this, this.executor, this.targets[0].withCredits(balance + this.amount));
            case TAKE -> TransactionResult.of(this, this.executor, this.targets[0].withCredits(balance - this.amount));
            case SET -> TransactionResult.of(this, this.executor, this.targets[0].withCredits(this.amount));
            case PAY -> {
                User exec = (User) this.executor;
                yield TransactionResult.of(this, exec.withCredits(this.executor.credits() - this.amount), this.targets[0].withCredits(balance + this.amount));
            }
            case REDEEM -> {
                PlayerProfile profile = this.getPlayerProfile(primary);
                profile.addLevels(this.skill, this.amount);
                profile.save(true);
                yield TransactionResult.of(this, this.executor, primary.withCredits(balance - this.amount).withRedeemed(primary.redeemed() + this.amount));
            }
        };
    }

    /**
     * Gets if the executor and target are the same entity.
     *
     * @return True if the executor and target are the same entity, otherwise false.
     */
    public boolean isSelfTransaction() {
        return this.executor.equals(this.targets[0]);
    }

    public Optional<String> isExecutable() {
        int balance = this.targets[0].credits();
        return switch (this.type) {
            case ADD -> this.amount + balance >= 0 ? Optional.empty() : Optional.of("not-enough-credits");
            case SET -> this.amount >= 0 ? Optional.empty() : Optional.of("not-enough-credits");
            case TAKE -> balance - this.amount >= 0 ? Optional.empty() : Optional.of("not-enough-credits");
            case PAY -> {
                if (this.executor.credits() - this.amount < 0 || this.targets[0].credits() + this.amount < 0) {
                    yield Optional.of("not-enough-credits");
                }
                if (this.isSelfTransaction()) {
                    yield Optional.of("credits-pay-same-user");
                }
                yield Optional.empty();
            }
            case REDEEM -> {
                if (balance < this.amount) {
                    yield Optional.of("not-enough-credits");
                }
                PlayerProfile profile = this.getPlayerProfile(this.targets[0]);
                if (profile == null || !profile.isLoaded()) {
                    yield Optional.of("mcmmo-profile-fail");
                }
                if (profile.getSkillLevel(this.skill) + this.amount > mcMMO.p.getGeneralConfig().getLevelCap(this.skill)) {
                    yield Optional.of("mcmmo-skill-cap");
                }
                yield Optional.empty();
            }
        };
    }

    /**
     * Gets the target's MCMMO profile.
     *
     * @param user The user to fetch a profile for.
     * @return The profile.
     */
    private @Nullable PlayerProfile getPlayerProfile(final User user) {
        Player player = user.player();
        return player == null ? mcMMO.getDatabaseManager().loadPlayerProfile(user.uuid()) : UserManager.getPlayer(player).getProfile();
    }
}
