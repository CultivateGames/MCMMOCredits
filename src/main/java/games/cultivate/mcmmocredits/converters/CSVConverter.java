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

import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.database.DatabaseProperties;
import games.cultivate.mcmmocredits.database.DatabaseType;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserDAO;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/**
 * Data Converter that reads data from a CSV file.
 */
public final class CSVConverter implements Converter {
    private final UserDAO destinationDAO;
    private final Path path;
    private final DatabaseProperties properties;
    private List<User> sourceUsers;

    /**
     * Constructs the object.
     *
     * @param config         MainConfig in order to read converter settings.
     * @param destinationDAO Destination database.
     * @param path           The plugin's data path.
     */
    @Inject
    public CSVConverter(final MainConfig config, final UserDAO destinationDAO, final @Named("plugin") Path path) {
        this.destinationDAO = destinationDAO;
        this.path = path;
        this.properties = config.getDatabaseProperties("settings", "database");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean load() {
        try {
            List<String> lines = Files.readAllLines(this.path.resolve("database.csv"));
            this.sourceUsers = lines.stream().map(this::userFromCSV).toList();
            return !this.sourceUsers.isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean convert() {
        this.destinationDAO.addUsers(this.sourceUsers);
        if (this.properties.type() == DatabaseType.H2) {
            this.destinationDAO.useHandle(x -> x.execute("CHECKPOINT SYNC"));
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean verify() {
        List<User> updatedCurrentUsers = this.destinationDAO.getAllUsers();
        return this.sourceUsers.parallelStream().allMatch(updatedCurrentUsers::contains);
    }

    /**
     * Parses User from line of a CSV file.
     *
     * @param line The line of text from CSV file.
     * @return The parsed User.
     */
    private User userFromCSV(final String line) {
        String[] arr = line.split(",");
        UUID uuid = UUID.fromString(arr[0]);
        return new User(uuid, arr[1], Integer.parseInt(arr[2]), Integer.parseInt(arr[3]));
    }
}
