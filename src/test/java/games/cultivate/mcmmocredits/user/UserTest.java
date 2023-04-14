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
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserTest {
    private UUID testUUID;
    private User user;

    @BeforeEach
    void setUp() {
        this.testUUID = UUID.randomUUID();
        this.user = new User(this.testUUID, "TestUser", 100, 50);
    }

    @Test
    void isPlayer_ReturnsTrue() {
        //Assert
        assertTrue(this.user.isPlayer());
    }

    @Test
    void isConsole_ReturnsFalse() {
        //Assert
        assertFalse(this.user.isConsole());
    }

    @Test
    void sender_ReturnsPlayer() {
        //Arrange
        try (MockedStatic<Bukkit> mockedBukkit = Mockito.mockStatic(Bukkit.class)) {
            Player player = mock(Player.class);
            mockedBukkit.when(() -> Bukkit.getPlayer(this.testUUID)).thenReturn(player);

            //Act
            CommandSender test = this.user.sender();

            //Assert
            assertNotNull(test);
            assertTrue(test instanceof Player);
        }
    }

    @Test
    void player_ReturnsPlayerWithSameUUID() {
        //Arrange
        try (MockedStatic<Bukkit> mockedBukkit = Mockito.mockStatic(Bukkit.class)) {
            Player player = mock(Player.class);
            when(player.getUniqueId()).thenReturn(this.testUUID);
            mockedBukkit.when(() -> Bukkit.getPlayer(this.testUUID)).thenReturn(player);

            //Act
            Player returnedPlayer = this.user.player();

            //Assert
            assertNotNull(returnedPlayer);
            assertEquals(this.testUUID, returnedPlayer.getUniqueId());
        }
    }

    @Test
    void withCredits_ReturnsUserWithUpdatedCredits() {
        //Arrange
        int newCredits = 200;

        //Act
        User updatedUser = this.user.withCredits(newCredits);

        //Assert
        assertEquals(this.user.uuid(), updatedUser.uuid());
        assertEquals(this.user.username(), updatedUser.username());
        assertEquals(newCredits, updatedUser.credits());
        assertEquals(this.user.redeemed(), updatedUser.redeemed());
    }

    @Test
    void withUsername_ReturnsUserWithUpdatedUsername() {
        //Arrange
        String newUsername = "UpdatedUser";

        //Act
        User updatedUser = this.user.withUsername(newUsername);

        //Assert
        assertEquals(this.user.uuid(), updatedUser.uuid());
        assertEquals(newUsername, updatedUser.username());
        assertEquals(this.user.credits(), updatedUser.credits());
        assertEquals(this.user.redeemed(), updatedUser.redeemed());
    }

    @Test
    void withRedeemed_ReturnsUserWithUpdatedRedeemed() {
        //Arrange
        int newRedeemed = 75;

        //Act
        User updatedUser = this.user.withRedeemed(newRedeemed);

        //Assert
        assertEquals(this.user.uuid(), updatedUser.uuid());
        assertEquals(this.user.username(), updatedUser.username());
        assertEquals(this.user.credits(), updatedUser.credits());
        assertEquals(newRedeemed, updatedUser.redeemed());
    }
}
