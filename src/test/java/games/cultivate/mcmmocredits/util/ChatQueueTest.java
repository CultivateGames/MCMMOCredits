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
import java.util.concurrent.CompletableFuture;
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
    private ChatQueue chatQueue;
    private final UUID uuid = UUID.randomUUID();
    private final String completion = "test";

    @BeforeEach
    void setUp() {
        this.chatQueue = new ChatQueue();
    }

    @Test
    void remove_UUIDInQueue_RemovesEntry() {
        //Arrange
        this.chatQueue.add(this.uuid);

        //Act
        this.chatQueue.remove(this.uuid);
        boolean contains = this.chatQueue.contains(this.uuid);

        //Assert
        assertFalse(contains);
    }

    @Test
    void remove_UUIDNotInQueue_DoesNotThrowException() {
        //Act
        this.chatQueue.remove(this.uuid);
        boolean contains = this.chatQueue.contains(this.uuid);

        //Assert
        assertFalse(contains);
    }

    @Test
    void get_ExistingUUID_ReturnsCorrectValue() {
        //Arrange
        this.chatQueue.add(this.uuid);

        //Act
        CompletableFuture<String> result = this.chatQueue.get(this.uuid);

        //Assert
        assertNotNull(result);
    }

    @Test
    void get_NonExistingUUID_ReturnsNull() {
        //Act
        CompletableFuture<String> result = this.chatQueue.get(this.uuid);

        //Assert
        assertNull(result);
    }

    @Test
    void contains_UUIDInQueue_ReturnsTrue() {
        //Arrange
        this.chatQueue.add(this.uuid);

        //Act
        boolean contains = this.chatQueue.contains(this.uuid);

        //Assert
        assertTrue(contains);
    }

    @Test
    void contains_UUIDNotInQueue_ReturnsFalse() {
        //Act
        boolean contains = this.chatQueue.contains(this.uuid);

        //Assert
        assertFalse(contains);
    }

    @Test
    void add_NonExistingUUID_AddsNewEntry() {
        //Arrange
        this.chatQueue.add(this.uuid);

        //Act
        boolean contains = this.chatQueue.contains(this.uuid);

        //Assert
        assertTrue(contains);
    }

    @Test
    void add_ExistingUUID_ReplacesExistingEntry() {
        //Arrange
        this.chatQueue.add(this.uuid);
        this.chatQueue.complete(this.uuid, this.completion);

        //Act
        this.chatQueue.add(this.uuid);
        CompletableFuture<String> newFuture = this.chatQueue.get(this.uuid);

        //Assert
        assertThrows(TimeoutException.class, () -> newFuture.get(5L, TimeUnit.MILLISECONDS));
    }

    @Test
    void act_AddsUUIDAndPerformsAction() {
        //Arrange
        AtomicBoolean actionExecuted = new AtomicBoolean(false);
        this.chatQueue.add(this.uuid);

        //Act
        this.chatQueue.act(this.uuid, s -> {
            assertEquals(this.completion, s);
            actionExecuted.set(true);
        });
        this.chatQueue.complete(this.uuid, this.completion);

        //Assert
        assertTrue(actionExecuted.get());
    }

    @Test
    void act_NonExistingUUID_DoesNotThrowException() {
        //Act & Assert
        assertDoesNotThrow(() -> this.chatQueue.act(this.uuid, s -> {}));
    }

    @Test
    void act_ExistingUUID_RemovesUUIDAfterAction() {
        //Arrange
        AtomicBoolean actionExecuted = new AtomicBoolean(false);
        this.chatQueue.add(this.uuid);

        //Act
        this.chatQueue.act(this.uuid, s -> {
            assertEquals(this.completion, s);
            actionExecuted.set(true);
        });
        this.chatQueue.complete(this.uuid, this.completion);

        //Assert
        assertFalse(this.chatQueue.contains(this.uuid));
        assertTrue(actionExecuted.get());
    }

    @Test
    void complete_ExistingUUID_CompletesFuture() {
        //Arrange
        this.chatQueue.add(this.uuid);

        //Act
        this.chatQueue.complete(this.uuid, this.completion);
        String result = this.chatQueue.get(this.uuid).join();

        //Assert
        assertEquals(this.completion, result);
    }

    @Test
    void complete_NonExistingUUID_DoesNotThrowException() {
        //Act & Assert
        assertDoesNotThrow(() -> this.chatQueue.complete(this.uuid, this.completion));
    }
}
