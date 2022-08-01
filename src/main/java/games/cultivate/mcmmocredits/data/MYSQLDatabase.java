package games.cultivate.mcmmocredits.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.SettingsConfig;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;

public final class MYSQLDatabase extends SQLDatabase {

    @Inject
    MYSQLDatabase(final SettingsConfig settings, final MCMMOCredits plugin) {
        super(plugin);
        HikariConfig config = new HikariConfig();
        config.setPoolName("MCMMOCredits MySQL");
        int databasePort = settings.integer("mysql.port", 3306);
        String databaseHost = settings.string("mysql.host");
        String databaseName = settings.string("mysql.name");
        config.setJdbcUrl("jdbc:mysql://" + databaseHost + ":" + databasePort + "/" + databaseName);
        config.setUsername(settings.string("mysql.username"));
        config.setPassword(settings.string("mysql.password"));
        config.addDataSourceProperty("useSSL", settings.bool("mysql.ssl"));
        config.addDataSourceProperty("maintainTimeStats", "false");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        this.hikari = new HikariDataSource(config);
        this.jdbi = Jdbi.create(this.hikari);
        this.jdbi.useHandle(x -> x.execute(SQLStatement.MYSQL_CREATE_TABLE.toString()));
    }
}
