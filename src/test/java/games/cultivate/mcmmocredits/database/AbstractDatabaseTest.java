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
package games.cultivate.mcmmocredits.database;

import games.cultivate.mcmmocredits.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractDatabaseTest {
    private final String username = "testUsername";
    private final UUID uuid = UUID.randomUUID();
    private final int credits = 60;
    private final int redeemed = 500;
    private final AbstractDatabase database = DatabaseUtil.create("test");
    private User user;

    @BeforeEach
    void setUp() {
        this.user = new User(this.uuid, this.username, this.credits, this.redeemed);
        this.database.addUser(this.user).join();
    }

    @AfterEach
    void tearDown() {
        this.database.jdbi().useHandle(x -> x.execute("DELETE FROM MCMMOCredits"));
    }

    @Test
    void addUser_NewUser_UserAdded() {
        CompletableFuture<Optional<User>> ouser = this.database.getUser(this.uuid);
        assertEquals(this.user, ouser.join().orElseThrow());
    }

    @Test
    void addUsers_NewUsers_UsersAdded() {
        User first = new User(UUID.randomUUID(), "tester1", 1000, 10);
        User second = new User(UUID.randomUUID(), "tester2", 2000, 20);
        this.database.addUsers(List.of(first, second)).join();
        assertEquals(first, this.database.getUser(first.uuid()).join().orElseThrow());
        assertEquals(second, this.database.getUser(second.uuid()).join().orElseThrow());
    }

    @Test
    void getAllUsers_ReturnAllUsers() {
        User first = new User(UUID.randomUUID(), "tester1", 1000, 10);
        User second = new User(UUID.randomUUID(), "tester2", 2000, 20);
        this.database.addUsers(List.of(first, second)).join();
        assertTrue(this.database.getAllUsers().join().containsAll(List.of(first, second, this.user)));
    }

    @Test
    void getUser_ByUsername_UserFound() {
        CompletableFuture<Optional<User>> ouser = this.database.getUser(this.username);
        assertEquals(this.user, ouser.join().orElseThrow());
    }

    @Test
    void rangeOfUsers_ThreeUsers_UsersRetrieved() {
        User first = new User(UUID.randomUUID(), "firstPlace", 1000, 10);
        User second = new User(UUID.randomUUID(), "secondPlace", 100, 10);
        this.database.addUsers(List.of(first, second)).join();
        List<User> users = this.database.rangeOfUsers(3, 0).join();
        assertTrue(users.containsAll(List.of(first, second, this.user)));
    }

    @Test
    void setUsername_ExistingUser_UsernameUpdated() {
        String newUsername = "updatedUsername";
        this.database.setUsername(this.uuid, newUsername).join();
        Optional<User> ouser = this.database.getUser(newUsername).join();
        User updatedUser = new User(this.uuid, newUsername, this.credits, this.redeemed);
        assertEquals(updatedUser, ouser.orElseThrow());
    }

    @Test
    void setUsername_MissingUser_ReturnsFalse() {
        assertFalse(this.database.setUsername(UUID.randomUUID(), "missingUser").join());
    }

    @Test
    void setCredits_ExistingUser_CreditsUpdated() {
        int newCredits = 200;
        this.database.setCredits(this.uuid, newCredits).join();
        CompletableFuture<Optional<User>> ouser = this.database.getUser(this.uuid);
        User updatedUser = new User(this.uuid, this.username, newCredits, this.redeemed);
        assertEquals(updatedUser, ouser.join().orElseThrow());
    }

    @Test
    void setCredits_MissingUser_ReturnsFalse() {
        assertFalse(this.database.setCredits(UUID.randomUUID(), 500).join());
    }

    @Test
    void setCredits_InvalidAmount_ThrowsException() {
        assertThrows(CompletionException.class, this.database.setCredits(this.uuid, -1)::join);
        assertEquals(this.user, this.database.getUser(this.uuid).join().orElseThrow());
    }

    @Test
    void updateUser_ExistingUser_ReturnsUpdatedUser() {
        this.database.updateUser(new User(this.uuid, this.username, 10000, this.redeemed)).join();
        User fromDAO = this.database.getUser(this.uuid).join().orElseThrow();
        assertEquals(10000, fromDAO.credits());
    }
}
