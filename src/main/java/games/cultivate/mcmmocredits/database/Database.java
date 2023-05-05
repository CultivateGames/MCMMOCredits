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

import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.user.UserDAO;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.h2.H2DatabasePlugin;
import org.jdbi.v3.core.locator.ClasspathSqlLocator;
import org.jdbi.v3.sqlite3.SQLitePlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import javax.inject.Inject;
import javax.inject.Provider;
import java.sql.Types;
import java.util.UUID;

public final class Database implements Provider<UserDAO> {
    private static final ClasspathSqlLocator LOCATOR = ClasspathSqlLocator.create();
    private final DatabaseProperties properties;
    private HikariDataSource source;
    private UserDAO dao;

    @Inject
    public Database(final DatabaseProperties properties) {
        this.properties = properties;
    }

    public void load() {
        this.source = this.properties.toDataSource();
        Jdbi jdbi = this.createJDBI();
        jdbi.useHandle(x -> x.execute(this.getQuery()));
        this.dao = jdbi.onDemand(UserDAO.class);
    }

    public void disable() {
        if (this.source != null) {
            this.source.close();
        }
    }

    @Override
    public UserDAO get() {
        if (this.dao == null) {
            this.load();
        }
        return this.dao;
    }

    private Jdbi createJDBI() {
        Jdbi jdbi = Jdbi.create(this.source).installPlugin(new SqlObjectPlugin());
        return switch (this.properties.type()) {
            case SQLITE -> jdbi.installPlugin(new SQLitePlugin());
            case H2 -> jdbi.installPlugin(new H2DatabasePlugin());
            case MYSQL -> jdbi.registerArgument(new UUIDArgumentFactory());
        };
    }

    private String getQuery() {
        String result = this.properties.type() == DatabaseType.SQLITE ? "CREATE-TABLE-SQLITE" : "CREATE-TABLE-MYSQL";
        return LOCATOR.getResource(this.getClass().getClassLoader(), result + ".sql");
    }

    static class UUIDArgumentFactory extends AbstractArgumentFactory<UUID> {
        protected UUIDArgumentFactory() {
            super(Types.VARCHAR);
        }

        @Override
        protected Argument build(final UUID value, final ConfigRegistry config) {
            return (p, s, c) -> s.setString(p, value.toString());
        }
    }
}
