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
import games.cultivate.mcmmocredits.config.ConfigService;
import games.cultivate.mcmmocredits.user.User;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlite3.SQLitePlugin;

import java.nio.file.Path;
import java.util.UUID;

/**
 * Provides a UserDAO using an SQLite Database.
 */
public class SQLiteDatabase implements Database {
    private final HikariDataSource source;
    private final Jdbi jdbi;

    /**
     * Constructs the object.
     *
     * @param path The plugin's file path.
     */
    public SQLiteDatabase(final Path path) {
        HikariConfig config = new HikariConfig();
        String url = "jdbc:sqlite:" + ConfigService.createFile(path, "database.db");
        config.setPoolName("MCMMOCredits SQLITE");
        config.setDataSourceClassName("org.sqlite.SQLiteDataSource");
        config.addDataSourceProperty("url", url);
        this.source = new HikariDataSource(config);
        this.jdbi = Jdbi.create(this.source)
                .installPlugin(new SQLitePlugin())
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
    public void createTable() {
        this.jdbi.useHandle(handle -> handle.execute("CREATE TABLE IF NOT EXISTS MCMMOCredits(id INTEGER PRIMARY KEY AUTOINCREMENT,UUID VARCHAR NOT NULL,username VARCHAR NOT NULL,credits INT CHECK(credits >= 0),redeemed INT);"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Jdbi jdbi() {
        return this.jdbi;
    }
}
