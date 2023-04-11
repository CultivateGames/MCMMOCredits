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
import games.cultivate.mcmmocredits.data.DAOProvider;
import games.cultivate.mcmmocredits.inject.PluginModule;
import games.cultivate.mcmmocredits.placeholders.CreditsExpansion;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.UserService;
import games.cultivate.mcmmocredits.util.Listeners;
import io.leangen.geantyref.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.interfaces.paper.PaperInterfaceListeners;
import org.incendo.interfaces.paper.utils.PaperUtils;

import java.util.List;
import java.util.function.Function;

/**
 * Main class of the application. Handles startup and shutdown logic.
 */
public final class MCMMOCredits extends JavaPlugin {
    private static UserService userService;
    private Injector injector;
    private MainConfig config;

    @SuppressWarnings("unused")
    public static UserService getAPI() {
        return userService;
    }

    @Override
    public void onEnable() {
        long start = System.nanoTime();
        this.injector = Guice.createInjector(new PluginModule(this));
        this.checkForDependencies();
        this.config = this.injector.getInstance(MainConfig.class);
        try {
            this.loadCommands();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.registerListeners();
        userService = this.injector.getInstance(UserService.class);
        long end = System.nanoTime();
        if (this.config.getBoolean("settings", "debug")) {
            this.getSLF4JLogger().info("Plugin enabled! Startup took: {} s.", (double) (end - start) / 1000000000);
        }
    }

    /**
     * Checks that all required software is present. Registers our Placeholder expansion if PlaceholderAPI is present.
     *
     * @see CreditsExpansion
     */
    @SuppressWarnings("UnstableApiUsage")
    private void checkForDependencies() {
        if (!PaperUtils.isPaper()) {
            this.getSLF4JLogger().warn("Not using Paper, disabling plugin...");
            this.setEnabled(false);
        }
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.getPlugin("mcMMO") == null) {
            this.getSLF4JLogger().warn("Not using mcMMO, disabling plugin...");
            this.setEnabled(false);
            return;
        }
        this.getSLF4JLogger().info("mcMMO has been found! Continuing to load...");
        if (pluginManager.getPlugin("PlaceholderAPI") != null) {
            this.injector.getInstance(CreditsExpansion.class).register();
        }
    }

    /**
     * Loads the Cloud Command Manager and our commands.
     *
     * @see CloudExceptionHandler
     */
    private void loadCommands() throws Exception {
        PaperCommandManager<CommandExecutor> manager = this.loadCommandManager();
        this.loadCommandParser(manager);
        this.parseCommands(manager);
        new CloudExceptionHandler(this.config, manager).apply();
    }

    private PaperCommandManager<CommandExecutor> loadCommandManager() throws Exception {
        PaperCommandManager<CommandExecutor> manager;
        Function<CommandSender, CommandExecutor> forwardsMapper = x -> userService.fromSender(x);
        var coordinator = AsynchronousCommandExecutionCoordinator.<CommandExecutor>builder().withAsynchronousParsing().build();
        manager = new PaperCommandManager<>(this, coordinator, forwardsMapper, CommandExecutor::sender);
        manager.registerBrigadier();
        manager.brigadierManager().setNativeNumberSuggestions(false);
        manager.registerAsynchronousCompletions();
        return manager;
    }

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
        Bukkit.getPluginManager().registerEvents(new PaperInterfaceListeners(this, 10L), this);
        Bukkit.getPluginManager().registerEvents(this.injector.getInstance(Listeners.class), this);
    }

    @Override
    public void onDisable() {
        long start = System.nanoTime();
        this.injector.getInstance(MainConfig.class).save();
        this.injector.getInstance(MenuConfig.class).save();
        this.injector.getInstance(DAOProvider.class).disable();
        long end = System.nanoTime();
        if (this.config.getBoolean("settings", "debug")) {
            this.getSLF4JLogger().info("Plugin disabled! Shutdown took: {} s.", (double) (end - start) / 1000000000);
        }
    }
}
