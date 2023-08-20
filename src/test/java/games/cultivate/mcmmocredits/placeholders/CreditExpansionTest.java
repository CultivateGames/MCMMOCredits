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
package games.cultivate.mcmmocredits.placeholders;

import games.cultivate.mcmmocredits.database.DatabaseUtil;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.replacer.CharsReplacer;
import me.clip.placeholderapi.replacer.Replacer;
import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class CreditExpansionTest {
    private final Replacer replace = new CharsReplacer(Replacer.Closure.PERCENT);
    private User user;
    private UserService service;
    private Map<String, PlaceholderExpansion> map;

    @BeforeEach
    void setUp() {
        this.user = new User(new UUID(2, 2), "testUser", 1000, 500);
        this.service = new UserService(DatabaseUtil.create("cs"));
        this.map = new HashMap<>();
        this.map.put("mcmmocredits", new CreditsExpansion(this.service));
    }

    @Test
    void onRequest_ValidUser_ValidPlaceholders() {
        this.service.addUser(this.user).join();
        String content = "%mcmmocredits_credits%, %mcmmocredits_redeemed%, %mcmmocredits_username%, %mcmmocredits_uuid%, %mcmmocredits_cached%";
        String expected = String.format("%s, %s, %s, %s, %s", this.user.credits(), this.user.redeemed(), this.user.username(), this.user.uuid().toString(), this.service.isUserCached(this.user));
        OfflinePlayer player = mock(OfflinePlayer.class);
        doReturn("testUser").when(player).getName();
        assertEquals(expected, this.replace.apply(content, player, this.map::get));
    }

    @Test
    void onRequest_InvalidUser_NoPlaceholders() {
        String content = "%mcmmocredits_credits%, %mcmmocredits_redeemed%, %mcmmocredits_username%, %mcmmocredits_uuid%, %mcmmocredits_cached%";
        String expected = MessageFormat.format("{0}, {0}, {0}, {0}, {0}", "0");
        OfflinePlayer player = mock(OfflinePlayer.class);
        doReturn("testUser").when(player).getName();
        assertEquals(expected, this.replace.apply(content, player, this.map::get));
    }
}
