package games.cultivate.mcmmocredits.data;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class InputStorage {
    private final Map<UUID, CompletableFuture<String>> map;

    public InputStorage() {
        this.map = new ConcurrentHashMap<>();
    }

    public void remove(UUID uuid) {
        if (map.containsKey(uuid)) {
            map.get(uuid).complete(null);
            map.remove(uuid);
        }
    }

    public boolean contains(UUID uuid) {
        return map.containsKey(uuid);
    }

    public void add(UUID uuid) {
        map.putIfAbsent(uuid, new CompletableFuture<>());
    }

    public void act(UUID uuid, Consumer<? super String> action) {
        map.get(uuid).thenAcceptAsync(action).whenCompleteAsync((i, throwable) -> this.remove(uuid));
    }

    public void complete(UUID uuid, String completion) {
        map.get(uuid).complete(completion);
    }
}
