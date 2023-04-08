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
package games.cultivate.mcmmocredits.data;

import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.util.Util;

/**
 * Enum representing the supported database types (e.g., MySQL, SQLite).
 * Provides methods to create URLs and table queries for each database type.
 *
 * @see DAOProvider
 */
public enum DatabaseType {
    MYSQL, SQLITE;

    /**
     * Generates a database URL for the specified {@link DatabaseProperties}.
     *
     * @param properties The properties containing the database configuration.
     * @return The database URL as a {@link String}.
     */
    public String createURL(final DatabaseProperties properties) {
        if (this == DatabaseType.SQLITE) {
            return "jdbc:sqlite:" + Util.getPluginPath().resolve("database.db");
        }
        return "jdbc:mysql://" + properties.host() + ":" + properties.port() + "/" + properties.name();
    }

    /**
     * Generates a table query string for the current database type.
     *
     * @return The table query string as a {@link String}.
     */
    public String createTableQuery() {
        return "CREATE-TABLE-" + this.name();
    }

    public HikariDataSource getDataSource(final DatabaseProperties properties, final MCMMOCredits plugin) {
        if (this == DatabaseType.SQLITE) {
            return DataSourceFactory.createSQLite(properties);
        }
        return DataSourceFactory.createMySQL(properties, plugin);
    }
}
