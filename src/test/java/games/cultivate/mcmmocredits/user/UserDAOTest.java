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

import org.jdbi.v3.core.h2.H2DatabasePlugin;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.testing.junit5.JdbiExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDAOTest {
    private final String username = "testUsername";
    private final UUID uuid = UUID.randomUUID();
    private final int credits = 60;
    private final int redeemed = 500;
    private final String query = "CREATE TABLE IF NOT EXISTS MCMMOCredits(id INTEGER PRIMARY KEY AUTO_INCREMENT, UUID VARCHAR(36) NOT NULL, username VARCHAR(16) NOT NULL, credits INT CHECK(credits >= 0), redeemed INT);";
    @RegisterExtension
    private final JdbiExtension jdbi = JdbiExtension.h2().withPlugins(new SqlObjectPlugin(), new H2DatabasePlugin()).withInitializer((x, y) -> y.execute(this.query));
    private UserDAO dao;
    private User user;

    @BeforeEach
    void setUp() {
        this.dao = this.jdbi.getJdbi().onDemand(UserDAO.class);
        this.user = new User(this.uuid, this.username, this.credits, this.redeemed);
        this.dao.addUser(this.user);
    }

    @AfterEach
    void tearDown() {
        this.jdbi.getSharedHandle().execute("DROP TABLE MCMMOCredits");
    }

    @Test
    void addUser_NewUser_UserAdded() {
        Optional<User> ouser = this.dao.getUser(this.uuid);
        assertTrue(ouser.isPresent());
        assertEquals(this.user, ouser.orElseThrow());
    }

    @Test
    void addUsers_NewUsers_UsersAdded() {
        User first = new User(UUID.randomUUID(), "tester1", 1000, 10);
        User second = new User(UUID.randomUUID(), "tester2", 2000, 20);
        this.dao.addUsers(List.of(first, second));
        assertEquals(first, this.dao.getUser(first.uuid()).orElseThrow());
        assertEquals(second, this.dao.getUser(second.uuid()).orElseThrow());
    }

    @Test
    void getAllUsers_ReturnAllUsers() {
        User first = new User(UUID.randomUUID(), "tester1", 1000, 10);
        User second = new User(UUID.randomUUID(), "tester2", 2000, 20);
        this.dao.addUsers(List.of(first, second));
        assertTrue(this.dao.getAllUsers().containsAll(List.of(first, second, this.user)));
    }

    @Test
    void getUser_ByUsername_UserFound() {
        Optional<User> ouser = this.dao.getUser(this.username);
        assertTrue(ouser.isPresent());
        assertEquals(this.user, ouser.orElseThrow());
    }

    @Test
    void getPageOfUsers_ThreeUsers_UsersRetrieved() {
        User first = new User(UUID.randomUUID(), "firstPlace", 1000, 10);
        User second = new User(UUID.randomUUID(), "secondPlace", 100, 10);
        this.dao.addUsers(List.of(first, second));
        List<User> users = this.dao.getPageOfUsers(3, 0);
        assertEquals(3, users.size());
        assertEquals(first, users.get(0));
        assertEquals(second, users.get(1));
        assertEquals(this.user, users.get(2));
    }

    @Test
    void setUsername_ExistingUser_UsernameUpdated() {
        String newUsername = "updatedUsername";
        boolean result = this.dao.setUsername(this.uuid, newUsername);
        Optional<User> ouser = this.dao.getUser(newUsername);
        User updatedUser = new User(this.uuid, newUsername, this.credits, this.redeemed);
        assertTrue(result);
        assertTrue(ouser.isPresent());
        assertEquals(updatedUser, ouser.orElseThrow());
    }

    @Test
    void setUsername_MissingUser_ReturnsFalse() {
        assertFalse(this.dao.setUsername(UUID.randomUUID(), "missingUser"));
    }

    @Test
    void setCredits_ExistingUser_CreditsUpdated() {
        int newCredits = 200;
        boolean result = this.dao.setCredits(this.uuid, newCredits);
        Optional<User> ouser = this.dao.getUser(this.uuid);
        User updatedUser = new User(this.uuid, this.username, newCredits, this.redeemed);
        assertTrue(result);
        assertTrue(ouser.isPresent());
        assertEquals(updatedUser, ouser.orElseThrow());
    }

    @Test
    void setCredits_MissingUser_ReturnsFalse() {
        assertFalse(this.dao.setCredits(UUID.randomUUID(), 500));
    }

    @Test
    void setCredits_InvalidAmount_ThrowsException() {
        assertThrows(UnableToExecuteStatementException.class, () -> this.dao.setCredits(this.uuid, -1));
        assertEquals(this.user, this.dao.getUser(this.uuid).orElseThrow());
    }

    @Test
    void updateUser_ExistingUser_ReturnsUpdatedUser() {
        this.dao.updateUser(new User(this.uuid, this.username, 10000, this.redeemed));
        User fromDAO = this.dao.getUser(this.uuid).orElseThrow();
        assertNotEquals(this.user, fromDAO);
        assertEquals(10000, fromDAO.credits());
    }
}
