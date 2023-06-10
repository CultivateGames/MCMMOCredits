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
package games.cultivate.mcmmocredits.database;

import games.cultivate.mcmmocredits.config.properties.DatabaseProperties;
import games.cultivate.mcmmocredits.user.UserDAO;

import jakarta.inject.Provider;
import java.nio.file.Path;

/**
 * Represents a Database connection and provider of the UserDAO.
 */
public interface Database extends Provider<UserDAO> {
    /**
     * Returns a Database from the provided parameters.
     *
     * @param properties The database's properties.
     * @param path       The plugin's data path.
     * @return The Database.
     */
    static Database getDatabase(final DatabaseProperties properties, final Path path) {
        return switch (properties.type()) {
            case H2 -> new H2Database(properties, path);
            case SQLITE -> new SQLiteDatabase(properties, path);
            case MYSQL -> new MySQLDatabase(properties);
        };
    }

    /**
     * Loads the DAO.
     */
    void load();

    /**
     * Disables the connection. Reserved for shutdown.
     */
    void disable();

    /**
     * Gets the underlying DatabaseProperties.
     *
     * @return The properties.
     */
    DatabaseProperties getProperties();
}
