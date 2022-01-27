package games.cultivate.mcmmocredits.database;

import java.sql.Connection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQLAdapter extends DatabaseAdapter {

    public MySQLAdapter() {

    }

    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public void enableAdapter() {

    }

    @Override
    public void disableAdapter() {

    }

    @Override
    public void addPlayer(UUID uuid, String username, int credits) {

    }

    @Override
    public void setUsername(UUID uuid, String username) {

    }

    @Override
    public CompletableFuture<String> getUsername(UUID uuid) {
        return null;
    }

    @Override
    public void setCredits(UUID uuid, int credits) {

    }

    @Override
    public void addCredits(UUID uuid, int amount) {

    }

    @Override
    public void takeCredits(UUID uuid, int amount) {

    }

    @Override
    public boolean doesPlayerExist(UUID uuid) {
        return false;
    }

    @Override
    public int getCredits(UUID uuid) {
        return 0;
    }

    @Override
    public CompletableFuture<UUID> getUUID(String username) {
        return null;
    }
}
