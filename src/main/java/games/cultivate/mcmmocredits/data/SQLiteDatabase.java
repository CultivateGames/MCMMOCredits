//
// MIT License
//
// Copyright (c) 2022 Cultivate Games
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
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.util.FileUtil;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlite3.SQLitePlugin;

import javax.inject.Inject;
import javax.inject.Named;
import java.nio.file.Path;

/**
 * {@link Database} instance that utilizes a SQLite database.
 */
public final class SQLiteDatabase extends SQLDatabase {

    @Inject
    public SQLiteDatabase(final MCMMOCredits plugin, final @Named("dir") Path dir) {
        super(plugin);
        HikariConfig config = new HikariConfig();
        config.setPoolName("MCMMOCredits SQLite");
        config.setDataSourceClassName("org.sqlite.SQLiteDataSource");
        FileUtil.createFile(dir, "database.db");
        config.addDataSourceProperty("url", "jdbc:sqlite:" + dir.resolve("database.db"));
        this.hikari = new HikariDataSource(config);
        this.jdbi = Jdbi.create(this.hikari).installPlugin(new SQLitePlugin());
        this.jdbi.useHandle(x -> x.execute(SQLStatement.SQLITE_CREATE_TABLE.toString()));
    }
}
