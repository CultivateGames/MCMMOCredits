package games.cultivate.mcmmocredits.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

//TODO: finish test coverage of Util class
class UtilTest {
    @Test
    void capitalizeWord() {
        String input = "testInput";
        String capitalized = Util.capitalizeWord(input);
        assertEquals("Testinput", capitalized);
    }

    @Test
    void joinString() {
        String delimiter = ",";
        List<String> members = Arrays.asList("one", "two", "three");
        String expectedResult = "one,two,three";
        assertEquals(expectedResult, Util.joinString(delimiter, members));
    }

    @Test
    void joinStringFromArray() {
        String delimiter = ",";
        String[] array = new String[]{"one", "two", "three"};
        String expectedResult = "one,two,three";
        assertEquals(expectedResult, Util.joinString(delimiter, array));
    }
}
