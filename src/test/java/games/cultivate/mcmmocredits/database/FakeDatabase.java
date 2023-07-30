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
import org.jdbi.v3.core.h2.H2DatabasePlugin;

import java.util.UUID;

public class FakeDatabase implements Database {
    private final HikariDataSource source;
    private final Jdbi jdbi;

    /**
     * Constructs the object.
     */
    public FakeDatabase(final String url) {
        HikariConfig config = new HikariConfig();
        config.setPoolName("MCMMOCredits H2");
        config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        config.addDataSourceProperty("url", url);
        this.source = new HikariDataSource(config);
        this.jdbi = Jdbi.create(this.source)
                .installPlugin(new H2DatabasePlugin())
                .registerRowMapper(User.class, (rs, ctx) -> new User(UUID.fromString(rs.getString("UUID")), rs.getString("username"), rs.getInt("credits"), rs.getInt("redeemed")));
        this.createTable();
    }

    /**
     * Constructs the object.
     */
    public FakeDatabase() {
        this("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MYSQL");
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
     * {@inheritDoc}
     */
    @Override
    public boolean isH2() {
        return true;
    }
}
