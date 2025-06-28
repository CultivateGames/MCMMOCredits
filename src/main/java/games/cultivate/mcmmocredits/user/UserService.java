//
// MIT License
//
// Copyright (c) 2024 Cultivate Games
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
package games.cultivate.mcmmocredits.user;

import games.cultivate.mcmmocredits.storage.StorageService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.cloud.SenderMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The public implementation of the UserService.
 */
public final class UserService {
    private final Map<String, CommandSourceData> usernameCache;
    private final Map<UUID, CommandSourceData> uuidCache;
    private final StorageService storageService;

    @Inject
    public UserService(final StorageService storageService) {
        this.storageService = storageService;
        this.usernameCache = new ConcurrentHashMap<>();
        this.uuidCache = new ConcurrentHashMap<>();
    }

    /**
     * Adds a user to the service.
     *
     * @param data Data to add to the service.
     * @return data which repesents the user.
     */
    public CommandSourceData addUser(final CommandSourceData data) {
        if (this.storageService.addUser(data)) {
            this.put(data);
        }
        return data;
    }

    /**
     * Adds a user to the service using the provided player.
     *
     * @param player The player
     * @return data which represents the user.
     */
    public CommandSourceData addUser(final Player player) {
        return this.addUser(new CommandSourceData(player));
    }

    /**
     * Gets a user based on the provided player, or creates the user if they do not exist.
     *
     * @param player The player to wrap around.
     * @return The user.
     */
    public CommandSourceData getOrCreate(final Player player) {
        CommandSourceData data = this.getUser(player.getUniqueId()).orElseGet(() -> this.addUser(player));
        this.put(data);
        return data;
    }

    /**
     * Defines any required actions to be taken when a user leaves.
     *
     * @param player The player who is leaving.
     */
    public void onLogout(final Player player) {
        this.uuidCache.remove(player.getUniqueId());
        this.usernameCache.remove(player.getName());
    }

    /**
     * Update the user located at the provided UUID with the provided User.
     *
     * @param data data which represents the user.
     */
    public boolean updateUser(final CommandSourceData data) {
        if (this.storageService.updateUser(data)) {
            this.put(data);
            return true;
        }
        return false;
    }

    public List<CommandSourceData> getUserGroup(final int limit, final int offset) {
        return this.storageService.getUserGroup(limit, offset);
    }

    public Optional<CommandSourceData> getUser(final CommandSource source) {
        if (source.isConsole()) {
            return Optional.empty();
        }
        var data = source.data();
        return this.getUser(data.uuid()).or(() -> this.getUser(data.username())).or(Optional::empty);
    }

    /**
     * Gets a user based on UUID if they exist.
     *
     * @param uuid The uuid of the user.
     * @return The user, or empty optional if they do not exist.
     */
    public Optional<CommandSourceData> getUser(final UUID uuid) {
        return this.getValue(this.uuidCache, uuid);
    }

    /**
     * Gets a user based on String username if they exist.
     *
     * @param username The username of the user.
     * @return The user, or empty optional if they do not exist.
     */
    public Optional<CommandSourceData> getUser(final String username) {
        return this.getValue(this.usernameCache, username);
    }

    /**
     * Gets all online users by converting all online players.
     *
     * @return The online users.
     */
    public Set<CommandSourceData> getOnlineUsers() {
        return Bukkit.getOnlinePlayers().stream().map(this::getOrCreate).collect(Collectors.toSet());
    }

    /**
     * Gets data for all online players, and removes the record representing the provided CommandSource.
     *
     * @param source Source of the command.
     * @return The online users, with the executor removed.
     */
    public Set<CommandSourceData> getOnlineUsers(final CommandSource source) {
        Set<CommandSourceData> users = this.getOnlineUsers();
        users.removeIf(x -> x.uuid().equals(source.data().uuid()));
        return users;
    }

    /**
     * Creates a {@link SenderMapper} out of the current UserService.
     *
     * @return A SenderMapper.
     */
    @SuppressWarnings("UnstableApiUsage")
    public SenderMapper<CommandSourceStack, CommandSource> asMapper() {
        return SenderMapper.create(m -> {
            if (m.getSender() instanceof Player player) {
                return new UserCommandSource(m, this.getUser(player.getUniqueId()).orElse(null));
            }
            return CommandSource.console(m);
        }, CommandSource::stack);
    }

    public Optional<Player> asPlayer(final CommandSourceData data) {
        return Optional.ofNullable(Bukkit.getPlayer(data.uuid()));
    }

    private <K> Optional<CommandSourceData> getValue(final Map<K, CommandSourceData> cache, final K key) {
        if (cache.containsKey(key)) {
            return Optional.ofNullable(cache.get(key));
        }
        Optional<CommandSourceData> csd = switch (key) {
            case String s -> this.storageService.getUser(s);
            case UUID u -> this.storageService.getUser(u);
            default -> Optional.empty();
        };
        csd.ifPresent(this::put);
        return csd;
    }

    private void put(final CommandSourceData data) {
        this.uuidCache.put(data.uuid(), data);
        this.usernameCache.put(data.username(), data);
    }
}
