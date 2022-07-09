package games.cultivate.mcmmocredits.data;

import games.cultivate.mcmmocredits.keys.StringKey;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract sealed class Database permits SQLDatabase {
    private static Database database;

    public static Database getDatabase() {
        return database;
    }

    public static void loadFromConfig() {
        if (StringKey.DATABASE_ADAPTER.get().equals("mysql")) {
            database = new MYSQLDatabase();
        } else {
            database = new SQLiteDatabase();
        }
    }

    public abstract void disable();

    public abstract void addPlayer(UUID uuid, String username, int credits);

    public abstract CompletableFuture<UUID> getUUID(String username);

    public abstract boolean doesPlayerExist(UUID uuid);

    public abstract void setUsername(UUID uuid, String username);

    public abstract CompletableFuture<String> getUsername(UUID uuid);

    public abstract void setCredits(UUID uuid, int credits);

    public abstract int getCredits(UUID uuid);

    public void addCredits(UUID uuid, int credits) {
        this.setCredits(uuid, this.getCredits(uuid) + credits);
    }

    public void takeCredits(UUID uuid, int credits) {
        this.setCredits(uuid, Math.max(0, this.getCredits(uuid) - credits));
    }
}
