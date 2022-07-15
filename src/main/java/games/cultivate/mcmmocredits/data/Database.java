package games.cultivate.mcmmocredits.data;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public sealed interface Database permits SQLDatabase {

    void disable();

    void addPlayer(UUID uuid, String username, int credits);

    CompletableFuture<UUID> getUUID(String username);

    boolean doesPlayerExist(UUID uuid);

    void setUsername(UUID uuid, String username);

    CompletableFuture<String> getUsername(UUID uuid);

    void setCredits(UUID uuid, int credits);

    int getCredits(UUID uuid);

    default void addCredits(UUID uuid, int credits) {
        this.setCredits(uuid, this.getCredits(uuid) + credits);
    }

    default void takeCredits(UUID uuid, int credits) {
        this.setCredits(uuid, Math.max(0, this.getCredits(uuid) - credits));
    }
}
