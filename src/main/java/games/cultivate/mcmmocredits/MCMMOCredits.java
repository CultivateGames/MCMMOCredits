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
import cloud.commandframework.arguments.parser.ParserRegistry;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.google.inject.Guice;
import com.google.inject.Injector;
import games.cultivate.mcmmocredits.commands.CloudExceptionHandler;
import games.cultivate.mcmmocredits.commands.Credits;
import games.cultivate.mcmmocredits.commands.SkillParser;
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.data.DAOProvider;
import games.cultivate.mcmmocredits.data.UserDAO;
import games.cultivate.mcmmocredits.inject.PluginModule;
import games.cultivate.mcmmocredits.placeholders.CreditsExpansion;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.util.Listeners;
import io.leangen.geantyref.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.interfaces.paper.PaperInterfaceListeners;
import org.incendo.interfaces.paper.utils.PaperUtils;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.Function;

/**
 * Main class of the application. Handles startup and shutdown logic.
 */
public final class MCMMOCredits extends JavaPlugin {
    private Injector injector;
    private MainConfig config;
    private Logger pluginLogger;
    private static MCMMOCreditsAPI creditsAPI;

    @Override
    public void onEnable() {
        long start = System.nanoTime();
        injector = Guice.createInjector(new PluginModule(this));
        creditsAPI = injector.getInstance(MCMMOCreditsAPI.class);
        this.config = injector.getInstance(MainConfig.class);
        this.pluginLogger = injector.getInstance(Logger.class);
        this.checkForDependencies();
        this.loadConfiguration();
        this.loadCommands();
        this.registerListeners();
        long end = System.nanoTime();
        if (this.config.bool("settings", "debug")) {
            this.pluginLogger.info("Plugin enabled! Startup took: {} s.", (double) (end - start) / 1000000000);
        }
    }

    /**
     * Loads injected instances of {@link Config}
     */
    public void loadConfiguration() {
        injector.getInstance(MainConfig.class).load();
        injector.getInstance(MenuConfig.class).load();
    }

    /**
     * Checks that all required software is present. Registers our Placeholder expansion if PlaceholderAPI is present.
     *
     * @see CreditsExpansion
     */
    private void checkForDependencies() {
        if (!PaperUtils.isPaper()) {
            this.pluginLogger.warn("Not using Paper, disabling plugin...");
            this.setEnabled(false);
        }
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.getPlugin("mcMMO") == null) {
            this.pluginLogger.warn("Not using mcMMO, disabling plugin...");
            this.setEnabled(false);
            return;
        }
        this.pluginLogger.info("mcMMO has been found! Continuing to load...");
        if (pluginManager.getPlugin("PlaceholderAPI") != null) {
            injector.getInstance(CreditsExpansion.class).register();
        }
    }

    /**
     * Loads the Cloud Command Manager and our commands.
     *
     * @see CloudExceptionHandler
     */
    private void loadCommands() {
        PaperCommandManager<CommandExecutor> manager;
        Function<CommandSender, CommandExecutor> forwardsMapper = x -> injector.getInstance(UserDAO.class).fromSender(x);
        try {
            manager = new PaperCommandManager<>(this, CommandExecutionCoordinator.simpleCoordinator(), forwardsMapper, CommandExecutor::sender);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        manager.registerBrigadier();
        manager.brigadierManager().setNativeNumberSuggestions(false);
        manager.registerAsynchronousCompletions();
        ParserRegistry<CommandExecutor> parser = manager.parserRegistry();
        parser.registerParserSupplier(TypeToken.get(PrimarySkillType.class), options -> new SkillParser<>());
        boolean tabCompletion = this.config.node("settings", "user-tab-complete").getBoolean();
        parser.registerSuggestionProvider("user", (c, i) -> {
            if (tabCompletion) {
                return Bukkit.getOnlinePlayers().stream().filter(x -> !(c.getSender() instanceof Player p) || x.canSee(p)).map(Player::getName).toList();
            }
            return List.of();
        });
        parser.registerSuggestionProvider("menus", (c, i) -> List.of("main", "config", "redeem"));
        AnnotationParser<CommandExecutor> annotationParser = new AnnotationParser<>(manager, CommandExecutor.class, p -> SimpleCommandMeta.empty());
        annotationParser.parse(injector.getInstance(Credits.class));
        new CloudExceptionHandler(this.config, manager).apply();
    }

    /**
     * Registers all required Event Listeners.
     */
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PaperInterfaceListeners(this, 10L), this);
        Bukkit.getPluginManager().registerEvents(injector.getInstance(Listeners.class), this);
    }

    @Override
    public void onDisable() {
        injector.getInstance(MainConfig.class).save();
        injector.getInstance(MenuConfig.class).save();
        injector.getInstance(DAOProvider.class).disable();
    }

    public static MCMMOCreditsAPI getAPI() {
        return creditsAPI;
    }
}
