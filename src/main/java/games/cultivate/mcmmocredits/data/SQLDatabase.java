package games.cultivate.mcmmocredits.data;

import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.MCMMOCredits;
import org.bukkit.Bukkit;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.StatementException;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static games.cultivate.mcmmocredits.data.SQLStatement.*;

public sealed class SQLDatabase implements Database permits MYSQLDatabase, SQLiteDatabase {
    private static final UUID ZERO_UUID = new UUID(0, 0);
    private final MCMMOCredits plugin;
    protected HikariDataSource hikari;
    protected Jdbi jdbi;

    @Inject
    SQLDatabase(final MCMMOCredits plugin) {
        this.plugin = plugin;
    }

    @Override
    public void disable() {
        if (this.hikari != null) {
            this.hikari.close();
        }
    }

    @Override
    public void addPlayer(final UUID uuid, final String username, final int credits) {
        this.update(ADD_PLAYER, uuid.toString(), username, credits);
    }

    @Override
    public CompletableFuture<UUID> getUUID(final String username) {
        return CompletableFuture.supplyAsync(() -> {
            UUID uuid = this.query(UUID.class, GET_UUID, username);
            return uuid != null ? uuid : ZERO_UUID;
        });
    }

    @Override
    public boolean doesPlayerExist(final UUID uuid) {
        if (uuid == null || uuid.equals(ZERO_UUID)) {
            return false;
        }
        return this.query(String.class, GET_USERNAME, uuid.toString()) != null;
    }

    @Override
    public void setUsername(final UUID uuid, final String username) {
        this.update(SET_USERNAME, username, uuid.toString());
    }

    @Override
    public String getUsername(final UUID uuid) {
        String content = this.query(String.class, GET_USERNAME, uuid.toString());
        return content != null ? content : "";
    }

    @Override
    public boolean setCredits(final UUID uuid, final int credits) {
        return this.updateSync(SET_CREDITS, credits, uuid.toString());
    }

    @Override
    public boolean addCredits(final UUID uuid, final int credits) {
        return this.updateSync(ADD_CREDITS, credits, uuid.toString());
    }

    @Override
    public boolean takeCredits(final UUID uuid, final int credits) {
        return this.updateSync(TAKE_CREDITS, credits, uuid.toString());
    }

    @Override
    public int getCredits(final UUID uuid) {
        Integer credits = this.query(Integer.class, GET_CREDITS, uuid.toString());
        return credits != null ? credits : 0;
    }

    private void update(final SQLStatement statement, final Object... args) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> this.jdbi.useHandle(x -> x.execute(statement.toString(), args)));
    }

    //Credit Updates are done synchronously so that information is current.
    private boolean updateSync(final SQLStatement statement, final Object... args) {
        try {
            return this.jdbi.withHandle(x -> x.execute(statement.toString(), args) > 0);
        } catch (StatementException e) {
            return false;
        }
    }

    private <T> T query(final Class<T> clazz, final SQLStatement statement, final Object... args) {
        try {
            return this.jdbi.withHandle(x -> x.select(statement.toString(), args).mapTo(clazz).one());
        } catch (IllegalStateException e) {
            return null;
        }
    }
}
