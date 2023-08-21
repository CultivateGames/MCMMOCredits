//
// MIT License
//
// Copyright (c) 2023 Cultivate Games
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package games.cultivate.mcmmocredits.converters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import games.cultivate.mcmmocredits.database.AbstractDatabase;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.util.MojangUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Plugin which uses external plugin data and the usercache to convert.
 */
public final class PluginConverter implements Converter {
    private final AbstractDatabase database;
    private final List<User> users = new ArrayList<>();
    private final Map<UUID, String> cache;
    private final long requestTime;
    private final long failureTime;
    private final Path path;

    /**
     * Constructs the object.
     *
     * @param database    The current database.
     * @param path        The path to the external plugin data.
     * @param requestTime The time between Mojang requests if needed.
     * @param failureTime The time between re-attempts if a Mojang request fails.
     * @throws IOException Thrown if loading the cache fails.
     */
    public PluginConverter(final AbstractDatabase database, final Path path, final long requestTime, final long failureTime) throws IOException {
        this.database = database;
        this.path = path;
        this.requestTime = requestTime;
        this.failureTime = failureTime;
        this.cache = this.loadMojangCache();
        this.loadUsers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Boolean> run() {
        return this.database.addUsers(this.users)
                .thenAccept(x -> { if (this.database.isH2()) this.database.jdbi().useHandle(y -> y.execute("CHECKPOINT SYNC")); })
                .thenCompose(y -> this.database.getAllUsers())
                .thenApply(z -> new HashSet<>(this.users).containsAll(z));
    }

    /**
     * Loads users from the provided file path.
     */
    private void loadUsers() {
        try (Stream<Path> stream = Files.list(this.path)) {
            stream.forEach(x -> {
                UUID uuid = UUID.fromString(x.getFileName().toString().replace(".yml", ""));
                YamlConfiguration config = YamlConfiguration.loadConfiguration(x.toFile());
                String username = this.cache.get(uuid);
                if (username != null) {
                    this.users.add(new User(uuid, username, config.getInt("Credits", 0), config.getInt("Credits_Spent", 0)));
                    return;
                }
                CompletableFuture<String> future = this.handleName(uuid, this.requestTime, this.failureTime);
                this.users.add(new User(uuid, future.join(), config.getInt("Credits", 0), config.getInt("Credits_Spent", 0)));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attempts to fetch name for a UUID from Mojang.
     *
     * @param uuid  The UUID.
     * @param delay delay between requests.
     * @param retry delay between failed requests.
     * @return The username.
     */
    private CompletableFuture<String> handleName(final UUID uuid, final long delay, final long retry) {
        return MojangUtil.getNameAsync(uuid)
                .completeOnTimeout(null, 15, TimeUnit.SECONDS)
                .thenCompose(name -> name != null ? this.sleep(delay, name) : this.sleep(retry, null).thenCompose(v -> this.handleName(uuid, delay, retry)));
    }

    /**
     * Returns the provided result after waiting for provided delay time.
     *
     * @param delay  The delay.
     * @param result The result.
     * @param <T>    The type of the result.
     * @return The result.
     */
    private <T> CompletableFuture<T> sleep(final long delay, T result) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return result;
        });
    }

    /**
     * Loads users from the usercache.json
     *
     * @return Cached users.
     * @throws IOException Thrown if there is an issue reading the file.
     */
    private Map<UUID, String> loadMojangCache() throws IOException {
        String json = new String(Files.readAllBytes(this.path.resolve("usercache.json")));
        Map<UUID, String> map = new HashMap<>();
        for (JsonElement element : JsonParser.parseString(json).getAsJsonArray()) {
            JsonObject obj = element.getAsJsonObject();
            map.put(UUID.fromString(obj.get("uuid").getAsString()), obj.get("name").getAsString());
        }
        return map;
    }
}
