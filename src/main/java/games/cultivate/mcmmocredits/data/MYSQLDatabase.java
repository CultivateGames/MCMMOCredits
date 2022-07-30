package games.cultivate.mcmmocredits.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.SettingsConfig;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;

public final class MYSQLDatabase extends SQLDatabase {
    private final int databasePort;
    private final String databaseHost;
    private final String databaseName;
    private final String databaseUsername;
    private final String databasePassword;
    private final boolean databaseSSL;

    @Inject
    public MYSQLDatabase(final SettingsConfig settings, final MCMMOCredits plugin) {
        super(settings, plugin);
        this.databasePort = settings.integer("mysql.port", 3306);
        this.databaseHost = settings.string("mysql.host");
        this.databaseName = settings.string("mysql.name", "database");
        this.databaseUsername = settings.string("mysql.username", "username");
        this.databasePassword = settings.string("mysql.password", "");
        this.databaseSSL = settings.bool("mysql.ssl", true);
    }

    @Override
    Jdbi createJDBI() {
        return Jdbi.create(this.hikari());
    }

    @Override
    HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("MCMMOCredits MySQL");
        config.setJdbcUrl("jdbc:mysql://" + this.databaseHost + ":" + this.databasePort + "/" + this.databaseName);
        config.setUsername(this.databaseUsername);
        config.setPassword(this.databasePassword);
        config.addDataSourceProperty("useSSL", this.databaseSSL);
        config.addDataSourceProperty("maintainTimeStats", "false");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        return new HikariDataSource(config);
    }
}
