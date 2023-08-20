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
import games.cultivate.mcmmocredits.database.AbstractDatabase;
import games.cultivate.mcmmocredits.transaction.TransactionResult;
import games.cultivate.mcmmocredits.util.MojangUtil;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * Handles getting and modifying users.
 */
public final class UserService {
    private final AbstractDatabase database;
    private final Cache<UUID, User> uuidCache;
    private final Cache<String, User> stringCache;

    /**
     * Constructs the object.
     *
     * @param database Database, used to interact with the user database.
     */
    @Inject
    public UserService(final AbstractDatabase database) {
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
    public CompletableFuture<Void> addUser(final User user) {
        return this.database.addUser(user).thenRun(() -> this.addToCache(user));
    }

    /**
     * Gets a user with the specified username.
     * The optional is empty if the cache and DAO do not contain the user.
     *
     * @param username The username of a user.
     * @return A user if it exists, otherwise an empty optional.
     */
    public CompletableFuture<Optional<User>> getUser(final String username) {
        User user = this.stringCache.getIfPresent(username);
        if (user != null) {
            return CompletableFuture.completedFuture(Optional.of(user));
        }
        return this.database.getUser(username).thenApply(opt -> {
            opt.ifPresent(this::addToCache);
            return opt;
        });
    }

    /**
     * Gets a user with the specified UUID.
     * The optional is empty if the cache and database do not contain the user.
     *
     * @param uuid The UUID of a user.
     * @return A user if it exists, otherwise an empty optional.
     */
    public CompletableFuture<Optional<User>> getUser(final UUID uuid) {
        User user = this.uuidCache.getIfPresent(uuid);
        if (user != null) {
            return CompletableFuture.completedFuture(Optional.of(user));
        }
        return this.database.getUser(uuid).thenApply(opt -> {
            opt.ifPresent(this::addToCache);
            return opt;
        });
    }

    /**
     * Gets a user via the specified player.
     *
     * @param player The player.
     * @return A user representing the player.
     */
    public CompletableFuture<Optional<User>> getUser(final Player player) {
        return this.getUser(player.getUniqueId());
    }

    /**
     * Gets a range of users using the specified limit and offset.
     *
     * @param limit  The max amount of users to get.
     * @param offset The starting index of where to start getting users.
     * @return A list of users within the provided bounds.
     */
    public CompletableFuture<List<User>> rangeOfUsers(final int limit, final int offset) {
        return this.database.rangeOfUsers(limit, offset);
    }

    /**
     * Translates all online players into online users.
     *
     * @return List of online users.
     */
    public CompletableFuture<List<User>> getOnlineUsers() {
        List<CompletableFuture<Optional<User>>> futures = Bukkit.getOnlinePlayers().stream().map(this::getUser).toList();
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).filter(Optional::isPresent).map(Optional::get).toList());
    }

    /**
     * Gets the credit balance of a user with the specified UUID.
     *
     * @param uuid The UUID of a user.
     * @return The credit balance of a user, or 0 if the user does not exist.
     */
    public CompletableFuture<Integer> getCredits(final UUID uuid) {
        return this.getUser(uuid).thenApply(x -> x.map(User::credits).orElse(0));
    }

    /**
     * Updates the username of a user with the specified UUID.
     * If there is a username conflict, update the user already in database with Mojang call.
     *
     * @param uuid     The UUID of a user.
     * @param username The username of a user.
     */
    public CompletableFuture<Void> setUsername(final UUID uuid, final String username) {
        return this.getUser(username).thenCompose(u -> {
                    if (u.isPresent() && !u.get().uuid().equals(uuid)) {
                        User old = u.get();
                        Logger.getLogger("Minecraft").severe(String.format("Duplicate username found! Old: %s, %s. New: %s, %s. Updating old user data ...", old.uuid(), old.username(), uuid, username));
                        return MojangUtil.getNameAsync(old.uuid())
                                .thenCompose(x -> this.database.setUsername(old.uuid(), x).thenApply(c -> {
                                    this.addToCache(old.withUsername(x));
                                    return c;
                                }));
                    }
                    return CompletableFuture.completedFuture(true);
                })
                .thenCompose(b -> this.database.setUsername(uuid, username))
                .thenAccept(z -> {
                    if (z) this.getUser(uuid).join().ifPresent(x -> this.addToCache(x.withUsername(username)));
                });
    }

    /**
     * Updates the credit balance of a user with the specified UUID.
     *
     * @param uuid   The UUID of a user.
     * @param amount Amount of credits to apply to balance.
     * @return If the transaction was successful.
     */
    public CompletableFuture<Boolean> setCredits(final UUID uuid, final int amount) {
        return this.database.setCredits(uuid, amount).thenCompose(x -> {
            if (x) {
                this.getUser(uuid).join().ifPresent(y -> this.addToCache(y.setCredits(amount)));
                return CompletableFuture.completedFuture(true);
            }
            return CompletableFuture.completedFuture(false);
        });
    }

    /**
     * Processes a TransactionResult and applies any changes to DAO and cache.
     *
     * @param result The transaction result to process.
     */
    public CompletableFuture<Void> processTransaction(final TransactionResult result) {
        List<User> users = new ArrayList<>(result.targets());
        if (!result.updatedTargets()) {
            users.removeAll(result.targets());
        }
        if (result.updatedExecutor()) {
            users.add(result.executor().toUser());
        }
        return this.database.applyTransaction(users).thenAccept(x -> {
            if (x) users.forEach(this::addToCache);
        });
    }

    /**
     * Maps a Bukkit CommandSender to a CommandExecutor, synchronously.
     *
     * @param sender The Bukkit CommandSender.
     * @return A user if one exists for the CommandSender, otherwise Console.
     */
    public CommandExecutor fromSender(final CommandSender sender) {
        return sender instanceof Player p ? this.getUser(p.getUniqueId()).join().orElseThrow() : Console.INSTANCE;
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
