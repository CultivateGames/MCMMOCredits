package games.cultivate.mcmmocredits.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.config.properties.DatabaseProperties;
import games.cultivate.mcmmocredits.user.UserDAO;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.locator.ClasspathSqlLocator;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import javax.inject.Inject;
import java.sql.Types;
import java.util.UUID;

/**
 * Provides a UserDAO using a MySQL Database.
 */
public class MySQLDatabase implements Database {
    private static final ClasspathSqlLocator LOCATOR = ClasspathSqlLocator.create();
    private final DatabaseProperties properties;
    private HikariDataSource source;
    private UserDAO dao;

    /**
     * Constructs the object.
     *
     * @param properties The database's properties.
     */
    @Inject
    public MySQLDatabase(final DatabaseProperties properties) {
        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("MCMMOCredits MYSQL");
        config.setJdbcUrl(this.properties.url());
        //https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("rewriteBatchedStatements", true);
        config.addDataSourceProperty("cacheResultSetMetadata", true);
        config.addDataSourceProperty("cacheServerConfiguration", true);
        config.addDataSourceProperty("elideSetAutoCommits", true);
        config.addDataSourceProperty("maintainTimeStats", false);
        config.addDataSourceProperty("user", this.properties.user());
        config.addDataSourceProperty("password", this.properties.password());
        this.source = new HikariDataSource(config);
        Jdbi jdbi = Jdbi.create(this.source).installPlugin(new SqlObjectPlugin()).registerArgument(new UUIDArgumentFactory());
        this.dao = jdbi.onDemand(UserDAO.class);
        this.dao.useHandle(x -> x.execute(LOCATOR.getResource(this.getClass().getClassLoader(), "CREATE-TABLE-MYSQL.sql")));
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
    public DatabaseProperties getProperties() {
        return this.properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDAO get() {
        if (this.dao == null) {
            this.load();
        }
        return this.dao;
    }

    /**
     * Argument Factory required for better MySQL compatibility with UUID data type.
     */
    static class UUIDArgumentFactory extends AbstractArgumentFactory<UUID> {
        protected UUIDArgumentFactory() {
            super(Types.VARCHAR);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Argument build(final UUID value, final ConfigRegistry config) {
            return (p, s, c) -> s.setString(p, value.toString());
        }
    }
}
