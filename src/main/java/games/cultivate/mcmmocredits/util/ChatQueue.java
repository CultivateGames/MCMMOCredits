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
 * Represents a chat queue that manages communication using UUIDs and Completable Futures.
 * The class utilizes a map for storing and processing chat messages.
 */
public final class ChatQueue {
    private final Map<UUID, CompletableFuture<String>> map;

    public ChatQueue() {
        this.map = new ConcurrentHashMap<>();
    }

    /**
     * Removes a UUID entry from the queue.
     *
     * @param uuid The UUID to remove.
     */
    public void remove(final UUID uuid) {
        if (this.contains(uuid)) {
            this.map.get(uuid).complete(null);
            this.map.remove(uuid);
        }
    }

    /**
     * Checks if the queue contains a specific UUID.
     *
     * @param uuid The UUID to check.
     * @return true if the UUID exists in the queue; false otherwise.
     */
    public boolean contains(final UUID uuid) {
        return this.map.containsKey(uuid);
    }

    /**
     * Adds a UUID to the queue, replacing any existing entry with the same UUID.
     *
     * @param uuid The UUID to add.
     */
    public void add(final UUID uuid) {
        this.remove(uuid);
        this.map.put(uuid, new CompletableFuture<>());
    }

    /**
     * Applies an action to an existing UUID entry in the queue.
     *
     * @param uuid   The UUID associated with the entry.
     * @param action The action to apply to the entry's value.
     */
    public void act(final UUID uuid, final Consumer<? super String> action) {
        this.add(uuid);
        this.map.get(uuid).thenAccept(action).whenComplete((i, throwable) -> this.remove(uuid));
    }

    /**
     * Completes a CompletableFuture associated with a UUID in the queue.
     *
     * @param uuid       The UUID of the entry to complete.
     * @param completion The completion value.
     */
    public void complete(final UUID uuid, final String completion) {
        if (this.contains(uuid)) {
            this.map.get(uuid).complete(completion);
        }
    }
}
