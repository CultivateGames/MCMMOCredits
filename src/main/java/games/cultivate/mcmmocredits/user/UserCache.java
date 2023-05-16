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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

/**
 * Represents a cache for User objects, allowing for retrieval of
 * User instances using either their UUID or username as a key.
 */
public final class UserCache {
    private final Map<UUID, User> uuidCache;
    private final Map<String, User> stringCache;

    /**
     * Constructs the object.
     */
    public UserCache() {
        this.uuidCache = new ConcurrentHashMap<>();
        this.stringCache = new ConcurrentHashMap<>();
    }

    /**
     * Checks if the cache contains the specified username.
     *
     * @param username The username to check.
     * @return true if the cache contains the username, otherwise false.
     */
    public boolean contains(final String username) {
        return this.stringCache.containsKey(username);
    }

    /**
     * Checks if the cache contains the specified UUID.
     *
     * @param uuid The UUID to check.
     * @return true if the cache contains the username, otherwise false.
     */
    public boolean contains(final UUID uuid) {
        return this.uuidCache.containsKey(uuid);
    }

    /**
     * Applies a function to a user, and updates the existing user with the function's result.
     *
     * @param uuid   UUID of the user.
     * @param action Action to apply to the cached user.
     * @return The updated user.
     */
    public User update(final UUID uuid, final UnaryOperator<User> action) {
        User user = this.get(uuid);
        if (user == null) {
            throw new IllegalArgumentException("User not found in database!");
        }
        this.remove(uuid);
        User updatedUser = action.apply(user);
        this.add(updatedUser);
        return updatedUser;
    }

    /**
     * Updates the user found at the specified UUID with the provided user.
     *
     * @param uuid The UUID to check.
     * @param user The user to replace with.
     * @return The updated user.
     */
    public User update(final UUID uuid, final User user) {
        return this.update(uuid, x -> user);
    }

    /**
     * Adds a user to the cache.
     *
     * @param user The user to add.
     */
    public void add(final User user) {
        if (this.contains(user.uuid())) {
            String oldUsername = this.uuidCache.remove(user.uuid()).username();
            this.stringCache.remove(oldUsername);
        }
        if (this.contains(user.username())) {
            UUID oldUUID = this.stringCache.remove(user.username()).uuid();
            this.uuidCache.remove(oldUUID);
        }
        this.uuidCache.put(user.uuid(), user);
        this.stringCache.put(user.username(), user);
    }

    /**
     * Removes a user from the cache.
     *
     * @param uuid The UUID to remove.
     */
    public void remove(final UUID uuid) {
        User removed = this.uuidCache.remove(uuid);
        this.stringCache.remove(removed.username());
    }

    /**
     * Gets a user from the cache with the specified UUID.
     *
     * @param uuid UUID of the user.
     * @return The user.
     */
    public User get(final UUID uuid) {
        return this.uuidCache.get(uuid);
    }

    /**
     * Gets a user from the cache with the specified UUID.
     *
     * @param username Username of the user.
     * @return The user.
     */
    public User get(final String username) {
        return this.stringCache.get(username);
    }
}
