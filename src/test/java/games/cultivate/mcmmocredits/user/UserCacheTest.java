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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserCacheTest {
    private UserCache userCache;
    private UUID testUUID;
    private String testUsername;
    private User testUser;

    @BeforeEach
    void setUp() {
        this.userCache = new UserCache();
        this.testUUID = UUID.randomUUID();
        this.testUsername = "testUsername";
        this.testUser = new User(this.testUUID, this.testUsername, 100, 50);
    }

    @Test
    void add_AddsNewUser_UserIsCached() {
        //Act
        this.userCache.add(this.testUser);

        //Assert
        assertTrue(this.userCache.contains(this.testUUID));
        assertTrue(this.userCache.contains(this.testUsername));
    }

    @Test
    void add_AddsUserWithSameUUID_ReplacesExistingUser() {
        //Arrange
        User anotherUserWithSameUUID = new User(this.testUUID, "anotherUsername", 150, 30);
        this.userCache.add(this.testUser);

        //Act
        this.userCache.add(anotherUserWithSameUUID);

        //Assert
        assertFalse(this.userCache.contains(this.testUsername));
        assertTrue(this.userCache.contains(anotherUserWithSameUUID.username()));
        assertTrue(this.userCache.contains(this.testUUID));
    }

    @Test
    void add_AddsUserWithSameUsername_ReplacesExistingUser() {
        //Arrange
        User anotherUserWithSameUsername = new User(UUID.randomUUID(), this.testUsername, 150, 30);
        this.userCache.add(this.testUser);

        //Act
        this.userCache.add(anotherUserWithSameUsername);

        //Assert
        assertFalse(this.userCache.contains(this.testUUID));
        assertTrue(this.userCache.contains(anotherUserWithSameUsername.uuid()));
        assertTrue(this.userCache.contains(this.testUsername));
    }

    @Test
    void add_AddsUserWithSameUUIDAndUsername_ReplacesExistingUser() {
        //Arrange
        User anotherUserWithSameUUIDAndUsername = new User(this.testUUID, this.testUsername, 150, 30);
        this.userCache.add(this.testUser);

        //Act
        this.userCache.add(anotherUserWithSameUUIDAndUsername);

        //Assert
        assertTrue(this.userCache.contains(this.testUUID));
        assertTrue(this.userCache.contains(this.testUsername));
        assertNotEquals(this.testUser, this.userCache.get(this.testUsername));
        assertEquals(anotherUserWithSameUUIDAndUsername, this.userCache.get(this.testUsername));
    }

    @Test
    void add_AddsMultipleUsersToCache() {
        //Arrange
        User anotherUser = new User(UUID.randomUUID(), "anotherUsername", 150, 30);
        this.userCache.add(this.testUser);

        //Act
        this.userCache.add(anotherUser);

        //Assert
        assertTrue(this.userCache.contains(this.testUsername));
        assertTrue(this.userCache.contains(this.testUUID));
        assertTrue(this.userCache.contains(anotherUser.username()));
        assertTrue(this.userCache.contains(anotherUser.uuid()));
    }

    @Test
    void remove_NonExistentUser_NoException() {
        //Arrange/Act (removal), Assert it doesn't throw.
        assertDoesNotThrow(() -> this.userCache.remove(this.testUUID, this.testUsername));
    }

    @Test
    void remove_RemovesUserFromCache() {
        //Arrange
        this.userCache.add(this.testUser);

        //Act
        this.userCache.remove(this.testUUID, this.testUsername);

        //Assert
        assertFalse(this.userCache.contains(this.testUsername));
        assertFalse(this.userCache.contains(this.testUUID));
    }

    @Test
    void get_WithUUID_ReturnsUser() {
        //Arrange
        this.userCache.add(this.testUser);

        //Act
        User retrievedUser = this.userCache.get(this.testUUID);

        //Assert
        assertEquals(this.testUser, retrievedUser);
    }

    @Test
    void get_WithUsername_ReturnsUser() {
        //Arrange
        this.userCache.add(this.testUser);

        //Act
        User retrievedUser = this.userCache.get(this.testUsername);

        //Assert
        assertEquals(this.testUser, retrievedUser);
    }

    @Test
    void get_WithNonExistentUUID_ReturnsNull() {
        //Act
        User retrievedUser = this.userCache.get(this.testUUID);

        //Assert
        assertNull(retrievedUser);
    }

    @Test
    void get_WithNonExistentUsername_ReturnsNull() {
        //Act
        User retrievedUser = this.userCache.get(this.testUsername);

        //Assert
        assertNull(retrievedUser);
    }

    @Test
    void update_UpdatesUserInCache() {
        //Arrange
        this.userCache.add(this.testUser);
        UnaryOperator<User> updateUserCredits = user -> user.withCredits(200);

        //Act
        User updatedUser = this.userCache.update(this.testUUID, updateUserCredits);

        //Assert
        assertEquals(200, updatedUser.credits());
        assertNotEquals(this.testUser.credits(), updatedUser.credits());
    }

    @Test
    void update_ThrowsExceptionWhenUserNotFound() {
        //Arrange
        UnaryOperator<User> updateUserCredits = user -> user.withCredits(200);

        //Act/Assert
        assertThrows(IllegalArgumentException.class, () -> this.userCache.update(this.testUUID, updateUserCredits));
    }
}
