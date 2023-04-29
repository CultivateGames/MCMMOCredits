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
import games.cultivate.mcmmocredits.database.DatabaseType;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserDAO;
import games.cultivate.mcmmocredits.util.Util;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/**
 * Data converter used to add users from CSV.
 */
public final class CSVConverter implements Converter {
    private final MainConfig config;
    private final UserDAO destinationDAO;
    private List<User> sourceUsers;

    @Inject
    public CSVConverter(final MainConfig config, final UserDAO destinationDAO) {
        this.config = config;
        this.destinationDAO = destinationDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean load() {
        Path path = Util.getPluginPath().resolve("database.csv");
        try {
            List<String> lines = Files.readAllLines(path);
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
        if (this.config.getDatabaseProperties("settings", "database").type() == DatabaseType.H2) {
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
     * {@inheritDoc}
     */
    @Override
    public void disable() {
        //nothing to clean up for csv.
    }

    /**
     * Parses User from CSV line.
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
