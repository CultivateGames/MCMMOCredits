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
import games.cultivate.mcmmocredits.util.FileUtil;
import games.cultivate.mcmmocredits.util.Queries;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlite3.SQLitePlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import javax.inject.Inject;
import javax.inject.Named;
import java.nio.file.Path;

public class SQLiteProvider implements DAOProvider {
    private final Queries queries = new Queries();
    protected HikariDataSource hikari;
    protected Jdbi jdbi;
    private @Inject @Named("dir") Path dir;

    @Override
    public UserDAO provide() {
        Path path = this.dir.resolve("database.db");
        FileUtil.createFile(this.dir, "database.db");
        HikariConfig config = new HikariConfig();
        config.setPoolName("MCMMOCredits SQLite");
        config.setDataSourceClassName("org.sqlite.SQLiteDataSource");
        config.addDataSourceProperty("url", "jdbc:sqlite:" + path);
        this.hikari = new HikariDataSource(config);
        this.jdbi = Jdbi.create(this.hikari).installPlugin(new SQLitePlugin()).installPlugin(new SqlObjectPlugin());
        this.jdbi.useHandle(x -> x.execute(this.queries.query("sqlitecreate")));
        return this.jdbi.onDemand(UserDAO.class);
    }

    @Override
    public void disable() {
        if (this.hikari != null) {
            this.hikari.close();
        }
    }
}
