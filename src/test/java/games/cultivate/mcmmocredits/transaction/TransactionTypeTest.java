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

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.Console;
import games.cultivate.mcmmocredits.user.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionTypeTest {
    private final CommandExecutor executor = Console.INSTANCE;
    private final User target = new User(UUID.randomUUID(), "tester3", 1000, 100);

    @Test
    void userMessageKey_ReturnsStaticValue() {
        Transaction transaction = new AddTransaction(this.executor, List.of(this.target), 100);
        assertEquals("credits-add-user", transaction.userMessageKey());
    }

    @Test
    void messageKey_SoloTransaction_ReturnsCorrectValue() {
        Transaction transaction = new AddTransaction(this.executor, List.of(this.target), 100);
        assertEquals("credits-add", transaction.messageKey());
    }

    @Test
    void messageKey_GroupTransaction_ReturnsCorrectValue() {
        User user2 = new User(UUID.randomUUID(), "test2", 2000, 200);
        Transaction addition = new AddTransaction(this.executor, List.of(this.target, user2), 50);
        assertEquals("credits-add-all", addition.messageKey());
    }

    @Test
    void all_PayTransaction_CorrectStaticValue() {
        Transaction pay = new PayTransaction(new User(UUID.randomUUID(), "tester1", 10000, 1), List.of(this.target), 100);
        assertEquals("credits-pay", pay.messageKey());
        assertEquals("credits-pay-user", pay.userMessageKey());
    }

    @Test
    void messageKey_SoloRedeemTransaction_CorrectValue() {
        Transaction redeem = new RedeemTransaction(this.target, List.of(this.target), PrimarySkillType.HERBALISM, 100);
        assertEquals("credits-redeem", redeem.messageKey());
    }

    @Test
    void messageKey_GroupRedeemTransaction_CorrectValue() {
        Transaction redeem = new RedeemTransaction(this.executor, List.of(new User(UUID.randomUUID(), "tester1", 10000, 1), this.target), PrimarySkillType.HERBALISM, 100);
        assertEquals("credits-redeem-all", redeem.messageKey());
    }

    @Test
    void notEnoughCredits_AllTransaction_ReturnsCorrectKey() {
        User user2 = new User(UUID.randomUUID(), "test2", 2000, 200);
        Transaction addition = new AddTransaction(this.executor, List.of(this.target, user2), 50);
        assertEquals("not-enough-credits-other", addition.type().notEnoughCredits());
    }

    @Test
    void notEnoughCredits_SelfTransaction_ReturnsCorrectKey() {
        Transaction addition = new AddTransaction(this.executor, List.of(this.target), 50);
        assertEquals("not-enough-credits", addition.type().notEnoughCredits());
    }
}
