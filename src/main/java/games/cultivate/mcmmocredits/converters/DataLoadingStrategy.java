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
import com.google.gson.JsonParser;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.user.User;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Strategies for Data Converters.
 */
public enum DataLoadingStrategy implements BiFunction<ConverterProperties, Path, List<User>> {
    CSV {
        @Override
        public List<User> apply(final ConverterProperties converterProperties, final Path path) {
            try {
                return Files.readAllLines(path.resolve("database.csv")).stream().map(User::fromCSV).toList();
            } catch (IOException e) {
                e.printStackTrace();
                return List.of();
            }
        }
    }, INTERNAL {
        @Override
        public List<User> apply(final ConverterProperties converterProperties, final Path path) {
            Database oldDatabase = converterProperties.oldDatabase().create(path);
            List<User> users = oldDatabase.getAllUsers();
            oldDatabase.disable();
            return users;
        }
    }, GUI_REDEEM_MCMMO {
        @Override
        public List<User> apply(final ConverterProperties converterProperties, final Path path) {
            return getUsersForPlugin(converterProperties, path);
        }
    }, MORPH_REDEEM {
        @Override
        public List<User> apply(final ConverterProperties converterProperties, final Path path) {
            return getUsersForPlugin(converterProperties, path);
        }
    };

    private static final HttpClient CLIENT = HttpClient.newBuilder().build();

    private static List<User> getUsersForPlugin(final ConverterProperties properties, final Path path) {
        Map<UUID, String> cached = loadMojangCache();
        List<User> users = new ArrayList<>();
        for (File file : path.toFile().listFiles()) {
            UUID uuid = UUID.fromString(file.getName().substring(0, file.getName().length() - 4));
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            String username = cached.containsKey(uuid) ? cached.get(uuid) : getMojangUsername(uuid, properties.failureDelay());
            users.add(new User(uuid, username, config.getInt("Credits", 0), config.getInt("Credits_Spent", 0)));
            try {
                Thread.sleep(properties.requestDelay());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return users;
            }
        }
        return users;
    }

    /**
     * Loads the usercache.json to grab names locally before requesting from Mojang.
     *
     * @return A map representing usercache.json.
     */
    private static Map<UUID, String> loadMojangCache() {
        try {
            String json = new String(Files.readAllBytes(new File(new File("."), "usercache.json").toPath()));
            return JsonParser.parseString(json).getAsJsonArray().asList().stream()
                    .map(JsonElement::getAsJsonObject)
                    .collect(Collectors.toMap(x -> UUID.fromString(x.get("uuid").getAsString()), y -> y.get("name").getAsString()));
        } catch (IOException e) {
            e.printStackTrace();
            return Map.of();
        }
    }

    /**
     * Requests a username from Mojang using the provided UUID.
     *
     * @param uuid         The UUID.
     * @param failureDelay Delay to wait before trying again on a failure.
     * @return The username.
     */
    private static String getMojangUsername(final UUID uuid, final long failureDelay) {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create("https://api.mojang.com/user/profile/" + uuid)).GET().build();
            HttpResponse<String> response = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
            int responseCode = response.statusCode();
            JsonElement element = JsonParser.parseString(response.body());
            if (!element.isJsonObject() || responseCode != 200) {
                Bukkit.getLogger().log(Level.WARNING, () -> String.format("Bad response received Retrying shortly. UUID: %s, Response Code: %d.", uuid, responseCode));
                Thread.sleep(failureDelay);
                getMojangUsername(uuid, failureDelay);
            }
            return element.getAsJsonObject().get("name").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "";
    }
}
