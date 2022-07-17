package games.cultivate.mcmmocredits.data;

import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.SettingsConfig;
import org.bukkit.Bukkit;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static games.cultivate.mcmmocredits.data.SQLStatements.*;

public abstract sealed class SQLDatabase implements Database permits MYSQLDatabase, SQLiteDatabase {
    private static final UUID ZERO_UUID = new UUID(0, 0);
    final SettingsConfig settings;
    final HikariDataSource hikari;
    private final MCMMOCredits plugin;
    private final Jdbi jdbi;

    @Inject
    SQLDatabase(SettingsConfig settings, MCMMOCredits plugin) {
        this.settings = settings;
        this.plugin = plugin;
        this.hikari = this.createDataSource();
        SQLStatements creation = settings.isMYSQL() ? MYSQL_CREATE_TABLE : SQLITE_CREATE_TABLE;
        this.jdbi = this.createJDBI();
        this.jdbi.useHandle(x -> x.execute(creation.toString()));
    }

    abstract Jdbi createJDBI();

    abstract HikariDataSource createDataSource();

    @Override
    public void disable() {
        if (this.hikari != null) {
            this.hikari.close();
        }
    }

    @Override
    public void addPlayer(UUID uuid, String username, int credits) {
        this.update(ADD_PLAYER, uuid.toString(), username, credits);
    }

    @Override
    public CompletableFuture<UUID> getUUID(String username) {
        return CompletableFuture.supplyAsync(() -> {
            UUID uuid = this.query(UUID.class, GET_UUID, username);
            return uuid != null ? uuid : ZERO_UUID;
        });
    }

    @Override
    public boolean doesPlayerExist(UUID uuid) {
        if (uuid == null || uuid.equals(ZERO_UUID)) {
            return false;
        }
        return this.query(String.class,GET_USERNAME, uuid.toString()) != null;
    }

    @Override
    public void setUsername(UUID uuid, String username) {
        this.update(SET_USERNAME, username, uuid.toString());
    }

    @Override
    public CompletableFuture<String> getUsername(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String content = this.query(String.class,GET_USERNAME, uuid.toString());
            return content != null ? content : "";
        });
    }

    @Override
    public void setCredits(UUID uuid, int credits) {
        this.update(SET_CREDITS, credits, uuid.toString());
    }

    @Override
    public void addCredits(UUID uuid, int credits) {
        this.update(ADD_CREDITS, credits, uuid.toString());
    }

    @Override
    public void takeCredits(UUID uuid, int credits) {
        this.update(TAKE_CREDITS, credits, uuid.toString());
    }

    @Override
    public int getCredits(UUID uuid) {
        Integer credits = this.query(Integer.class, GET_CREDITS, uuid.toString());
        return credits != null ? credits : 0;
    }

    private void update(SQLStatements statement, Object... args) {
       Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> this.jdbi.useHandle(x -> x.execute(statement.toString(), args)));
    }

    private <T> T query(Class<T> clazz, SQLStatements statement, Object... args) {
        try {
            return this.jdbi.withHandle(x -> x.select(statement.toString(), args).mapTo(clazz).one());
        } catch (IllegalStateException e) {
            return null;
        }
    }
}
