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

import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for methods with no clear association.
 */
public final class Util {
    //We are keeping a string list rather than calculating it to reduce complexity.
    @SuppressWarnings("checkstyle:linelength")
    private static final List<String> MCMMO_SKILLS = List.of("acrobatics", "alchemy", "archery", "axes", "excavation", "fishing", "herbalism", "mining", "repair", "swords", "taming", "unarmed", "woodcutting");
    private static Path pluginPath;

    private Util() {
        throw new AssertionError("Util cannot be instantiated!");
    }

    /**
     * Returns a list of non-child skill names from MCMMO, formatted in lowercase.
     *
     * @return A List of formatted non-child skill names.
     */
    public static List<String> getSkillNames() {
        return MCMMO_SKILLS;
    }

    /**
     * Returns a list of non-child skill names from MCMMO, joined by a delimiter.
     *
     * @return A List of formatted non-child skill names.
     */
    public static String getJoinedSkillNames() {
        return Util.joinString(",", MCMMO_SKILLS);
    }

    /**
     * Creates a file and associated directories if they do not exist, given the file name.
     *
     * @param fileName Name of the {@link File} to create.
     */
    public static void createFile(final String fileName) {
        createFile(pluginPath, fileName);
    }

    /**
     * Creates a file and associated directories if they do not exist, given the directory path and the file name.
     *
     * @param dir      The {@link Path} to check for creation.
     * @param fileName Name of the {@link File} to create.
     */
    public static void createFile(final Path dir, final String fileName) {
        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            Files.createFile(dir.resolve(fileName));
        } catch (FileAlreadyExistsException ignored) { //Ignore if file already exists.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the path of the plugin directory.
     *
     * @return Path of the plugin directory.
     */
    public static Path getPluginPath() {
        if (pluginPath == null) {
            pluginPath = new File(Bukkit.getPluginsFolder(), "MCMMOCredits").toPath();
        }
        return pluginPath;
    }

    /**
     * Capitalizes the first letter of the input string and sets the remaining characters to lowercase.
     *
     * @param string The input string to be capitalized.
     * @return The capitalized string.
     */
    public static String capitalizeWord(final String string) {
        if (string == null || string.isEmpty()) {
            return string;
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    /**
     * Utility method that will join a collection of strings with the provided delimiter.
     *
     * @param delimiter string-based delimited.
     * @param members   collection of object to delimit.
     * @param <T>       The object being delimited. Converted to string.
     * @return The combined string.
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
     * Utility method that will join an array of strings with the provided delimiter.
     *
     * @param delimiter string-based delimited.
     * @param array     array of object to delimit. Converted to iterable list.
     * @param <T>       The object being delimited. Converted to string.
     * @return The combined string.
     */
    public static <T> String joinString(final String delimiter, final T[] array) {
        return Util.joinString(delimiter, Arrays.asList(array));
    }
}
