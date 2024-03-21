//
// MIT License
//
// Copyright (c) 2024 Cultivate Games
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

import games.cultivate.mcmmocredits.messages.Resolver;
import games.cultivate.mcmmocredits.messages.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.UnaryOperator;

/**
 * Represents a user of the application.
 */
public interface CommandExecutor {
    /**
     * Determines if the CommandExecutor is a {@link Player}
     *
     * @return if this is a Player.
     */
    boolean isPlayer();

    /**
     * Gets the Bukkit Player from this CommandExecutor.
     *
     * @return the Player.
     */
    Player player();

    /**
     * Gets the UUID of the executor.
     *
     * @return the UUID.
     */
    UUID uuid();

    /**
     * Gets the username of the executor.
     *
     * @return the username.
     */
    String username();

    /**
     * Gets the current credit balance of the executor.
     *
     * @return the credit balance.
     */
    int credits();

    /**
     * Gets the current redemption statistic of the executor.
     *
     * @return the redeemed statistic.
     */
    int redeemed();

    /**
     * Gets the Bukkit CommandSender from this CommandExecutor.
     *
     * @return the CommandSender.
     */
    default CommandSender sender() {
        return this.isPlayer() ? this.player() : Bukkit.getConsoleSender();
    }

    /**
     * Converts this instance to a User if the CommandExecutor is known to be a player.
     *
     * @return The User, or null if not a player.
     */
    default @Nullable User toUser() {
        return this.isPlayer() ? (User) this : null;
    }

    /**
     * Sends provided message that is parsed with provided resolver.
     *
     * @param message  The message to send.
     * @param resolver The resolver to parse it with.
     */
    default void sendText(final String message, final Resolver resolver) {
        Text.fromString(this, message, resolver).send();
    }

    /**
     * Sends provided message that is parsed with auto-generated resolver.
     *
     * @param message The message to send.
     */
    default void sendText(final String message) {
        Text.forOneUser(this, message).send();
    }

    /**
     * Sends provided message that is parsed with customized resolver.
     *
     * @param message  The message to send.
     * @param operator Function to apply to resolver.
     */
    default void sendText(final String message, final UnaryOperator<Resolver> operator) {
        Text.forOneUser(this, message, operator).send();
    }
}
