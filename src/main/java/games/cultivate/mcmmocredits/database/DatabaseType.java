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
import games.cultivate.mcmmocredits.config.ConfigService;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.h2.H2DatabasePlugin;
import org.jdbi.v3.sqlite3.SQLitePlugin;

import java.nio.file.Path;
import java.sql.Types;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * Database connection strategies.
 */
public enum DatabaseType implements BiFunction<DatabaseProperties, Path, Database> {
    MYSQL {
        @Override
        public Database apply(final DatabaseProperties properties, final Path path) {
            HikariConfig config = new HikariConfig();
            config.setPoolName("MCMMOCredits MYSQL");
            config.setJdbcUrl(properties.url());
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
            config.addDataSourceProperty("user", properties.user());
            config.addDataSourceProperty("password", properties.password());
            return new Database(config, jdbi -> jdbi.registerArgument(new UUIDArgumentFactory()));
        }
    }, SQLITE {
        @Override
        public Database apply(final DatabaseProperties properties, final Path path) {
            HikariConfig config = new HikariConfig();
            String url = "jdbc:sqlite:" + ConfigService.createFile(path, "database.db");
            config.setPoolName("MCMMOCredits SQLITE");
            config.setDataSourceClassName("org.sqlite.SQLiteDataSource");
            config.addDataSourceProperty("url", url);
            return new Database(config, jdbi -> jdbi.installPlugin(new SQLitePlugin()));
        }
    }, H2 {
        @Override
        public Database apply(final DatabaseProperties properties, final Path path) {
            HikariConfig config = new HikariConfig();
            String h2Path = ConfigService.createFile(path, "database.mv.db").toString().replace(".mv.db", "");
            String url = String.format("jdbc:h2:file:./%s;DB_CLOSE_DELAY=-1;MODE=MYSQL", h2Path);
            config.setPoolName("MCMMOCredits H2");
            config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
            config.addDataSourceProperty("url", url);
            return new Database(config, jdbi -> jdbi.installPlugin(new H2DatabasePlugin()));
        }
    };

    /**
     * Argument Factory required for better MySQL compatibility with UUID data type.
     */
    static class UUIDArgumentFactory extends AbstractArgumentFactory<UUID> {
        protected UUIDArgumentFactory() {
            super(Types.VARCHAR);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Argument build(final UUID value, final ConfigRegistry config) {
            return (p, s, c) -> s.setString(p, value.toString());
        }
    }
}
