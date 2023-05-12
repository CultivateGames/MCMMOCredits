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
import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.database.DatabaseType;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserDAO;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Data Converter used to add Users from external plugin data.
 * This converter connects to Mojang to validate username data, as it is not stored by other plugins.
 */
public final class PluginConverter implements Converter {
    private final MainConfig config;
    private final UserDAO destinationDAO;
    private final ConverterType type;
    private final HttpClient client;
    private final long retryDelay;
    private final long attemptDelay;
    private List<User> sourceUsers;

    @Inject
    public PluginConverter(final MainConfig config, final UserDAO destinationDAO) {
        this.config = config;
        this.destinationDAO = destinationDAO;
        this.type = config.getConverterType("converter", "type");
        this.client = HttpClient.newHttpClient();
        this.retryDelay = this.config.getLong("converter", "external", "retry-delay");
        this.attemptDelay = this.config.getLong("converter", "external", "attempt-delay");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean load() {
        Path pluginPath = this.type == ConverterType.EXTERNAL_GRM ? Path.of("GuiRedeemMCMMO", "playerdata") : Path.of("MorphRedeem", "PlayerData");
        Path filePath = Bukkit.getPluginsFolder().toPath().resolve(pluginPath);
        File[] files = filePath.toFile().listFiles();
        if (files == null || files.length < 1) {
            throw new IllegalStateException("External converter plugin path is empty!");
        }
        Map<UUID, int[]> userMap = new HashMap<>();
        for (File f : files) {
            YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);
            int credits = conf.getInt("Credits");
            int redeemed = this.type == ConverterType.EXTERNAL_GRM ? 0 : conf.getInt("Credits_Spent");
            userMap.put(UUID.fromString(f.getName().replace(".yml", "")), new int[]{credits, redeemed});
        }
        Map<UUID, String> users = new HashMap<>();
        int mapSize = userMap.keySet().size();
        for (UUID uuid : userMap.keySet()) {
            try {
                users.put(uuid, this.sendMojangRequest(uuid));
                Thread.sleep(this.attemptDelay);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
            if (users.size() % 50 == 0) {
                Bukkit.getLogger().log(Level.INFO, "Progress: {0}/{1}", new Object[]{users.size(), mapSize});
            }
        }
        this.sourceUsers = users.entrySet().stream().map(x -> {
            int[] stat = userMap.get(x.getKey());
            return new User(x.getKey(), x.getValue(), stat[0], stat[1]);
        }).toList();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean convert() {
        this.destinationDAO.addUsers(this.sourceUsers);
        if (this.config.getDatabaseProperties("settings", "database").type() == DatabaseType.H2) {
            this.destinationDAO.useHandle(x -> x.execute("CHECKPOINT SYNC"));
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean verify() {
        List<User> updatedCurrentUsers = this.destinationDAO.getAllUsers();
        return this.sourceUsers.parallelStream().allMatch(updatedCurrentUsers::contains);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disable() {
        //nothing to clean up for plugins.
    }

    private String sendMojangRequest(final UUID uuid) throws IOException, InterruptedException {
        URI uri = URI.create("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString());
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
        String result = response.body();
        int responseCode = response.statusCode();
        JsonElement element = JsonParser.parseString(result);
        if (!element.isJsonObject() || responseCode != 200) {
            this.logConversionError(uuid, responseCode);
            Thread.sleep(this.retryDelay);
            this.sendMojangRequest(uuid);
        }
        return element.getAsJsonObject().get("name").getAsString();
    }

    private void logConversionError(final UUID uuid, final int responseCode) {
        String log = String.format("""
                Malformed result received, waiting to resume! Do not shut off the server.
                Check this UUID when conversion is done %s.
                HTTP Response Code from Mojang Request: %d.
                If this repeatedly occurs, check validity of UUID or send less requests (code 429).
                """, uuid, responseCode);
        Bukkit.getLogger().log(Level.WARNING, log);
    }
}
