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
        // Act
        this.userCache.add(this.testUser);

        // Assert
        assertTrue(this.userCache.contains(this.testUUID));
        assertTrue(this.userCache.contains(this.testUsername));
    }

    @Test
    void add_AddsUserWithSameUUID_ReplacesExistingUser() {
        // Arrange
        User anotherUserWithSameUUID = new User(this.testUUID, "anotherUsername", 150, 30);

        // Act
        this.userCache.add(this.testUser);
        this.userCache.add(anotherUserWithSameUUID);

        // Assert
        assertFalse(this.userCache.contains(this.testUsername));
        assertTrue(this.userCache.contains(anotherUserWithSameUUID.username()));
        assertTrue(this.userCache.contains(this.testUUID));
    }

    @Test
    void add_AddsUserWithSameUsername_ReplacesExistingUser() {
        //Arrange
        User anotherUserWithSameUsername = new User(UUID.randomUUID(), this.testUsername, 150, 30);

        //Act
        this.userCache.add(this.testUser);
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

        //Act
        this.userCache.add(this.testUser);
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

        //Act
        this.userCache.add(this.testUser);
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
        this.userCache.add(this.testUser);
        User retrievedUser = this.userCache.get(this.testUUID);
        assertEquals(this.testUser, retrievedUser);
    }

    @Test
    void get_WithUsername_ReturnsUser() {
        this.userCache.add(this.testUser);
        User retrievedUser = this.userCache.get(this.testUsername);
        assertEquals(this.testUser, retrievedUser);
    }

    @Test
    void get_WithNonExistentUUID_ReturnsNull() {
        User retrievedUser = this.userCache.get(this.testUUID);
        assertNull(retrievedUser);
    }

    @Test
    void get_WithNonExistentUsername_ReturnsNull() {
        User retrievedUser = this.userCache.get(this.testUsername);
        assertNull(retrievedUser);
    }

    @Test
    void update_UpdatesUserInCache() {
        this.userCache.add(this.testUser);
        UnaryOperator<User> updateUserCredits = user -> user.withCredits(200);
        User updatedUser = this.userCache.update(this.testUUID, updateUserCredits);

        assertEquals(200, updatedUser.credits());
        assertNotEquals(this.testUser.credits(), updatedUser.credits());
    }

    @Test
    void update_ThrowsExceptionWhenUserNotFound() {
        UnaryOperator<User> updateUserCredits = user -> user.withCredits(200);
        assertThrows(IllegalArgumentException.class, () -> this.userCache.update(this.testUUID, updateUserCredits));
    }
}
