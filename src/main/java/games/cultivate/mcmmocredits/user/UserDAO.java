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

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.jdbi.v3.sqlobject.SqlObject;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * DAO that accesses {@link User} instances from Database.
 */
public interface UserDAO extends SqlObject {
    /**
     * Adds a user to the database.
     *
     * @param user The user to add.
     * @return True if the transaction was successful, false otherwise.
     */
    @SqlUpdate("INSERT INTO MCMMOCredits(uuid, username, credits, redeemed) VALUES(:uuid,:username,:credits,:redeemed);")
    boolean addUser(@BindMethods User user);

    /**
     * Adds a collection of users to the database.
     *
     * @param users The users to add.
     */
    default void addUsers(Collection<User> users) {
        try (Handle handle = this.getHandle()) {
            PreparedBatch batch = handle.prepareBatch("INSERT INTO MCMMOCredits(uuid, username, credits, redeemed) VALUES(:uuid,:username,:credits,:redeemed)");
            for (User u : users) {
                batch = batch.bind("uuid", u.uuid().toString())
                        .bind("username", u.username())
                        .bind("credits", u.credits())
                        .bind("redeemed", u.redeemed()).add();
            }
            batch.execute();
        }
    }

    /**
     * Retrieves a user from the database using their username.
     *
     * @param username The username of the user.
     * @return An Optional User which contains the user if found.
     */
    @SqlQuery("SELECT * FROM MCMMOCredits WHERE username LIKE :username LIMIT 1;")
    @RegisterConstructorMapper(User.class)
    Optional<User> getUser(String username);

    /**
     * Retrieves a user from the database using their UUID.
     *
     * @param uuid The UUID of the user.
     * @return An Optional User which contains the user if found.
     */
    @SqlQuery("SELECT * FROM MCMMOCredits WHERE uuid = :uuid;")
    @RegisterConstructorMapper(User.class)
    Optional<User> getUser(UUID uuid);

    /**
     * Sets the username of an existing user in the database.
     *
     * @param uuid     The UUID of the user.
     * @param username The new username to set.
     * @return True if the transaction was successful, false otherwise.
     */
    @SqlUpdate("UPDATE MCMMOCredits SET username = :username WHERE UUID = :uuid;")
    boolean setUsername(UUID uuid, String username);

    /**
     * Sets the credit balance of an existing user in the database.
     *
     * @param uuid   The UUID of the user.
     * @param amount The new credit balance to set.
     * @return True if the transaction was successful, false otherwise.
     */
    @SqlUpdate("UPDATE MCMMOCredits SET credits = :amount WHERE UUID = :uuid;")
    boolean setCredits(UUID uuid, int amount);

    /**
     * Gets the credit balance of an existing user in the database.
     *
     * @param uuid The UUID of the user.
     * @return The credit balance of the user.
     */
    @SqlQuery("SELECT credits FROM MCMMOCredits WHERE UUID = :uuid;")
    int getCredits(UUID uuid);

    /**
     * Adds a specified amount of credits to an existing user's credit balance in the database.
     *
     * @param uuid   The UUID of the user.
     * @param amount The number of credits to add.
     * @return True if the transaction was successful, false otherwise.
     */
    @SqlUpdate("UPDATE MCMMOCredits SET credits = credits + :amount WHERE UUID = :uuid;")
    boolean addCredits(UUID uuid, int amount);

    /**
     * Takes a specified amount of credits from an existing user's credit balance in the database.
     *
     * @param uuid   The UUID of the user.
     * @param amount The number of credits to remove.
     * @return True if the transaction was successful, false otherwise.
     */
    @SqlUpdate("UPDATE MCMMOCredits SET credits = credits - :amount WHERE UUID = :uuid;")
    boolean takeCredits(UUID uuid, int amount);

    /**
     * Redeems credits for an existing user in the database. This operation takes credits and adds them to the redeemed balance.
     *
     * @param uuid   The UUID of the user.
     * @param amount The number of credits to redeem.
     * @return True if the transaction was successful, false otherwise.
     */
    @SqlUpdate("UPDATE MCMMOCredits SET credits = credits - :amount, redeemed = redeemed + :amount WHERE UUID = :uuid")
    boolean redeemCredits(UUID uuid, int amount);

    /**
     * Gets a page of users from the database ordered by credit balance in descending order.
     *
     * @param limit  The maximum number of users to retrieve.
     * @param offset The offset for pagination.
     * @return A list of users.
     */
    @SqlQuery("SELECT * FROM MCMMOCredits ORDER BY credits DESC LIMIT :limit OFFSET :offset")
    @RegisterConstructorMapper(User.class)
    List<User> getPageOfUsers(int limit, int offset);

    /**
     * Gets all users from the current Database.
     * <p>
     * Note: This should be used with extreme caution on larger datasets.
     *
     * @return A list of all users.
     */
    @SqlQuery("SELECT * FROM MCMMOCredits")
    @RegisterConstructorMapper(User.class)
    List<User> getAllUsers();
}
