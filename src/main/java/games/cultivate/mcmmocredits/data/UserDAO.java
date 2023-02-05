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
package games.cultivate.mcmmocredits.data;

import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.Console;
import games.cultivate.mcmmocredits.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jdbi.v3.sqlobject.SqlObject;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

/**
 * DAO that accesses {@link User} instances from Database.
 */
public interface UserDAO extends SqlObject {
    /**
     * Adds provided User to the database.
     *
     * @param user The user.
     */
    @SqlUpdate("INSERT INTO MCMMOCredits(uuid, username, credits, redeemed) VALUES(:uuid,:username,:credits,:redeemed);")
    void addUser(@BindMethods User user);

    /**
     * Gets Optional User from the Database. Optional is empty if User does not exist.
     *
     * @param username Username of the user.
     * @return Optional User.
     */
    @SqlQuery("SELECT * FROM MCMMOCREDITS WHERE username LIKE :username LIMIT 1;")
    @RegisterConstructorMapper(User.class)
    Optional<User> getUser(String username);

    /**
     * Gets Optional User from the Database. Optional is empty if User does not exist.
     *
     * @param uuid UUID of the user.
     * @return Optional User.
     */
    @SqlQuery("SELECT * FROM MCMMOCREDITS WHERE uuid = :uuid;")
    @RegisterConstructorMapper(User.class)
    Optional<User> getUser(UUID uuid);

    /**
     * Sets username of existing {@link User} in database.
     *
     * @param uuid     UUID to search for User with.
     * @param username Value used to update username.
     */
    @SqlUpdate("UPDATE MCMMOCredits SET username = :username WHERE UUID = :uuid;")
    void setUsername(UUID uuid, String username);

    /**
     * Sets credit balance of existing {@link User} in database to provided amount.
     *
     * @param uuid   UUID to search for User with.
     * @param amount number of credits
     * @return if the transaction was successful.
     */
    @SqlUpdate("UPDATE MCMMOCredits SET credits = :amount WHERE UUID = :uuid;")
    boolean setCredits(UUID uuid, int amount);

    /**
     * Gets credit balance of existing {@link User} in database.
     *
     * @param uuid UUID to search for User with.
     * @return the result of the query.
     */
    @SqlQuery("SELECT credits FROM MCMMOCredits WHERE UUID = :uuid;")
    int getCredits(UUID uuid);

    /**
     * Adds provided amount of credits to credit balance of existing {@link User}.
     *
     * @param uuid   UUID to search for User with.
     * @param amount number of credits to add to User.
     * @return if the transaction was successful.
     */
    @SqlUpdate("UPDATE MCMMOCredits SET credits = credits + :amount WHERE UUID = :uuid;")
    boolean addCredits(UUID uuid, int amount);

    /**
     * Takes provided amount of credits from credit balance of existing {@link User}.
     *
     * @param uuid   UUID to search for User with.
     * @param amount number of credits to remove from User.
     * @return if the transaction was successful.
     */
    @SqlUpdate("UPDATE MCMMOCredits SET credits = credits - :amount WHERE UUID = :uuid;")
    boolean takeCredits(UUID uuid, int amount);

    /**
     * Redeems credits using existing {@link User}. Operation takes credits and adds them to redeemed balance.
     *
     * @param uuid   UUID to search for User with.
     * @param amount number of credits that have been redeemed.,
     */
    @SqlUpdate("UPDATE MCMMOCredits SET credits = credits - :amount, redeemed = redeemed + :amount WHERE UUID = :uuid")
    void redeemCredits(UUID uuid, int amount);

    /**
     * Returns a {@link User} from the provided {@link CommandSender}.
     *
     * @param sender The CommandSender.
     * @return The User, or Console.
     * @see Console
     */
    default CommandExecutor fromSender(CommandSender sender) {
        if (sender instanceof Player player) {
            return this.forceUser(player.getUniqueId());
        }
        return Console.INSTANCE;
    }

    /**
     * Gets User when the user is known to exist.
     *
     * @param uuid UUID to get User from.
     * @return The User, or a {@link NoSuchElementException} is thrown.
     */
    default User forceUser(UUID uuid) {
        return this.getUser(uuid).orElseThrow();
    }
}
