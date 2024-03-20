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
package games.cultivate.mcmmocredits.converters.loaders;

import games.cultivate.mcmmocredits.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/**
 * Loads users from a CSV file. The CSV file should have the following line structure with no header:
 * 069a79f4-44e9-4726-a5be-fca90e38aaf5,Notch,9999,100
 */
public final class CSVLoader implements UserLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CSVLoader.class);
    private final Path path;

    public CSVLoader(final Path path) {
        //TODO: database.csv or custom file name?
        this.path = path;
    }

    @Override
    public List<User> getUsers() {
        List<String> lines;
        try {
            lines = Files.readAllLines(this.path);
        } catch (IOException e) {
            LOGGER.error("There was an error loading users from csv!", e);
            return List.of();
        }
        return lines.parallelStream()
                .map(line -> line.split(","))
                .filter(values -> values.length == 4)
                .map(this::fromLine)
                .toList();
    }

    private User fromLine(final String[] value) {
        return new User(UUID.fromString(value[0]), value[1], Integer.parseInt(value[2]), Integer.parseInt(value[3]));
    }
}
