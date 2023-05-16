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

import org.jdbi.v3.sqlobject.SqlObject;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.BatchChunkSize;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles execution of a DAO that stores users.
 */
public interface UserDAO extends SqlObject {
    /**
     * Adds a user to the database.
     *
     * @param user The user to add.
     * @return True if the transaction was successful, otherwise false.
     */
    @SqlUpdate("INSERT INTO MCMMOCredits(uuid, username, credits, redeemed) VALUES(:uuid,:username,:credits,:redeemed);")
    boolean addUser(@BindMethods User user);

    /**
     * Adds a collection of users to the database.
     *
     * @param users The users to add.
     */
    @SqlBatch("INSERT INTO MCMMOCredits(uuid, username, credits, redeemed) VALUES(:uuid,:username,:credits,:redeemed);")
    @BatchChunkSize(1000)
    void addUsers(@BindMethods Collection<User> users);

    /**
     * Gets a user with the specified username.
     * The optional is empty if the database does not contain the username.
     *
     * @param username The username of a user.
     * @return A user if it exists, otherwise an empty optional.
     */
    @SqlQuery("SELECT * FROM MCMMOCredits WHERE username LIKE :username LIMIT 1;")
    @RegisterConstructorMapper(User.class)
    Optional<User> getUser(String username);

    /**
     * Gets a user with the specified UUID.
     * The optional is empty if the database does not contain the UUID.
     *
     * @param uuid The UUID of a user.
     * @return A user if it exists, otherwise an empty optional.
     */
    @SqlQuery("SELECT * FROM MCMMOCredits WHERE uuid = :uuid;")
    @RegisterConstructorMapper(User.class)
    Optional<User> getUser(UUID uuid);

    /**
     * Gets a range of users using the specified limit and offset.
     *
     * @param limit  The max amount of users to get.
     * @param offset The starting index of where to start getting users.
     * @return A list of users within the provided bounds.
     */
    @SqlQuery("SELECT * FROM MCMMOCredits ORDER BY credits DESC LIMIT :limit OFFSET :offset;")
    @RegisterConstructorMapper(User.class)
    List<User> getPageOfUsers(int limit, int offset);

    /**
     * Gets all users.
     *
     * @return a list of all users.
     */
    @SqlQuery("SELECT * FROM MCMMOCredits")
    @RegisterConstructorMapper(User.class)
    List<User> getAllUsers();

    /**
     * Gets the credit balance of a user with the specified UUID.
     *
     * @param uuid The UUID of the user.
     * @return The credit balance of the user.
     */
    @SqlQuery("SELECT credits FROM MCMMOCredits WHERE UUID = :uuid;")
    int getCredits(UUID uuid);

    /**
     * Updates the username of a user with the specified UUID.
     *
     * @param uuid     The UUID of a user.
     * @param username The username of a user.
     * @return True if the transaction was successful, otherwise false.
     */
    @SqlUpdate("UPDATE MCMMOCredits SET username = :username WHERE UUID = :uuid;")
    boolean setUsername(UUID uuid, String username);

    /**
     * Sets the credit balance of a user with the specified UUID.
     *
     * @param uuid   The UUID of the user.
     * @param amount The new amount of credits.
     * @return True if the transaction was successful, otherwise false.
     */
    @SqlUpdate("UPDATE MCMMOCredits SET credits = :amount WHERE UUID = :uuid;")
    boolean setCredits(UUID uuid, int amount);

    /**
     * Updates an existing user in the database with the provided user.
     *
     * @param user The user to update.
     */
    @SqlUpdate("UPDATE MCMMOCredits SET username = :username, credits = :credits, redeemed = :redeemed WHERE UUID = :uuid;")
    void updateUser(@BindMethods User user);
}
