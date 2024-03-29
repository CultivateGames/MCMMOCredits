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

import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * CommandExecutor which represents the Bukkit ConsoleCommandSender.
 */
public final class Console implements CommandExecutor {
    public static final Console INSTANCE = new Console();
    private static final UUID UUID = new UUID(0, 0);
    private static final String USERNAME = "CONSOLE";
    private static final int CREDITS = 0;
    private static final int REDEEMED = 0;

    private Console() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPlayer() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player player() {
        throw new UnsupportedOperationException("Console is not a player!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID uuid() {
        return UUID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String username() {
        return USERNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int credits() {
        return CREDITS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int redeemed() {
        return REDEEMED;
    }
}
