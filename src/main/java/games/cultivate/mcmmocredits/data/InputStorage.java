package games.cultivate.mcmmocredits.data;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class InputStorage {
    private final Map<UUID, CompletableFuture<String>> map;

    public InputStorage() {
        this.map = new ConcurrentHashMap<>();
    }

    public void remove(final UUID uuid) {
        if (this.contains(uuid)) {
            this.map.get(uuid).complete(null);
            this.map.remove(uuid);
        }
    }

    public boolean contains(final UUID uuid) {
        return this.map.containsKey(uuid);
    }

    public void add(final UUID uuid) {
        this.remove(uuid);
        this.map.put(uuid, new CompletableFuture<>());
    }

    public void act(final UUID uuid, final Consumer<? super String> action) {
        this.add(uuid);
        this.map.get(uuid).thenAccept(action).whenComplete((i, throwable) -> this.remove(uuid));
    }

    public void complete(final UUID uuid, final String completion) {
        if (this.contains(uuid)) {
            this.map.get(uuid).complete(completion);
        }
    }
}
