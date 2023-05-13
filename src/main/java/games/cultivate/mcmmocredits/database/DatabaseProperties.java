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
import games.cultivate.mcmmocredits.util.Util;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * Properties used in creation of the {@linkplain Database}
 *
 * @param type     Type of the Database.
 * @param host     Host of the Database.
 * @param name     Name of the Database.
 * @param user     Username for the Database user.
 * @param password Password for the Database user.
 * @param port     Port of the Database.
 * @param ssl      If useSSL should be set to "true" in the JDBC connection url.
 */
@ConfigSerializable
public record DatabaseProperties(DatabaseType type, String host, String name, String user, String password, int port, boolean ssl) {
    /**
     * Constructs the object with sane defaults.
     *
     * @return The object.
     */
    public static DatabaseProperties defaults() {
        return new DatabaseProperties(DatabaseType.SQLITE, "127.0.0.1", "database", "root", "passw0rd+", 3306, true);
    }

    /**
     * Converts the object into a HikariDataSource.
     *
     * @return The configured HikariDataSource.
     */
    public HikariDataSource toDataSource() {
        return switch (this.type) {
            case MYSQL -> this.createMySQLDataSource();
            case H2 -> this.createH2DataSource();
            case SQLITE -> this.createSQLiteDataSource();
        };
    }

    /**
     * Creates a HikariDataSource for the MySQL database type.
     *
     * @return The configured HikariDataSource.
     */
    private HikariDataSource createMySQLDataSource() {
        HikariConfig config = new HikariConfig();
        String url = String.format("jdbc:mysql://%s:%d/%s?useSSL=%s", this.host, this.port, this.name, this.ssl);
        config.setPoolName("MCMMOCredits MYSQL");
        config.setJdbcUrl(url);
        //https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("rewriteBatchedStatements", true);
        config.addDataSourceProperty("cacheResultSetMetadata", true);
        config.addDataSourceProperty("cacheServerConfiguration", true);
        config.addDataSourceProperty("elideSetAutoCommits", true);
        config.addDataSourceProperty("maintainTimeStats", false);
        config.addDataSourceProperty("user", this.user);
        config.addDataSourceProperty("password", this.password);
        return new HikariDataSource(config);
    }

    /**
     * Creates a HikariDataSource for the SQLite database type.
     *
     * @return The configured HikariDataSource.
     */
    private HikariDataSource createSQLiteDataSource() {
        HikariConfig config = new HikariConfig();
        String url = "jdbc:sqlite:" + Util.getPluginPath().resolve("database.db");
        config.setPoolName("MCMMOCredits SQLITE");
        config.setDataSourceClassName("org.sqlite.SQLiteDataSource");
        config.addDataSourceProperty("url", url);
        Util.createFile("database.db");
        return new HikariDataSource(config);
    }

    /**
     * Creates a HikariDataSource for the H2 database type.
     *
     * @return The configured HikariDataSource.
     */
    private HikariDataSource createH2DataSource() {
        HikariConfig config = new HikariConfig();
        String url = "jdbc:h2:file:./" + Util.getPluginPath().resolve("database") + ";DB_CLOSE_DELAY=-1;MODE=MYSQL";
        config.setPoolName("MCMMOCredits H2");
        config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        config.addDataSourceProperty("url", url);
        Util.createFile("database.mv.db");
        return new HikariDataSource(config);
    }
}
