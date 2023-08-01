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
package games.cultivate.mcmmocredits.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.commands.Credits;
import games.cultivate.mcmmocredits.config.ConfigService;
import games.cultivate.mcmmocredits.converters.Converter;
import games.cultivate.mcmmocredits.converters.ConverterProperties;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.database.DatabaseProperties;
import games.cultivate.mcmmocredits.user.UserService;
import games.cultivate.mcmmocredits.util.ChatQueue;
import games.cultivate.mcmmocredits.util.Dir;
import jakarta.inject.Singleton;

import java.nio.file.Path;

/**
 * Handles Guice Dependency Injection.
 */
public final class PluginModule extends AbstractModule {
    private final MCMMOCredits plugin;

    /**
     * Constructs the Guice Module.
     *
     * @param plugin Instance of the plugin, obtained from initialization logic.
     */
    public PluginModule(final MCMMOCredits plugin) {
        this.plugin = plugin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        this.bind(MCMMOCredits.class).toInstance(this.plugin);
        this.bind(Path.class).annotatedWith(Dir.class).toInstance(this.plugin.getDataFolder().toPath());
        this.bind(UserService.class).asEagerSingleton();
        this.bind(ChatQueue.class).asEagerSingleton();
        this.bind(Credits.class).asEagerSingleton();
        this.bind(ConfigService.class).asEagerSingleton();
    }

    /**
     * Provides the Database from the Config.
     *
     * @param configService The ConfigService to read the database.
     * @param path          Path to create the database if needed.
     * @return The Database.
     */
    @Provides
    @Singleton
    public Database provideDatabase(final ConfigService configService, final @Dir Path path) {
        return configService.mainConfig().get(DatabaseProperties.class, DatabaseProperties.defaults(), "settings", "database").create(path);
    }

    /**
     * Provides the Converter from the Config.
     *
     * @param configService The ConfigService to read the database.
     * @param database      The current database.
     * @param path          The current plugin's data path.
     * @return The Converter
     */
    @Provides
    @Singleton
    public Converter provideConverter(final ConfigService configService, final Database database, final @Dir Path path) {
        ConverterProperties properties = configService.mainConfig().get(ConverterProperties.class, null, "converter");
        return properties.create(database, path);
    }
}
