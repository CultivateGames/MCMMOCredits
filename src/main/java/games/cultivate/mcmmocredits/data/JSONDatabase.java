package games.cultivate.mcmmocredits.data;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

//TODO skeleton class
public final class JSONDatabase implements Database {
    @Override
    public void disable() {

    }

    @Override
    public void addPlayer(UUID uuid, String username, int credits) {

    }

    @Override
    public CompletableFuture<UUID> getUUID(String username) {
        return null;
    }

    @Override
    public boolean doesPlayerExist(UUID uuid) {
        return false;
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
    public int getCredits(UUID uuid) {
        return 0;
    }
}
