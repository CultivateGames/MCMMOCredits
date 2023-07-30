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
import games.cultivate.mcmmocredits.transaction.Transaction;
import games.cultivate.mcmmocredits.transaction.TransactionResult;
import jakarta.inject.Inject;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;

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
        this.database.addUser(user);
        this.addToCache(user);
    }

    /**
     * Removes a user from the cache.
     *
     * @param uuid The UUID of a user.
     */
    public void removeUser(final UUID uuid) {
        User user = this.fromCache(uuid);
        this.removeFromCache(uuid);
        this.removeFromCache(user.username());
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
     *
     * @param uuid     The UUID of a user.
     * @param username The username of a user.
     * @return The updated user, or null if the update failed.
     */
    public @Nullable User setUsername(final UUID uuid, final String username) {
        return this.database.setUsername(uuid, username) ? this.updateUser(uuid, u -> u.withUsername(username)) : null;
    }

    /**
     * Updates the credit balance of a user with the specified UUID.
     *
     * @param uuid   The UUID of a user.
     * @param amount Amount of credits to apply to balance.
     * @return The updated user, or null if the update failed.
     */
    public @Nullable User setCredits(final UUID uuid, final int amount) {
        return this.database.setCredits(uuid, amount) ? this.updateUser(uuid, u -> u.setCredits(amount)) : null;
    }

    /**
     * Updates an existing user in the DAO/Cache with the provided user.
     *
     * @param user The user to use in update.
     */
    public void updateUser(final User user) {
        if (this.database.updateUser(user)) {
            this.updateUser(user.uuid(), x -> user);
        }
    }

    /**
     * Processes a TransactionResult and applies any changes to DAO and cache.
     *
     * @param result The transaction result to process.
     */
    public void processTransaction(final TransactionResult result) {
        Transaction transaction = result.transaction();
        if (result.isTargetUpdated()) {
            this.updateUser(result.target());
        }
        CommandExecutor exec = result.executor();
        if (!transaction.isSelfTransaction() && result.isExecutorUpdated()) {
            this.updateUser((User) exec);
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
     * Applies a function to a user, and updates the existing user with the function's result.
     *
     * @param uuid   UUID of the user.
     * @param action Action to apply to the cached user.
     * @return The updated user.
     */
    public User updateUser(final UUID uuid, final UnaryOperator<User> action) {
        User user = this.fromCache(uuid);
        if (user == null) {
            throw new IllegalArgumentException("User not found in database!");
        }
        this.removeFromCache(uuid);
        this.removeFromCache(user.username());
        User updatedUser = action.apply(user);
        this.addToCache(updatedUser);
        return updatedUser;
    }

    /**
     * Adds a user to the cache.
     *
     * @param user The user to add.
     */
    private void addToCache(final User user) {
        this.removeFromCache(user.uuid());
        this.removeFromCache(user.username());
        this.uuidCache.put(user.uuid(), user);
        this.stringCache.put(user.username(), user);
    }

    /**
     * Removes a user from the cache.
     *
     * @param uuid The UUID to remove.
     */
    private void removeFromCache(final UUID uuid) {
        User oldUUID = this.fromCache(uuid);
        if (oldUUID != null) {
            this.stringCache.invalidate(oldUUID.username());
            this.uuidCache.invalidate(oldUUID.uuid());
        }
    }

    /**
     * Removes a user from the cache.
     *
     * @param username The username to remove.
     */
    private void removeFromCache(final String username) {
        User oldString = this.fromCache(username);
        if (oldString != null) {
            this.uuidCache.invalidate(oldString.uuid());
            this.stringCache.invalidate(oldString.username());
        }
    }

    /**
     * Gets a user from the cache with the specified UUID.
     *
     * @param uuid UUID of the user.
     * @return The user.
     */
    private User fromCache(final UUID uuid) {
        return this.uuidCache.getIfPresent(uuid);
    }

    /**
     * Gets a user from the cache with the specified UUID.
     *
     * @param username Username of the user.
     * @return The user.
     */
    private User fromCache(final String username) {
        return this.stringCache.getIfPresent(username);
    }
}
