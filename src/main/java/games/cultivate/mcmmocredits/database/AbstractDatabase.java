//
// MIT License
//
// Copyright (c) 2024 Cultivate Games
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
import org.jdbi.v3.core.async.JdbiExecutor;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.jdbi.v3.core.statement.StatementContext;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

/**
 * Represents a database.
 */
public abstract class AbstractDatabase {
    final Jdbi jdbi;
    final DataSource source;
    final JdbiExecutor executor;

    /**
     * Constructs the object.
     *
     * @param source The DataSource to wrap.
     */
    AbstractDatabase(final DataSource source) {
        this.source = source;
        this.jdbi = this.createJdbi();
        this.executor = JdbiExecutor.create(this.jdbi, Executors.newFixedThreadPool(2));
        this.createTable();
    }

    /**
     * Method used to wrap a DataSource with JDBI.
     *
     * @return JDBI instance.
     */
    abstract Jdbi createJdbi();

    /**
     * Shuts down the underlying data source.
     */
    public void disable() {
        if (this.source instanceof Closeable closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates the Database's table based on properties.
     */
    public void createTable() {
        this.jdbi.useHandle(handle -> handle.execute("CREATE TABLE IF NOT EXISTS MCMMOCredits(id INTEGER PRIMARY KEY AUTO_INCREMENT,UUID VARCHAR(36) NOT NULL,username VARCHAR(16) NOT NULL,credits INT CHECK(credits >= 0),redeemed INT);"));
    }

    /**
     * Adds a user to the database.
     *
     * @param user The user to add.
     * @return True if the transaction was successful, otherwise false.
     */
    public CompletableFuture<Boolean> addUser(final User user) {
        return this.executor.withHandle(handle -> handle.createUpdate("INSERT INTO MCMMOCredits(uuid, username, credits, redeemed) VALUES(:uuid,:username,:credits,:redeemed);").bindMethods(user).execute() == 1).toCompletableFuture();
    }

    /**
     * Adds a collection of users to the database.
     *
     * @param users The users to add.
     * @return CompletableFuture holding status of the task.
     */
    public CompletableFuture<Void> addUsers(final Collection<User> users) {
        return this.executor.useHandle(handle -> {
            PreparedBatch batch = handle.prepareBatch("INSERT INTO MCMMOCredits(uuid, username, credits, redeemed) VALUES(:uuid,:username,:credits,:redeemed);");
            users.forEach(x -> batch.bindMethods(x).add());
            batch.execute();
        }).toCompletableFuture();
    }

    /**
     * Gets a user with the specified UUID.
     * The optional is empty if the database does not contain the UUID.
     *
     * @param uuid The UUID of a user.
     * @return A user if it exists, otherwise an empty optional.
     */
    public CompletableFuture<Optional<User>> getUser(final UUID uuid) {
        return this.executor.withHandle(handle -> handle.createQuery("SELECT * FROM MCMMOCredits WHERE uuid = :uuid;").bind("uuid", uuid).mapTo(User.class).findOne()).toCompletableFuture();
    }

    /**
     * Gets a user with the specified username.
     * The optional is empty if the database does not contain the username.
     *
     * @param username The username of a user.
     * @return A user if it exists, otherwise an empty optional.
     */
    public CompletableFuture<Optional<User>> getUser(final String username) {
        return this.executor.withHandle(handle -> handle.createQuery("SELECT * FROM MCMMOCredits WHERE username LIKE :username LIMIT 1;").bind("username", username).mapTo(User.class).findOne()).toCompletableFuture();
    }

    /**
     * Gets a range of users using the specified limit and offset.
     *
     * @param limit  The max amount of users to get.
     * @param offset The starting index of where to start getting users.
     * @return A list of users within the provided bounds.
     */
    public CompletableFuture<List<User>> rangeOfUsers(final int limit, final int offset) {
        return this.executor.withHandle(handle -> handle.createQuery("SELECT * FROM MCMMOCredits INNER JOIN (SELECT id FROM MCMMOCredits ORDER BY credits DESC LIMIT :limit OFFSET :offset) AS tmp USING(id) ORDER BY credits DESC;").bind("limit", limit).bind("offset", offset).mapTo(User.class).list()).toCompletableFuture();
    }

    /**
     * Gets all users.
     *
     * @return a list of all users.
     */
    public CompletableFuture<List<User>> getAllUsers() {
        return this.executor.withHandle(handle -> handle.createQuery("SELECT * FROM MCMMOCredits").mapTo(User.class).list()).toCompletableFuture();
    }

    /**
     * Updates the username of a user with the specified UUID.
     *
     * @param uuid     The UUID of a user.
     * @param username The username of a user.
     * @return True if the transaction was successful, otherwise false.
     */
    public CompletableFuture<Boolean> setUsername(final UUID uuid, final String username) {
        return this.executor.withHandle(handle -> handle.createUpdate("UPDATE MCMMOCredits SET username = :username WHERE UUID = :uuid;").bind("uuid", uuid).bind("username", username).execute() == 1).toCompletableFuture();
    }

    /**
     * Sets the credit balance of a user with the specified UUID.
     *
     * @param uuid   The UUID of the user.
     * @param amount The new amount of credits.
     * @return True if the transaction was successful, otherwise false.
     */
    public CompletableFuture<Boolean> setCredits(final UUID uuid, final int amount) {
        return this.executor.withHandle(handle -> handle.createUpdate("UPDATE MCMMOCredits SET credits = :amount WHERE UUID = :uuid;").bind("uuid", uuid).bind("amount", amount).execute() == 1).toCompletableFuture();
    }

    /**
     * Updates credits and redeemed for the provided list of users.
     *
     * @param users The users.
     * @return Returns if update count of each statement in the batch is equal to 1.
     */
    public CompletableFuture<Boolean> applyTransaction(final List<User> users) {
        return this.executor.withHandle(handle -> {
            PreparedBatch batch = handle.prepareBatch("UPDATE MCMMOCredits SET credits = :credits, redeemed = :redeemed WHERE UUID = :uuid;");
            users.forEach(x -> batch.bindMethods(x).add());
            int[] results = batch.execute();
            return Arrays.stream(results).allMatch(x -> x == 1);
        }).toCompletableFuture();
    }

    /**
     * Updates an existing user in the database with the provided user.
     *
     * @param user The user to update.
     * @return True if the transaction was successful, otherwise false.
     */
    public CompletableFuture<Boolean> updateUser(final User user) {
        return this.executor.withHandle(handle -> handle.createUpdate("UPDATE MCMMOCredits SET username = :username, credits = :credits, redeemed = :redeemed WHERE UUID = :uuid;").bindMethods(user).execute() == 1).toCompletableFuture();
    }

    /**
     * Builds a User from a ResultSet.
     */
    static class UserMapper implements RowMapper<User> {
        @Override
        public User map(final ResultSet rs, final StatementContext ctx) throws SQLException {
            return new User(UUID.fromString(rs.getString("UUID")), rs.getString("username"), rs.getInt("credits"), rs.getInt("redeemed"));
        }
    }
}
