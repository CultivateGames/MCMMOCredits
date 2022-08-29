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
        JSONUser notch = new JSONUser(new UUID(0, 0), "Notch", 0);
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

    @Override
    public void disable() {
        this.writeUsers();
    }

    @Override
    public void addPlayer(final UUID uuid, final String username, final int credits) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.users.add(new JSONUser(uuid, username, credits));
            this.writeUsers();
        });
    }

    @Override
    public CompletableFuture<UUID> getUUID(final String username) {
        return CompletableFuture.supplyAsync(() -> this.findUserByUsername(username).map(JSONUser::uuid).orElse(new UUID(0, 0)));
    }

    @Override
    public boolean doesPlayerExist(final UUID uuid) {
        if (uuid == null || uuid.equals(new UUID(0, 0))) {
            return false;
        }
        return this.users.stream().anyMatch(u -> u.uuid().equals(uuid));
    }

    @Override
    public void setUsername(final UUID uuid, final String username) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> this.findUserByUUID(uuid).ifPresent(user -> {
            this.users.remove(user);
            this.users.add(new JSONUser(user.uuid(), username, user.credits()));
            this.writeUsers();
        }));
    }

    @Override
    public String getUsername(final UUID uuid) {
        return this.findUserByUUID(uuid).map(JSONUser::username).orElse(null);
    }

    @Override
    public boolean setCredits(final UUID uuid, final int credits) {
        Optional<JSONUser> possibleUser = this.findUserByUUID(uuid);
        if (credits < 0 || possibleUser.isEmpty()) {
            return false;
        }
        JSONUser user = possibleUser.orElseThrow();
        this.users.remove(user);
        this.users.add(new JSONUser(user.uuid(), user.username(), credits));
        this.writeUsers();
        return true;
    }

    @Override
    public int getCredits(final UUID uuid) {
        return this.findUserByUUID(uuid).map(JSONUser::credits).orElse(0);
    }

    @Override
    public boolean addCredits(final UUID uuid, final int credits) {
        return this.setCredits(uuid, this.getCredits(uuid) + credits);
    }

    @Override
    public boolean takeCredits(final UUID uuid, final int credits) {
        return this.setCredits(uuid, Math.max(0, this.getCredits(uuid) - credits));
    }

    private void writeUsers() {
        try (FileWriter fw = new FileWriter(this.file);
             JsonWriter jw = this.gson.newJsonWriter(fw)) {
            jw.beginArray();
            for (JSONUser user : this.users) {
                jw.beginObject().name("uuid").value(user.uuid().toString())
                        .name("name").value(user.username())
                        .name("credits").value(user.credits()).endObject().flush();
            }
            jw.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Optional<JSONUser> findUserByUUID(final UUID uuid) {
        return this.users.stream().filter(u -> u.uuid().equals(uuid)).findAny();
    }

    private Optional<JSONUser> findUserByUsername(final String username) {
        return this.users.stream().filter(u -> u.username().equalsIgnoreCase(username)).findAny();
    }
}
