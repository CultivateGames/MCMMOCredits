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
import games.cultivate.mcmmocredits.config.properties.ConverterProperties;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.user.User;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Data Converter used to add Users from external plugin data.
 * This converter connects to Mojang to validate username data, as it is not always stored by other plugins.
 */
public final class PluginConverter extends AbstractConverter {
    private final HttpClient client;

    /**
     * Constructs the object.
     *
     * @param database   The current UserDAO to write users.
     * @param properties The properties of the converter.
     */
    @Inject
    public PluginConverter(final Database database, final ConverterProperties properties) {
        super(database, properties);
        this.client = HttpClient.newHttpClient();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load() throws IOException, InterruptedException {
        Map<UUID, String> cached = this.loadCache();
        Set<User> users = this.getUsers();
        File[] files = this.getExternalPath().toFile().listFiles();
        int size = files.length;
        for (File file : files) {
            UUID uuid = UUID.fromString(file.getName().substring(0, file.getName().length() - 4));
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            String username = cached.containsKey(uuid) ? cached.get(uuid) : this.getName(uuid);
            users.add(new User(uuid, username, config.getInt("Credits", 0), config.getInt("Credits_Spent", 0)));
            if (users.size() % 100 == 0) {
                Bukkit.getLogger().info("Progress: " + users.size() + "/" + size);
            }
            Thread.sleep(this.getConverterProperties().requestDelay());
        }
    }

    /**
     * Loads the usercache.json to grab names locally before requesting from Mojang.
     *
     * @return A map representing usercache.json.
     * @throws IOException If there is an issue loading the file.
     */
    private Map<UUID, String> loadCache() throws IOException {
        String json = new String(Files.readAllBytes(new File(new File("."), "usercache.json").toPath()));
        return JsonParser.parseString(json).getAsJsonArray().asList().stream()
                .map(JsonElement::getAsJsonObject)
                .collect(Collectors.toMap(x -> UUID.fromString(x.get("uuid").getAsString()), y -> y.get("name").getAsString()));
    }

    /**
     * Requests a username from Mojang using the provided UUID.
     *
     * @param uuid The UUID.
     * @return The username.
     * @throws IOException          Thrown if there is an issue reading the response.
     * @throws InterruptedException Thrown if the sleep between attempts is interrupted.
     */
    private String getName(final UUID uuid) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(URI.create("https://api.mojang.com/user/profile/" + uuid)).GET().build();
        HttpResponse<String> response = this.client.send(req, BodyHandlers.ofString());
        int responseCode = response.statusCode();
        JsonElement element = JsonParser.parseString(response.body());
        if (!element.isJsonObject() || responseCode != 200) {
            Bukkit.getLogger().log(Level.WARNING, () -> String.format("Bad response received Retrying shortly. UUID: %s, Response Code: %d.", uuid, responseCode));
            Thread.sleep(this.getConverterProperties().failureDelay());
            this.getName(uuid);
        }
        return element.getAsJsonObject().get("name").getAsString();
    }

    /**
     * Gets the correct path for external data conversions.
     *
     * @return The Path.
     */
    private Path getExternalPath() {
        return Bukkit.getPluginsFolder().toPath().resolve(this.getConverterProperties().type() == ConverterType.GUI_REDEEM_MCMMO ? Path.of("GuiRedeemMCMMO", "playerdata") : Path.of("MorphRedeem", "PlayerData"));
    }
}
