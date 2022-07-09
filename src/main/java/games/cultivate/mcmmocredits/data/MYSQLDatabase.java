package games.cultivate.mcmmocredits.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.keys.BooleanKey;
import games.cultivate.mcmmocredits.keys.IntegerKey;
import games.cultivate.mcmmocredits.keys.StringKey;

final class MYSQLDatabase extends SQLDatabase {

    MYSQLDatabase() {
        super();
    }

    @Override
    HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("MCMMOCredits MySQL");
        config.setJdbcUrl("jdbc:mysql://" + StringKey.DATABASE_HOST.get() + ":" + IntegerKey.DATABASE_PORT.get() + "/" + StringKey.DATABASE_NAME.get());
        config.setUsername(StringKey.DATABASE_USERNAME.get());
        config.setPassword(StringKey.DATABASE_PASSWORD.get());
        config.addDataSourceProperty("useSSL", BooleanKey.DATABASE_SSL.get());
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
