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
package games.cultivate.mcmmocredits.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreditOperationTest {
    private final int a = 100;
    private final int b = 50;


    @Test
    void apply_AddOperation_PositiveNumbers_ReturnsCorrectSum() {
        //Arrange
        int expectedResult = this.a + this.b;

        //Act
        int result = CreditOperation.ADD.apply(this.a, this.b);

        //Assert
        assertEquals(expectedResult, result);
    }

    @Test
    void apply_TakeOperation_PositiveNumbers_ReturnsCorrectDifference() {
        //Arrange
        int expectedResult = this.a - this.b;

        //Act
        int result = CreditOperation.TAKE.apply(this.a, this.b);

        //Assert
        assertEquals(expectedResult, result);
    }

    @Test
    void apply_SetOperation_PositiveNumbers_ReturnsSecondNumber() {
        //Act
        int result = CreditOperation.SET.apply(this.a, this.b);

        //Assert
        assertEquals(this.b, result);
    }

    @Test
    void getMessageKey_AllOperations_ReturnsExpectedMessageKeys() {
        assertEquals("credits-add", CreditOperation.ADD.getMessageKey());
        assertEquals("credits-take", CreditOperation.TAKE.getMessageKey());
        assertEquals("credits-set", CreditOperation.SET.getMessageKey());
    }

    @Test
    void getUserMessageKey_AllOperations_ReturnsExpectedUserMessageKeys() {
        assertEquals("credits-add-user", CreditOperation.ADD.getUserMessageKey());
        assertEquals("credits-take-user", CreditOperation.TAKE.getUserMessageKey());
        assertEquals("credits-set-user", CreditOperation.SET.getUserMessageKey());
    }

    @Test
    void toString_AllOperations_ReturnsLowerCaseNames() {
        assertEquals("add", CreditOperation.ADD.toString());
        assertEquals("take", CreditOperation.TAKE.toString());
        assertEquals("set", CreditOperation.SET.toString());
    }
}
