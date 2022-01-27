package games.cultivate.mcmmocredits.database;

import java.sql.Connection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public abstract class DatabaseAdapter {

    protected abstract Connection getConnection();

    public abstract void enableAdapter();

    public abstract void disableAdapter();

    public abstract void addPlayer(UUID uuid, String username, int credits);

    public abstract void setUsername(UUID uuid, String username);

    public abstract CompletableFuture<String> getUsername(UUID uuid);

    public abstract void setCredits(UUID uuid, int credits);

    public abstract void addCredits(UUID uuid, int amount);

    public abstract void takeCredits(UUID uuid, int amount);

    public abstract boolean doesPlayerExist(UUID uuid);

    public abstract int getCredits(UUID uuid);

    public abstract CompletableFuture<UUID> getUUID(String username);
}