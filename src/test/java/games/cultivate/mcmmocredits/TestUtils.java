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
package games.cultivate.mcmmocredits;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.storage.StorageService;
import games.cultivate.mcmmocredits.user.CommandSourceData;
import org.apache.commons.lang3.RandomStringUtils;
import org.jdbi.v3.core.async.JdbiExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class TestUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final ExecutorService EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    private TestUtils() {
        throw new AssertionError();
    }

    public static StorageService create() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("MCMMOCredits Test Database");
        config.setMaximumPoolSize(1);
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:mem:DB_CLOSE_DELAY=-1;MODE=MySQL;IGNORECASE=TRUE");
        config.setThreadFactory(Thread.ofVirtual().factory());
        var source = new HikariDataSource(config);
        try {
            Constructor<StorageService> constructor = StorageService.class.getDeclaredConstructor(DataSource.class, ExecutorService.class);
            constructor.setAccessible(true);
            return constructor.newInstance(source, EXECUTOR);
        } catch (Exception e) {
            LOGGER.error("There was an issue constructing a StorageService", e);
            return null;
        }
    }

    public static CommandSourceData createUser() {
        UUID uuid = UUID.randomUUID();
        String name = RandomStringUtils.random(RANDOM.nextInt(3, 16), true, true);
        int credits = RANDOM.nextInt(0, 99999);
        int redeemed = RANDOM.nextInt(0, 99999);
        return new CommandSourceData(uuid, name, credits, redeemed);
    }

    public static List<CommandSourceData> createUsers(final int amount) {
        return IntStream.range(0, amount).mapToObj(_ -> createUser()).toList();
    }

    public static void deleteDatabase(final StorageService storageService) {
        try {
            //TODO: not great, but may have to do. look into fixing this.
            Field field = StorageService.class.getDeclaredField("jdbiExecutor");
            field.setAccessible(true);
            JdbiExecutor jdbiExecutor = (JdbiExecutor) field.get(storageService);
            jdbiExecutor.useHandle(handle -> handle.execute("DELETE FROM MCMMOCredits"));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Could not delete table between test runs!", e);
        }
    }
}
