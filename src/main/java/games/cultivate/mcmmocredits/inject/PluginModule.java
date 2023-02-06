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
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.config.MainConfig.DatabaseProperties;
import games.cultivate.mcmmocredits.config.MainConfig.DatabaseType;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.data.DAOProvider;
import games.cultivate.mcmmocredits.data.UserDAO;
import games.cultivate.mcmmocredits.menu.MenuFactory;
import games.cultivate.mcmmocredits.util.ChatQueue;
import games.cultivate.mcmmocredits.util.PluginPath;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.nio.file.Path;

/**
 * Adds bindings to Guice.
 */
//TODO: increase usage of bound Logger.
public final class PluginModule extends AbstractModule {
    private final MCMMOCredits plugin;
    private UserDAO dao;
    private MenuFactory factory;

    /**
     * Constructs the Guice Module.
     *
     * @param plugin Instance of the plugin. Used to bind a Logger and Path.
     */
    public PluginModule(final MCMMOCredits plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        this.bind(MCMMOCredits.class).toInstance(this.plugin);
        this.bind(JavaPlugin.class).toInstance(this.plugin);
        this.bind(Logger.class).toInstance(this.plugin.getSLF4JLogger());
        this.bind(Path.class).annotatedWith(PluginPath.class).toInstance(this.plugin.getDataFolder().toPath());
        this.bind(MainConfig.class).asEagerSingleton();
        this.bind(MenuConfig.class).asEagerSingleton();
        this.bind(ChatQueue.class).asEagerSingleton();
        this.bind(DAOProvider.class).asEagerSingleton();
    }

    /**
     * Provides the MenuFactory. Required due to passing in multiple injected objects needed to construct Menus.
     *
     * @param menuConfig Instance of the MenuConfig.
     * @param config     Instance of the MainConfig.
     * @param queue      Instance of the ChatQueue.
     * @param plugin     Instance of the plugin.
     * @return The MenuFactory.
     */
    @Provides
    public MenuFactory provideFactory(final MenuConfig menuConfig, final MainConfig config, final ChatQueue queue, final MCMMOCredits plugin) {
        if (this.factory == null) {
            this.factory = new MenuFactory(menuConfig, config, queue, plugin);
        }
        return this.factory;
    }

    /**
     * Provides the UserDAO. Required since DB type is determined by {@link Config}.
     *
     * @param config   Config that stores the {@link DatabaseType}.
     * @param provider The DAOProvider. Loads database based on configuration.
     * @return The UserDAO.
     */
    @Provides
    public UserDAO provideDAO(final MainConfig config, final DAOProvider provider) {
        if (this.dao == null) {
            config.load();
            DatabaseType type = config.getDatabaseType();
            DatabaseProperties properties = config.getDatabaseProperties();
            this.dao = type == MainConfig.DatabaseType.SQLITE ? provider.provideSQLite() : provider.provideSQL(properties);
        }
        return this.dao;
    }
}
