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
package games.cultivate.mcmmocredits.user;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Command Executor which is a Player. Interacted with through the {@link UserDAO}
 */
public final class User extends CommandExecutor {
    /**
     * Constructs the User. Users are immutable.
     *
     * @param uuid     The UUID of the executor.
     * @param username The username of the executor.
     * @param credits  The default credit balance of the executor.
     * @param redeemed The default redeemed statistic of the executor.
     */
    public User(final UUID uuid, final String username, final int credits, final int redeemed) {
        super(uuid, username, credits, redeemed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPlayer() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConsole() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandSender sender() {
        return this.player();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player player() {
        return Bukkit.getPlayer(this.uuid());
    }

    /**
     * Provides copy of existing User with updated credit balance.
     *
     * @param credits credit balance to apply.
     * @return An updated copy of the User.
     */
    public User withCredits(final int credits) {
        return new User(this.uuid(), this.username(), credits, this.redeemed());
    }

    /**
     * Provides copy of existing User with updated credit balance.
     *
     * @param username new username to apply.
     * @return An updated copy of the User.
     */
    public User withUsername(final String username) {
        return new User(this.uuid(), username, this.credits(), this.redeemed());
    }

    /**
     * Provides copy of existing User with updated redeemed credit balance.
     *
     * @param redeemed redeemed credit balance to apply.
     * @return An updated copy of the User.
     */
    public User withRedeemed(final int redeemed) {
        return new User(this.uuid(), this.username(), this.credits(), redeemed);
    }
}
