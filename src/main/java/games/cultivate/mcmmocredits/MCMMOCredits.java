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
package games.cultivate.mcmmocredits;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.PropertyReplacingStringProcessor;
import cloud.commandframework.arguments.parser.ParserRegistry;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.google.inject.Guice;
import com.google.inject.Injector;
import games.cultivate.mcmmocredits.commands.CloudExceptionHandler;
import games.cultivate.mcmmocredits.commands.Credits;
import games.cultivate.mcmmocredits.commands.SkillParser;
import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.converters.Converter;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.inject.PluginModule;
import games.cultivate.mcmmocredits.placeholders.CreditsExpansion;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.UserService;
import games.cultivate.mcmmocredits.util.Listeners;
import io.leangen.geantyref.TypeToken;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.interfaces.paper.PaperInterfaceListeners;
import org.incendo.interfaces.paper.utils.PaperUtils;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Main class of the application. Handles startup and shutdown logic.
 */
public final class MCMMOCredits extends JavaPlugin {
    private static UserService userService;
    private Injector injector;
    private MainConfig config;
    private Logger logger;

    @SuppressWarnings("unused")
    public static UserService getAPI() {
        return userService;
    }

    /**
     * Handles startup of the plugin. Duration is tracked if debug is enabled.
     */
    @Override
    public void onEnable() {
        long start = System.nanoTime();
        this.logger = this.getSLF4JLogger();
        this.injector = Guice.createInjector(new PluginModule(this));
        this.checkForDependencies();
        this.config = this.injector.getInstance(MainConfig.class);
        this.runConversionProcess();
        this.loadCommands();
        this.registerListeners();
        userService = this.injector.getInstance(UserService.class);
        this.enableMetrics();
        long end = System.nanoTime();
        if (this.config.getBoolean("settings", "debug")) {
            this.logger.info("Plugin enabled! Startup took: {}s.", (double) (end - start) / 1000000000);
        }
    }

    /**
     * Checks that all required software is present. Registers our Placeholder expansion if PlaceholderAPI is present.
     *
     * @see CreditsExpansion
     */
    @SuppressWarnings("UnstableApiUsage")
    private void checkForDependencies() {
        this.logger.info("Checking Dependencies...");
        if (!PaperUtils.isPaper()) {
            this.logger.warn("Not using Paper, disabling plugin...");
            this.setEnabled(false);
        }
        this.logger.info("Paper has been found! Continuing to load...");
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.getPlugin("mcMMO") == null) {
            this.logger.warn("Not using mcMMO, disabling plugin...");
            this.setEnabled(false);
            return;
        }
        this.logger.info("mcMMO has been found! Continuing to load...");
        if (pluginManager.getPlugin("PlaceholderAPI") != null) {
            this.injector.getInstance(CreditsExpansion.class).register();
        }
        this.logger.info("Dependencies loaded!");
    }

    /**
     * Loads the Cloud Command Manager and our commands.
     *
     * @see CloudExceptionHandler
     */
    private void loadCommands() {
        this.logger.info("Checking Commands...");
        PaperCommandManager<CommandExecutor> manager;
        try {
            manager = this.loadCommandManager();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        this.loadCommandParser(manager);
        this.parseCommands(manager);
        new CloudExceptionHandler(this.config, manager).apply();
        this.logger.info("Commands loaded!");
    }

    /**
     * Loads the actual CommandManager. Currently only compatible with Paper.
     *
     * @return The loaded CommandManager.
     * @throws Exception thrown when initiating the manager.
     */
    private PaperCommandManager<CommandExecutor> loadCommandManager() throws Exception {
        PaperCommandManager<CommandExecutor> manager;
        Function<CommandSender, CommandExecutor> forwardsMapper = x -> userService.fromSender(x);
        var coordinator = AsynchronousCommandExecutionCoordinator.<CommandExecutor>builder().withAsynchronousParsing().build();
        manager = new PaperCommandManager<>(this, coordinator, forwardsMapper, CommandExecutor::sender);
        manager.registerBrigadier();
        Objects.requireNonNull(manager.brigadierManager()).setNativeNumberSuggestions(false);
        manager.registerAsynchronousCompletions();
        return manager;
    }

    /**
     * Loads the ParserRegistry using the CommandManager.
     * The CommandManager must be loaded before calling this.
     *
     * @param manager The loaded CommandManager.
     */
    private void loadCommandParser(final PaperCommandManager<CommandExecutor> manager) {
        ParserRegistry<CommandExecutor> parser = manager.parserRegistry();
        parser.registerParserSupplier(TypeToken.get(PrimarySkillType.class), x -> new SkillParser<>());
        boolean tabCompletion = this.config.getBoolean("settings", "user-tab-complete");
        parser.registerSuggestionProvider("user", (c, i) -> {
            if (tabCompletion) {
                return Bukkit.getOnlinePlayers().stream().filter(x -> !(c.getSender() instanceof Player p) || x.canSee(p)).map(Player::getName).toList();
            }
            return List.of();
        });
        List<String> menus = List.of("main", "config", "redeem");
        parser.registerSuggestionProvider("menus", (c, i) -> menus);
    }

    /**
     * Parses existing commands using an AnnotationParser, and sets the customizable command prefix.
     *
     * @param manager The loaded CommandManager.
     */
    private void parseCommands(final PaperCommandManager<CommandExecutor> manager) {
        AnnotationParser<CommandExecutor> annotationParser = new AnnotationParser<>(manager, CommandExecutor.class, p -> SimpleCommandMeta.empty());
        String commandPrefix = this.config.getString("command-prefix");
        annotationParser.stringProcessor(new PropertyReplacingStringProcessor(x -> {
            if (x.equals("command.prefix")) {
                return commandPrefix;
            }
            return x;
        }));
        annotationParser.parse(this.injector.getInstance(Credits.class));
    }

    /**
     * Registers all required Event Listeners.
     */
    private void registerListeners() {
        this.logger.info("Registering Listeners...");
        Bukkit.getPluginManager().registerEvents(new PaperInterfaceListeners(this, 10L), this);
        Bukkit.getPluginManager().registerEvents(this.injector.getInstance(Listeners.class), this);
        this.logger.info("Listeners registered!");
    }

    private void enableMetrics() {
        if (this.config.getBoolean("settings", "bstats-metrics-enabled")) {
            this.logger.info("Enabling Bstats.. To disable metrics, set bstats-metrics-enabled to false in config.yml");
            new Metrics(this, 18254);
        }
    }

    private void runConversionProcess() {
        if (this.config.getBoolean("converter", "enabled")) {
            long start = System.nanoTime();
            Converter converter = this.injector.getInstance(Converter.class);
            converter.run(this.logger);
            long end = System.nanoTime();
            this.logger.info("Conversion completed! Process took: {}s.", (double) (end - start) / 1000000000);
        }
    }

    /**
     * Handles shutdown of the plugin. Duration is tracked if debug is enabled.
     */
    @Override
    public void onDisable() {
        long start = System.nanoTime();
        this.injector.getInstance(MainConfig.class).save();
        this.injector.getInstance(MenuConfig.class).save();
        this.injector.getInstance(Database.class).disable();
        long end = System.nanoTime();
        if (this.config.getBoolean("settings", "debug")) {
            this.logger.info("Plugin disabled! Shutdown took: {}s.", (double) (end - start) / 1000000000);
        }
    }
}
