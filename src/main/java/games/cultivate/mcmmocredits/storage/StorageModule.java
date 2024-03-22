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

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.Settings;
import games.cultivate.mcmmocredits.config.Settings.StorageProperties;
import games.cultivate.mcmmocredits.inject.Dir;
import games.cultivate.mcmmocredits.inject.ForConversion;
import jakarta.inject.Singleton;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

public final class StorageModule extends AbstractModule {
    @Provides
    @Singleton
    public StorageService provideStorage(final @Dir Path path, final Settings settings, final ExecutorService executorService) {
        return this.createStorageService(path, settings.database(), executorService);
    }

    @Provides
    @Singleton
    @ForConversion
    public StorageService provideConverterStorage(final @Dir Path path, final Settings settings, final ExecutorService executorService) {
        return this.createStorageService(path, settings.converter().internal(), executorService);
    }

    private StorageService createStorageService(final Path path, final StorageProperties properties, final ExecutorService service) {
        var source = switch (properties.type()) {
            case H2 -> this.createH2Source(path);
            case MYSQL -> this.createSqlSource(properties);
        };
        return new DefaultStorageService(source, service);
    }

    private DataSource createH2Source(final Path path) {
        MCMMOCredits.createFile(path.resolve("database.mv.db"));
        var config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl(String.format("jdbc:h2:file:./%s;MODE=MySQL;IGNORECASE=TRUE", path.resolve("database")));
        config.setMaximumPoolSize(20);
        config.setPoolName("MCMMOCredits Database");
        return new HikariDataSource(config);
    }

    private DataSource createSqlSource(final StorageProperties properties) {
        var config = new HikariConfig();
        config.setMaximumPoolSize(20);
        config.setPoolName("MCMMOCredits Database");
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("rewriteBatchedStatements", true);
        config.addDataSourceProperty("cacheResultSetMetadata", true);
        config.addDataSourceProperty("cacheServerConfiguration", true);
        config.addDataSourceProperty("elideSetAutoCommits", true);
        config.addDataSourceProperty("maintainTimeStats", false);
        config.setJdbcUrl(properties.url());
        config.setPassword(properties.password());
        config.setUsername(properties.username());
        return new HikariDataSource(config);
    }
}
