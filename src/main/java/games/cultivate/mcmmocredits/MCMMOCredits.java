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
import cloud.commandframework.exceptions.ArgumentParseException;
import cloud.commandframework.exceptions.CommandExecutionException;
import cloud.commandframework.exceptions.InvalidCommandSenderException;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.exceptions.NoPermissionException;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler.ExceptionType;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.common.base.CaseFormat;
import com.google.inject.Guice;
import com.google.inject.Injector;
import games.cultivate.mcmmocredits.commands.Credits;
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.GeneralConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.data.DAOProvider;
import games.cultivate.mcmmocredits.data.UserDAO;
import games.cultivate.mcmmocredits.inject.PluginModule;
import games.cultivate.mcmmocredits.placeholders.CreditsExpansion;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.placeholders.ResolverFactory;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.util.Listeners;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.interfaces.paper.PaperInterfaceListeners;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Main class of the application. Handles startup and shutdown logic.
 */
public final class MCMMOCredits extends JavaPlugin {
    private Injector injector;
    private GeneralConfig config;
    private boolean isPaper;

    /**
     * Called when the application starts up. Handles injection, checks for required dependencies (Paper, MCMMO etc.),
     * and loads configurations/commands.
     * <p>
     * If debug is enabled in {@link GeneralConfig}, we also track the startup time of the application
     * and print it to console.
     */
    @Override
    public void onEnable() {
        long start = System.nanoTime();
        this.injector = Guice.createInjector(new PluginModule(this));
        this.config = this.injector.getInstance(GeneralConfig.class);
        this.loadConfiguration();
        this.checkForDependencies();
        this.loadCommands();
        this.registerListeners();
        long end = System.nanoTime();
        if (this.config.bool("debug")) {
            this.getSLF4JLogger().info("Plugin enabled! Startup took: {} s.", (double) (end - start) / 1000000000);
        }
    }

    /**
     * Handles all startup loading logic for {@link Config} instances.
     */
    public void loadConfiguration() {
        this.injector.getInstance(GeneralConfig.class).load();
        this.injector.getInstance(MenuConfig.class).load();
    }

    /**
     * Handles all logic required to check for required software (Paper, MCMMO).
     * <p>
     * Registers {@link PlaceholderExpansion} if PlaceholderAPI is present.
     */
    private void checkForDependencies() {
        try {
            Class.forName("com.destroystokyo.paper.MaterialSetTag");
            this.isPaper = true;
        } catch (Exception e) {
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
     * Handles all logic required to enable/load commands.
     */
    private void loadCommands() {
        PaperCommandManager<CommandSender> manager;
        try {
            manager = new PaperCommandManager<>(this, CommandExecutionCoordinator.simpleCoordinator(), t -> t, t -> t);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (this.isPaper) {
            manager.registerBrigadier();
            Objects.requireNonNull(manager.brigadierManager()).setNativeNumberSuggestions(false);
            manager.registerAsynchronousCompletions();
        }
        manager.parserRegistry().registerSuggestionProvider("user", (c, i) -> {
            if (this.config.bool("playerTabCompletion", true)) {
                if (c.getSender() instanceof Player sp) {
                    return Bukkit.getOnlinePlayers().stream().filter(sp::canSee).map(Player::getName).toList();
                }
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            }
            return List.of();
        });
        AnnotationParser<CommandSender> parser = new AnnotationParser<>(manager, CommandSender.class, p -> SimpleCommandMeta.empty());
        parser.parse(this.injector.getInstance(Credits.class));
        MinecraftExceptionHandler<CommandSender> handler = new MinecraftExceptionHandler<>();
        EnumSet.allOf(ExceptionType.class).forEach(x -> handler.withHandler(x, this.buildError(x)));
        handler.apply(manager, AudienceProvider.nativeAudience());
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PaperInterfaceListeners(this, 10L), this);
        Bukkit.getPluginManager().registerEvents(this.injector.getInstance(Listeners.class), this);
    }

    /**
     * Called when the application shuts down. Saves all valuable data.
     */
    @Override
    public void onDisable() {
        this.injector.getInstance(GeneralConfig.class).save();
        this.injector.getInstance(MenuConfig.class).save();
        this.injector.getInstance(DAOProvider.class).disable();
    }

    private BiFunction<CommandSender, Exception, Component> buildError(final ExceptionType exType) {
        String path = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, exType.name());
        return (sender, ex) -> {
            if (this.config.bool("debug") || ex instanceof CommandExecutionException) {
                ex.printStackTrace();
            }
            Resolver.Builder builder = this.injector.getInstance(ResolverFactory.class).builder();
            builder.sender(this.injector.getInstance(UserDAO.class).fromSender(sender));
            switch (exType) {
                case NO_PERMISSION -> builder.exception((NoPermissionException) ex);
                case INVALID_SENDER -> builder.exception((InvalidCommandSenderException) ex);
                case INVALID_SYNTAX -> builder.exception((InvalidSyntaxException) ex);
                case ARGUMENT_PARSING -> builder.exception((ArgumentParseException) ex);
                case COMMAND_EXECUTION -> builder.exception((CommandExecutionException) ex);
            }
            return Text.fromString(sender, this.config.string(path), builder.build()).toComponent();
        };
    }
}
