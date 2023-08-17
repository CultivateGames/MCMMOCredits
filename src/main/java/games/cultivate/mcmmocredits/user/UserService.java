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
package games.cultivate.mcmmocredits.user;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.transaction.TransactionResult;
import games.cultivate.mcmmocredits.util.Util;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles getting and modifying users.
 */
public final class UserService {
    private final Database database;
    private final Cache<UUID, User> uuidCache;
    private final Cache<String, User> stringCache;

    /**
     * Constructs the object.
     *
     * @param database Database, used to interact with the user database.
     */
    @Inject
    public UserService(final Database database) {
        this.database = database;
        this.stringCache = Caffeine.newBuilder().build();
        this.uuidCache = Caffeine.newBuilder().build();
    }

    /**
     * Checks if the user is cached by UUID and username.
     *
     * @param user The user.
     * @return True if the user is cached, false otherwise.
     */
    public boolean isUserCached(final User user) {
        return this.uuidCache.getIfPresent(user.uuid()) != null && this.stringCache.getIfPresent(user.username()) != null;
    }

    /**
     * Adds a user with the specified UUID and username to the DAO and cache.
     *
     * @param user The user to add.
     */
    public void addUser(final User user) {
        if (this.database.addUser(user)) {
            this.addToCache(user);
        }
    }

    /**
     * Gets a user with the specified username.
     * The optional is empty if the cache and DAO do not contain the user.
     *
     * @param username The username of a user.
     * @return A user if it exists, otherwise an empty optional.
     */
    public Optional<User> getUser(final String username) {
        User user = this.stringCache.getIfPresent(username);
        if (user != null) {
            return Optional.of(user);
        }
        Optional<User> opt = this.database.getUser(username);
        opt.ifPresent(this::addToCache);
        return opt;
    }

    /**
     * Gets a user with the specified UUID.
     * The optional is empty if the cache and database do not contain the user.
     *
     * @param uuid The UUID of a user.
     * @return A user if it exists, otherwise an empty optional.
     */
    public Optional<User> getUser(final UUID uuid) {
        User user = this.uuidCache.getIfPresent(uuid);
        if (user != null) {
            return Optional.of(user);
        }
        Optional<User> opt = this.database.getUser(uuid);
        opt.ifPresent(this::addToCache);
        return opt;
    }

    /**
     * Gets a user via the specified player.
     *
     * @param player The player.
     * @return A user representing the player.
     */
    public Optional<User> getUser(final Player player) {
        return this.getUser(player.getUniqueId());
    }

    /**
     * Gets a range of users using the specified limit and offset.
     *
     * @param limit  The max amount of users to get.
     * @param offset The starting index of where to start getting users.
     * @return A list of users within the provided bounds.
     */
    public List<User> rangeOfUsers(final int limit, final int offset) {
        return this.database.rangeOfUsers(limit, offset);
    }

    /**
     * Translates all online players into online users.
     *
     * @return List of online users.
     */
    public List<User> getOnlineUsers() {
        return Bukkit.getOnlinePlayers().stream()
                .map(x -> this.getUser(x).orElseThrow())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Gets the credit balance of a user with the specified UUID.
     *
     * @param uuid The UUID of a user.
     * @return The credit balance of a user, or 0 if the user does not exist.
     */
    public int getCredits(final UUID uuid) {
        return this.getUser(uuid).map(User::credits).orElse(0);
    }

    /**
     * Updates the username of a user with the specified UUID.
     * If there is a username conflict, update the user already in database with Mojang call.
     *
     * @param uuid     The UUID of a user.
     * @param username The username of a user.
     */
    public void setUsername(final UUID uuid, final String username) {
        Optional<User> existingUser = this.getUser(username);
        if (existingUser.isPresent() && !existingUser.get().uuid().equals(uuid)) {
            User us = existingUser.get();
            this.database.setUsername(us.uuid(), Util.getMojangUsername(us.uuid()));
            Bukkit.getLogger().severe(() -> String.format("""
                    Duplicate username found!
                    Old User: UUID = %s, Username = %s
                    New User: UUID = %s, Username = %s
                    Updating the older user's username... If you are seeing this message frequently, you likely have a corrupted database!
                    """, us.uuid(), us.username(), uuid, username));
        }
        if (this.database.setUsername(uuid, username)) {
            User user = this.getUser(uuid).orElseThrow(() -> new IllegalArgumentException("User not found!")).withUsername(username);
            this.addToCache(user);
        }
    }

    /**
     * Updates the credit balance of a user with the specified UUID.
     *
     * @param uuid   The UUID of a user.
     * @param amount Amount of credits to apply to balance.
     * @return If the transaction was successful.
     */
    public boolean setCredits(final UUID uuid, final int amount) {
        if (this.database.setCredits(uuid, amount)) {
            User user = this.getUser(uuid).orElseThrow(() -> new IllegalArgumentException("User not found!")).setCredits(amount);
            this.addToCache(user);
            return true;
        }
        return false;
    }

    /**
     * Processes a TransactionResult and applies any changes to DAO and cache.
     *
     * @param result The transaction result to process.
     */
    public void processTransaction(final TransactionResult result) {
        if (result.updatedTargets()) {
            List<User> users = result.targets();
            if (this.database.applyTransaction(users)) {
                users.forEach(this::addToCache);
            }
        }
        if (result.updatedExecutor()) {
            User exec = (User) result.executor();
            if (this.database.updateUser(exec)) {
                this.addToCache(exec);
            }
        }
    }

    /**
     * Maps a Bukkit CommandSender to a CommandExecutor.
     *
     * @param sender The Bukkit CommandSender.
     * @return A user if one exists for the CommandSender, otherwise Console.
     */
    public CommandExecutor fromSender(final CommandSender sender) {
        return sender instanceof Player p ? this.getUser(p.getUniqueId()).orElseThrow() : Console.INSTANCE;
    }

    /**
     * Adds a user to the cache.
     *
     * @param user The user to add.
     */
    private void addToCache(final User user) {
        this.removeFromCache(user.uuid());
        this.uuidCache.put(user.uuid(), user);
        this.stringCache.put(user.username(), user);
    }

    /**
     * Removes a user from the cache.
     *
     * @param uuid The UUID to remove.
     */
    public void removeFromCache(final UUID uuid) {
        User oldUUID = this.uuidCache.getIfPresent(uuid);
        if (oldUUID != null) {
            this.stringCache.invalidate(oldUUID.username());
            this.uuidCache.invalidate(oldUUID.uuid());
        }
    }
}
