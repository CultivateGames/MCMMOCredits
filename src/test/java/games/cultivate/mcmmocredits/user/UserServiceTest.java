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

import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.database.DatabaseUtil;
import games.cultivate.mcmmocredits.transaction.Transaction;
import games.cultivate.mcmmocredits.transaction.TransactionResult;
import games.cultivate.mcmmocredits.transaction.TransactionType;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private final UUID uuid = new UUID(2, 2);
    private final String username = "TestUser";
    private final int credits = 100;
    private User user;
    private UserService service;
    private final Database database = DatabaseUtil.create();
    @Mock
    private MockedStatic<Bukkit> mockBukkit;
    @Mock
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        this.user = new User(this.uuid, this.username, this.credits, 50);
        this.service = new UserService(this.database);
    }

    @AfterEach
    void tearDown() {
        this.database.jdbi().useHandle(x -> x.execute("DELETE FROM MCMMOCredits"));
    }

    @Test
    void isUserCached_UserIsCached_ReturnsTrue() {
        this.service.addUser(this.user);
        assertTrue(this.service.isUserCached(this.user));
    }

    @Test
    void isUserCached_UserIsNotCached_ReturnsFalse() {
        assertFalse(this.service.isUserCached(this.user));
    }

    @Test
    void addUser_AddsUserToCacheAndDatabase() {
        this.service.addUser(this.user);
        assertTrue(this.service.isUserCached(this.user));
    }

    @Test
    void getUser_CachedUser_ReturnsUser() {
        this.service.addUser(this.user);
        Optional<User> result = this.service.getUser(this.username);
        assertTrue(result.isPresent());
        assertEquals(this.user, result.get());
    }

    @Test
    void getUser_NotCachedUser_ReturnsUser() {
        this.database.addUser(this.user);
        Optional<User> result = this.service.getUser(this.username);
        assertTrue(result.isPresent());
        assertEquals(this.user, result.get());
    }

    @Test
    void getUser_NonExistentUser_ReturnsEmptyOptional() {
        Optional<User> result = this.service.getUser(this.username);
        assertFalse(result.isPresent());
    }

    @Test
    void setUsername_UsernameUpdated_ReturnsUpdatedUser() {
        UUID uuidtest = UUID.randomUUID();
        this.service.addUser(new User(uuidtest, "new1", 100, 100));
        User result = this.service.setUsername(uuidtest, "newUsername");
        assertEquals("newUsername", result.username());
    }

    @Test
    void setUsername_UsernameNotUpdated_ReturnsNull() {
        assertNull(this.service.setUsername(this.uuid, "newUsername"));
    }

    @Test
    void getCredits_UserIsCached_ReturnsCredits() {
        this.service.addUser(this.user);
        assertEquals(this.credits, this.service.getCredits(this.uuid));
    }

    @Test
    void getCredits_UserIsNotCached_ReturnsCredits() {
        this.service.addUser(this.user);
        assertEquals(this.credits, this.service.getCredits(this.uuid));
    }

    @Test
    void getCredits_UserDoesNotExist_ReturnsZero() {
        int credits = this.service.getCredits(this.uuid);
        assertEquals(0, credits);
    }

    @Test
    void setCredits_UserExists_ReturnsUpdatedUser() {
        this.service.addUser(this.user);
        assertEquals(200, this.service.setCredits(this.uuid, 200).credits());
    }

    @Test
    void setCredits_UserDoesNotExist_ReturnsNull() {
        assertNull(this.service.setCredits(this.uuid, 100));
    }

    @Test
    void processTransaction_UserExists_TransactionAppliedToService() {
        this.service.addUser(this.user);
        Transaction transaction = Transaction.builder().self(this.user).amount(1000).type(TransactionType.ADD).build();
        TransactionResult result = transaction.execute();
        this.service.processTransaction(result);
        assertEquals(this.credits + 1000, this.service.getUser(this.uuid).orElseThrow().credits());
    }

    @Test
    void rangeOfUsers_ReturnsPageOfUsers() {
        User tester = new User(UUID.randomUUID(), "TestUser", 50, 10);
        List<User> users = List.of(this.user, tester);
        this.service.addUser(this.user);
        this.service.addUser(tester);
        List<User> result = this.service.rangeOfUsers(2, 0);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(users, result);
    }

    @Test
    void fromSender_SenderIsPlayer_ReturnsUser() {
        this.service.addUser(this.user);
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
        this.service.addUser(testUser);
        this.service.removeUser(this.uuid);
        assertFalse(this.service.isUserCached(testUser));
    }
}
