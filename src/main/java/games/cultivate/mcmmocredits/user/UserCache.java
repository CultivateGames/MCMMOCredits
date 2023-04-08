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

public class UserCache {
    private final Map<UUID, User> uuidCache;
    private final Map<String, User> stringCache;

    public UserCache() {
        this.uuidCache = new ConcurrentHashMap<>();
        this.stringCache = new ConcurrentHashMap<>();
    }

    public boolean contains(final String username) {
        return this.stringCache.containsKey(username);
    }

    public boolean contains(final UUID id) {
        return this.uuidCache.containsKey(id);
    }

    public void update(final UUID uuid, final UnaryOperator<User> action) {
        User user = this.get(uuid);
        if (user == null) {
            throw new IllegalArgumentException("User not found in database!");
        }
        this.remove(uuid, user.username());
        User updatedUser = action.apply(user);
        this.add(updatedUser);
    }

    public void add(final User user) {
        this.uuidCache.put(user.uuid(), user);
        this.stringCache.put(user.username(), user);
    }

    public void remove(final UUID uuid, final String username) {
        this.uuidCache.remove(uuid);
        this.stringCache.remove(username);
    }

    public User get(final UUID uuid) {
        return this.uuidCache.get(uuid);
    }

    public User get(final String username) {
        return this.stringCache.get(username);
    }
}
