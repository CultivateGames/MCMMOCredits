//
// MIT License
//
// Copyright (c) 2024 Cultivate Games
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
import com.google.inject.assistedinject.FactoryModuleBuilder;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.command.Commands;
import games.cultivate.mcmmocredits.command.UserParser;
import games.cultivate.mcmmocredits.config.ConfigManager;
import games.cultivate.mcmmocredits.config.Settings;
import games.cultivate.mcmmocredits.converters.ConverterProperties;
import games.cultivate.mcmmocredits.converters.CSVLoader;
import games.cultivate.mcmmocredits.converters.ExternalLoader;
import games.cultivate.mcmmocredits.converters.StorageLoader;
import games.cultivate.mcmmocredits.converters.UserLoader;
import games.cultivate.mcmmocredits.menu.MenuService;
import games.cultivate.mcmmocredits.messages.MessageService;
import games.cultivate.mcmmocredits.storage.StorageService;
import games.cultivate.mcmmocredits.transaction.TransactionFactory;
import games.cultivate.mcmmocredits.user.CommandSource;
import games.cultivate.mcmmocredits.user.UserService;
import jakarta.inject.Singleton;
import org.bukkit.Bukkit;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.processors.cooldown.annotation.CooldownBuilderModifier;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

/**
 * Handles Guice Dependency Injection.
 */
@SuppressWarnings("UnstableApiUsage")
public final class PluginModule extends AbstractModule {
    private static final Path MR_PATH = Path.of("MorphRedeem", "PlayerData");
    private static final Path GRM_PATH = Path.of("GuiRedeemMCMMO", "playerdata");
    private final MCMMOCredits plugin;
    private final ExecutorService executorService;

    /**
     * Constructs the Guice Module.
     *
     * @param plugin Instance of the plugin, obtained from initialization logic.
     */
    public PluginModule(final MCMMOCredits plugin, final ExecutorService executorService) {
        this.plugin = plugin;
        this.executorService = executorService;
    }

    @Override
    protected void configure() {
        this.bind(MCMMOCredits.class).toInstance(this.plugin);
        this.bind(ExecutorService.class).toInstance(this.executorService);
        this.bind(Path.class).annotatedWith(Dir.class).toInstance(this.plugin.getDataFolder().toPath());
        this.bind(Commands.class).asEagerSingleton();
        this.bind(ConfigManager.class).asEagerSingleton();
        this.bind(MessageService.class).asEagerSingleton();
        this.bind(MenuService.class).asEagerSingleton();
        this.bind(UserService.class).asEagerSingleton();
        this.install(new FactoryModuleBuilder().build(TransactionFactory.class));
    }

    @Provides
    @Singleton
    public UserLoader getUserLoader(final Settings settings, final ExecutorService executorService, final Injector injector) {
        ConverterProperties properties = settings.converter();
        Path pluginPath = Bukkit.getPluginsFolder().toPath();
        return switch (properties.type()) {
            case CSV -> injector.getInstance(CSVLoader.class);
            case INTERNAL -> injector.getInstance(StorageLoader.class);
            case PLUGIN_MR -> new ExternalLoader(pluginPath.resolve(MR_PATH), executorService, properties);
            case PLUGIN_GRM -> new ExternalLoader(pluginPath.resolve(GRM_PATH), executorService, properties);
        };
    }

    @Provides
    @Singleton
    public StorageService provideStorage(final @Dir Path path, final ConfigManager configs, final ExecutorService executorService) {
        return new StorageService(path, configs.database(), executorService);
    }

    @Provides
    @Singleton
    @ForConversion
    public StorageService provideConversionStorage(final @Dir Path path, final ConfigManager configs, final ExecutorService executorService) {
        return new StorageService(path, configs.converter().internal(), executorService);
    }

    @Provides
    @Singleton
    public PaperCommandManager<CommandSource> createManager(final Commands commands, final UserService service, final ConfigManager configs) {
        ExecutionCoordinator<CommandSource> coordinator = ExecutionCoordinator.coordinatorFor(this.executorService);
        UserParser parser = new UserParser(service, configs.settings().userTabComplete());
        PaperCommandManager<CommandSource> manager = PaperCommandManager.builder(service.asMapper()).executionCoordinator(coordinator).buildOnEnable(this.plugin);
        manager.brigadierManager().setNativeNumberSuggestions(false);
        manager.parserRegistry().registerParser(parser);
        AnnotationParser<CommandSource> annotationParser = new AnnotationParser<>(manager, CommandSource.class);
        annotationParser.parse(commands);
        CooldownBuilderModifier.install(annotationParser);
        return manager;
    }
}
