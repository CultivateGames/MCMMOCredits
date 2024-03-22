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
package games.cultivate.mcmmocredits.storage;

import games.cultivate.mcmmocredits.user.User;
import jakarta.inject.Inject;
import org.jdbi.v3.core.HandleCallback;
import org.jdbi.v3.core.HandleConsumer;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.async.JdbiExecutor;
import org.jdbi.v3.core.locator.ClasspathSqlLocator;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.jdbi.v3.core.statement.StatementContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

final class DefaultStorageService implements StorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultStorageService.class);
    private final JdbiExecutor jdbiExecutor;
    private final ClasspathSqlLocator locator;
    private final DataSource source;

    @Inject
    DefaultStorageService(final DataSource source, final ExecutorService service) {
        this.source = source;
        this.locator = ClasspathSqlLocator.create();
        Jdbi jdbi = Jdbi.create(source).registerArgument(new UUIDFactory()).registerRowMapper((ResultSet rs, StatementContext ctx) -> {
            UUID uuid = UUID.fromString(rs.getString("UUID"));
            return new User(uuid, rs.getString("username"), rs.getInt("credits"), rs.getInt("redeemed"));
        });
        this.jdbiExecutor = JdbiExecutor.create(jdbi, service);
        this.useHandle(h -> h.execute(this.query("create_table")));
    }

    @Override
    public boolean addUser(final User user) {
        return this.withHandle(h -> h.createUpdate(this.query("add_user")).bindMethods(user).execute() == 1);
    }

    @Override
    public void disable() {
        if (this.source instanceof Closeable closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                LOGGER.error("There was an issue closing the Storage Service!", e);
            }
        }
    }

    @Override
    public void addUsers(final Collection<User> users) {
        this.useHandle(h -> {
            PreparedBatch batch = h.prepareBatch(this.query("add_user"));
            users.forEach(x -> batch.bindMethods(x).add());
            batch.execute();
        });
    }

    @Override
    public Optional<User> getUser(final UUID uuid) {
        return this.withHandle(h -> h.createQuery(this.query("get_user_uuid")).bind("uuid", uuid).mapTo(User.class).findOne());
    }

    @Override
    public Optional<User> getUser(final String username) {
        return this.withHandle(h -> h.createQuery(this.query("get_user_username")).bind("username", username).mapTo(User.class).findOne());
    }

    @Override
    public List<User> getUserGroup(final int limit, final int offset) {
        return this.withHandle(h -> h.createQuery(this.query("get_user_group")).bind("limit", limit).bind("offset", offset).mapTo(User.class).list());
    }

    @Override
    public List<User> getAllUsers() {
        return this.withHandle(h -> h.createQuery(this.query("get_all_users")).mapTo(User.class).list());
    }

    @Override
    public boolean updateUser(final User user) {
        return this.withHandle(h -> h.createUpdate(this.query("update_user")).bindMethods(user).execute() == 1);
    }

    private String query(final String name) {
        return this.locator.getResource(DefaultStorageService.class.getClassLoader(), "queries/%s.sql".formatted(name));
    }

    private <T, X extends Exception> T withHandle(final HandleCallback<T, X> callback) {
        return this.jdbiExecutor.withHandle(callback).toCompletableFuture().join();
    }

    private <X extends Exception> void useHandle(final HandleConsumer<X> consumer) {
        this.jdbiExecutor.useHandle(consumer).toCompletableFuture().join();
    }
}
