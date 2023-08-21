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

import games.cultivate.mcmmocredits.database.AbstractDatabase;
import games.cultivate.mcmmocredits.database.DatabaseUtil;
import games.cultivate.mcmmocredits.transaction.AddTransaction;
import games.cultivate.mcmmocredits.transaction.Transaction;
import games.cultivate.mcmmocredits.transaction.TransactionResult;
import games.cultivate.mcmmocredits.util.MojangUtil;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private final AbstractDatabase database = DatabaseUtil.create("us");
    private User user;
    private UserService service;
    @Mock
    private MockedStatic<Bukkit> mockBukkit;
    @Mock
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        this.user = new User(UUID.randomUUID(), "Tester", 100, 50);
        this.service = new UserService(this.database);
    }

    @AfterEach
    void tearDown() {
        this.database.jdbi().useHandle(x -> x.execute("DELETE FROM MCMMOCredits"));
    }

    @Test
    void isUserCached_UserIsCached_ReturnsTrue() {
        this.service.addUser(this.user).join();
        assertTrue(this.service.isUserCached(this.user));
    }

    @Test
    void isUserCached_UserIsNotCached_ReturnsFalse() {
        assertFalse(this.service.isUserCached(this.user));
    }

    @Test
    void addUser_AddsUserToCacheAndDatabase() {
        this.service.addUser(this.user).join();
        assertTrue(this.service.isUserCached(this.user));
    }

    @Test
    void getUser_CachedUser_ReturnsUser() {
        this.service.addUser(this.user).join();
        Optional<User> result = this.service.getUser(this.user.uuid()).join();
        assertEquals(this.user, result.get());
    }

    @Test
    void getUser_NotCachedUser_ReturnsUser() {
        this.database.addUser(this.user).join();
        Optional<User> result = this.service.getUser(this.user.uuid()).join();
        assertEquals(this.user, result.get());
    }

    @Test
    void getUser_NonExistentUser_ReturnsEmptyOptional() {
        Optional<User> result = this.service.getUser(this.user.uuid()).join();
        assertFalse(result.isPresent());
    }

    @Test
    void setUsername_UsernameUpdated_ReturnsUpdatedUser() {
        this.database.addUser(this.user).join();
        this.service.setUsername(this.user.uuid(), "newUsername").join();
        assertEquals("newUsername", this.service.getUser(this.user.uuid()).join().get().username());
    }

    @Test
    void getCredits_UserIsCached_ReturnsCredits() {
        this.service.addUser(this.user).join();
        assertEquals(100, this.service.getCredits(this.user.uuid()).join());
    }

    @Test
    void getCredits_UserIsNotCached_ReturnsCredits() {
        this.service.addUser(this.user).join();
        assertEquals(100, this.service.getCredits(this.user.uuid()).join());
    }

    @Test
    void getCredits_UserDoesNotExist_ReturnsZero() {
        int credits = this.service.getCredits(this.user.uuid()).join();
        assertEquals(0, credits);
    }

    @Test
    void setCredits_UserExists_ReturnsUpdatedUser() {
        this.service.addUser(this.user).join();
        this.service.setCredits(this.user.uuid(), 200).join();
        assertEquals(200, this.service.getUser(this.user.uuid()).join().get().credits());
    }

    @Test
    void processTransaction_UserExists_TransactionAppliedToService() {
        this.service.addUser(this.user).join();
        Transaction transaction = new AddTransaction(this.user, List.of(this.user), 1000);
        TransactionResult result = transaction.execute();
        this.service.processTransaction(result).join();
        assertEquals(1100, this.service.getUser(this.user.uuid()).join().orElseThrow().credits());
    }

    @Test
    void rangeOfUsers_ReturnsPageOfUsers() {
        User tester = new User(UUID.randomUUID(), "TestUser2", 50, 10);
        List<User> users = List.of(this.user, tester);
        this.service.addUser(this.user).join();
        this.service.addUser(tester).join();
        List<User> result = this.service.rangeOfUsers(2, 0).join();
        assertEquals(users, result);
    }

    @Test
    void fromSender_SenderIsPlayer_ReturnsUser() {
        this.service.addUser(this.user).join();
        this.mockBukkit.when(() -> Bukkit.getPlayer(this.user.uuid())).thenReturn(this.mockPlayer);
        when(this.mockPlayer.getUniqueId()).thenReturn(this.user.uuid());
        CommandExecutor test = this.service.fromSender(this.mockPlayer);
        assertEquals(this.user, test);
    }

    @Test
    void fromSender_SenderIsConsole_ReturnsConsole() {
        ConsoleCommandSender sender = mock(ConsoleCommandSender.class);
        CommandExecutor result = this.service.fromSender(sender);
        assertEquals(Console.INSTANCE, result);
    }

    @Test
    void removeFromCache_UserExists_RemovesFromCache() {
        this.service.addUser(this.user).join();
        this.service.removeFromCache(this.user.uuid());
        assertFalse(this.service.isUserCached(this.user));
    }

    @Test
    void setUsername_UsernameConflict_OlderUserReplaced() {
        //jeb_: 853c80ef-3c37-49fd-aa49-938b674adae6
        //Notch: 069a79f4-44e9-4726-a5be-fca90e38aaf5
        User notch = new User(UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5"), "jeb_", 100, 100);
        User jeb = new User(UUID.fromString("853c80ef-3c37-49fd-aa49-938b674adae6"), "Notch", 500, 500);
        this.database.addUser(notch).join();
        this.database.addUser(jeb).join();
        this.service.setUsername(jeb.uuid(), "jeb_").join();
        this.service.setUsername(notch.uuid(), "Notch").join();
        assertEquals("Notch", this.service.getUser(notch.uuid()).join().get().username());
        assertEquals("jeb_", this.service.getUser(jeb.uuid()).join().get().username());
    }

    @Test
    void setUsername_CaseSensitivityIssue_OlderUserUpdated() {
        UUID fakeUUID = UUID.fromString("853c80ef-3c37-49fd-aa49-938b674adae6");
        User fakeNotch = new User(fakeUUID, "nOtCh", 100, 100);
        this.database.addUser(fakeNotch).join();
        UUID originalUUID = UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5");
        this.database.addUser(new User(originalUUID, "notNotch", 100, 10)).join();
        this.service.setUsername(originalUUID, "Notch").join();
        Optional<User> updatedFakeUser = this.service.getUser(fakeUUID).join();
        String username = MojangUtil.getName(fakeUUID);
        assertEquals(username, updatedFakeUser.get().username());
        Optional<User> originalUser = this.service.getUser(originalUUID).join();
        assertEquals("Notch", originalUser.get().username());
    }

    @Test
    void getOnlineUsers_ValidPlayers_GetsAll() {
        this.service.addUser(this.user);
        this.mockBukkit.when(() -> Bukkit.getPlayer(this.user.uuid())).thenReturn(this.mockPlayer);
        this.mockBukkit.when(Bukkit::getOnlinePlayers).thenReturn(List.of(this.mockPlayer));
        when(this.mockPlayer.getUniqueId()).thenReturn(this.user.uuid());
        assertEquals(this.user.uuid(), this.service.getOnlineUsers().join().get(0).uuid());
    }
}
