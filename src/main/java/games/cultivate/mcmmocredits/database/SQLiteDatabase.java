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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.config.properties.DatabaseProperties;
import games.cultivate.mcmmocredits.user.UserDAO;
import games.cultivate.mcmmocredits.util.Dir;
import games.cultivate.mcmmocredits.util.Util;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.locator.ClasspathSqlLocator;
import org.jdbi.v3.sqlite3.SQLitePlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import javax.inject.Inject;
import java.nio.file.Path;

/**
 * Provides a UserDAO using an SQLite Database.
 */
public class SQLiteDatabase implements Database {
    private static final ClasspathSqlLocator LOCATOR = ClasspathSqlLocator.create();
    private final Path path;
    private final DatabaseProperties properties;
    private HikariDataSource source;
    private UserDAO dao;

    /**
     * Constructs the object.
     *
     * @param properties The database's properties.
     * @param path       The plugin's file path.
     */
    @Inject
    public SQLiteDatabase(final DatabaseProperties properties, final @Dir Path path) {
        this.properties = properties;
        this.path = path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load() {
        HikariConfig config = new HikariConfig();
        String url = "jdbc:sqlite:" + Util.createFile(this.path, "database.db");
        config.setPoolName("MCMMOCredits SQLITE");
        config.setDataSourceClassName("org.sqlite.SQLiteDataSource");
        config.addDataSourceProperty("url", url);
        this.source = new HikariDataSource(config);
        Jdbi jdbi = Jdbi.create(this.source).installPlugin(new SqlObjectPlugin()).installPlugin(new SQLitePlugin());
        this.dao = jdbi.onDemand(UserDAO.class);
        this.dao.useHandle(x -> x.execute(LOCATOR.getResource(this.getClass().getClassLoader(), "CREATE-TABLE-SQLITE.sql")));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disable() {
        if (this.source != null) {
            this.source.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatabaseProperties getProperties() {
        return this.properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDAO get() {
        if (this.dao == null) {
            this.load();
        }
        return this.dao;
    }
}
