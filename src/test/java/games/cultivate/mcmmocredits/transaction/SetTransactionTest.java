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

class SetTransactionTest {
    private User target;
    private User executor;

    @BeforeEach
    void setUp() {
        this.target = new User(UUID.randomUUID(), "tester1", 1000, 100);
        this.executor = new User(UUID.randomUUID(), "tester2", 1500, 150);
    }

    @Test
    void execute_ValidUsers_TransactionApplied() {
        Transaction set = new SetTransaction(this.executor, List.of(this.target), 600);
        TransactionResult result = set.execute();
        assertEquals(600, result.targets().get(0).credits());
    }

    @Test
    void execute_ValidSelf_TransactionApplied() {
        Transaction set = new SetTransaction(this.executor, List.of(this.executor), 600);
        TransactionResult result = set.execute();
        assertEquals(600, result.targets().get(0).credits());
    }

    @Test
    void validate_ValidTransaction_ReturnsNoFailure() {
        Transaction set = new SetTransaction(this.executor, List.of(this.target), 100);
        assertEquals(Optional.empty(), set.validate(this.target));
    }

    @Test
    void validate_InvalidTransaction_ReturnsFailure() {
        Transaction sub = new SetTransaction(this.executor, List.of(this.target), -1500);
        assertEquals(Optional.of("not-enough-credits"), sub.validate(this.target));
    }
}
