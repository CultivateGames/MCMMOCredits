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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserTest {
    private final UUID uuid = new UUID(2, 2);
    private final String username = "TestUser";
    private final int credits = 100;
    private final int redeemed = 50;
    private User user;
    @Mock
    private MockedStatic<Bukkit> mockBukkit;
    @Mock
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        this.user = new User(this.uuid, this.username, this.credits, this.redeemed);
    }

    @Test
    void credits_ReturnsCorrectCredits() {
        assertEquals(this.credits, this.user.credits());
    }

    @Test
    void username_ReturnsCorrectCredits() {
        assertEquals(this.username, this.user.username());
    }

    @Test
    void redeemed_ReturnsCorrectCredits() {
        assertEquals(this.redeemed, this.user.redeemed());
    }

    @Test
    void uuid_ReturnsCorrectCredits() {
        assertEquals(this.uuid, this.user.uuid());
    }

    @Test
    void isPlayer_ReturnsTrue() {
        assertTrue(this.user.isPlayer());
    }

    @Test
    void sender_ReturnsPlayer() {
        this.mockBukkit.when(() -> Bukkit.getPlayer(this.uuid)).thenReturn(this.mockPlayer);
        CommandSender sender = this.user.sender();
        assertNotNull(sender);
        assertTrue(sender instanceof Player);
    }

    @Test
    void player_ReturnsPlayerWithSameUUID() {
        this.mockBukkit.when(() -> Bukkit.getPlayer(this.uuid)).thenReturn(this.mockPlayer);
        when(this.mockPlayer.getUniqueId()).thenReturn(this.uuid);
        Player uplayer = this.user.player();
        assertNotNull(uplayer);
        assertEquals(this.uuid, uplayer.getUniqueId());
    }

    @Test
    void withCredits_ReturnsSameUserWithUpdatedCredits() {
        int newCredits = 200;
        User updatedUser = this.user.withCredits(newCredits);
        assertNotEquals(this.user, updatedUser);
        assertEquals(newCredits, updatedUser.credits());
        assertEquals(this.uuid, updatedUser.uuid());
        assertEquals(this.username, updatedUser.username());
        assertEquals(this.redeemed, updatedUser.redeemed());
    }

    @Test
    void withUsername_ReturnsSameUserWithUpdatedUsername() {
        String newUsername = "UpdatedUser";
        User updatedUser = this.user.withUsername(newUsername);
        assertNotEquals(this.user, updatedUser);
        assertEquals(newUsername, updatedUser.username());
        assertEquals(this.uuid, updatedUser.uuid());
        assertEquals(this.credits, updatedUser.credits());
        assertEquals(this.redeemed, updatedUser.redeemed());
    }

    @Test
    void withRedeemed_ReturnsUserWithUpdatedRedeemed() {
        int newRedeemed = 75;
        User updatedUser = this.user.withRedeemed(newRedeemed);
        assertNotEquals(this.user, updatedUser);
        assertEquals(newRedeemed, updatedUser.redeemed());
        assertEquals(this.uuid, updatedUser.uuid());
        assertEquals(this.username, updatedUser.username());
        assertEquals(this.credits, updatedUser.credits());
    }
}
