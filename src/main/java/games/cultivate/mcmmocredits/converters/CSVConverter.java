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
package games.cultivate.mcmmocredits.converters;

import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.user.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Data converter which uses database.csv in the plugin's folder.
 */
public final class CSVConverter implements Converter {
    private final Database database;
    private final List<User> users;

    /**
     * Constructs the object.
     *
     * @param database The database.
     * @param path     The plugin's data folder.
     * @throws IOException If reading the csv file errors.
     */
    public CSVConverter(final Database database, final Path path) throws IOException {
        this.database = database;
        this.users = Files.readAllLines(path.resolve("database.csv")).stream().map(User::fromCSV).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean run() {
        this.database.addUsers(this.users);
        if (this.database.isH2()) {
            this.database.jdbi().useHandle(x -> x.execute("CHECKPOINT SYNC"));
        }
        List<User> updatedCurrentUsers = this.database.getAllUsers();
        return this.users.parallelStream().allMatch(updatedCurrentUsers::contains);
    }
}
