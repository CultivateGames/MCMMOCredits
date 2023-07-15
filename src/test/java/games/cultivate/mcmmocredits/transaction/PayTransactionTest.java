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
        Transaction pay = Transaction.builder().users(this.executor, this.target).amount(600).type(TransactionType.PAY);
        TransactionResult result = pay.execute();
        assertEquals(900, result.executor().credits());
        assertEquals(1600, result.target().credits());
    }

    @Test
    void of_ValidProperties_ValidTransaction() {
        Transaction pay = Transaction.builder().users(this.executor, this.target).amount(600).type(TransactionType.PAY);
        assertEquals(this.executor, pay.executor());
        assertEquals(this.target, pay.targets()[0]);
        assertEquals(600, pay.amount());
        assertEquals(Optional.empty(), pay.isExecutable());
    }

    @Test
    void executable_InvalidTransaction_ReturnsFailure() {
        Transaction pay = Transaction.builder().users(this.executor, this.target).amount(10000).type(TransactionType.PAY);
        assertEquals(Optional.of("not-enough-credits"), pay.isExecutable());
    }

    @Test
    void isSelfTransaction_regularTransactionReturnsFalse() {
        Transaction pay = Transaction.builder().users(this.executor, this.target).amount(10000).type(TransactionType.PAY);
        assertFalse(pay.isSelfTransaction());
    }
}
