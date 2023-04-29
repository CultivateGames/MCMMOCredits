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
     * Constructs a new, empty UserCache.
     */
    public UserCache() {
        this.uuidCache = new ConcurrentHashMap<>();
        this.stringCache = new ConcurrentHashMap<>();
    }

    /**
     * Checks if the cache contains the specified username.
     *
     * @param username the username to check for
     * @return true if the username is found in the cache, false otherwise
     */
    public boolean contains(final String username) {
        return this.stringCache.containsKey(username);
    }

    /**
     * Checks if the cache contains the specified UUID.
     *
     * @param id the UUID to check for
     * @return true if the UUID is found in the cache, false otherwise
     */
    public boolean contains(final UUID id) {
        return this.uuidCache.containsKey(id);
    }

    /**
     * Updates a User object in the cache using the given action.
     *
     * @param uuid   the UUID of the User to update
     * @param action a UnaryOperator function to update the User
     * @return the updated User object
     * @throws IllegalArgumentException if the User is not found in the cache
     */
    public User update(final UUID uuid, final UnaryOperator<User> action) {
        User user = this.get(uuid);
        if (user == null) {
            throw new IllegalArgumentException("User not found in database!");
        }
        this.remove(uuid, user.username());
        User updatedUser = action.apply(user);
        this.add(updatedUser);
        return updatedUser;
    }

    /**
     * Adds a User object to the cache.
     *
     * @param user the User object to add
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
     * Removes a User object from the cache using the specified UUID and username.
     *
     * @param uuid     the UUID of the User to remove
     * @param username the username of the User to remove
     */
    public void remove(final UUID uuid, final String username) {
        this.uuidCache.remove(uuid);
        this.stringCache.remove(username);
    }

    /**
     * Retrieves a User object from the cache using the specified UUID.
     *
     * @param uuid the UUID of the User to retrieve
     * @return the User object with the specified UUID, or null if not found
     */
    public User get(final UUID uuid) {
        return this.uuidCache.get(uuid);
    }

    /**
     * Retrieves a User object from the cache using the specified username.
     *
     * @param username the username of the User to retrieve
     * @return the User object with the specified username, or null if not found
     */
    public User get(final String username) {
        return this.stringCache.get(username);
    }
}
