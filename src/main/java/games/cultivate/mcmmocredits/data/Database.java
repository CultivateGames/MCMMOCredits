package games.cultivate.mcmmocredits.data;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public sealed interface Database permits SQLDatabase, JSONDatabase {

    void disable();

    void addPlayer(final UUID uuid, final String username, final int credits);

    CompletableFuture<UUID> getUUID(final String username);

    boolean doesPlayerExist(final UUID uuid);

    void setUsername(final UUID uuid, final String username);

    CompletableFuture<String> getUsername(final UUID uuid);

    void setCredits(final UUID uuid, final int credits);

    int getCredits(final UUID uuid);

    default void addCredits(final UUID uuid, final int credits) {
        this.setCredits(uuid, this.getCredits(uuid) + credits);
    }

    default void takeCredits(final UUID uuid, final int credits) {
        this.setCredits(uuid, Math.max(0, this.getCredits(uuid) - credits));
    }
}
