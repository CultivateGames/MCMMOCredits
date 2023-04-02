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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.inject.PluginPath;
import games.cultivate.mcmmocredits.util.Util;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlite3.SQLitePlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import javax.inject.Inject;
import java.nio.file.Path;

/**
 * Provides an instance to the UserDAO.
 */
public final class DAOProvider {
    private final Queries queries = new Queries();
    private HikariDataSource hikari;
    @Inject
    @PluginPath
    private Path dir;

    /**
     * Provides instance of the UserDAO.
     *
     * @param type       DatabaseType of the database.
     * @param properties DatabaseProperties of the database. Used if type is MySQL.
     * @return Instance of the UserDAO.
     */
    public UserDAO provide(final DatabaseType type, final DatabaseProperties properties) {
        HikariConfig config = createHikariConfig(type, properties);
        Jdbi jdbi = createJdbi(config, type);
        return jdbi.onDemand(UserDAO.class);
    }

    /**
     * Creates the Hikari Config used to init the connection pool.
     *
     * @param type       DatabaseType of the database.
     * @param properties DatabaseProperties of the database. Used if type is MySQL.
     * @return A HikariConfig.
     */
    private HikariConfig createHikariConfig(final DatabaseType type, final DatabaseProperties properties) {
        HikariConfig config = new HikariConfig();
        if (type == DatabaseType.MYSQL) {
            config.setPoolName("MCMMOCredits MySQL");
            config.setJdbcUrl("jdbc:mysql://" + properties.host() + ":" + properties.port() + "/" + properties.name());
            config.setUsername(properties.user());
            config.setPassword(properties.password());
            config.addDataSourceProperty("useSSL", properties.ssl());
            config.addDataSourceProperty("maintainTimeStats", "false");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
        } else {
            Path path = dir.resolve("database.db");
            Util.createFile(dir, "database.db");
            config.setPoolName("MCMMOCredits SQLite");
            config.setDataSourceClassName("org.sqlite.SQLiteDataSource");
            config.addDataSourceProperty("url", "jdbc:sqlite:" + path);
        }
        return config;
    }

    /**
     * Creates the JDBI instance used to access the {@link UserDAO}
     *
     * @param config HikariConfig used to init the connection pool.
     * @param type   DatabaseType of the database.
     * @return An instance of JDBI.
     */
    private Jdbi createJdbi(final HikariConfig config, final DatabaseType type) {
        this.hikari = new HikariDataSource(config);
        Jdbi jdbi;
        if (type == DatabaseType.MYSQL) {
            jdbi = Jdbi.create(this.hikari).installPlugin(new SqlObjectPlugin());
            jdbi.useHandle(x -> x.execute(this.queries.query("CREATE-TABLE-MYSQL")));
        } else {
            jdbi = Jdbi.create(this.hikari).installPlugin(new SQLitePlugin()).installPlugin(new SqlObjectPlugin());
            jdbi.useHandle(x -> x.execute(this.queries.query("CREATE-TABLE-SQLITE")));
        }
        return jdbi;
    }

    /**
     * Disables the connection pool. Called when the server is shutting down.
     */
    public void disable() {
        if (this.hikari != null) {
            this.hikari.close();
        }
    }
}
