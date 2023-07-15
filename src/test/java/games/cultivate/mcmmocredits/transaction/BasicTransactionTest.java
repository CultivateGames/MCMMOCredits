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

import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.Console;
import games.cultivate.mcmmocredits.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BasicTransactionTest {
    private User target;
    private CommandExecutor executor;

    @BeforeEach
    void setUp() {
        this.target = new User(UUID.randomUUID(), "tester1", 1000, 100);
        this.executor = Console.INSTANCE;
    }

    @Test
    void execute_ValidUser_TransactionApplied() {
        Transaction addition = Transaction.builder().self(this.target).type(TransactionType.ADD).amount(100);
        Transaction subtraction = Transaction.builder().self(this.target).type(TransactionType.TAKE).amount(100);
        Transaction setting = Transaction.builder().self(this.target).type(TransactionType.SET).amount(100);
        assertEquals(1100, addition.execute().target().credits());
        assertEquals(900, subtraction.execute().target().credits());
        assertEquals(100, setting.execute().target().credits());
    }

    @Test
    void execute_ValidUsers_TransactionApplied() {
        Transaction addition = Transaction.builder().users(this.executor, this.target).type(TransactionType.ADD).amount(100);
        Transaction subtraction = Transaction.builder().users(this.executor, this.target).type(TransactionType.TAKE).amount(100);
        Transaction setting = Transaction.builder().users(this.executor, this.target).type(TransactionType.SET).amount(100);
        assertEquals(1100, addition.execute().target().credits());
        assertEquals(900, subtraction.execute().target().credits());
        assertEquals(100, setting.execute().target().credits());
    }

    @Test
    void selfOf_ValidProperties_ValidTransaction() {
        Transaction addition = Transaction.builder().self(this.target).type(TransactionType.ADD).amount(100);
        assertEquals(this.target, addition.executor());
        assertEquals(this.target, addition.targets()[0]);
        assertEquals(TransactionType.ADD, addition.type());
        assertEquals(100, addition.amount());
        assertEquals(Optional.empty(), addition.isExecutable());
    }

    @Test
    void of_ValidProperties_ValidTransaction() {
        Transaction addition = Transaction.builder().users(this.executor, this.target).type(TransactionType.ADD).amount(100);
        assertEquals(this.executor, addition.executor());
        assertEquals(this.target, addition.targets()[0]);
        assertEquals(TransactionType.ADD, addition.type());
        assertEquals(100, addition.amount());
        assertEquals(Optional.empty(), addition.isExecutable());
    }

    @Test
    void executable_ValidTransaction_ReturnsNoFailure() {
        Transaction addition = Transaction.builder().users(this.executor, this.target).type(TransactionType.ADD).amount(100);
        assertEquals(Optional.empty(), addition.isExecutable());
    }

    @Test
    void executable_InvalidTransaction_ReturnsFailure() {
        Transaction subtraction = Transaction.builder().users(this.executor, this.target).type(TransactionType.TAKE).amount(1001);
        assertEquals(Optional.of("not-enough-credits"), subtraction.isExecutable());
    }

    @Test
    void executable_InvalidTransactionWithOverflowedInt_DoesNotThrow() {
        Transaction addition = Transaction.builder().users(this.executor, this.target).type(TransactionType.ADD).amount(Integer.MAX_VALUE);
        assertDoesNotThrow(addition::isExecutable);
    }

    @Test
    void isSelfTransaction_selfTransactionReturnsTrue() {
        Transaction addition = Transaction.builder().self(this.target).type(TransactionType.ADD).amount(100);
        assertTrue(addition.isSelfTransaction());
    }

    @Test
    void isSelfTransaction_regularTransactionReturnsFalse() {
        Transaction addition = Transaction.builder().users(this.executor, this.target).type(TransactionType.ADD).amount(100);
        assertFalse(addition.isSelfTransaction());
    }
}
