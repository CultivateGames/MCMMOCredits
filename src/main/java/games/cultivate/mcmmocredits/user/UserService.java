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

import games.cultivate.mcmmocredits.util.CreditOperation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles getting and modifying users.
 */
public final class UserService {
    private final UserDAO dao;
    private final UserCache cache;

    /**
     * Constructs the object.
     *
     * @param dao   UserDAO, used to interact with the user database.
     * @param cache UserCache, used to interact with the in-memory cache of users.
     */
    @Inject
    public UserService(final UserDAO dao, final UserCache cache) {
        this.dao = dao;
        this.cache = cache;
    }

    /**
     * Checks if the user is cached by UUID and username.
     *
     * @param user The user.
     * @return True if the user is cached, false otherwise.
     */
    public boolean isCached(final User user) {
        return this.cache.contains(user.uuid()) && this.cache.contains(user.username());
    }

    /**
     * Adds a user with the specified UUID and username to the DAO and cache.
     *
     * @param uuid     The UUID of a new user.
     * @param username The username of a new user.
     */
    public void addUser(final UUID uuid, final String username) {
        User user = new User(uuid, username, 0, 0);
        this.dao.addUser(user);
        this.cache.add(user);
    }

    /**
     * Gets a user with the specified username.
     * The optional is empty if the cache and DAO do not contain the user.
     *
     * @param username The username of a user.
     * @return A user if it exists, otherwise an empty optional.
     */
    public Optional<User> getUser(final String username) {
        if (this.cache.contains(username)) {
            return Optional.of(this.cache.get(username));
        }
        Optional<User> optionalUser = this.dao.getUser(username);
        optionalUser.ifPresent(this.cache::add);
        return optionalUser;
    }

    /**
     * Gets a user with the specified UUID.
     * The optional is empty if the cache and dao do not contain the user.
     *
     * @param uuid The UUID of a user.
     * @return A user if it exists, otherwise an empty optional.
     */
    public Optional<User> getUser(final UUID uuid) {
        if (this.cache.contains(uuid)) {
            return Optional.of(this.cache.get(uuid));
        }
        Optional<User> optionalUser = this.dao.getUser(uuid);
        optionalUser.ifPresent(this.cache::add);
        return optionalUser;
    }

    /**
     * Updates the username of a user with the specified UUID.
     *
     * @param uuid     The UUID of a user.
     * @param username The username of a user.
     * @return The updated user, or null if the update failed.
     */
    public @Nullable User setUsername(final UUID uuid, final String username) {
        if (this.dao.setUsername(uuid, username)) {
            return this.cache.update(uuid, user -> user.withUsername(username));
        }
        return null;
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
     * Updates the credit balance of a user with the specified UUID.
     *
     * @param uuid      The UUID of a user.
     * @param operation Operation applied to credit balance (add, set, take).
     * @param amount    Amount of credits to apply to balance.
     * @return The updated user, or null if the update failed.
     */
    public @Nullable User modifyCredits(final UUID uuid, final CreditOperation operation, final int amount) {
        boolean status = switch (operation) {
            case ADD -> this.dao.addCredits(uuid, amount);
            case SET -> this.dao.setCredits(uuid, amount);
            case TAKE -> this.dao.takeCredits(uuid, amount);
        };
        return status ? this.cache.update(uuid, u -> u.withCredits(operation.apply(u.credits(), amount))) : null;
    }

    /**
     * Performs a credit redemption and returns the updated user.
     *
     * @param uuid   The UUID of a user.
     * @param amount Amount of credits to use in redemption.
     * @return the updated user, or null if the update failed.
     */
    public @Nullable User redeemCredits(final UUID uuid, final int amount) {
        if (this.dao.redeemCredits(uuid, amount)) {
            return this.cache.update(uuid, user -> user.withCredits(user.credits() - amount).withRedeemed(user.redeemed() + amount));
        }
        return null;
    }

    /**
     * Gets a range of users using the specified limit and offset.
     *
     * @param limit  The max amount of users to get.
     * @param offset The starting index of where to start getting users.
     * @return A list of users within the provided bounds.
     */
    public List<User> getPageOfUsers(final int limit, final int offset) {
        return this.dao.getPageOfUsers(limit, offset);
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
     * Removes a user from the cache.
     *
     * @param uuid     The UUID of a user.
     * @param username The username of a user.
     */
    public void removeFromCache(final UUID uuid, final String username) {
        this.cache.remove(uuid, username);
    }
}
