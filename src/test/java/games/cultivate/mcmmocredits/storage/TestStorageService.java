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

import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.user.User;
import org.jdbi.v3.core.async.JdbiExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;

/**
 * Delegating implementation of StorageService used for testing.
 */
public final class TestStorageService implements StorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestStorageService.class);
    private final StorageService storageService;

    private TestStorageService(final DataSource source) {
        this.storageService = new DefaultStorageService(source, Executors.newVirtualThreadPerTaskExecutor());
    }

    public static TestStorageService create(final String name) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1;MODE=MYSQL;IGNORECASE=TRUE".formatted(name));
        return new TestStorageService(ds);
    }

    public void delete() {
        try {
            //TODO: not great, but may have to do. look into fixing this.
            Field jdbiExecutorField = DefaultStorageService.class.getDeclaredField("jdbiExecutor");
            jdbiExecutorField.setAccessible(true);
            JdbiExecutor jdbiExecutor = (JdbiExecutor) jdbiExecutorField.get(storageService);
            jdbiExecutor.useHandle(handle -> handle.execute("DELETE FROM MCMMOCredits"));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Could not delete table between test runs!", e);
        }
    }

    @Override
    public boolean addUser(final User user) {
        return this.storageService.addUser(user);
    }

    @Override
    public void disable() {
        this.storageService.disable();
    }

    @Override
    public void addUsers(final Collection<User> users) {
        this.storageService.addUsers(users);
    }

    @Override
    public Optional<User> getUser(final UUID uuid) {
        return this.storageService.getUser(uuid);
    }

    @Override
    public Optional<User> getUser(final String username) {
        return this.storageService.getUser(username);
    }

    @Override
    public List<User> getUserGroup(final int limit, final int offset) {
        return this.storageService.getUserGroup(limit, offset);
    }

    @Override
    public List<User> getAllUsers() {
        return this.storageService.getAllUsers();
    }

    @Override
    public boolean updateUser(final User user) {
        return this.storageService.updateUser(user);
    }
}
