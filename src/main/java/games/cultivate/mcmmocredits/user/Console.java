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

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * CommandExecutor which represents the Bukkit ConsoleCommandSender.
 */
public enum Console implements CommandExecutor {
    INSTANCE;

    private final CommandSender sender;
    private final UUID uuid;
    private final String username;
    private final int credits;
    private final int redeemed;

    Console() {
        this.sender = Bukkit.getConsoleSender();
        this.uuid = UUID.randomUUID();
        this.username = "CONSOLE";
        this.credits = -1;
        this.redeemed = -1;
    }

    @Override
    public CommandSender sender() {
        return this.sender;
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
