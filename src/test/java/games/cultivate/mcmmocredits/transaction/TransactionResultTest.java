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

import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.user.Console;
import games.cultivate.mcmmocredits.user.User;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionResultTest {
    @Mock
    private Console mockExecutor;
    @Mock
    private CommandSender mockSender;
    @Mock
    private MainConfig mockConfig;
    @Mock
    private Player mockPlayer;

    @Test
    void of_ValidProperties_ValidTransactionResult() {
        User target = new User(UUID.randomUUID(), "testUser", 100, 10);
        Transaction transaction = BasicTransaction.of(this.mockExecutor, target, BasicTransactionType.ADD, 100);
        TransactionResult result = transaction.execute();
        assertNotEquals(target, result.target());
        assertEquals(this.mockExecutor, result.executor());
        assertEquals(transaction, result.transaction());
    }

    @Test
    void sendFeedback_ValidTransaction_SendsFeedbackIfEnabled() {
        User target = new User(new UUID(2, 2), "testUser", 10, 20);
        TransactionResult withSpy = new TransactionResult(BasicTransaction.of(this.mockExecutor, target, BasicTransactionType.ADD, 0), this.mockExecutor, target);
        when(this.mockConfig.getMessage(withSpy.transaction().getMessageKey())).thenReturn("THE MESSAGE");
        when(this.mockExecutor.sender()).thenReturn(this.mockSender);
        when(this.mockExecutor.username()).thenReturn("username");
        when(this.mockExecutor.uuid()).thenReturn(new UUID(1, 1));
        when(this.mockExecutor.credits()).thenReturn(0);
        when(this.mockExecutor.redeemed()).thenReturn(0);
        withSpy.sendFeedback(this.mockConfig, false, true);
        verify(this.mockSender).sendMessage(any(Component.class));
        verify(this.mockPlayer, times(0)).sendMessage(any(Component.class));
    }
}
