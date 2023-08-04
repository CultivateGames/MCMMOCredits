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
        Transaction pay = Transaction.builder().users(this.executor, this.target).amount(600).type(TransactionType.PAY).build();
        TransactionResult result = pay.execute();
        assertEquals(900, result.executor().credits());
        assertEquals(1600, result.target().credits());
    }

    @Test
    void of_ValidProperties_ValidTransaction() {
        Transaction pay = Transaction.builder().users(this.executor, this.target).amount(600).type(TransactionType.PAY).build();
        assertEquals(this.executor, pay.executor());
        assertEquals(this.target, pay.targets()[0]);
        assertEquals(600, pay.amount());
        assertEquals(Optional.empty(), pay.valid());
    }

    @Test
    void executable_InvalidTransaction_ReturnsFailure() {
        Transaction pay = Transaction.builder().users(this.executor, this.target).amount(10000).type(TransactionType.PAY).build();
        assertEquals(Optional.of("not-enough-credits"), pay.valid());
    }

    @Test
    void isSelfTransaction_regularTransactionReturnsFalse() {
        Transaction pay = Transaction.builder().users(this.executor, this.target).amount(10000).type(TransactionType.PAY).build();
        assertFalse(pay.isSelfTransaction());
    }
}
