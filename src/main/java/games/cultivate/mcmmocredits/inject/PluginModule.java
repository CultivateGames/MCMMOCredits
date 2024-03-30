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
import com.google.inject.Provides;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.command.Commands;
import games.cultivate.mcmmocredits.command.UserParser;
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.Data;
import games.cultivate.mcmmocredits.config.MenuSettings;
import games.cultivate.mcmmocredits.config.Settings;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.UserService;
import jakarta.inject.Singleton;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.processors.cooldown.annotation.CoooldownBuilderModifier;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

/**
 * Handles Guice Dependency Injection.
 */
public final class PluginModule extends AbstractModule {
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
    }

    @Provides
    @Singleton
    public Settings provideSettings(final @Dir Path path) {
        //TODO: singleton config = no reload capability. determine if necessary.
        return this.createConfig(Settings.class, path.resolve("config.yml")).data();
    }

    @Provides
    @Singleton
    public MenuSettings provideMenuSettings(final @Dir Path path) {
        return this.createConfig(MenuSettings.class, path.resolve("menu.yml")).data();
    }

    @Provides
    @Singleton
    public UserParser provideParser(final UserService userService, final Settings settings) {
        return new UserParser(userService, settings.userTabComplete());
    }

    @Provides
    @Singleton
    public PaperCommandManager<CommandExecutor> createManager(final Commands commands, final UserService userService, final UserParser parser) {
        PaperCommandManager<CommandExecutor> manager = new PaperCommandManager<>(this.plugin, ExecutionCoordinator.coordinatorFor(this.executorService), userService.toSenderMapper());
        manager.registerBrigadier();
        manager.brigadierManager().setNativeNumberSuggestions(false);
        manager.registerAsynchronousCompletions();
        manager.parserRegistry().registerParser(parser);
//        ImmutableConstantCaptionProvider<CommandExecutor> captionProvider = CaptionProvider.<CommandExecutor>constantProvider().putAllCaptions(captions.messages()).build();
//        manager.captionRegistry().registerProvider(captionProvider);
//        manager.registerCommandPreProcessor(ppc -> ppc.commandContext().store(Keys.SENDER, ppc.commandContext().sender()));
        AnnotationParser<CommandExecutor> annotationParser = new AnnotationParser<>(manager, CommandExecutor.class);
        annotationParser.parse(commands);
        CoooldownBuilderModifier.install(annotationParser);
        return manager;
    }

    private <D extends Data> Config<D> createConfig(final Class<? extends D> type, final Path path) {
        MCMMOCredits.createFile(path);
        Config<D> config = new Config<>(type, path);
        config.load();
        return config;
    }
}
