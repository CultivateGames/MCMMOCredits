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

import java.util.Arrays;
import java.util.List;

/**
 * Utility class for methods with no clear association.
 */
public final class Util {
    @SuppressWarnings("checkstyle:linelength")
    private static final List<String> MCMMO_SKILLS = List.of("acrobatics", "alchemy", "archery", "axes", "excavation", "fishing", "herbalism", "mining", "repair", "swords", "taming", "unarmed", "woodcutting");

    private Util() {
        throw new AssertionError("Util cannot be instantiated!");
    }

    /**
     * Gets a list of lowercase, eligible MCMMO skills.
     *
     * @return The list.
     */
    public static List<String> getSkillNames() {
        return MCMMO_SKILLS;
    }

    /**
     * Capitalizes the first letter of the input and sets the rest of the string to lowercase.
     *
     * @param string The input to be capitalized.
     * @return The capitalized string.
     */
    public static String capitalizeWord(final String string) {
        if (string == null || string.isEmpty()) {
            return string;
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    /**
     * Joins a collection of strings with the provided delimiter.
     *
     * @param delimiter The delimiter.
     * @param members   The objects to delimit.
     * @param <T>       Type of the object being delimited.
     * @return The delimited string.
     */
    public static <T> String joinString(final String delimiter, final Iterable<T> members) {
        StringBuilder sb = new StringBuilder();
        for (T obj : members) {
            sb.append(obj).append(delimiter);
        }
        int last = sb.lastIndexOf(delimiter);
        if (last != -1) {
            sb.deleteCharAt(last);
        }
        return sb.toString();
    }

    /**
     * Joins an array of objects with the provided delimiter.
     *
     * @param delimiter The delimiter.
     * @param array     An array of objects to delimit.
     * @param <T>       Type of the object being delimited.
     * @return The delimited string.
     */
    public static <T> String joinString(final String delimiter, final T[] array) {
        return Util.joinString(delimiter, Arrays.asList(array));
    }
}
