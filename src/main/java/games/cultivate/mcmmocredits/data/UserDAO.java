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

import games.cultivate.mcmmocredits.util.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jdbi.v3.sqlobject.SqlObject;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Optional;
import java.util.UUID;

public interface UserDAO extends SqlObject {
    UUID ZERO_UUID = new UUID(0, 0);

    /**
     * Adds a {@link User} to the database if they are not in the database.
     *
     * @param user that we are adding to the database.
     */
    @SqlUpdate("INSERT INTO MCMMOCredits(uuid, username, credits, redeemed) VALUES(:uuid,:username,:credits,:redeemed);")
    void addUser(@BindMethods User user);

    /**
     * Gets a full {@link User} through username. Optional is empty if the user doesn't exist.
     *
     * @param username username to apply to search.
     * @return The user, or an empty optional.
     */
    @SqlQuery("SELECT * FROM MCMMOCREDITS WHERE username LIKE :username LIMIT 1;")
    @RegisterConstructorMapper(User.class)
    Optional<User> getUser(String username);

    /**
     * Gets a full {@link User} through username. Optional is empty if the user doesn't exist.
     *
     * @param uuid {@link UUID} to apply to search.
     * @return The user, or an empty optional.
     */
    @SqlQuery("SELECT * FROM MCMMOCREDITS WHERE uuid = :uuid;")
    @RegisterConstructorMapper(User.class)
    Optional<User> getUser(UUID uuid);

    /**
     * Returns a {@link User} from the provided {@link Player}.
     *
     * @param player the player.
     * @return A user from the DAO.
     */
    default Optional<User> fromPlayer(Player player) {
        return this.getUser(player.getUniqueId());
    }

    /**
     * Returns a {@link User} from the provided {@link CommandSender}.
     *
     * @param sender the sender.
     * @return A user from the DAO, or a fake user which encapsulates the sender.
     */
    default User fromSender(CommandSender sender) {
        if (sender instanceof Player player) {
            //We throw an exception here because we know the passed in player exists.
            return this.fromPlayer(player).orElseThrow();
        }
        return new User(ZERO_UUID, sender.getName(), 0, 0);
    }

    /**
     * Sets username of an existing user in our database. Finds user via UUID.
     *
     * @param uuid     {@link UUID} to search for player with.
     * @param username username to overwrite with in database.
     */
    @SqlUpdate("UPDATE MCMMOCredits SET username = :username WHERE UUID = :uuid;")
    void setUsername(UUID uuid, String username);

    /**
     * Sets credit amount of player in our database. Finds user via UUID.
     *
     * @param uuid   {@link UUID} to search for player with.
     * @param amount amount of credits to set the player's balance to.
     * @return if the operation was successful.
     */
    @SqlUpdate("UPDATE MCMMOCredits SET credits = :amount WHERE UUID = :uuid;")
    boolean setCredits(UUID uuid, int amount);

    /**
     * Gets credit balance for player in our database. Finds user via UUID.
     *
     * @param uuid {@link UUID} to search for player with.
     * @return amount of credits the player currently has.
     */
    @SqlQuery("SELECT credits FROM MCMMOCredits WHERE UUID = :uuid LIMIT 1;")
    int getCredits(UUID uuid);

    /**
     * Adds credit amount to existing credit balance of user in our database. Finds user via UUID.
     *
     * @param uuid   {@link UUID} to search for player with.
     * @param amount amount of credits to add to player.
     * @return if the operation was successful.
     */
    @SqlUpdate("UPDATE MCMMOCredits SET credits = credits + :amount WHERE UUID = :uuid;")
    boolean addCredits(UUID uuid, int amount);

    /**
     * Takes credit amount from existing credit balance of user in our database. Finds user via UUID.
     *
     * @param uuid   {@link UUID} to search for player with.
     * @param amount amount of credits to add to player.
     * @return if the operation was successful.
     */
    @SqlUpdate("UPDATE MCMMOCredits SET credits = credits - :amount WHERE UUID = :uuid;")
    boolean takeCredits(UUID uuid, int amount);

    /**
     * Adds specified amount to "credits redeemed" statistic.
     *
     * @param uuid   {@link UUID} to search for player with.
     * @param amount amount of credits to add to statistic.
     * @return if the operation was successful.
     */
    @SqlUpdate("UPDATE MCMMOCredits SET redeemed = redeemed + :amount WHERE UUID = :uuid;")
    boolean addRedeemedCredits(UUID uuid, int amount);

    /**
     * Gets "credits redeemed" statistic for player in our database. Finds user via UUID.
     *
     * @param uuid {@link UUID} to search for player with.
     * @return amount of credits the player currently has redeemed over their existence.
     */
    @SqlQuery("SELECT redeemed FROM MCMMOCredits WHERE UUID = :uuid;")
    int getRedeemedCredits(UUID uuid);
}
