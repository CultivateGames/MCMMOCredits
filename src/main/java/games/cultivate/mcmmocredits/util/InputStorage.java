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
package games.cultivate.mcmmocredits.util;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Object which represents a Chat Queue. Stores the {@link UUID} of a user, and a {@link CompletableFuture} representing the response status of the user as a {@link Map}.
 */
public final class InputStorage {
    private final Map<UUID, CompletableFuture<String>> map;

    public InputStorage() {
        this.map = new ConcurrentHashMap<>();
    }

    /**
     * Removes an entry associated with the provided {@link UUID} if it exists.
     *
     * @param uuid The {@link UUID} of the associated user to remove from the underlying map.
     */
    public void remove(final UUID uuid) {
        if (this.contains(uuid)) {
            this.map.get(uuid).complete(null);
            this.map.remove(uuid);
        }
    }

    /**
     * Indicates if an entry exists in the underlying {@link Map}
     *
     * @param uuid The {@link UUID} of the associated user to search for.
     * @return Whether the entry exists for the provided {@link UUID}
     */
    public boolean contains(final UUID uuid) {
        return this.map.containsKey(uuid);
    }

    /**
     * @param uuid The {@link UUID} of the associated user.
     */
    public void add(final UUID uuid) {
        this.remove(uuid);
        this.map.put(uuid, new CompletableFuture<>());
    }

    /**
     * Performs the provided {@link Consumer} using data from the completed {@link CompletableFuture} associated with the {@link UUID}.
     * Attempts to add the entry if it doesn't exist, and removes the entry after the action is completed.
     *
     * @param uuid   The {@link UUID} of the associated user.
     * @param action Action to perform after {@link CompletableFuture} is completed with data.
     */
    public void act(final UUID uuid, final Consumer<? super String> action) {
        this.add(uuid);
        this.map.get(uuid).thenAccept(action).whenComplete((i, throwable) -> this.remove(uuid));
    }

    /**
     * Completes the {@link CompletableFuture} associated with the provided {@link UUID}
     *
     * @param uuid       The {@link UUID} of the associated user.
     * @param completion String used to complete the {@link CompletableFuture}
     */
    public void complete(final UUID uuid, final String completion) {
        if (this.contains(uuid)) {
            this.map.get(uuid).complete(completion);
        }
    }
}
