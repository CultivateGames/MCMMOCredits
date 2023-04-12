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
import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.data.DatabaseProperties;
import games.cultivate.mcmmocredits.menu.ClickFactory;
import games.cultivate.mcmmocredits.user.UserCache;
import games.cultivate.mcmmocredits.user.UserService;
import games.cultivate.mcmmocredits.util.ChatQueue;

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
    }

    /**
     * Provides properties of the Database through the configuration.
     *
     * @param config The injected MainConfig.
     * @return the Database properties.
     */
    @Provides
    public DatabaseProperties provideProperties(final MainConfig config) {
        return config.getDatabaseProperties();
    }

    /**
     * Provides the MainConfig for injection. Loads the config first.
     *
     * @return The loaded MainConfig.
     */
    @Provides
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
    public MenuConfig provideMenuConfig() {
        MenuConfig config = new MenuConfig();
        config.load();
        return config;
    }
}
