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
    private static final List<String> MCMMO_SKILLS = List.of("acrobatics", "alchemy", "archery", "axes", "excavation", "fishing", "herbalism", "mining", "repair", "swords", "taming", "unarmed", "woodcutting");
    private final String delimiter = ",";
    private final String empty = "";

    @Test
    void getSkillNames_ReturnsCorrectSkills() {
        assertEquals(MCMMO_SKILLS, Util.getSkillNames());
    }

    @Test
    void capitalizeWord_ValidInput_CapitalizesFirstLetter() {
        assertEquals("Testinput", Util.capitalizeWord("testInput"));
    }

    @Test
    void capitalizeWord_EmptyInput_ReturnsEmptyString() {
        assertEquals(this.empty, Util.capitalizeWord(this.empty));
    }

    @Test
    void joinString_ValidList_ReturnsJoinedString() {
        assertEquals("one,two,three", Util.joinString(this.delimiter, List.of("one", "two", "three")));
    }

    @Test
    void joinString_EmptyList_ReturnsEmptyString() {
        assertEquals(this.empty, Util.joinString(this.delimiter, List.of()));
    }

    @Test
    void joinStringFromArray_ValidArray_ReturnsJoinedString() {
        assertEquals("one,two,three", Util.joinString(this.delimiter, new String[]{"one", "two", "three"}));
    }

    @Test
    void joinStringFromArray_EmptyArray_ReturnsEmptyString() {
        assertEquals(this.empty, Util.joinString(this.delimiter, new String[]{}));
    }
}
