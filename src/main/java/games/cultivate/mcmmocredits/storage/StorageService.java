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

import games.cultivate.mcmmocredits.inject.Dir;
import games.cultivate.mcmmocredits.user.CommandSourceData;
import jakarta.inject.Inject;
import org.jdbi.v3.core.HandleCallback;
import org.jdbi.v3.core.HandleConsumer;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.async.JdbiExecutor;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.locator.ClasspathSqlLocator;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.jdbi.v3.core.statement.StatementContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public final class StorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageService.class);
    private static final ClassLoader CLASS_LOADER = StorageService.class.getClassLoader();
    private final JdbiExecutor jdbiExecutor;
    private final ClasspathSqlLocator locator;
    private final DataSource source;

    private StorageService(final DataSource source, final ExecutorService executorService) {
        this.source = source;
        this.locator = ClasspathSqlLocator.create();
        Jdbi jdbi = Jdbi.create(this.source).registerArgument(new UUIDFactory()).registerRowMapper(new CSDMapper());
        this.jdbiExecutor = JdbiExecutor.create(jdbi, executorService);
        this.useHandle(h -> h.execute(this.query("create_table")));
    }

    @Inject
    public StorageService(@Dir final Path path, final StorageProperties properties, final ExecutorService executorService) {
        this(DataSourceFactory.create(path, properties), executorService);
    }

    public boolean addUser(final CommandSourceData data) {
        return this.withHandle(h -> h.createUpdate(this.query("add_user")).bindMethods(data).execute() == 1);
    }

    public void disable() {
        if (this.source instanceof Closeable closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                LOGGER.error("There was an issue closing the Storage Service!", e);
            }
        }
    }

    public void addUsers(final Collection<CommandSourceData> data) {
        this.useHandle(h -> {
            PreparedBatch batch = h.prepareBatch(this.query("add_user"));
            data.forEach(x -> batch.bindMethods(x).add());
            batch.execute();
        });
    }

    public Optional<CommandSourceData> getUser(final UUID uuid) {
        return this.withHandle(h -> h.createQuery(this.query("get_user_uuid"))
                .bind("uuid", uuid)
                .mapTo(CommandSourceData.class)
                .findOne());
    }

    public Optional<CommandSourceData> getUser(final String username) {
        return this.withHandle(h -> h.createQuery(this.query("get_user_username"))
                .bind("username", username)
                .mapTo(CommandSourceData.class)
                .findOne());
    }

    public List<CommandSourceData> getUserGroup(final int limit, final int offset) {
        return this.withHandle(h -> h.createQuery(this.query("get_user_group"))
                .bind("limit", limit)
                .bind("offset", offset)
                .mapTo(CommandSourceData.class)
                .list());
    }

    public List<CommandSourceData> getAllUsers() {
        return this.withHandle(h -> h.createQuery(this.query("get_all_users")).mapTo(CommandSourceData.class).list());
    }

    public boolean updateUser(final CommandSourceData data) {
        return this.withHandle(h -> h.createUpdate(this.query("update_user")).bindMethods(data).execute() == 1);
    }

    private String query(final String name) {
        return this.locator.getResource(CLASS_LOADER, "queries/%s.sql".formatted(name));
    }

    private <T, X extends Exception> T withHandle(final HandleCallback<T, X> callback) {
        return this.jdbiExecutor.withHandle(callback).toCompletableFuture().join();
    }

    private <X extends Exception> void useHandle(final HandleConsumer<X> consumer) {
        this.jdbiExecutor.useHandle(consumer).toCompletableFuture().join();
    }

    /**
     * Argument Factory required for better MySQL compatibility with UUID data type.
     */
    private static final class UUIDFactory extends AbstractArgumentFactory<UUID> {
        UUIDFactory() {
            super(Types.VARCHAR);
        }

        @Override
        protected Argument build(final UUID value, final ConfigRegistry config) {
            return (p, s, _) -> s.setString(p, value.toString());
        }
    }

    private static final class CSDMapper implements RowMapper<CommandSourceData> {
        @Override
        public CommandSourceData map(final ResultSet rs, final StatementContext ctx) throws SQLException {
            UUID uuid = UUID.fromString(rs.getString("UUID"));
            return new CommandSourceData(uuid, rs.getString("username"), rs.getInt("credits"), rs.getInt("redeemed"));
        }
    }
}
