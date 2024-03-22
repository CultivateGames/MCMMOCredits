//
// MIT License
//
// Copyright (c) 2024 Cultivate Games
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddTransactionTest {
    private final CommandExecutor executor = Console.INSTANCE;
    private User target;

    @BeforeEach
    void setUp() {
        this.target = new User(UUID.randomUUID(), "tester1", 1000, 100);
    }

    @Test
    void execute_ValidUser_TransactionApplied() {
        Transaction transaction = new AddTransaction(this.executor, List.of(this.target), 100);
        TransactionResult result = transaction.execute();
        assertEquals(1100, result.targets().get(0).credits());
    }

    @Test
    void execute_ValidSelf_TransactionApplied() {
        Transaction set = new AddTransaction(this.target, List.of(this.target), 600);
        TransactionResult result = set.execute();
        assertEquals(1600, result.targets().get(0).credits());
    }

    @Test
    void validate_ValidTransaction_ReturnsNoFailure() {
        Transaction transaction = new AddTransaction(this.executor, List.of(this.target), 100);
        assertEquals(Optional.empty(), transaction.validate(this.target));
    }

    @Test
    void validate_InvalidTransaction_ReturnsFailure() {
        Transaction sub = new AddTransaction(this.executor, List.of(this.target), -1001);
        assertEquals(Optional.of("not-enough-credits"), sub.validate(this.target));
    }

    @Test
    void validate_InvalidTransactionWithOverflowedInt_DoesNotThrow() {
        Transaction max = new AddTransaction(this.executor, List.of(this.target), Integer.MAX_VALUE);
        assertDoesNotThrow(() -> max.validate(this.target));
    }

    @Test
    void validateTransaction_DetectsInvalidUsers() {
        User user = new User(UUID.randomUUID(), "tester4", 10, 100);
        Transaction invalid = new AddTransaction(this.executor, List.of(user, this.target), -11);
        var map = invalid.validateTransaction();
        assertEquals(Optional.of("not-enough-credits-other"), map.get(user));
        assertEquals(Optional.empty(), map.get(this.target));
    }

    @Test
    void isSelfTransaction_selfTransactionReturnsTrue() {
        Transaction addition = new AddTransaction(this.target, List.of(this.target), 100);
        assertTrue(addition.isSelfTransaction());
    }

    @Test
    void isSelfTransaction_groupTransactionReturnsFalse() {
        User user2 = new User(UUID.randomUUID(), "test2", 2000, 200);
        Transaction addition = new AddTransaction(this.executor, List.of(this.target, user2), 50);
        assertFalse(addition.isSelfTransaction());
    }
}

