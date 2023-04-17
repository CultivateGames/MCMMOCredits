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

import games.cultivate.mcmmocredits.data.UserDAO;
import games.cultivate.mcmmocredits.util.CreditOperation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service to get and modify Users.
 */
public final class UserService {
    private final UserDAO dao;
    private final UserCache cache;

    /**
     * Constructs the service.
     *
     * @param dao   Instance of UserDAO.
     * @param cache Instance of UserCache.
     */
    @Inject
    public UserService(final UserDAO dao, final UserCache cache) {
        this.dao = dao;
        this.cache = cache;
    }

    /**
     * Checks if the user is cached by UUID and username.
     *
     * @param user The User to check.
     * @return If the User is cached.
     * There is no scenario in which a user is only cached by one of the underlying caches.
     */
    public boolean isCached(final User user) {
        return this.cache.contains(user.uuid()) && this.cache.contains(user.username());
    }

    /**
     * Adds a new user with the specified UUID and username.
     *
     * @param uuid     the UUID of the new user
     * @param username the username of the new user
     */
    public void addUser(final UUID uuid, final String username) {
        User user = new User(uuid, username, 0, 0);
        this.dao.addUser(user);
        this.cache.add(user);
    }

    /**
     * Retrieves an optional User from the specified username.
     *
     * @param username the username of the user to retrieve
     * @return an optional User object if the user exists, otherwise an empty optional
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
     * Retrieves an optional User from the specified UUID.
     *
     * @param uuid the UUID of the user to retrieve
     * @return an optional User object if the user exists, otherwise an empty optional
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
     * Updates the username for the specified user with a new username.
     *
     * @param uuid     the UUID of the user to update
     * @param username the new username for the user
     * @return the updated User object if the update was successful, otherwise null
     */
    public @Nullable User setUsername(final UUID uuid, final String username) {
        if (this.dao.setUsername(uuid, username)) {
            return this.cache.update(uuid, user -> user.withUsername(username));
        }
        return null;
    }

    /**
     * Retrieves the number of credits for the specified user.
     *
     * @param uuid the UUID of the user to retrieve credits for
     * @return the number of credits for the specified user, or 0 if the user does not exist
     */
    public int getCredits(final UUID uuid) {
        return this.getUser(uuid).map(User::credits).orElse(0);
    }

    /**
     * Modifies credit balance of a User and returns the updated User.
     *
     * @param uuid      UUID of the user.
     * @param operation the operation to apply.
     * @param amount    amount of credits to redeem.
     * @return the updated User.
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
     * Performs a credit redemption and returns the updated User.
     *
     * @param uuid   UUID of the user.
     * @param amount amount of credits to redeem.
     * @return the updated User.
     */
    public @Nullable User redeemCredits(final UUID uuid, final int amount) {
        if (this.dao.redeemCredits(uuid, amount)) {
            return this.cache.update(uuid, user -> user.withCredits(user.credits() - amount).withRedeemed(user.redeemed() + amount));
        }
        return null;
    }

    /**
     * Retrieves a page of users with the specified limit and offset.
     *
     * @param limit  the maximum number of users to retrieve
     * @param offset the starting index of the page of users
     * @return a List of User objects representing the page of users
     */
    public List<User> getPageOfUsers(final int limit, final int offset) {
        return this.dao.getPageOfUsers(limit, offset);
    }

    /**
     * Returns a CommandExecutor instance based on the provided CommandSender.
     *
     * @param sender The CommandSender.
     * @return The CommandExecutor, either a User or Console.
     */
    public CommandExecutor fromSender(final CommandSender sender) {
        return sender instanceof Player p ? this.getUser(p.getUniqueId()).orElseThrow() : Console.INSTANCE;
    }

    /**
     * Removes the User from cache without adding them back.
     * Used primarily to handle user logouts.
     *
     * @param uuid     UUID of the user.
     * @param username String username of the user.
     */
    public void removeFromCache(final UUID uuid, final String username) {
        this.cache.remove(uuid, username);
    }
}
