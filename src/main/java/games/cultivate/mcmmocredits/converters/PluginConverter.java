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
import games.cultivate.mcmmocredits.database.DatabaseProperties;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * Data Converter used to add Users from external plugin data.
 * This converter connects to Mojang to validate username data, as it is not stored by other plugins.
 */
public final class PluginConverter implements Converter {
    private final UserDAO destinationDAO;
    private final ConverterType type;
    private final HttpClient client;
    private final long retryDelay;
    private final long attemptDelay;
    private final List<User> sourceUsers;
    private final DatabaseProperties properties;

    /**
     * Constructs the object.
     *
     * @param config         The config to read converter properties.
     * @param destinationDAO The current UserDAO to write users.
     */
    @Inject
    public PluginConverter(final MainConfig config, final UserDAO destinationDAO) {
        this.destinationDAO = destinationDAO;
        this.sourceUsers = new ArrayList<>();
        this.client = HttpClient.newHttpClient();
        this.type = config.getConverterType("converter", "type");
        this.properties = config.getDatabaseProperties("settings", "database");
        this.retryDelay = config.getLong("converter", "external", "retry-delay");
        this.attemptDelay = config.getLong("converter", "external", "attempt-delay");
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
        Pattern yml = Pattern.compile(".yml");
        for (File f : files) {
            YamlConfiguration userConf = YamlConfiguration.loadConfiguration(f);
            int credits = userConf.getInt("Credits");
            int redeemed = userConf.getInt("Credits_Spent");
            UUID uuid = UUID.fromString(yml.matcher(f.getName()).replaceAll(""));
            try {
                this.sourceUsers.add(new User(uuid, this.sendMojangRequest(uuid), credits, redeemed));
                if (this.sourceUsers.size() % 100 == 0) {
                    Bukkit.getLogger().log(Level.INFO, "Progress: {0}/{1}", new Object[]{this.sourceUsers.size(), files.length});
                }
                Thread.sleep(this.attemptDelay);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean convert() {
        this.destinationDAO.addUsers(this.sourceUsers);
        if (this.properties.type() == DatabaseType.H2) {
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

    /**
     * Logs an error when conversion has failed.
     *
     * @param uuid         UUID of the user who couldn't be converted.
     * @param responseCode HTTP Response Code sent in the response.
     */
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
