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
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Creates {@link HikariConfig} objects based on configured database properties.
 */
public final class DataSourceFactory {
    private DataSourceFactory() {
        throw new AssertionError("DataSourceFactory cannot be instantiated!");
    }

    /**
     * Creates a HikariDataSource for SQLite with the given database properties.
     *
     * @param properties The {@link DatabaseProperties} for configuring the data source.
     * @return A configured {@link HikariDataSource} instance for SQLite.
     */
    public static HikariConfig createSQLite(final DatabaseProperties properties) {
        HikariConfig config = new HikariConfig();
        DatabaseType type = properties.type();
        config.setPoolName("MCMMOCredits " + type.name());
        config.setDataSourceClassName("org.sqlite.SQLiteDataSource");
        config.addDataSourceProperty("url", type.createURL(properties));
        Util.createFile("database.db");
        return config;
    }

    /**
     * Creates a HikariDataSource for MySQL with the given database properties and plugin.
     *
     * @param properties The {@link DatabaseProperties} for configuring the data source.
     * @param plugin     The {@link MCMMOCredits} plugin for loading resource files.
     * @return A configured {@link HikariDataSource} instance for MySQL.
     */
    public static HikariConfig createMySQL(final DatabaseProperties properties, final MCMMOCredits plugin) {
        HikariConfig config = new HikariConfig();
        DatabaseType type = properties.type();
        config.setPoolName("MCMMOCredits " + type.name());
        config.setJdbcUrl(type.createURL(properties));
        config.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
        Properties hproperties = new Properties();
        try (InputStream is = plugin.getResource("hikari.properties")) {
            hproperties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        hproperties.forEach((k, v) -> config.addDataSourceProperty((String) k, v));
        config.setUsername(properties.user());
        config.setPassword(properties.password());
        config.addDataSourceProperty("useSSL", properties.ssl());
        return config;
    }
}
