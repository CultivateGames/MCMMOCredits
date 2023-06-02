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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatQueueTest {
    private ChatQueue queue;
    private final UUID uuid = UUID.randomUUID();
    private final String completion = "test";

    @BeforeEach
    void setUp() {
        this.queue = new ChatQueue();
    }

    @Test
    void remove_UUIDInQueue_RemovesEntry() {
        this.queue.add(this.uuid);
        this.queue.remove(this.uuid);
        assertFalse(this.queue.contains(this.uuid));
    }

    @Test
    void remove_UUIDNotInQueue_DoesNotThrowException() {
        this.queue.remove(this.uuid);
        assertFalse(this.queue.contains(this.uuid));
    }

    @Test
    void get_ExistingUUID_ReturnsCorrectValue() {
        this.queue.add(this.uuid);
        assertNotNull(this.queue.get(this.uuid));
    }

    @Test
    void get_NonExistingUUID_ReturnsNull() {
        assertNull(this.queue.get(this.uuid));
    }

    @Test
    void contains_UUIDInQueue_ReturnsTrue() {
        this.queue.add(this.uuid);
        assertTrue(this.queue.contains(this.uuid));
    }

    @Test
    void contains_UUIDNotInQueue_ReturnsFalse() {
        assertFalse(this.queue.contains(this.uuid));
    }

    @Test
    void add_NonExistingUUID_AddsNewEntry() {
        this.queue.add(this.uuid);
        assertTrue(this.queue.contains(this.uuid));
    }

    @Test
    void add_ExistingUUID_ReplacesExistingEntry() {
        this.queue.add(this.uuid);
        this.queue.complete(this.uuid, this.completion);
        this.queue.add(this.uuid);
        assertThrows(TimeoutException.class, () -> this.queue.get(this.uuid).get(5L, TimeUnit.MILLISECONDS));
    }

    @Test
    void act_AddsUUIDAndPerformsAction() {
        AtomicBoolean actionExecuted = new AtomicBoolean(false);
        this.queue.add(this.uuid);
        this.queue.act(this.uuid, s -> {
            assertEquals(this.completion, s);
            actionExecuted.set(true);
        });
        this.queue.complete(this.uuid, this.completion);
        assertTrue(actionExecuted.get());
    }

    @Test
    void act_NonExistingUUID_DoesNotThrowException() {
        assertDoesNotThrow(() -> this.queue.act(this.uuid, s -> {}));
    }

    @Test
    void act_ExistingUUID_RemovesUUIDAfterAction() {
        AtomicBoolean actionExecuted = new AtomicBoolean(false);
        this.queue.add(this.uuid);
        this.queue.act(this.uuid, s -> {
            assertEquals(this.completion, s);
            actionExecuted.set(true);
        });
        this.queue.complete(this.uuid, this.completion);
        assertFalse(this.queue.contains(this.uuid));
        assertTrue(actionExecuted.get());
    }

    @Test
    void complete_ExistingUUID_CompletesFuture() {
        this.queue.add(this.uuid);
        this.queue.complete(this.uuid, this.completion);
        assertEquals(this.completion, this.queue.get(this.uuid).join());
    }

    @Test
    void complete_NonExistingUUID_DoesNotThrowException() {
        assertDoesNotThrow(() -> this.queue.complete(this.uuid, this.completion));
    }
}
