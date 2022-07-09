package games.cultivate.mcmmocredits.inject;

import com.google.inject.AbstractModule;
import games.cultivate.mcmmocredits.config.MessagesConfig;

public final class SingletonModule extends AbstractModule {

        @Override
        protected void configure() {
            this.bind(MessagesConfig.class).asEagerSingleton();
        }
}
