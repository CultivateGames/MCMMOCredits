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

import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.user.UserDAO;
import games.cultivate.mcmmocredits.util.Util;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.h2.H2DatabasePlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public final class H2Database implements Database {
    private final HikariDataSource source;
    private UserDAO dao;

    public H2Database() {
        String url = "jdbc:h2:file:./" + Util.getPluginPath().resolve("h2-database") + ";DB_CLOSE_DELAY=-1;MODE=MYSQL";
        this.source = new HikariDataSource();
        this.source.setPoolName("MCMMOCredits H2");
        this.source.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        this.source.addDataSourceProperty("url", url);
        Util.createFile("h2-database.mv.db");
    }

    @Override
    public void load() {
        if (this.dao == null) {
            Jdbi jdbi = Jdbi.create(this.source).installPlugin(new SqlObjectPlugin()).installPlugin(new H2DatabasePlugin());
            String query = this.findQuery("CREATE-TABLE-MYSQL");
            jdbi.useHandle(x -> x.execute(query));
            this.dao = jdbi.onDemand(UserDAO.class);
        }
    }

    @Override
    public void disable() {
        if (this.source != null) {
            this.source.close();
        }
    }

    @Override
    public UserDAO get() {
        this.load();
        return this.dao;
    }
}
