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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilTest {
    private final String delimiter = ",";
    private final String empty = "";

    @Test
    void capitalizeWord_ValidInput_CapitalizesFirstLetter() {
        //Arrange
        String input = "testInput";
        //Act
        String capitalized = Util.capitalizeWord(input);

        //Assert
        assertEquals("Testinput", capitalized);
    }

    @Test
    void capitalizeWord_EmptyInput_ReturnsEmptyString() {
        //Act
        String capitalized = Util.capitalizeWord(this.empty);

        //Assert
        assertEquals("", capitalized);
    }

    @Test
    void joinString_ValidList_ReturnsJoinedString() {
        //Arrange
        List<String> members = List.of("one", "two", "three");
        String expectedResult = "one,two,three";

        //Act
        String result = Util.joinString(this.delimiter, members);

        //Assert
        assertEquals(expectedResult, result);
    }

    @Test
    void joinString_EmptyList_ReturnsEmptyString() {
        //Arrange
        List<String> members = List.of();
        String expectedResult = "";

        //Act
        String result = Util.joinString(this.delimiter, members);

        //Assert
        assertEquals(expectedResult, result);
    }

    @Test
    void joinStringFromArray_ValidArray_ReturnsJoinedString() {
        //Arrange
        String[] array = new String[]{"one", "two", "three"};
        String expectedResult = "one,two,three";

        //Act
        String result = Util.joinString(this.delimiter, array);

        //Assert
        assertEquals(expectedResult, result);
    }

    @Test
    void joinStringFromArray_EmptyArray_ReturnsEmptyString() {
        //Arrange
        String[] array = new String[]{};

        //Act
        String result = Util.joinString(this.delimiter, array);

        //Assert
        assertEquals(this.empty, result);
    }
}
