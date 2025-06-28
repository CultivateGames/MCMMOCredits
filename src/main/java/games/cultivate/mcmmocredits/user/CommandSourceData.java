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

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * Data object to wrap {@linkplain CommandSource}.
 *
 * @param uuid     UUID of the source.
 * @param username Username of the source.
 * @param credits  Credit balance of the source.
 * @param redeemed Redeemed credits of the source.
 */
public record CommandSourceData(UUID uuid, String username, int credits, int redeemed) {
    private static final String[] TAGS = {"name", "uuid", "credits", "redeemed"};

    public CommandSourceData {
        if (username.length() > 16 || username.length() <= 2) {
            throw new MalformedUserException("Username is malformed! Username: " + username);
        }
        if (uuid.version() != 4) {
            throw new MalformedUserException("UUID is malformed! UUID: " + uuid);
        }
        if (credits < 0) {
            throw new MalformedUserException("Credits is malformed! Credits: " + credits);
        }
        if (redeemed < 0) {
            throw new MalformedUserException("Credits is malformed! Credits: " + credits);
        }
    }

    public CommandSourceData(final Player player) {
        this(player.getUniqueId(), player.getName(), 0, 0);
    }

    public CommandSourceData(final String[] value) {
        this(UUID.fromString(value[0]), value[1], Integer.parseInt(value[2]), Integer.parseInt(value[3]));
    }

    public CommandSourceData withCredits(final int amount) {
        return new CommandSourceData(this.uuid, this.username, amount, this.redeemed);
    }

    public CommandSourceData addCredits(final int amount) {
        return this.withCredits(this.credits + amount);
    }

    public CommandSourceData takeCredits(final int amount) {
        return this.withCredits(this.credits - amount);
    }

    public CommandSourceData redeemCredits(final int amount) {
        return new CommandSourceData(this.uuid, this.username, this.credits - amount, this.redeemed + amount);
    }

    public CommandSourceData withName(final String username) {
        return new CommandSourceData(this.uuid, username, this.credits, this.redeemed);
    }

    @Override
    public String toString() {
        return "%s,%s,%s,%s".formatted(this.uuid, this.username, this.credits, this.redeemed);
    }

    public Map<String, TagResolver.Single> resolvers(final String prefix) {
        String[] ev = this.toString().split(",");
        Map<String, TagResolver.Single> resolver = new HashMap<>();
        IntStream.range(0, ev.length).forEach(i -> {
            String key = prefix + "_" + TAGS[i];
            resolver.put(key, Placeholder.parsed(key, ev[i]));
        });
        return resolver;
    }
}
