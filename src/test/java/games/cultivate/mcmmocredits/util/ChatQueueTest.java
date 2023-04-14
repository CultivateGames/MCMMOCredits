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

    @BeforeEach
    void setUp() {
        this.chatQueue = new ChatQueue();
    }

    @Test
    void remove_UUIDInQueue_RemovesEntry() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        // Act
        this.chatQueue.add(uuid);
        this.chatQueue.remove(uuid);
        boolean contains = this.chatQueue.contains(uuid);

        // Assert
        assertFalse(contains);
    }

    @Test
    void remove_UUIDNotInQueue_DoesNotThrowException() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        // Act
        this.chatQueue.remove(uuid);
        boolean contains = this.chatQueue.contains(uuid);

        // Assert
        assertFalse(contains);
    }

    @Test
    void get_ExistingUUID_ReturnsCorrectValue() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        this.chatQueue.add(uuid);

        // Act
        CompletableFuture<String> result = this.chatQueue.get(uuid);

        // Assert
        assertNotNull(result);
    }

    @Test
    void get_NonExistingUUID_ReturnsNull() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        // Act
        CompletableFuture<String> result = this.chatQueue.get(uuid);

        // Assert
        assertNull(result);
    }

    @Test
    void contains_UUIDInQueue_ReturnsTrue() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        // Act
        this.chatQueue.add(uuid);

        // Assert
        assertTrue(this.chatQueue.contains(uuid));
    }

    @Test
    void contains_UUIDNotInQueue_ReturnsFalse() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        // Act
        boolean contains = this.chatQueue.contains(uuid);

        // Assert
        assertFalse(contains);
    }

    @Test
    void add_NonExistingUUID_AddsNewEntry() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        // Act
        this.chatQueue.add(uuid);
        boolean contains = this.chatQueue.contains(uuid);

        // Assert
        assertTrue(contains);
    }

    @Test
    void add_ExistingUUID_ReplacesExistingEntry() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String initialCompletion = "initial";

        // Act
        this.chatQueue.add(uuid);
        this.chatQueue.complete(uuid, initialCompletion);
        this.chatQueue.add(uuid);
        CompletableFuture<String> newFuture = this.chatQueue.get(uuid);

        // Assert
        assertThrows(TimeoutException.class, () -> newFuture.get(5L, TimeUnit.MILLISECONDS));
    }

    @Test
    void act_AddsUUIDAndPerformsAction() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String completion = "test";
        AtomicBoolean actionExecuted = new AtomicBoolean(false);

        // Act
        this.chatQueue.add(uuid);
        this.chatQueue.act(uuid, s -> {
            assertEquals(completion, s);
            actionExecuted.set(true);
        });
        this.chatQueue.complete(uuid, completion);

        // Assert
        assertTrue(actionExecuted.get());
    }

    @Test
    void act_NonExistingUUID_DoesNotThrowException() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        // Act & Assert
        assertDoesNotThrow(() -> this.chatQueue.act(uuid, s -> {}));
    }

    @Test
    void act_ExistingUUID_RemovesUUIDAfterAction() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String completion = "test";
        AtomicBoolean actionExecuted = new AtomicBoolean(false);

        // Act
        this.chatQueue.add(uuid);
        this.chatQueue.act(uuid, s -> {
            assertEquals(completion, s);
            actionExecuted.set(true);
        });
        this.chatQueue.complete(uuid, completion);

        // Assert
        assertFalse(this.chatQueue.contains(uuid));
        assertTrue(actionExecuted.get());
    }

    @Test
    void complete_ExistingUUID_CompletesFuture() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String completion = "test";
        this.chatQueue.add(uuid);

        // Act
        this.chatQueue.complete(uuid, completion);
        String result = this.chatQueue.get(uuid).join();

        // Assert
        assertEquals(completion, result);
    }

    @Test
    void complete_NonExistingUUID_DoesNotThrowException() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String completion = "test";

        // Act & Assert
        assertDoesNotThrow(() -> this.chatQueue.complete(uuid, completion));
    }
}
