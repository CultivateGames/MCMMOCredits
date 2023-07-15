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

import games.cultivate.mcmmocredits.transaction.Transaction;
import games.cultivate.mcmmocredits.transaction.TransactionResult;
import games.cultivate.mcmmocredits.transaction.TransactionType;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private final UUID uuid = new UUID(2, 2);
    private final String username = "TestUser";
    private final int credits = 100;
    private User user;
    private UserCache cache;
    private UserService service;
    @Mock
    private UserDAO dao;
    @Mock
    private MockedStatic<Bukkit> mockBukkit;
    @Mock
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        this.user = new User(this.uuid, this.username, this.credits, 50);
        this.cache = new UserCache();
        this.service = new UserService(this.dao, this.cache);
    }

    @Test
    void isCached_UserIsCached_ReturnsTrue() {
        this.cache.add(this.user);
        assertTrue(this.service.isCached(this.user));
    }

    @Test
    void isCached_UserIsNotCached_ReturnsFalse() {
        assertFalse(this.service.isCached(this.user));
    }

    @Test
    void addUser_AddsUserToCacheAndDao() {
        when(this.dao.addUser(any())).thenReturn(true);
        this.service.addUser(this.uuid, this.username);
        assertTrue(this.cache.contains(this.uuid));
        assertTrue(this.cache.contains(this.username));
        verify(this.dao, atLeastOnce()).addUser(any());
    }

    @Test
    void getUser_CachedUser_ReturnsUser() {
        this.cache.add(this.user);
        Optional<User> result = this.service.getUser(this.username);
        assertTrue(result.isPresent());
        assertEquals(this.user, result.get());
    }

    @Test
    void getUser_NotCachedUser_ReturnsUser() {
        when(this.dao.getUser(this.username)).thenReturn(Optional.of(this.user));
        Optional<User> result = this.service.getUser(this.username);
        assertTrue(result.isPresent());
        assertEquals(this.user, result.get());
    }

    @Test
    void getUser_NonExistentUser_ReturnsEmptyOptional() {
        when(this.dao.getUser(this.username)).thenReturn(Optional.empty());
        Optional<User> result = this.service.getUser(this.username);
        assertFalse(result.isPresent());
    }

    @Test
    void setUsername_UsernameUpdated_ReturnsUpdatedUser() {
        this.cache.add(this.user);
        when(this.dao.setUsername(this.uuid, "newUsername")).thenReturn(true);
        User result = this.service.setUsername(this.uuid, "newUsername");
        assertNotNull(result);
        assertEquals("newUsername", result.username());
    }

    @Test
    void setUsername_UsernameNotUpdated_ReturnsNull() {
        this.cache.add(this.user);
        when(this.dao.setUsername(this.uuid, "newUsername")).thenReturn(false);
        assertNull(this.service.setUsername(this.uuid, "newUsername"));
    }

    @Test
    void getCredits_UserIsCached_ReturnsCredits() {
        this.cache.add(this.user);
        assertEquals(this.credits, this.service.getCredits(this.uuid));
    }

    @Test
    void getCredits_UserIsNotCached_ReturnsCredits() {
        when(this.dao.getUser(this.uuid)).thenReturn(Optional.of(this.user));
        assertEquals(this.credits, this.service.getCredits(this.uuid));
    }

    @Test
    void getCredits_UserDoesNotExist_ReturnsZero() {
        when(this.dao.getUser(this.uuid)).thenReturn(Optional.empty());
        int credits = this.service.getCredits(this.uuid);
        assertEquals(0, credits);
    }

    @Test
    void setCredits_UserExists_ReturnsUpdatedUser() {
        this.cache.add(this.user);
        when(this.dao.setCredits(this.uuid, 200)).thenReturn(true);
        assertEquals(200, this.service.setCredits(this.uuid, 200).credits());
    }

    @Test
    void setCredits_UserDoesNotExist_ReturnsNull() {
        when(this.dao.setCredits(this.uuid, 100)).thenReturn(false);
        assertNull(this.service.setCredits(this.uuid, 100));
    }

    @Test
    void processTransaction_UserExists_TransactionAppliedToService() {
        this.cache.add(this.user);
        Transaction transaction = Transaction.builder().self(this.user).amount(1000).type(TransactionType.ADD);
        TransactionResult result = transaction.execute();
        when(this.dao.updateUser(result.target())).thenReturn(true);
        this.service.processTransaction(result);
        assertEquals(this.credits + 1000, this.service.getUser(this.uuid).orElseThrow().credits());
    }

    @Test
    void getPageOfUsers_ReturnsPageOfUsers() {
        List<User> users = List.of(this.user, new User(UUID.randomUUID(), "TestUser", 50, 10));
        when(this.dao.getPageOfUsers(2, 0)).thenReturn(users);
        List<User> result = this.service.getPageOfUsers(2, 0);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(users, result);
    }

    @Test
    void fromSender_SenderIsPlayer_ReturnsUser() {
        this.cache.add(this.user);
        this.mockBukkit.when(() -> Bukkit.getPlayer(this.uuid)).thenReturn(this.mockPlayer);
        when(this.mockPlayer.getUniqueId()).thenReturn(this.uuid);
        CommandExecutor test = this.service.fromSender(this.mockPlayer);
        assertNotNull(test);
        assertTrue(test instanceof User);
        assertEquals(this.user, test);
    }

    @Test
    void fromSender_SenderIsConsole_ReturnsConsole() {
        ConsoleCommandSender sender = mock(ConsoleCommandSender.class);
        CommandExecutor result = this.service.fromSender(sender);
        assertTrue(result instanceof Console);
        assertEquals(Console.INSTANCE, result);
    }

    @Test
    void removeFromCache_UserExists_RemovesFromCache() {
        User testUser = new User(this.uuid, this.username, 100, 0);
        this.cache.add(testUser);
        this.service.removeFromCache(this.uuid);
        assertFalse(this.cache.contains(this.uuid));
        assertFalse(this.cache.contains(this.username));
    }
}
