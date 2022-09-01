package games.cultivate.mcmmocredits.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.GeneralConfig;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;

/**
 * {@link Database} instance that utilizes a MySQL database.
 */
public final class MYSQLDatabase extends SQLDatabase {

    @Inject
    MYSQLDatabase(final GeneralConfig config, final MCMMOCredits plugin) {
        super(plugin);
        HikariConfig hconfig = new HikariConfig();
        hconfig.setPoolName("MCMMOCredits MySQL");
        int databasePort = config.integer("mysql.port", 3306);
        String databaseHost = config.string("mysql.host");
        String databaseName = config.string("mysql.name");
        hconfig.setJdbcUrl("jdbc:mysql://" + databaseHost + ":" + databasePort + "/" + databaseName);
        hconfig.setUsername(config.string("mysql.username"));
        hconfig.setPassword(config.string("mysql.password"));
        hconfig.addDataSourceProperty("useSSL", config.bool("mysql.ssl"));
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
        this.jdbi = Jdbi.create(this.hikari);
        this.jdbi.useHandle(x -> x.execute(SQLStatement.MYSQL_CREATE_TABLE.toString()));
    }
}
