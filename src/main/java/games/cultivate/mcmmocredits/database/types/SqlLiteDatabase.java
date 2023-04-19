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
package games.cultivate.mcmmocredits.database.types;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.user.UserDAO;
import games.cultivate.mcmmocredits.util.Util;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlite3.SQLitePlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import javax.inject.Inject;

//TODO: change url structure, find way to write unit tests.
public final class SqlLiteDatabase implements Database {
    private final String url;
    private HikariDataSource source;
    private UserDAO dao;

    @Inject
    public SqlLiteDatabase() {
        this.url = "jdbc:sqlite:" + Util.getPluginPath().resolve("database.db");
    }

    @Override
    public HikariConfig createConfig() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("MCMMOCredits SQLITE");
        config.setDataSourceClassName("org.sqlite.SQLiteDataSource");
        config.addDataSourceProperty("url", this.url);
        Util.createFile("database.db");
        return config;
    }

    @Override
    public void disable() {
        if (this.source != null) {
            this.source.close();
        }
    }

    @Override
    public UserDAO get() {
        if (this.dao != null && this.source != null) {
            return this.dao;
        }
        this.source = new HikariDataSource(this.createConfig());
        Jdbi jdbi = Jdbi.create(this.source).installPlugin(new SqlObjectPlugin()).installPlugin(new SQLitePlugin());
        String query = this.findQuery("CREATE-TABLE-SQLITE");
        jdbi.useHandle(x -> x.execute(query));
        this.dao = jdbi.onDemand(UserDAO.class);
        return this.dao;
    }
}
