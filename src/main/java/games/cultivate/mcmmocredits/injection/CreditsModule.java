package games.cultivate.mcmmocredits.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.database.Database;

public class CreditsModule extends AbstractModule {
    private final MCMMOCredits plugin;
    private final Database database;

    public CreditsModule(MCMMOCredits plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        this.bind(MCMMOCredits.class).toInstance(this.plugin);
        this.bind(Database.class).toInstance(this.database);
    }
}
