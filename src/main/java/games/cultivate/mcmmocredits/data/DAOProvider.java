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
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.locator.ClasspathSqlLocator;
import org.jdbi.v3.sqlite3.SQLitePlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * A provider for {@link UserDAO} instances, responsible for managing database connections
 * using {@link HikariDataSource} and {@link Jdbi} libraries.
 */
public final class DAOProvider implements Provider<UserDAO> {
    private static final ClasspathSqlLocator LOCATOR = ClasspathSqlLocator.create();
    private final DatabaseType type;
    private final HikariDataSource hikari;
    private UserDAO dao;

    /**
     * Creates a DAOProvider with the given {@link DatabaseProperties} and {@link MCMMOCredits} plugin.
     *
     * @param properties The properties for configuring the data source.
     * @param plugin     The MCMMOCredits plugin for loading resource files.
     */
    @Inject
    public DAOProvider(final DatabaseProperties properties, final MCMMOCredits plugin) {
        this.type = properties.type();
        this.hikari = new HikariDataSource(this.type.getDataConfig(properties, plugin));
    }

    /**
     * Returns a {@link UserDAO} instance, initializing it if needed.
     *
     * @return An initialized {@link UserDAO} instance.
     */
    @Override
    public UserDAO get() {
        if (this.dao != null) {
            return this.dao;
        }
        Jdbi jdbi = Jdbi.create(this.hikari).installPlugin(new SqlObjectPlugin());
        if (this.type == DatabaseType.SQLITE) {
            jdbi = jdbi.installPlugin(new SQLitePlugin());
        }
        jdbi.useHandle(x -> x.execute(LOCATOR.locate(this.getClass(), this.type.createTableQuery())));
        this.dao = jdbi.onDemand(UserDAO.class);
        return this.dao;
    }

    /**
     * Disables the connection pool. This method should be called when the server is shutting down
     * to release resources associated with the {@link HikariDataSource}.
     */
    public void disable() {
        if (this.hikari != null) {
            this.hikari.close();
        }
    }
}
