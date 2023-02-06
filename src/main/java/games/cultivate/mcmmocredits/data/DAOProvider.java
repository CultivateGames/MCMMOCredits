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
import games.cultivate.mcmmocredits.config.MainConfig.DatabaseProperties;
import games.cultivate.mcmmocredits.util.PluginPath;
import games.cultivate.mcmmocredits.util.Queries;
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
     * Provides instance of UserDAO if Database type is MySQL.
     *
     * @param properties Properties used to build the MySQL connection.
     * @return Instance of UserDAO.
     */
    public UserDAO provideSQL(final DatabaseProperties properties) {
        int databasePort = properties.port();
        String databaseHost = properties.host();
        String databaseName = properties.name();
        HikariConfig hconfig = new HikariConfig();
        hconfig.setPoolName("MCMMOCredits MySQL");
        hconfig.setJdbcUrl("jdbc:mysql://" + databaseHost + ":" + databasePort + "/" + databaseName);
        hconfig.setUsername(properties.user());
        hconfig.setPassword(properties.password());
        hconfig.addDataSourceProperty("useSSL", properties.ssl());
        hconfig.addDataSourceProperty("maintainTimeStats", "false");
        hconfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hconfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hconfig.addDataSourceProperty("cachePrepStmts", "true");
        hconfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hconfig.addDataSourceProperty("useServerPrepStmts", "true");
        hconfig.addDataSourceProperty("useLocalSessionState", "true");
        hconfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hconfig.addDataSourceProperty("cacheServerConfiguration", "true");
        this.hikari = new HikariDataSource(hconfig);
        Jdbi jdbi = Jdbi.create(this.hikari).installPlugin(new SqlObjectPlugin());
        jdbi.useHandle(x -> x.execute(this.queries.query("CREATE-TABLE-MYSQL")));
        return jdbi.onDemand(UserDAO.class);
    }

    /**
     * Provides instance of UserDAO if Database type is SQLite.
     *
     * @return Instance of UserDAO.
     */
    public UserDAO provideSQLite() {
        Path path = this.dir.resolve("database.db");
        Util.createFile(this.dir, "database.db");
        HikariConfig config = new HikariConfig();
        config.setPoolName("MCMMOCredits SQLite");
        config.setDataSourceClassName("org.sqlite.SQLiteDataSource");
        config.addDataSourceProperty("url", "jdbc:sqlite:" + path);
        this.hikari = new HikariDataSource(config);
        Jdbi jdbi = Jdbi.create(this.hikari).installPlugin(new SQLitePlugin()).installPlugin(new SqlObjectPlugin());
        jdbi.useHandle(x -> x.execute(this.queries.query("CREATE-TABLE-SQLITE")));
        return jdbi.onDemand(UserDAO.class);
    }

    /**
     * Disables the DAO. If called, the DAO will no longer be functional until it is re-provided.
     */
    public void disable() {
        if (this.hikari != null) {
            this.hikari.close();
        }
    }
}
