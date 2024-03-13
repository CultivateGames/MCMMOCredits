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
package games.cultivate.mcmmocredits.events;

import games.cultivate.mcmmocredits.transaction.Transaction;
import games.cultivate.mcmmocredits.transaction.TransactionType;
import games.cultivate.mcmmocredits.user.User;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class CreditTransactionEventTest {
    private Transaction transaction;
    private CreditTransactionEvent event;
    @Mock
    private MockedStatic<Bukkit> mockBukkit;

    @BeforeEach
    void setUp() {
        this.mockBukkit.when(Bukkit::isPrimaryThread).thenReturn(true);
        this.transaction = Transaction.of(new User(UUID.randomUUID(), "tester1", 100, 50), TransactionType.ADD, 200);
        this.event = new CreditTransactionEvent(this.transaction, false, false);
    }

    @Test
    void setCancelled_EnsuresEventIsCancelled() {
        this.event.setCancelled(true);
        assertTrue(this.event.isCancelled());
    }

    @Test
    void createEvent_ValidTraits_ValidEvent() {
        assertEquals(this.transaction, this.event.transaction());
        assertFalse(this.event.userFeedback());
        assertFalse(this.event.senderFeedback());
    }
}
