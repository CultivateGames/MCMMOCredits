package games.cultivate.mcmmocredits.util;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Object which represents a Chat Queue. Stores the {@link UUID} of a user, and a {@link CompletableFuture} representing the response status of the user as a {@link Map}.
 */
public final class InputStorage {
    private final Map<UUID, CompletableFuture<String>> map;

    public InputStorage() {
        this.map = new ConcurrentHashMap<>();
    }

    /**
     * Removes an entry associated with the provided {@link UUID} if it exists.
     *
     * @param uuid The {@link UUID} of the associated user to remove from the underlying map.
     */
    public void remove(final UUID uuid) {
        if (this.contains(uuid)) {
            this.map.get(uuid).complete(null);
            this.map.remove(uuid);
        }
    }

    /**
     * Indicates if an entry exists in the underlying {@link Map}
     *
     * @param uuid The {@link UUID} of the associated user to search for.
     * @return Whether the entry exists for the provided {@link UUID}
     */
    public boolean contains(final UUID uuid) {
        return this.map.containsKey(uuid);
    }

    /**
     * @param uuid The {@link UUID} of the associated user.
     */
    public void add(final UUID uuid) {
        this.remove(uuid);
        this.map.put(uuid, new CompletableFuture<>());
    }

    /**
     * Performs the provided {@link Consumer} using data from the completed {@link CompletableFuture} associated with the {@link UUID}.
     * Attempts to add the entry if it doesn't exist, and removes the entry after the action is completed.
     *
     * @param uuid   The {@link UUID} of the associated user.
     * @param action Action to perform after {@link CompletableFuture} is completed with data.
     */
    public void act(final UUID uuid, final Consumer<? super String> action) {
        this.add(uuid);
        this.map.get(uuid).thenAccept(action).whenComplete((i, throwable) -> this.remove(uuid));
    }

    /**
     * Completes the {@link CompletableFuture} associated with the provided {@link UUID}
     *
     * @param uuid       The {@link UUID} of the associated user.
     * @param completion String used to complete the {@link CompletableFuture}
     */
    public void complete(final UUID uuid, final String completion) {
        if (this.contains(uuid)) {
            this.map.get(uuid).complete(completion);
        }
    }
}
