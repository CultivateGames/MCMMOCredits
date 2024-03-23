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
package games.cultivate.mcmmocredits.storage;

import games.cultivate.mcmmocredits.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StorageServiceTest {
    private final String username = "testUsername";
    private final UUID uuid = UUID.randomUUID();
    private final int credits = 60;
    private final int redeemed = 500;
    private final TestStorageService database = TestStorageService.create("test");
    private User user;

    @BeforeEach
    void setUp() {
        this.user = new User(this.uuid, this.username, this.credits, this.redeemed);
        this.database.addUser(this.user);
    }

    @AfterEach
    void tearDown() {
        this.database.delete();
    }

    @Test
    void addUser_NewUser_UserAdded() {
        Optional<User> ouser = this.database.getUser(this.uuid);
        assertEquals(this.user, ouser.orElseThrow());
    }

    @Test
    void addUsers_NewUsers_UsersAdded() {
        User first = new User(UUID.randomUUID(), "tester1", 1000, 10);
        User second = new User(UUID.randomUUID(), "tester2", 2000, 20);
        this.database.addUsers(List.of(first, second));
        assertEquals(first, this.database.getUser(first.uuid()).orElseThrow());
        assertEquals(second, this.database.getUser(second.uuid()).orElseThrow());
    }

    @Test
    void getAllUsers_ReturnAllUsers() {
        User first = new User(UUID.randomUUID(), "tester1", 1000, 10);
        User second = new User(UUID.randomUUID(), "tester2", 2000, 20);
        this.database.addUsers(List.of(first, second));
        assertTrue(this.database.getAllUsers().containsAll(List.of(first, second, this.user)));
    }

    @Test
    void getUser_ByUsername_UserFound() {
        Optional<User> ouser = this.database.getUser(this.username);
        assertEquals(this.user, ouser.orElseThrow());
    }

    @Test
    void getUser_MissingUser_ReturnsEmpty() {
        assertEquals(Optional.empty(), this.database.getUser(UUID.randomUUID()));
    }

    @Test
    void getUserGroup_ThreeUsers_UsersRetrieved() {
        User first = new User(UUID.randomUUID(), "firstPlace", 1000, 10);
        User second = new User(UUID.randomUUID(), "secondPlace", 100, 10);
        this.database.addUsers(List.of(first, second));
        List<User> users = this.database.getUserGroup(3, 0);
        assertTrue(users.containsAll(List.of(first, second, this.user)));
    }

//TODO: bring back when methods are implemented.
//    @Test
//    void setUsername_ExistingUser_UsernameUpdated() {
//        String newUsername = "updatedUsername";
//        this.database.setUsername(this.uuid, newUsername);
//        Optional<User> ouser = this.database.getUser(newUsername);
//        User updatedUser = new User(this.uuid, newUsername, this.credits, this.redeemed);
//        assertEquals(updatedUser, ouser.orElseThrow());
//    }
//
//    @Test
//    void setUsername_MissingUser_ReturnsFalse() {
//        assertFalse(this.database.setUsername(UUID.randomUUID(), "missingUser"));
//    }
//
//    @Test
//    void setCredits_ExistingUser_CreditsUpdated() {
//        int newCredits = 200;
//        this.database.setCredits(this.uuid, newCredits);
//        Optional<User> ouser = this.database.getUser(this.uuid);
//        User updatedUser = new User(this.uuid, this.username, newCredits, this.redeemed);
//        assertEquals(updatedUser, ouser.orElseThrow());
//    }

    @Test
    void updateUser_InvalidAmount_ThrowsException() {
        User error = this.user.withCredits(-1);
        assertThrows(SQLException.class, () -> this.database.updateUser(error));
        assertEquals(this.user, this.database.getUser(this.uuid).orElseThrow());
    }

    @Test
    void updateUser_ExistingUser_ReturnsUpdatedUser() {
        this.database.updateUser(new User(this.uuid, this.username, 10000, this.redeemed));
        User fromDAO = this.database.getUser(this.uuid).orElseThrow();
        assertEquals(10000, fromDAO.credits());
    }
}