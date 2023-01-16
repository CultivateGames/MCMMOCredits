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
import games.cultivate.mcmmocredits.config.GeneralConfig;
import games.cultivate.mcmmocredits.util.Queries;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import javax.inject.Inject;

public final class MySQLProvider implements DAOProvider {
    private final GeneralConfig config;
    private final Queries queries = new Queries();
    private HikariDataSource hikari;

    @Inject
    MySQLProvider(final GeneralConfig config) {
        this.config = config;
    }

    @Override
    public UserDAO provide() {
        int databasePort = this.config.integer("mysql.port", 3306);
        String databaseHost = this.config.string("mysql.host");
        String databaseName = this.config.string("mysql.name");
        HikariConfig hconfig = new HikariConfig();
        hconfig.setPoolName("MCMMOCredits MySQL");
        hconfig.setJdbcUrl("jdbc:mysql://" + databaseHost + ":" + databasePort + "/" + databaseName);
        hconfig.setUsername(this.config.string("mysql.username"));
        hconfig.setPassword(this.config.string("mysql.password"));
        hconfig.addDataSourceProperty("useSSL", this.config.bool("mysql.ssl"));
        hconfig.addDataSourceProperty("maintainTimeStats", "false");
        hconfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hconfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hconfig.addDataSourceProperty("cachePrepStmts", "true");
        hconfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hconfig.addDataSourceProperty("useServerPrepStmts", "true");
        hconfig.addDataSourceProperty("useLocalSessionState", "true");
        hconfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hconfig.addDataSourceProperty("cacheServerConfiguration", "true");
        this.hikari = new HikariDataSource(hconfig);
        Jdbi jdbi = Jdbi.create(this.hikari).installPlugin(new SqlObjectPlugin());
        jdbi.useHandle(x -> x.execute(this.queries.query("mysqlcreate")));
        return jdbi.onDemand(UserDAO.class);
    }

    @Override
    public void disable() {
        if (this.hikari != null) {
            this.hikari.close();
        }
    }
}
