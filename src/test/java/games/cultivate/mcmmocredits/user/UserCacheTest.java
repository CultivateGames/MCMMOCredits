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
    private final UUID uuid = new UUID(2, 2);
    private final String username = "TestUser";
    private final int credits = 100;
    private final int redeemed = 50;
    private UserCache cache;
    private User user;

    @BeforeEach
    void setUp() {
        this.cache = new UserCache();
        this.user = new User(this.uuid, this.username, this.credits, this.redeemed);
    }

    @Test
    void add_AddsNewUser_UserIsCached() {
        this.cache.add(this.user);
        assertTrue(this.cache.contains(this.uuid));
        assertTrue(this.cache.contains(this.username));
    }

    @Test
    void add_AddsUserWithSameUUID_ReplacesExistingUser() {
        this.cache.add(this.user);
        User updatedUser = new User(this.uuid, "anotherUsername", 150, 30);
        this.cache.add(updatedUser);
        assertNotEquals(this.user, updatedUser);
        assertFalse(this.cache.contains(this.username));
        assertTrue(this.cache.contains(updatedUser.username()));
        assertTrue(this.cache.contains(this.uuid));
    }

    @Test
    void add_AddsUserWithSameUsername_ReplacesExistingUser() {
        this.cache.add(this.user);
        User updatedUser = new User(UUID.randomUUID(), this.username, 150, 30);
        this.cache.add(updatedUser);
        assertNotEquals(this.user, updatedUser);
        assertFalse(this.cache.contains(this.uuid));
        assertTrue(this.cache.contains(updatedUser.uuid()));
        assertTrue(this.cache.contains(this.username));
    }

    @Test
    void add_AddsUserWithSameUUIDAndUsername_ReplacesExistingUser() {
        this.cache.add(this.user);
        User updatedUser = new User(this.uuid, this.username, 150, 30);
        this.cache.add(updatedUser);
        assertNotEquals(this.user, updatedUser);
        assertTrue(this.cache.contains(this.uuid));
        assertTrue(this.cache.contains(this.username));
        assertEquals(150, this.cache.get(this.uuid).credits());
        assertEquals(30, this.cache.get(this.uuid).redeemed());
    }

    @Test
    void add_AddsMultipleUsersToCache() {
        this.cache.add(this.user);
        User anotherUser = new User(UUID.randomUUID(), "anotherUsername", 150, 30);
        this.cache.add(anotherUser);
        assertTrue(this.cache.contains(this.username));
        assertTrue(this.cache.contains(this.uuid));
        assertTrue(this.cache.contains(anotherUser.username()));
        assertTrue(this.cache.contains(anotherUser.uuid()));
    }

    @Test
    void remove_NonExistentUser_NoException() {
        assertDoesNotThrow(() -> this.cache.remove(this.uuid));
    }

    @Test
    void remove_RemovesUserFromCache() {
        this.cache.add(this.user);
        this.cache.remove(this.uuid);
        assertFalse(this.cache.contains(this.username));
        assertFalse(this.cache.contains(this.uuid));
    }

    @Test
    void get_WithUUID_ReturnsCorrectUser() {
        this.cache.add(this.user);
        User retrievedUser = this.cache.get(this.uuid);
        assertEquals(this.user, retrievedUser);
        assertEquals(this.uuid, retrievedUser.uuid());
        assertEquals(this.username, retrievedUser.username());
        assertEquals(this.credits, retrievedUser.credits());
        assertEquals(this.redeemed, retrievedUser.redeemed());
    }

    @Test
    void get_WithUsername_ReturnsCorrectUser() {
        this.cache.add(this.user);
        User retrievedUser = this.cache.get(this.username);
        assertEquals(this.user, retrievedUser);
        assertEquals(this.uuid, retrievedUser.uuid());
        assertEquals(this.username, retrievedUser.username());
        assertEquals(this.credits, retrievedUser.credits());
        assertEquals(this.redeemed, retrievedUser.redeemed());
    }

    @Test
    void get_WithNonExistentUUID_ReturnsNull() {
        assertNull(this.cache.get(this.uuid));
    }

    @Test
    void get_WithNonExistentUsername_ReturnsNull() {
        assertNull(this.cache.get(this.uuid));
    }

    @Test
    void update_UpdateUserInCache_ReturnsUpdatedUser() {
        this.cache.add(this.user);
        UnaryOperator<User> updateUserCredits = user -> user.setCredits(200);
        User updatedUser = this.cache.update(this.uuid, updateUserCredits);
        assertEquals(200, updatedUser.credits());
        assertNotEquals(this.user.credits(), updatedUser.credits());
    }

    @Test
    void update_UpdateWithCompletedUser_ReturnsUpdatedUser() {
        this.cache.add(this.user);
        User anotherUser = new User(UUID.randomUUID(), "anotherUsername", 150, 30);
        User updatedUser = this.cache.update(this.uuid, anotherUser);
        assertEquals(anotherUser, updatedUser);
        assertFalse(this.cache.contains(this.username));
        assertFalse(this.cache.contains(this.uuid));
    }

    @Test
    void update_ThrowsExceptionWhenUserNotFound() {
        UnaryOperator<User> updateUserCredits = user -> user.setCredits(200);
        assertThrows(IllegalArgumentException.class, () -> this.cache.update(this.uuid, updateUserCredits));
    }
}
