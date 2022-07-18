package games.cultivate.mcmmocredits.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.config.MessagesConfig;
import games.cultivate.mcmmocredits.config.SettingsConfig;
import games.cultivate.mcmmocredits.data.Database;
import games.cultivate.mcmmocredits.data.InputStorage;
import games.cultivate.mcmmocredits.data.MYSQLDatabase;
import games.cultivate.mcmmocredits.data.SQLiteDatabase;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class PluginModule extends AbstractModule {
    private final MCMMOCredits mcmmoCredits;
    private Database database;

    public PluginModule(MCMMOCredits mcmmoCredits) {
        this.mcmmoCredits = mcmmoCredits;
    }

    @Override
    protected void configure() {
        this.bind(MCMMOCredits.class).toInstance(this.mcmmoCredits);
        this.bind(JavaPlugin.class).toInstance(this.mcmmoCredits);
        this.bind(MessagesConfig.class).asEagerSingleton();
        this.bind(SettingsConfig.class).asEagerSingleton();
        this.bind(MenuConfig.class).asEagerSingleton();
        this.bind(InputStorage.class).asEagerSingleton();
        this.requestStaticInjection(Resolver.Builder.class);
    }

    @Provides
    public @NonNull Database provideDatabase(SettingsConfig settings, MCMMOCredits plugin) {
        if (this.database == null) {
            this.database = settings.isMYSQL() ? new MYSQLDatabase(settings, plugin) : new SQLiteDatabase(settings, plugin);
        }
        return this.database;
    }
}
