package games.cultivate.mcmmocredits.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChatQueueTest {
    private static final ChatQueue QUEUE = new ChatQueue();
    private static final UUID TEST_UUID = new UUID(1, 1);

    @BeforeEach
    void setUp() {
        QUEUE.add(TEST_UUID);
    }

    @AfterEach
    void tearDown() {
        QUEUE.complete(TEST_UUID, null);
        QUEUE.remove(TEST_UUID);
    }

    @Test
    void testRemoveEntry() {
        boolean containsBefore = QUEUE.contains(TEST_UUID);
        QUEUE.remove(TEST_UUID);
        boolean containsAfter = QUEUE.contains(TEST_UUID);
        assertNotEquals(containsBefore, containsAfter);
    }

    @Test
    void testContainsValue() {
        assertTrue(QUEUE.contains(TEST_UUID));
    }

    @Test
    void testAddValue() {
        QUEUE.add(new UUID(2, 2));
        assertTrue(QUEUE.contains(new UUID(2, 2)));
    }

    @Test
    void testCompleteValue() {
        QUEUE.complete(TEST_UUID, "hello");
        QUEUE.act(TEST_UUID, x -> assertEquals("hello", x));
    }
}
