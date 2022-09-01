package games.cultivate.mcmmocredits.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.GeneralConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.data.Database;
import games.cultivate.mcmmocredits.data.JSONDatabase;
import games.cultivate.mcmmocredits.data.MYSQLDatabase;
import games.cultivate.mcmmocredits.data.SQLiteDatabase;
import games.cultivate.mcmmocredits.menu.MenuFactory;
import games.cultivate.mcmmocredits.placeholders.ResolverFactory;
import games.cultivate.mcmmocredits.util.InputStorage;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.Map;

/**
 * Class used to interface with {@link Guice}. Responsible for application-wide dependency injection.
 */
public final class PluginModule extends AbstractModule {
    private final MCMMOCredits plugin;
    private final Map<String, Class<? extends Database>> map;
    private Database database;
    private MenuFactory factory;
    private ResolverFactory resolverFactory;

    public PluginModule(final MCMMOCredits plugin) {
        this.plugin = plugin;
        this.map = Map.of("JSON", JSONDatabase.class, "MYSQL", MYSQLDatabase.class, "SQLITE", SQLiteDatabase.class);
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
     * Provides the {@link Database} we are using for injection into constructors.
     *
     * @param config   {@link GeneralConfig} instance to grab the {@link Database} type we want to load.
     * @param injector {@link Injector} instance to get the injected instance of the {@link Database}.
     * @return the {@link Database}
     * @see Database
     */
    @Provides
    public Database provideDatabase(final GeneralConfig config, final Injector injector) {
        if (this.database == null) {
            //Load the config here to make sure db type is accessible.
            config.load();
            this.database = injector.getInstance(this.map.get(config.string("databaseType", false).toUpperCase()));
        }
        return this.database;
    }

    /**
     * Provides the {@link ResolverFactory} we are using for injection into constructors.
     *
     * @param database {@link Database} to use for construction of the object.
     * @return the {@link ResolverFactory}
     * @see ResolverFactory
     */
    @Provides
    public ResolverFactory provideResolverFactory(final Database database) {
        if (this.resolverFactory == null) {
            this.resolverFactory = new ResolverFactory(database);
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
