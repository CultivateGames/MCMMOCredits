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

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.skills.SkillTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for methods with no clear association.
 */
public final class Util {
    private static List<String> skills = new ArrayList<>();

    private Util() {
        throw new AssertionError("Util cannot be instantiated!");
    }

    /**
     * Returns a list of non-child skill names from MCMMO, formatted.
     *
     * @return A List of formatted non-child skills name.
     */
    public static List<String> getSkillNames() {
        if (skills.isEmpty()) {
            skills = SkillTools.NON_CHILD_SKILLS.stream().map(PrimarySkillType::name).map(String::toLowerCase).toList();
        }
        return skills;
    }

    /**
     * Creates a file and associated directories if they do not exist.
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
}
