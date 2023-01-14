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
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.GeneralConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.data.MySQLProvider;
import games.cultivate.mcmmocredits.data.SQLiteProvider;
import games.cultivate.mcmmocredits.data.UserDAO;
import games.cultivate.mcmmocredits.menu.MenuFactory;
import games.cultivate.mcmmocredits.placeholders.ResolverFactory;
import games.cultivate.mcmmocredits.util.InputStorage;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

/**
 * Class used to interface with {@link Guice}. Responsible for application-wide dependency injection.
 */
public final class PluginModule extends AbstractModule {
    private final MCMMOCredits plugin;
    private MenuFactory factory;
    private ResolverFactory resolverFactory;
    private UserDAO dao;

    public PluginModule(final MCMMOCredits plugin) {
        this.plugin = plugin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        this.bind(MCMMOCredits.class).toInstance(this.plugin);
        this.bind(JavaPlugin.class).toInstance(this.plugin);
        this.bind(Path.class).annotatedWith(Names.named("dir")).toInstance(this.plugin.getDataFolder().toPath());
        this.bind(GeneralConfig.class).asEagerSingleton();
        this.bind(MenuConfig.class).asEagerSingleton();
        this.bind(InputStorage.class).asEagerSingleton();
    }

    /**
     * Provides the {@link UserDAO} we are using for constructors.
     *
     * @param config {@link GeneralConfig} to grab the Database type we want.
     * @return the {@link UserDAO}
     */
    @Provides
    public UserDAO provideDAO(final GeneralConfig config, final Injector injector) {
        if (this.dao == null) {
            //Load configuration to make sure we know the DB type.
            config.load();
            String type = config.string("databaseType", false).toUpperCase();
            if (type.equals("MYSQL")) {
                this.dao = injector.getInstance(MySQLProvider.class).provide();
                return this.dao;
            }
            if (type.equals("SQLITE")) {
                this.dao = injector.getInstance(SQLiteProvider.class).provide();
                return this.dao;
            }
        }
        return this.dao;
    }

    /**
     * Provides the {@link ResolverFactory} we are using for injection into constructors.
     *
     * @param dao {@link UserDAO} to use for construction of the object.
     * @return the {@link ResolverFactory}
     * @see ResolverFactory
     */
    @Provides
    public ResolverFactory provideResolverFactory(final UserDAO dao) {
        if (this.resolverFactory == null) {
            this.resolverFactory = new ResolverFactory(dao);
        }
        return this.resolverFactory;
    }

    /**
     * Provides the {@link MenuFactory} we are using for injection into constructors.
     *
     * @param menus           {@link MenuConfig} to use for construction of the object.
     * @param resolverFactory {@link ResolverFactory} to use for construction of the object.
     * @param config          {@link GeneralConfig} to use for construction of the object.
     * @param storage         {@link InputStorage} to use for construction of the object.
     * @param plugin          {@link MCMMOCredits} to use for construction of the object
     * @return the {@link MenuFactory}
     * @see MenuFactory
     */
    @Provides
    public MenuFactory provideMenuFactory(final MenuConfig menus, final ResolverFactory resolverFactory, final GeneralConfig config, final InputStorage storage, final MCMMOCredits plugin) {
        if (this.factory == null) {
            this.factory = new MenuFactory(menus, resolverFactory, config, storage, plugin);
        }
        return this.factory;
    }
}
