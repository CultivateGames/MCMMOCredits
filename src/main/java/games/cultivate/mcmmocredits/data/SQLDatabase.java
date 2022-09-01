package games.cultivate.mcmmocredits.data;

import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.MCMMOCredits;
import org.bukkit.Bukkit;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.StatementException;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static games.cultivate.mcmmocredits.data.SQLStatement.*;

/**
 * Class used to represent a SQL-based {@link Database}.
 */
public sealed class SQLDatabase implements Database permits MYSQLDatabase, SQLiteDatabase {
    private static final UUID ZERO_UUID = new UUID(0, 0);
    private final MCMMOCredits plugin;
    protected HikariDataSource hikari;
    protected Jdbi jdbi;

    @Inject
    SQLDatabase(final MCMMOCredits plugin) {
        this.plugin = plugin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disable() {
        if (this.hikari != null) {
            this.hikari.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPlayer(final UUID uuid, final String username, final int credits) {
        this.update(ADD_PLAYER, uuid.toString(), username, credits);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<UUID> getUUID(final String username) {
        return CompletableFuture.supplyAsync(() -> {
            UUID uuid = this.query(UUID.class, GET_UUID, username);
            return uuid != null ? uuid : ZERO_UUID;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean doesPlayerExist(final UUID uuid) {
        if (uuid == null || uuid.equals(ZERO_UUID)) {
            return false;
        }
        return this.query(String.class, GET_USERNAME, uuid.toString()) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUsername(final UUID uuid, final String username) {
        this.update(SET_USERNAME, username, uuid.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername(final UUID uuid) {
        String content = this.query(String.class, GET_USERNAME, uuid.toString());
        return content != null ? content : "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setCredits(final UUID uuid, final int credits) {
        return this.updateSync(SET_CREDITS, credits, uuid.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addCredits(final UUID uuid, final int credits) {
        return this.updateSync(ADD_CREDITS, credits, uuid.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean takeCredits(final UUID uuid, final int credits) {
        return this.updateSync(TAKE_CREDITS, credits, uuid.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCredits(final UUID uuid) {
        Integer credits = this.query(Integer.class, GET_CREDITS, uuid.toString());
        return credits != null ? credits : 0;
    }

    /**
     * Used to execute a SQL-based "UPDATE" query asynchronously.
     *
     * @param statement {@link SQLStatement} we are using to update the database.
     * @param args      {@link Object}s we are populating a {@link PreparedStatement} with.
     * @see #addPlayer(UUID, String, int)
     */
    private void update(final SQLStatement statement, final Object... args) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> this.jdbi.useHandle(x -> x.execute(statement.toString(), args)));
    }

    /**
     * Used to execute a SQL-based "UPDATE" query synchronously. Specifically used for Credit balance updates.
     *
     * @param statement {@link SQLStatement} we are using to update the database.
     * @param args      {@link Object}s we are populating a {@link PreparedStatement} with.
     * @return If the update was successful.
     * @see #setCredits(UUID, int)
     */
    private boolean updateSync(final SQLStatement statement, final Object... args) {
        try {
            return this.jdbi.withHandle(x -> x.execute(statement.toString(), args) > 0);
        } catch (StatementException e) {
            return false;
        }
    }

    /**
     * Used to execute a SQL-based "SELECT" query to fetch information from the database.
     *
     * @param clazz     type of the value we are trying to fetch.
     * @param statement {@link SQLStatement} we are using to query the database.
     * @param args      {@link Object}s we are populating a {@link PreparedStatement} with.
     * @param <T>       type of the value we are trying to fetch.
     * @return one result from the database, if it exists.
     */
    private <T> T query(final Class<T> clazz, final SQLStatement statement, final Object... args) {
        try {
            return this.jdbi.withHandle(x -> x.select(statement.toString(), args).mapTo(clazz).one());
        } catch (IllegalStateException e) {
            return null;
        }
    }
}
