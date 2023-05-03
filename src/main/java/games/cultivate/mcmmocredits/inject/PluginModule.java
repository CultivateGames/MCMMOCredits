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
import com.google.inject.Injector;
import com.google.inject.Provides;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.converters.CSVConverter;
import games.cultivate.mcmmocredits.converters.Converter;
import games.cultivate.mcmmocredits.converters.InternalConverter;
import games.cultivate.mcmmocredits.converters.PluginConverter;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.database.DatabaseProperties;
import games.cultivate.mcmmocredits.menu.ClickFactory;
import games.cultivate.mcmmocredits.user.UserCache;
import games.cultivate.mcmmocredits.user.UserDAO;
import games.cultivate.mcmmocredits.user.UserService;
import games.cultivate.mcmmocredits.util.ChatQueue;

import javax.inject.Singleton;

/**
 * Adds bindings to Guice.
 */
public final class PluginModule extends AbstractModule {
    private final MCMMOCredits plugin;

    /**
     * Constructs the Guice Module.
     *
     * @param plugin Instance of the plugin. Used to bind a Logger and Path.
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
        this.bind(UserService.class).asEagerSingleton();
        this.bind(UserCache.class).asEagerSingleton();
        this.bind(ChatQueue.class).asEagerSingleton();
        this.bind(ClickFactory.class).asEagerSingleton();
        this.bind(Database.class).asEagerSingleton();
    }

    /**
     * Provides the DAO as configured in config.
     *
     * @param database The injected Database object.
     * @return The UserDAO.
     */
    @Provides
    @Singleton
    public UserDAO provideDAO(final Database database) {
        return database.get();
    }

    /**
     * Provides the DatabaseProperties from the config.
     *
     * @param config The injected MainConfig.
     * @return The DatabaseProperties.
     */
    @Provides
    public DatabaseProperties provideProperties(final MainConfig config) {
        return config.getDatabaseProperties("settings", "database");
    }

    /**
     * Provides the MainConfig for injection. Loads the config first.
     *
     * @return The loaded MainConfig.
     */
    @Provides
    @Singleton
    public MainConfig provideConfig() {
        MainConfig config = new MainConfig();
        config.load();
        return config;
    }

    /**
     * Provides the MenuConfig for injection. Loads the config first.
     *
     * @return The loaded MenuConfig.
     */
    @Provides
    @Singleton
    public MenuConfig provideMenuConfig() {
        MenuConfig config = new MenuConfig();
        config.load();
        return config;
    }

    @Provides
    @Singleton
    public Converter provideConverter(final MainConfig config, final Injector injector) {
        return switch (config.getConverterType("converter", "type")) {
            case EXTERNAL_GRM, EXTERNAL_MORPH -> injector.getInstance(PluginConverter.class);
            case EXTERNAL_CSV -> injector.getInstance(CSVConverter.class);
            case INTERNAL_SQLITE, INTERNAL_H2, INTERNAL_MYSQL -> injector.getInstance(InternalConverter.class);
        };
    }
}
