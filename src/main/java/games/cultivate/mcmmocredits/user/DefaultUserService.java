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
import jakarta.inject.Inject;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The default implementation of the UserService.
 */
public final class DefaultUserService implements UserService {
    private final Map<String, User> usernameCache;
    private final Map<UUID, User> uuidCache;
    private final StorageService storageService;

    @Inject
    public DefaultUserService(final StorageService storageService) {
        this.storageService = storageService;
        this.usernameCache = new ConcurrentHashMap<>();
        this.uuidCache = new ConcurrentHashMap<>();
    }

    public void addUser(final User user) {
        //TODO: not sure if needed.
        if (this.storageService.addUser(user)) {
            this.uuidCache.put(user.uuid(), user);
            this.usernameCache.put(user.username(), user);
        }
    }

    @Override
    public User createUser(final Player player) {
        User user = new User(player, 0, 0);
        if (this.storageService.addUser(user)) {
            this.uuidCache.put(user.uuid(), user);
            this.usernameCache.put(user.username(), user);
        }
        return user;
    }

    @Override
    public User getOrCreate(final Player player) {
        User user = this.storageService.getUser(player).orElseGet(() -> this.createUser(player));
        this.uuidCache.put(user.uuid(), user);
        this.usernameCache.put(user.username(), user);
        return user;
    }

    @Override
    public void onLogout(final Player player) {
        this.uuidCache.remove(player.getUniqueId());
        this.usernameCache.remove(player.getName());
    }

    @Override
    public void updateUser(final User user) {
        if (this.storageService.updateUser(user)) {
            this.uuidCache.put(user.uuid(), user);
            this.usernameCache.put(user.username(), user);
        }
    }

    @Override
    public List<User> getUserGroup(final int limit, final int offset) {
        return this.storageService.getUserGroup(limit, offset);
    }

    @Override
    public CommandExecutor fromBukkit(final CommandSender sender) {
        if (sender instanceof Player player) {
            UUID uuid = player.getUniqueId();
            if (this.uuidCache.containsKey(uuid)) {
                return this.uuidCache.get(uuid);
            }
            return this.getOrCreate(player);
        }
        return Console.INSTANCE;
    }

    @Override
    public Optional<User> getUser(final UUID uuid) {
        return this.getValue(this.uuidCache, uuid);
    }

    @Override
    public Optional<User> getUser(final String username) {
        return this.getValue(this.usernameCache, username);
    }

    private <K> Optional<User> getValue(final Map<K, User> cache, final K key) {
        if (cache.containsKey(key)) {
            return Optional.ofNullable(cache.get(key));
        }
        Optional<User> storageUser = switch (key) {
            case String s -> this.storageService.getUser(s);
            case UUID u -> this.storageService.getUser(u);
            default -> Optional.empty();
        };
        storageUser.ifPresent(user -> {
            this.usernameCache.put(user.username(), user);
            this.uuidCache.put(user.uuid(), user);
        });
        return storageUser;
    }
}
