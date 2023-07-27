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
import games.cultivate.mcmmocredits.actions.Action;
import games.cultivate.mcmmocredits.commands.Credits;
import games.cultivate.mcmmocredits.config.ConfigService;
import games.cultivate.mcmmocredits.config.properties.ConverterProperties;
import games.cultivate.mcmmocredits.config.properties.DatabaseProperties;
import games.cultivate.mcmmocredits.converters.CSVConverter;
import games.cultivate.mcmmocredits.converters.Converter;
import games.cultivate.mcmmocredits.converters.InternalConverter;
import games.cultivate.mcmmocredits.converters.PluginConverter;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.menu.Item;
import games.cultivate.mcmmocredits.menu.Menu;
import games.cultivate.mcmmocredits.serializers.ActionSerializer;
import games.cultivate.mcmmocredits.serializers.ItemSerializer;
import games.cultivate.mcmmocredits.serializers.MenuSerializer;
import games.cultivate.mcmmocredits.user.UserCache;
import games.cultivate.mcmmocredits.user.UserDAO;
import games.cultivate.mcmmocredits.user.UserService;
import games.cultivate.mcmmocredits.util.ChatQueue;
import games.cultivate.mcmmocredits.util.Dir;
import jakarta.inject.Singleton;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Handles Guice Dependency Injection.
 */
public final class PluginModule extends AbstractModule {
    private final MCMMOCredits plugin;
    private ConfigService configService;

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
        this.bind(UserCache.class).asEagerSingleton();
        this.bind(ChatQueue.class).asEagerSingleton();
        this.bind(UserDAO.class).toProvider(Database.class).in(Singleton.class);
        this.bind(Credits.class).asEagerSingleton();
    }

    @Provides
    public ConfigService provideConfigService(final @Dir Path path) {
        if (this.configService == null) {
            this.configService = new ConfigService(YamlConfigurationLoader.builder()
                    .defaultOptions(opts -> opts.serializers(build -> build
                            .register(Item.class, ItemSerializer.INSTANCE)
                            .register(Menu.class, MenuSerializer.INSTANCE)
                            .register(Action.class, ActionSerializer.INSTANCE)))
                    .headerMode(HeaderMode.PRESET)
                    .indent(2)
                    .nodeStyle(NodeStyle.BLOCK), path);
        }
        return this.configService;
    }

    /**
     * Provides the Database from DatabaseProperties.
     *
     * @param properties The properties of the database.
     * @param path       The plugin's data path.
     * @return The Database.
     */
    @Provides
    @Singleton
    public Database provideDatabase(final DatabaseProperties properties, @Dir final Path path) {
        return Database.getDatabase(properties, path);
    }

    /**
     * Provides the ConverterProperties from the config.
     *
     * @param configService The injected ConfigService.
     * @return The ConverterProperties.
     */
    @Provides
    public ConverterProperties provideConverterProperties(final ConfigService configService) {
        return configService.mainConfig().getConverterProperties("converter");
    }

    /**
     * Provides the DatabaseProperties from the config.
     *
     * @param configService The injected ConfigService.
     * @return The DatabaseProperties.
     */
    @Provides
    public DatabaseProperties provideProperties(final ConfigService configService) {
        return configService.mainConfig().getDatabaseProperties("settings", "database");
    }

    /**
     * Provides the Converter for injection.
     *
     * @param properties The converter's properties.
     * @param injector   The injector to grab a specific instance of the Converter.
     * @return A Converter.
     */
    @Provides
    @Singleton
    public Converter provideConverter(final ConverterProperties properties, final Injector injector) {
        return switch (Objects.requireNonNull(properties.type())) {
            case GUI_REDEEM_MCMMO, MORPH_REDEEM -> injector.getInstance(PluginConverter.class);
            case CSV -> injector.getInstance(CSVConverter.class);
            case INTERNAL -> injector.getInstance(InternalConverter.class);
        };
    }
}
