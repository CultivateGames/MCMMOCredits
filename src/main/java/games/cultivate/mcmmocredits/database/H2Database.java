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
import jakarta.inject.Inject;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.h2.H2DatabasePlugin;
import org.jdbi.v3.core.locator.ClasspathSqlLocator;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.nio.file.Path;

/**
 * Provides a UserDAO using an H2 Database.
 */
public class H2Database implements Database {
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
    public H2Database(final DatabaseProperties properties, final @Dir Path path) {
        this.properties = properties;
        this.path = path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load() {
        HikariConfig config = new HikariConfig();
        String h2Path = Util.createFile(this.path, "database.mv.db").toString().replace(".mv.db", "");
        String url = String.format("jdbc:h2:file:./%s;DB_CLOSE_DELAY=-1;MODE=MYSQL", h2Path);
        config.setPoolName("MCMMOCredits H2");
        config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        config.addDataSourceProperty("url", url);
        this.source = new HikariDataSource(config);
        Jdbi jdbi = Jdbi.create(this.source).installPlugin(new SqlObjectPlugin()).installPlugin(new H2DatabasePlugin());
        this.dao = jdbi.onDemand(UserDAO.class);
        this.dao.useHandle(x -> x.execute(LOCATOR.getResource(this.getClass().getClassLoader(), "CREATE-TABLE-MYSQL.sql")));
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
