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

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDAOTest {
    private Jdbi jdbi;
    private UserDAO userDAO;
    private String username;
    private UUID uuid;
    private User user;

    @BeforeEach
    void setUp() {
        this.jdbi = Jdbi.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        this.jdbi.installPlugin(new SqlObjectPlugin());
        this.jdbi.useHandle(x -> x.execute("CREATE TABLE IF NOT EXISTS MCMMOCredits(id INTEGER PRIMARY KEY AUTO_INCREMENT, UUID VARCHAR NOT NULL, username VARCHAR NOT NULL, credits INT CHECK(credits >= 0), redeemed INT);"));
        this.userDAO = jdbi.onDemand(UserDAO.class);
        this.username = "testUsername";
        this.uuid = UUID.randomUUID();
        int credits = 60;
        int redeemed = 500;
        this.user = new User(this.uuid, this.username, credits, redeemed);
        this.userDAO.addUser(this.user);
    }

    @AfterEach
    void tearDown() {
        this.jdbi.useHandle(x -> x.execute("DROP TABLE MCMMOCredits"));
    }

    @Test
    void addUser_NewUser_UserAdded() {
        // Act
        Optional<User> optionalUser = this.userDAO.getUser(this.uuid);

        // Assert
        assertTrue(optionalUser.isPresent());
        assertEquals(this.user, optionalUser.get());
    }

    @Test
    void getUser_ByUsername_UserFound() {
        // Act
        Optional<User> optionalUser = this.userDAO.getUser(this.username);

        // Assert
        assertTrue(optionalUser.isPresent());
        assertEquals(this.user, optionalUser.get());
    }

    @Test
    void setUsername_ExistingUser_UsernameUpdated() {
        // Arrange
        String newUsername = "updatedUsername";

        // Act
        boolean setUsernameResult = this.userDAO.setUsername(this.uuid, newUsername);
        Optional<User> optionalUser = this.userDAO.getUser(newUsername);

        // Assert
        assertTrue(setUsernameResult);
        assertTrue(optionalUser.isPresent());
        assertEquals(newUsername, optionalUser.get().username());
    }

    @Test
    void setCredits_ExistingUser_CreditsUpdated() {
        // Arrange
        int newCredits = 200;

        // Act
        boolean setCreditsResult = this.userDAO.setCredits(this.uuid, newCredits);
        int updatedCredits = this.userDAO.getCredits(this.uuid);

        // Assert
        assertTrue(setCreditsResult);
        assertEquals(newCredits, updatedCredits);
    }

    @Test
    void addCredits_ExistingUser_CreditsIncreased() {
        // Arrange
        int addedCredits = 50;

        // Act
        boolean addCreditsResult = this.userDAO.addCredits(this.uuid, addedCredits);
        int updatedCredits = this.userDAO.getCredits(this.uuid);

        // Assert
        assertTrue(addCreditsResult);
        assertEquals(this.user.credits() + addedCredits, updatedCredits);
    }

    @Test
    void takeCredits_ExistingUser_CreditsDecreased() {
        // Arrange
        int takenCredits = 50;

        // Act
        boolean takeCreditsResult = this.userDAO.takeCredits(this.uuid, takenCredits);
        int updatedCredits = this.userDAO.getCredits(this.uuid);

        // Assert
        assertTrue(takeCreditsResult);
        assertEquals(this.user.credits() - takenCredits, updatedCredits);
    }

    @Test
    void redeemCredits_ExistingUser_CreditsRedeemed() {
        // Arrange
        int redeemedCredits = 50;

        // Act
        boolean redeemCreditsResult = this.userDAO.redeemCredits(this.uuid, redeemedCredits);
        int updatedCredits = this.userDAO.getCredits(this.uuid);
        Optional<User> updatedUser = this.userDAO.getUser(this.uuid);

        // Assert
        assertTrue(redeemCreditsResult);
        assertTrue(updatedUser.isPresent());
        assertEquals(this.user.credits() - redeemedCredits, updatedCredits);
        assertEquals(this.user.redeemed() + redeemedCredits, updatedUser.get().redeemed());
    }

    @Test
    void getPageOfUsers_ThreeUsers_UsersRetrieved() {
        //Arrange
        User firstPlace = new User(UUID.randomUUID(), "firstPlace", 1000, 10);
        User secondPlace = new User(UUID.randomUUID(), "secondPlace", 100, 10);
        this.userDAO.addUser(firstPlace);
        this.userDAO.addUser(secondPlace);

        // Act
        List<User> users = this.userDAO.getPageOfUsers(3, 0);

        // Assert
        assertNotNull(users);
        assertEquals(3, users.size());
        assertEquals(firstPlace, users.get(0));
        assertEquals(secondPlace, users.get(1));
        assertEquals(this.user, users.get(2));
    }
}
