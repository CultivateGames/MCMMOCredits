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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BasicTransactionTypeTest {
    private final int a = 100;
    private final int b = 50;

    @Test
    void apply_AllOperations_ReturnsExpectedResults() {
        assertEquals(this.a + this.b, BasicTransactionType.ADD.apply(this.a, this.b));
        assertEquals(this.a - this.b, BasicTransactionType.TAKE.apply(this.a, this.b));
        assertEquals(this.b, BasicTransactionType.SET.apply(this.a, this.b));
    }

    @Test
    void apply_AllOperations_InvalidCreditAmountThrowsException() {
        assertThrows(ArithmeticException.class, () -> BasicTransactionType.ADD.apply(this.a, Integer.MAX_VALUE));
        assertThrows(ArithmeticException.class, () -> BasicTransactionType.TAKE.apply(Integer.MIN_VALUE, this.b));
        //SET is not tested because SET inherently cannot cause an overflow.
    }

    @Test
    void getMessageKey_AllOperations_ReturnsExpectedMessageKeys() {
        assertEquals("credits-add", BasicTransactionType.ADD.getMessageKey());
        assertEquals("credits-take", BasicTransactionType.TAKE.getMessageKey());
        assertEquals("credits-set", BasicTransactionType.SET.getMessageKey());
    }

    @Test
    void getUserMessageKey_AllOperations_ReturnsExpectedUserMessageKeys() {
        assertEquals("credits-add-user", BasicTransactionType.ADD.getUserMessageKey());
        assertEquals("credits-take-user", BasicTransactionType.TAKE.getUserMessageKey());
        assertEquals("credits-set-user", BasicTransactionType.SET.getUserMessageKey());
    }

    @Test
    void toString_AllOperations_ReturnsLowerCaseNames() {
        assertEquals("add", BasicTransactionType.ADD.toString());
        assertEquals("take", BasicTransactionType.TAKE.toString());
        assertEquals("set", BasicTransactionType.SET.toString());
    }
}