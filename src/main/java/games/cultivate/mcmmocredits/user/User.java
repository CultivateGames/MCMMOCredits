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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents a human.
 */
public final class User implements CommandExecutor {
    private final UUID uuid;
    private final String username;
    private final int credits;
    private final int redeemed;
    private final Player player;

    private User(final UUID uuid, final String username, final int credits, final int redeemed, final Player player) {
        this.uuid = uuid;
        this.username = username;
        this.credits = credits;
        this.redeemed = redeemed;
        this.player = player;
    }

    public User(final UUID uuid, final String username, final int credits, final int redeemed) {
        this(uuid, username, credits, redeemed, null);
    }

    public User(final Player player, final int credits, final int redeemed) {
        this(player.getUniqueId(), player.getName(), credits, redeemed, player);
    }

    public User withPlayer(final Player player) {
        return new User(this.uuid, this.username, this.credits, this.redeemed, player);
    }

    public User withCredits(final int amount) {
        return new User(this.uuid, this.username, amount, this.redeemed, this.player);
    }

    public User addCredits(final int amount) {
        return this.withCredits(this.credits + amount);
    }

    public User takeCredits(final int amount) {
        return this.withCredits(this.credits - amount);
    }

    public User applyRedemption(final int amount) {
        return new User(this.uuid, this.username, this.credits - amount, this.redeemed + amount, this.player);
    }

    public User withUsername(final String username) {
        return new User(this.uuid, username, this.credits, this.redeemed, this.player);
    }

    @Override
    public @Nullable CommandSender sender() {
        return this.player;
    }

    @Override
    public UUID uuid() {
        return this.uuid;
    }

    @Override
    public String username() {
        return this.username;
    }

    @Override
    public int credits() {
        return this.credits;
    }

    @Override
    public int redeemed() {
        return this.redeemed;
    }
}
