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

import java.util.function.IntBinaryOperator;

/**
 * Represents different types of basic credit transactions.
 */
public enum BasicTransactionType {
    ADD(Math::addExact),
    TAKE(Math::subtractExact),
    SET((a, b) -> b);

    private final IntBinaryOperator operator;

    /**
     * Constructs the object.
     *
     * @param operator Operators that represent how credits should be modified.
     */
    BasicTransactionType(final IntBinaryOperator operator) {
        this.operator = operator;
    }

    /**
     * Applies the transaction using the provided numbers.
     *
     * @param a An int.
     * @param b An int.
     * @return The result of the operation.
     */
    public int apply(final int a, final int b) {
        return this.operator.applyAsInt(a, b);
    }

    /**
     * Generates the message key for a message sent to the executor of the transaction.
     *
     * @return The message key.
     */
    public String getMessageKey() {
        return "credits-" + this;
    }

    /**
     * Generates the message key for a message sent to the target of the transaction.
     *
     * @return The message key.
     */
    public String getUserMessageKey() {
        return this.getMessageKey() + "-user";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
