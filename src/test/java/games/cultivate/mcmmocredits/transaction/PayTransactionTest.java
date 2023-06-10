package games.cultivate.mcmmocredits.transaction;

import games.cultivate.mcmmocredits.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PayTransactionTest {
    private User target;
    private User executor;

    @BeforeEach
    void setUp() {
        this.target = new User(UUID.randomUUID(), "tester1", 1000, 100);
        this.executor = new User(UUID.randomUUID(), "tester2", 1500, 150);
    }

    @Test
    void execute_ValidUsers_TransactionApplied() {
        PayTransaction pay = PayTransaction.of(this.executor, this.target, 600);
        TransactionResult result = pay.execute();
        assertEquals(900, result.executor().credits());
        assertEquals(1600, result.target().credits());
    }

    @Test
    void of_ValidProperties_ValidTransaction() {
        PayTransaction pay = PayTransaction.of(this.executor, this.target, 600);
        assertEquals(this.executor, pay.executor());
        assertEquals(this.target, pay.target());
        assertEquals(600, pay.amount());
        assertEquals(Optional.empty(), pay.executable());
    }

    @Test
    void executable_InvalidTransaction_ReturnsFailure() {
        PayTransaction pay = PayTransaction.of(this.executor, this.target, 10000);
        assertEquals(Optional.of(FailureReason.NOT_ENOUGH_CREDITS), pay.executable());
    }

    @Test
    void isSelfTransaction_regularTransactionReturnsFalse() {
        PayTransaction pay = PayTransaction.of(this.executor, this.target, 10000);
        assertFalse(pay.isSelfTransaction());
    }
}
