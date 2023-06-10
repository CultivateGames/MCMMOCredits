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

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Represents a queue of chat messages. Manages chat-based plugin transactions.
 */
public final class ChatQueue {
    private final Map<UUID, CompletableFuture<String>> map;

    /**
     * Constructs the object.
     */
    public ChatQueue() {
        this.map = new ConcurrentHashMap<>();
    }

    /**
     * Removes an entry from the queue at the specified UUID.
     *
     * @param uuid The UUID of the entry to remove.
     */
    public void remove(final UUID uuid) {
        if (this.contains(uuid)) {
            this.map.get(uuid).complete(null);
            this.map.remove(uuid);
        }
    }

    /**
     * Gets a value from the provided UUID key.
     *
     * @param uuid The UUID key.
     * @return Value from the queue if present, otherwise null.
     */
    public @Nullable CompletableFuture<String> get(final UUID uuid) {
        return this.map.get(uuid);
    }

    /**
     * Checks if the queue contains a specific UUID key.
     *
     * @param uuid The UUID key to check.
     * @return True if the UUID exists in the queue, otherwise false.
     */
    public boolean contains(final UUID uuid) {
        return this.map.containsKey(uuid);
    }

    /**
     * Adds an entry to the queue, with the provided UUID as the key.
     *
     * @param uuid The UUID key to add.
     */
    public void add(final UUID uuid) {
        this.remove(uuid);
        this.map.put(uuid, new CompletableFuture<>());
    }

    /**
     * Applies an action to the entry in the queue with the specified UUID key, and removes it upon completion.
     *
     * @param uuid   The UUID key.
     * @param action The action to apply.
     */
    public void act(final UUID uuid, final Consumer<? super String> action) {
        this.add(uuid);
        this.map.get(uuid).thenAccept(action).whenComplete((i, throwable) -> this.remove(uuid));
    }

    /**
     * Completes the CompletableFuture associated with the specified UUID key.
     *
     * @param uuid       The UUID key.
     * @param completion The value to complete the CompletableFuture with.
     */
    public void complete(final UUID uuid, final String completion) {
        if (this.contains(uuid)) {
            this.map.get(uuid).complete(completion);
        }
    }
}
