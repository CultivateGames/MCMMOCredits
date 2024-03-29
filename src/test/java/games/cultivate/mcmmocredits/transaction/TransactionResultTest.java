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

import games.cultivate.mcmmocredits.user.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionResultTest {

    @Test
    void updatedUsers_ValidTransaction_ValidResult() {
        User executor = new User(UUID.randomUUID(), "username69", 100, 10);
        User target = new User(UUID.randomUUID(), "username71", 200, 20);
        Transaction transaction = new TransactionBuilder(executor, TransactionType.PAY, 100).targets(target).build();
        TransactionResult result = new TransactionResult(transaction, executor.takeCredits(100), List.of(target.addCredits(100)));
        assertTrue(result.updatedExecutor());
        assertTrue(result.updatedTargets());
        assertTrue(result.targetExecutor().isEmpty());
    }

    @Test
    void targetExecutor_SelfTransaction_ReturnsTrue() {
        User executor = new User(UUID.randomUUID(), "username69", 100, 10);
        Transaction transaction = Transaction.of(executor, TransactionType.ADD, 100);
        TransactionResult result = new TransactionResult(transaction, executor, List.of(executor.addCredits(100)));
        assertEquals(executor.addCredits(100), result.targetExecutor().get());
    }
}
