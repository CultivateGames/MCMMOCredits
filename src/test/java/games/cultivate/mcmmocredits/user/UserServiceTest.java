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

import games.cultivate.mcmmocredits.data.UserDAO;
import games.cultivate.mcmmocredits.util.CreditOperation;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {
    private final String testUsername = "this.testUsername";
    private final UserDAO mockDao = mock(UserDAO.class);
    private UUID testUUID;
    private UserCache cache;
    private UserService service;
    private User user;

    @BeforeEach
    void setUp() {
        this.testUUID = UUID.randomUUID();
        this.cache = new UserCache();
        this.service = new UserService(this.mockDao, this.cache);
        this.user = new User(this.testUUID, this.testUsername, 0, 0);
    }

    @Test
    void isCached_UserIsCached_ReturnsTrue() {
        //Arrange
        this.cache.add(this.user);

        //Act
        boolean isCached = this.service.isCached(this.user);

        //Assert
        assertTrue(isCached);
    }

    @Test
    void isCached_UserIsNotCached_ReturnsFalse() {
        //Act
        boolean isCached = this.service.isCached(user);

        //Assert
        assertFalse(isCached);
    }

    @Test
    void addUser_AddsUserToCacheAndDao() {
        //Arrange
        when(this.mockDao.addUser(any())).thenReturn(true);

        //Act
        this.service.addUser(this.testUUID, this.testUsername);

        //Assert
        assertTrue(this.cache.contains(this.testUUID));
        assertTrue(this.cache.contains(this.testUsername));
        verify(this.mockDao, times(1)).addUser(any());
    }

    @Test
    void getUser_CachedUser_ReturnsUser() {
        //Arrange
        this.cache.add(this.user);

        //Act
        Optional<User> result = this.service.getUser(this.testUsername);

        //Assert
        assertTrue(result.isPresent());
        assertEquals(this.user, result.get());
    }

    @Test
    void getUser_NotCachedUser_ReturnsUser() {
        //Arrange
        when(this.mockDao.getUser(this.testUsername)).thenReturn(Optional.of(this.user));

        //Act
        Optional<User> result = this.service.getUser(this.testUsername);

        //Assert
        assertTrue(result.isPresent());
        assertEquals(this.user, result.get());
    }

    @Test
    void getUser_NonExistentUser_ReturnsEmptyOptional() {
        //Arrange
        when(this.mockDao.getUser(this.testUsername)).thenReturn(Optional.empty());

        //Act
        Optional<User> result = this.service.getUser(this.testUsername);

        //Assert
        assertFalse(result.isPresent());
    }

    @Test
    void setUsername_UsernameUpdated_ReturnsUpdatedUser() {
        //Arrange
        this.cache.add(this.user);
        String newUsername = "newUsername";
        when(this.mockDao.setUsername(this.testUUID, newUsername)).thenReturn(true);

        //Act
        User result = this.service.setUsername(this.testUUID, newUsername);

        //Assert
        assertNotNull(result);
        assertEquals(newUsername, result.username());
    }

    @Test
    void setUsername_UsernameNotUpdated_ReturnsNull() {
        //Arrange
        this.cache.add(this.user);
        String newUsername = "newUsername";
        when(this.mockDao.setUsername(this.testUUID, newUsername)).thenReturn(false);

        //Act
        User result = this.service.setUsername(this.testUUID, newUsername);

        //Assert
        assertNull(result);
    }

    @Test
    void getCredits_UserExists_ReturnsCredits() {
        //Arrange
        User testUser = new User(this.testUUID, this.testUsername, 100, 0);
        this.cache.add(testUser);

        //Act
        int credits = this.service.getCredits(this.testUUID);

        //Assert
        assertEquals(100, credits);
    }

    @Test
    void getCredits_UserDoesNotExist_ReturnsZero() {
        //Arrange
        when(this.mockDao.getUser(this.testUUID)).thenReturn(Optional.empty());

        //Act
        int credits = this.service.getCredits(this.testUUID);

        //Assert
        assertEquals(0, credits);
    }

    @Test
    void modifyCredits_OperationSuccessful_ReturnsUpdatedUser() {
        //Arrange
        User testUser = new User(this.testUUID, this.testUsername, 100, 0);
        this.cache.add(testUser);
        when(this.mockDao.addCredits(this.testUUID, 50)).thenReturn(true);

        //Act
        User result = this.service.modifyCredits(this.testUUID, CreditOperation.ADD, 50);

        //Assert
        assertNotNull(result);
        assertEquals(150, result.credits());
    }

    @Test
    void modifyCredits_OperationUnsuccessful_ReturnsNull() {
        //Arrange
        User testUser = new User(this.testUUID, this.testUsername, 100, 0);
        this.cache.add(testUser);
        when(this.mockDao.addCredits(this.testUUID, 50)).thenReturn(false);

        //Act
        User result = this.service.modifyCredits(this.testUUID, CreditOperation.ADD, 50);

        //Assert
        assertNull(result);
    }

    @Test
    void redeemCredits_RedemptionSuccessful_ReturnsUpdatedUser() {
        //Arrange
        User testUser = new User(this.testUUID, this.testUsername, 100, 0);
        this.cache.add(testUser);
        when(this.mockDao.redeemCredits(this.testUUID, 50)).thenReturn(true);

        //Act
        User result = this.service.redeemCredits(this.testUUID, 50);

        //Assert
        assertNotNull(result);
        assertEquals(50, result.credits());
        assertEquals(50, result.redeemed());
    }

    @Test
    void redeemCredits_RedemptionUnsuccessful_ReturnsNull() {
        //Arrange
        User testUser = new User(this.testUUID, this.testUsername, 100, 0);
        this.cache.add(testUser);
        when(this.mockDao.redeemCredits(this.testUUID, 50)).thenReturn(false);
        //Act
        User result = this.service.redeemCredits(this.testUUID, 50);

        //Assert
        assertNull(result);
    }

    @Test
    void getPageOfUsers_ReturnsPageOfUsers() {
        //Arrange
        List<User> users = Arrays.asList(
                new User(this.testUUID, this.testUsername, 100, 0),
                new User(UUID.randomUUID(), "user2", 50, 10)
        );
        when(this.mockDao.getPageOfUsers(2, 0)).thenReturn(users);

        //Act
        List<User> result = this.service.getPageOfUsers(2, 0);

        //Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(users, result);
    }

    @Test
    void fromSender_SenderIsPlayer_ReturnsUser() {
        //Arrange
        this.cache.add(this.user);
        try (MockedStatic<Bukkit> mockedBukkit = Mockito.mockStatic(Bukkit.class)) {
            Player player = mock(Player.class);
            when(player.getUniqueId()).thenReturn(this.testUUID);
            CommandSender sender = player;
            mockedBukkit.when(() -> Bukkit.getPlayer(this.testUUID)).thenReturn(player);

            //Act
            CommandExecutor test = this.service.fromSender(sender);

            //Assert
            assertNotNull(test);
            assertTrue(test instanceof User);
            assertEquals(this.user, test);
        }
    }

    @Test
    void fromSender_SenderIsConsole_ReturnsConsole() {
        //Arrange
        CommandSender sender = mock(CommandSender.class);

        //Act
        CommandExecutor result = this.service.fromSender(sender);

        //Assert
        assertTrue(result instanceof Console);
        assertEquals(Console.INSTANCE, result);
    }

    @Test
    void removeFromCache_UserExists_RemovesFromCache() {
        //Arrange
        User testUser = new User(this.testUUID, this.testUsername, 100, 0);
        this.cache.add(testUser);

        //Act
        this.service.removeFromCache(this.testUUID, this.testUsername);

        //Assert
        assertFalse(this.cache.contains(this.testUUID));
        assertFalse(this.cache.contains(this.testUsername));
    }
}
