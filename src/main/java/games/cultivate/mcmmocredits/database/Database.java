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
package games.cultivate.mcmmocredits.database;

import games.cultivate.mcmmocredits.user.User;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a Database connection and provider of the UserDAO.
 */
public interface Database {
    /**
     * Disables the connection. Reserved for shutdown.
     */
    void disable();

    /**
     * Returns instance of JDBI.
     *
     * @return Instance of JDBI.
     */
    Jdbi jdbi();

    /**
     * Returns if the database type is H2.
     *
     * @return If the database type is H2.
     */
    default boolean isH2() {
        return false;
    }

    /**
     * Adds a user to the database.
     *
     * @param user The user to add.
     * @return True if the transaction was successful, otherwise false.
     */
    default boolean addUser(User user) {
        return this.jdbi().withHandle(handle -> handle.createUpdate("INSERT INTO MCMMOCredits(uuid, username, credits, redeemed) VALUES(:uuid,:username,:credits,:redeemed);").bindMethods(user).execute() == 1);
    }

    /**
     * Adds a collection of users to the database.
     *
     * @param users The users to add.
     */
    default void addUsers(Collection<User> users) {
        this.jdbi().useHandle(handle -> {
            PreparedBatch batch = handle.prepareBatch("INSERT INTO MCMMOCredits(uuid, username, credits, redeemed) VALUES(:uuid,:username,:credits,:redeemed);");
            users.forEach(x -> batch.bindMethods(x).add());
            batch.execute();
        });
    }

    /**
     * Gets a user with the specified UUID.
     * The optional is empty if the database does not contain the UUID.
     *
     * @param uuid The UUID of a user.
     * @return A user if it exists, otherwise an empty optional.
     */
    default Optional<User> getUser(UUID uuid) {
        return this.jdbi().withHandle(handle -> handle.createQuery("SELECT * FROM MCMMOCredits WHERE uuid = :uuid;").bind("uuid", uuid).mapTo(User.class).findOne());
    }

    /**
     * Gets a user with the specified username.
     * The optional is empty if the database does not contain the username.
     *
     * @param username The username of a user.
     * @return A user if it exists, otherwise an empty optional.
     */
    default Optional<User> getUser(String username) {
        return this.jdbi().withHandle(handle -> handle.createQuery("SELECT * FROM MCMMOCredits WHERE username LIKE :username LIMIT 1;").bind("username", username).mapTo(User.class).findOne());
    }

    /**
     * Gets a range of users using the specified limit and offset.
     *
     * @param limit  The max amount of users to get.
     * @param offset The starting index of where to start getting users.
     * @return A list of users within the provided bounds.
     */
    default List<User> rangeOfUsers(int limit, int offset) {
        return this.jdbi().withHandle(handle -> handle.createQuery("SELECT * FROM MCMMOCredits ORDER BY credits DESC LIMIT :limit OFFSET :offset;").bind("limit", limit).bind("offset", offset).mapTo(User.class).list());
    }

    /**
     * Gets all users.
     *
     * @return a list of all users.
     */
    default List<User> getAllUsers() {
        return this.jdbi().withHandle(handle -> handle.createQuery("SELECT * FROM MCMMOCredits").mapTo(User.class).list());
    }

    /**
     * Updates the username of a user with the specified UUID.
     *
     * @param uuid     The UUID of a user.
     * @param username The username of a user.
     * @return True if the transaction was successful, otherwise false.
     */
    default boolean setUsername(UUID uuid, String username) {
        return this.jdbi().withHandle(handle -> handle.createUpdate("UPDATE MCMMOCredits SET username = :username WHERE UUID = :uuid;").bind("uuid", uuid).bind("username", username).execute() == 1);
    }

    /**
     * Sets the credit balance of a user with the specified UUID.
     *
     * @param uuid   The UUID of the user.
     * @param amount The new amount of credits.
     * @return True if the transaction was successful, otherwise false.
     */
    default boolean setCredits(UUID uuid, int amount) {
        return this.jdbi().withHandle(handle -> handle.createUpdate("UPDATE MCMMOCredits SET credits = :amount WHERE UUID = :uuid;").bind("uuid", uuid).bind("amount", amount).execute() == 1);
    }

    /**
     * Updates an existing user in the database with the provided user.
     *
     * @param user The user to update.
     * @return True if the transaction was successful, otherwise false.
     */
    default boolean updateUser(User user) {
        return this.jdbi().withHandle(handle -> handle.createUpdate("UPDATE MCMMOCredits SET username = :username, credits = :credits, redeemed = :redeemed WHERE UUID = :uuid;").bindMethods(user).execute() == 1);
    }

    default void createTable() {
        this.jdbi().useHandle(handle -> handle.execute("CREATE TABLE IF NOT EXISTS MCMMOCredits(id INTEGER PRIMARY KEY AUTO_INCREMENT,UUID VARCHAR(36) NOT NULL,username VARCHAR(16) NOT NULL,credits INT CHECK(credits >= 0),redeemed INT);"));
    }
}
