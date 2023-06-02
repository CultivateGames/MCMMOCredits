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
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Represents a human.
 *
 * @param uuid     UUID of the user.
 * @param username Username of the user.
 * @param credits  Default credit balance of the user.
 * @param redeemed Default credit redeemed stat of the user.
 */
public record User(UUID uuid, String username, int credits, int redeemed) implements CommandExecutor {

    /**
     * Parses User from line of a CSV file.
     *
     * @param line The line of text from CSV file.
     * @return The parsed User.
     */
    public static User fromCSV(final String line) {
        String[] arr = line.split(",");
        return new User(UUID.fromString(arr[0]), arr[1], Integer.parseInt(arr[2]), Integer.parseInt(arr[3]));
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
    public Player player() {
        return Bukkit.getPlayer(this.uuid());
    }

    /**
     * Provides a copy of the User with an updated credit amount.
     *
     * @param credits credit amount to set on the User.
     * @return An updated User.
     */
    public User withCredits(final int credits) {
        return new User(this.uuid, this.username, credits, this.redeemed);
    }

    /**
     * Provides a copy of the User with a new username.
     *
     * @param username New username to set on the User.
     * @return An updated User.
     */
    public User withUsername(final String username) {
        return new User(this.uuid, username, this.credits, this.redeemed);
    }

    /**
     * Provides a copy of the User with an updated redeemed amount.
     *
     * @param redeemed credit redeemed amount to set on the User.
     * @return An updated User.
     */
    public User withRedeemed(final int redeemed) {
        return new User(this.uuid, this.username, this.credits, redeemed);
    }
}
