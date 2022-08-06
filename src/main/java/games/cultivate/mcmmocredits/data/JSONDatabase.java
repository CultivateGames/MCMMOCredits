package games.cultivate.mcmmocredits.data;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

//TODO skeleton class
public final class JSONDatabase implements Database {
    @Override
    public void disable() {

    }

    @Override
    public void addPlayer(final UUID uuid, final String username, final int credits) {

    }

    @Override
    public CompletableFuture<UUID> getUUID(final String username) {
        return null;
    }

    @Override
    public boolean doesPlayerExist(final UUID uuid) {
        return false;
    }

    @Override
    public void setUsername(final UUID uuid, final String username) {

    }

    @Override
    public CompletableFuture<String> getUsername(final UUID uuid) {
        return null;
    }

    @Override
    public void setCredits(final UUID uuid, final int credits) {

    }

    @Override
    public int getCredits(final UUID uuid) {
        return 0;
    }
}
