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

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder class for Transactions.
 */
public final class TransactionBuilder {
    private final int amount;
    private final CommandExecutor executor;
    private final TransactionType type;
    private List<User> targets = new ArrayList<>();
    private PrimarySkillType skill;

    /**
     * Constructs the object.
     *
     * @param executor The executor of the transaction.
     * @param type     The type of the transaction.
     * @param amount   The amount of the transaction.
     */
    public TransactionBuilder(final CommandExecutor executor, final TransactionType type, final int amount) {
        this.executor = executor;
        this.amount = amount;
        this.type = type;
    }

    /**
     * Sets PrimarySkillType of the transaction, and also sets the transaction type to REDEEM.
     *
     * @param skill The skill.
     * @return The builder.
     */
    public TransactionBuilder skill(final PrimarySkillType skill) {
        if (this.type != TransactionType.REDEEM && this.type != TransactionType.REDEEMALL) {
            throw new IllegalStateException("MCMMO Skill assigned to non-redeem transaction!");
        }
        this.skill = skill;
        return this;
    }

    /**
     * Sets the targets of the transaction.
     *
     * @param targets The targets.
     * @return The builder.
     */
    public TransactionBuilder targets(final List<User> targets) {
        this.targets = targets;
        return this;
    }

    /**
     * Sets the targets of the transaction.
     *
     * @param target The target.
     * @return The builder.
     */
    public TransactionBuilder targets(final User target) {
        this.targets.add(target);
        return this;
    }

    /**
     * Builds the transaction.
     *
     * @return The transaction.
     */
    public Transaction build() {
        if (this.targets.isEmpty()) {
            if (this.type == TransactionType.PAY || !this.executor.isPlayer()) {
                throw new IllegalStateException("Transactions must have at least one recipient!");
            }
            this.targets.add(this.executor.toUser());
        }
        return switch (this.type) {
            case ADD, ADDALL -> new AddTransaction(this.executor, this.targets, this.amount);
            case SET, SETALL -> new SetTransaction(this.executor, this.targets, this.amount);
            case TAKE, TAKEALL -> new TakeTransaction(this.executor, this.targets, this.amount);
            case REDEEM, REDEEMALL -> new RedeemTransaction(this.executor, this.targets, this.skill, this.amount);
            case PAY -> this.executor.toUser() != null ? new PayTransaction(this.executor.toUser(), this.targets, this.amount) : null;
        };
    }
}
