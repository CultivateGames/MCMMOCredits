package games.cultivate.mcmmocredits.inject;

import com.google.inject.AbstractModule;
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
import games.cultivate.mcmmocredits.util.InputStorage;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.Map;

public final class PluginModule extends AbstractModule {
    private final MCMMOCredits mcmmoCredits;
    private Database database;
    private MenuFactory factory;
    private final Map<String, Class<? extends Database>> map;

    public PluginModule(final MCMMOCredits mcmmoCredits) {
        this.mcmmoCredits = mcmmoCredits;
        this.map = Map.of("JSON", JSONDatabase.class, "MYSQL", MYSQLDatabase.class, "SQLITE", SQLiteDatabase.class);
    }

    @Override
    protected void configure() {
        this.bind(MCMMOCredits.class).toInstance(this.mcmmoCredits);
        this.bind(JavaPlugin.class).toInstance(this.mcmmoCredits);
        this.bind(Path.class).annotatedWith(Names.named("dir")).toInstance(this.mcmmoCredits.getDataFolder().toPath());
        this.bind(GeneralConfig.class).asEagerSingleton();
        this.bind(MenuConfig.class).asEagerSingleton();
        this.bind(InputStorage.class).asEagerSingleton();
    }

    @Provides
    public Database provideDatabase(final GeneralConfig config, final Injector injector) {
        if (this.database == null) {
            //Load the config here to make sure db type is accessible.
            config.load();
            this.database = injector.getInstance(this.map.get(config.string("databaseType", false).toUpperCase()));
        }
        return this.database;
    }

    @Provides
    public MenuFactory provideMenuFactory(final MenuConfig menus, final GeneralConfig config, final InputStorage storage, final MCMMOCredits plugin) {
        if (this.factory == null) {
            this.factory = new MenuFactory(menus, config, storage, plugin);
        }
        return this.factory;
    }
}
