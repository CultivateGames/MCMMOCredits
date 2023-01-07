//
// MIT License
//
// Copyright (c) 2022 Cultivate Games
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
package games.cultivate.mcmmocredits.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.serializers.PlayerSerializer;
import games.cultivate.mcmmocredits.util.FileUtil;
import games.cultivate.mcmmocredits.util.JSONUser;
import org.bukkit.Bukkit;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * {@link Database} which utilizes local JSON storage to store player data.
 * <br>
 * Usage of this database is not recommended due to IO speed. It will slow down as the database increases in size. It is provided in case a user does not want to use a SQL-based storage option, or the user wants a temporary data store.
 */
public final class JSONDatabase implements Database {
    private final Gson gson;
    private final File file;
    private final MCMMOCredits plugin;
    private List<JSONUser> users;

    @Inject
    JSONDatabase(final MCMMOCredits plugin, final @Named("dir") Path dir) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().registerTypeAdapter(JSONUser.class, new PlayerSerializer()).create();
        String fileName = "database.json";
        FileUtil.createFile(dir, fileName);
        this.file = dir.resolve(fileName).toFile();
        JSONUser notch = new JSONUser(ZERO_UUID, "Notch", 0, 0);
        if (this.file.length() == 0) {
            this.users = new ArrayList<>();
            this.users.add(notch);
            this.writeUsers();
            return;
        }
        try {
            this.users = new ArrayList<>(Arrays.asList(this.gson.fromJson(Files.readString(this.file.toPath()), JSONUser[].class)));
            //Remove default user when we have existing data store.
            this.users.remove(notch);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disable() {
        this.writeUsers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPlayer(final UUID uuid, final String username, final int credits, final int redeemed) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.users.add(new JSONUser(uuid, username, credits, redeemed));
            this.writeUsers();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<UUID> getUUID(final String username) {
        return CompletableFuture.supplyAsync(() -> this.findUserByUsername(username).map(JSONUser::uuid).orElse(ZERO_UUID));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean doesPlayerExist(final UUID uuid) {
        if (uuid == null || uuid.equals(ZERO_UUID)) {
            return false;
        }
        return this.findUserByUUID(uuid).isPresent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUsername(final UUID uuid, final String username) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> this.findUserByUUID(uuid).ifPresent(user -> {
            this.users.remove(user);
            this.users.add(new JSONUser(user.uuid(), username, user.credits(), user.redeemed()));
            this.writeUsers();
        }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername(final UUID uuid) {
        return this.findUserByUUID(uuid).map(JSONUser::username).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setCredits(final UUID uuid, final int credits) {
        Optional<JSONUser> possibleUser = this.findUserByUUID(uuid);
        if (credits < 0 || possibleUser.isEmpty()) {
            return false;
        }
        JSONUser user = possibleUser.orElseThrow();
        this.users.remove(user);
        this.users.add(new JSONUser(user.uuid(), user.username(), credits, user.redeemed()));
        this.writeUsers();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCredits(final UUID uuid) {
        return this.findUserByUUID(uuid).map(JSONUser::credits).orElse(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addCredits(final UUID uuid, final int credits) {
        return this.setCredits(uuid, this.getCredits(uuid) + credits);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean takeCredits(final UUID uuid, final int credits) {
        return this.setCredits(uuid, Math.max(0, this.getCredits(uuid) - credits));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addRedeemedCredits(final UUID uuid, final int credits) {
        Optional<JSONUser> possibleUser = this.findUserByUUID(uuid);
        if (credits < 0 || possibleUser.isEmpty()) {
            return false;
        }
        JSONUser user = possibleUser.orElseThrow();
        this.users.remove(user);
        this.users.add(new JSONUser(user.uuid(), user.username(), user.credits(), credits));
        this.writeUsers();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRedeemedCredits(final UUID uuid) {
        return this.findUserByUUID(uuid).map(JSONUser::redeemed).orElse(0);
    }

    /**
     * Writes {@link JSONUser}s to the database.json file, stored as an array.
     */
    private void writeUsers() {
        try (FileWriter fw = new FileWriter(this.file);
             JsonWriter jw = this.gson.newJsonWriter(fw)) {
            jw.beginArray();
            for (JSONUser user : this.users) {
                jw.beginObject().name("uuid").value(user.uuid().toString())
                        .name("username").value(user.username())
                        .name("credits").value(user.credits())
                        .name("redeemed").value(user.redeemed()).endObject().flush();
            }
            jw.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to find a {@link JSONUser} in our database via a {@link UUID}
     *
     * @param uuid UUID of the {@link JSONUser} we want to find.
     * @return Optional containing the {@link JSONUser} if they exist.
     */
    private Optional<JSONUser> findUserByUUID(final UUID uuid) {
        return this.users.stream().filter(u -> u.uuid().equals(uuid)).findAny();
    }

    /**
     * Used to find a {@link JSONUser} in our database via a {@link String}-based username.
     *
     * @param username username of the {@link JSONUser} we want to find.
     * @return Optional containing the {@link JSONUser} if they exist.
     */
    private Optional<JSONUser> findUserByUsername(final String username) {
        return this.users.stream().filter(u -> u.username().equalsIgnoreCase(username)).findAny();
    }
}
