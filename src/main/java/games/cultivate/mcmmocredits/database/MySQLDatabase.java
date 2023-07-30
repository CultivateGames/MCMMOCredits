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
import games.cultivate.mcmmocredits.user.User;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.sql.Types;
import java.util.UUID;

/**
 * Provides a UserDAO using a MySQL Database.
 */
public class MySQLDatabase implements Database {
    private final HikariDataSource source;
    private final Jdbi jdbi;

    /**
     * Constructs the object.
     *
     * @param user     Username for the database.
     * @param password Password for the database.
     * @param url      URL of the database.
     */
    public MySQLDatabase(final String user, final String password, final String url) {
        HikariConfig config = new HikariConfig();
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
        config.addDataSourceProperty("user", user);
        config.addDataSourceProperty("password", password);
        this.source = new HikariDataSource(config);
        this.jdbi = Jdbi.create(this.source)
                .registerArgument(new UUIDArgumentFactory())
                .registerRowMapper(User.class, (rs, ctx) -> new User(UUID.fromString(rs.getString("UUID")), rs.getString("username"), rs.getInt("credits"), rs.getInt("redeemed")));
        this.createTable();
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
    public Jdbi jdbi() {
        return this.jdbi;
    }

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
