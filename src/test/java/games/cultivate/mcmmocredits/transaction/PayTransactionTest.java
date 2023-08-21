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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        Transaction pay = new PayTransaction(this.executor, List.of(this.target), 600);
        TransactionResult result = pay.execute();
        assertEquals(900, result.executor().credits());
        assertEquals(1600, result.targets().get(0).credits());
    }

    @Test
    void validate_ValidTransaction_ReturnsNoFailure() {
        Transaction transaction = new PayTransaction(this.executor, List.of(this.target), 100);
        assertEquals(Optional.empty(), transaction.validate(this.target));
    }

    @Test
    void validate_InvalidTransaction_ReturnsFailure() {
        Transaction sub = new PayTransaction(this.executor, List.of(this.target), 1501);
        assertEquals(Optional.of("not-enough-credits"), sub.validate(this.target));
    }

    @Test
    void validate_SelfTransaction_ReturnsFailure() {
        Transaction sub = new PayTransaction(this.target, List.of(this.target), 1501);
        assertEquals(Optional.of("credits-pay-same-user"), sub.validate(this.target));
    }

    @Test
    void validateTransaction_OverriddenMethod_DetectsInvalidUsers() {
        Transaction invalid = new PayTransaction(this.executor, List.of(this.target), 1501);
        var map = invalid.validateTransaction();
        assertEquals(Optional.of("not-enough-credits"), map.get(this.target));
    }
}
