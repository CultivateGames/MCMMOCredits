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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Represents any entity that can execute a command. Typically a User or Console.
 */
public abstract class CommandExecutor {
    private final UUID uuid;
    private final String username;
    private final int credits;
    private final int redeemed;

    /**
     * Constructs the object.
     *
     * @param uuid     The UUID of the executor.
     * @param username The username of the executor.
     * @param credits  The default credit balance of the executor.
     * @param redeemed The default redeemed statistic of the executor.
     */
    CommandExecutor(final UUID uuid, final String username, final int credits, final int redeemed) {
        this.uuid = uuid;
        this.username = username;
        this.credits = credits;
        this.redeemed = redeemed;
    }

    /**
     * Gets the UUID of the executor.
     *
     * @return the UUID.
     */
    public UUID uuid() {
        return this.uuid;
    }

    /**
     * Gets the username of the executor.
     *
     * @return the username.
     */
    public String username() {
        return this.username;
    }

    /**
     * Gets the current credit balance of the executor.
     *
     * @return the credit balance.
     */
    public int credits() {
        return this.credits;
    }

    /**
     * Gets the current redemption statistic of the executor.
     *
     * @return the redeemed statistic.
     */
    public int redeemed() {
        return this.redeemed;
    }

    /**
     * Determines if the CommandExecutor is a {@link Player}
     *
     * @return if this is a Player.
     */
    public abstract boolean isPlayer();

    /**
     * Determines if the CommandExecutor is an instance of Console.
     *
     * @return if this is Console.
     */
    public abstract boolean isConsole();

    /**
     * Gets the Bukkit CommandSender from this CommandExecutor.
     *
     * @return the CommandSender.
     */
    public abstract CommandSender sender();

    /**
     * Gets the Bukkit Player from this CommandExecutor.
     *
     * @return the Player.
     */
    public abstract Player player();
}
