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

import games.cultivate.mcmmocredits.placeholders.Resolver;
import net.kyori.adventure.text.minimessage.tag.PreProcess;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class CommandExecutor {
    private final UUID uuid;
    private final String username;
    private final int credits;
    private final int redeemed;

    CommandExecutor(final UUID uuid, final String username, final int credits, final int redeemed) {
        this.uuid = uuid;
        this.username = username;
        this.credits = credits;
        this.redeemed = redeemed;
    }

    public Map<String, PreProcess> placeholders(final String prefix) {
        Map<String, String> map = new HashMap<>();
        map.put(prefix, this.username);
        map.put(prefix + "_credits", this.credits + "");
        map.put(prefix + "_uuid", this.uuid.toString());
        map.put(prefix + "_redeemed", this.redeemed + "");
        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, x -> Tag.preProcessParsed(x.getValue())));
    }

    public Resolver resolver() {
        return Resolver.builder().user(this, "sender").build();
    }

    public UUID uuid() {
        return this.uuid;
    }

    public String username() {
        return this.username;
    }

    public int credits() {
        return this.credits;
    }

    public int redeemed() {
        return this.redeemed;
    }

    public abstract boolean isPlayer();

    public abstract boolean isConsole();

    public abstract CommandSender sender();

    public abstract Player player();
}
