//
// MIT License
//
// Copyright (c) 2024 Cultivate Games
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
import games.cultivate.mcmmocredits.config.ConfigService;

import javax.sql.DataSource;
import java.nio.file.Path;

/**
 * Factory that creates DataSources.
 */
public final class DataSourceFactory {

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private DataSourceFactory() {
    }

    /**
     * Creates a DataSource from the provided properties.
     *
     * @param properties The DatabaseProperties.
     * @param path       The plugin's data path.
     * @return The data source.
     */
    public static DataSource createSource(final DatabaseProperties properties, final Path path) {
        HikariConfig config = new HikariConfig();
        config.setPoolName("MCMMOCredits %s".formatted(properties.type().name()));
        config.setMaximumPoolSize(20);
        switch (properties.type()) {
            case H2 -> {
                ConfigService.createFile(path, "database.mv.db");
                config.setJdbcUrl(String.format("jdbc:h2:file:./%s;MODE=MYSQL;DB_CLOSE_ON_EXIT=FALSE;IGNORECASE=TRUE", path.resolve("database")));
            }
            case SQLITE -> {
                Path p = ConfigService.createFile(path, "database.db");
                config.setJdbcUrl("jdbc:sqlite:%s".formatted(p));
            }
            case MYSQL -> {
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
                config.setUsername(properties.user());
                config.setPassword(properties.password());
                config.setJdbcUrl(properties.url());
            }
            default -> throw new IllegalStateException("Invalid database type!");
        }
        return new HikariDataSource(config);
    }
}
