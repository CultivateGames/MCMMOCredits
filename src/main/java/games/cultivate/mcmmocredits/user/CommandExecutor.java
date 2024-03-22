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

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Represents a user of the application.
 */
public interface CommandExecutor {
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
    CommandSender sender();

    default void send(final Component component) {
        if (this.sender() instanceof Player player && !player.isOnline()) {
            return;
        }
        this.sender().sendMessage(component);
    }
}
