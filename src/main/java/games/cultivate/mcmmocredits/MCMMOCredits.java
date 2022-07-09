package games.cultivate.mcmmocredits;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.exceptions.InvalidCommandSenderException;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler.ExceptionType;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import games.cultivate.mcmmocredits.commands.Credits;
import games.cultivate.mcmmocredits.commands.ModifyCredits;
import games.cultivate.mcmmocredits.commands.Redeem;
import games.cultivate.mcmmocredits.config.ConfigUtil;
import games.cultivate.mcmmocredits.data.Database;
import games.cultivate.mcmmocredits.inject.SingletonModule;
import games.cultivate.mcmmocredits.keys.BooleanKey;
import games.cultivate.mcmmocredits.keys.StringKey;
import games.cultivate.mcmmocredits.placeholders.CreditsExpansion;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.util.Listeners;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.interfaces.paper.PaperInterfaceListeners;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * This class is responsible for startup/shutdown logic, and command loading.
 */
public class MCMMOCredits extends JavaPlugin {
    private Injector injector;

    /**
     * This handles all startup logic.
     * <p>
     * This includes creating any necessary instances and dependency checks.
     * <p>
     * This is also responsible for loading configurations,
     * audiences, and registering our event listeners/commands.
     */
    @Override
    public void onEnable() {
        this.injector = Guice.createInjector(new SingletonModule());
        this.checkForDependencies();
        ConfigUtil.loadAllConfigs();
        Database.loadFromConfig();
        this.loadCommands();
        this.loadMenus();
        this.loadListeners();
    }

    /**
     * This handles all shutdown logic.
     * <p>
     * This includes shutting down the Database connection, and saving our Configs.
     */
    @Override
    public void onDisable() {
        ConfigUtil.saveAllConfigs();
        Database.getDatabase().disable();
    }

    private void checkForDependencies() {
        try {
            Class.forName("com.destroystokyo.paper.MaterialSetTag");
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Not running Paper, disabling plugin...");
            this.setEnabled(false);
        }

        if (Bukkit.getPluginManager().getPlugin("mcMMO") == null) {
            this.getLogger().log(Level.SEVERE, "MCMMO is not found, disabling plugin...");
            this.setEnabled(false);
            return;
        }
        this.getLogger().log(Level.INFO, "MCMMO has been found! Continuing to load...");

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CreditsExpansion().register();
        }
    }

    private void loadCommands() {
        PaperCommandManager<CommandSender> commandManager;
        try {
            commandManager = new PaperCommandManager<>(this,
                    AsynchronousCommandExecutionCoordinator.<CommandSender>newBuilder().withAsynchronousParsing().build(),
                    Function.identity(),
                    Function.identity());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        commandManager.registerBrigadier();
        commandManager.registerAsynchronousCompletions();
        commandManager.parserRegistry().registerSuggestionProvider("players", (c, i) -> {
            if (BooleanKey.PLAYER_TAB_COMPLETION.get()) {
                 return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            }
            return List.of();
        });

        AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(commandManager, CommandSender.class, parameters -> SimpleCommandMeta.empty());
        annotationParser.parse(this.injector.getInstance(Credits.class));
        annotationParser.parse(this.injector.getInstance(ModifyCredits.class));
        annotationParser.parse(this.injector.getInstance(Redeem.class));

        MinecraftExceptionHandler<CommandSender> handler = new MinecraftExceptionHandler<>();
        handler.withHandler(ExceptionType.NO_PERMISSION, this.exceptionFunction(StringKey.NO_PERMS));
        handler.withHandler(ExceptionType.ARGUMENT_PARSING, this.exceptionFunction(StringKey.INVALID_ARGUMENTS));
        handler.withHandler(ExceptionType.COMMAND_EXECUTION, this.exceptionFunction(StringKey.COMMAND_ERROR));
        handler.withHandler(ExceptionType.INVALID_SYNTAX, this.exceptionFunction(StringKey.INVALID_ARGUMENTS));
        handler.withHandler(ExceptionType.INVALID_SENDER, this.exceptionFunction(StringKey.INVALID_ARGUMENTS));
        handler.apply(commandManager, AudienceProvider.nativeAudience());
    }

    private BiFunction<CommandSender, Exception, Component> exceptionFunction(StringKey key) {
        return (s, e) -> {
            if (BooleanKey.SETTINGS_DEBUG.get()) {
                e.printStackTrace();
            }
            Resolver.Builder rb = Resolver.builder().sender(s);
            if (e instanceof InvalidSyntaxException ex) {
                rb = rb.tags("correct_syntax", "/" + ex.getCorrectSyntax());
            }
            if (e instanceof InvalidCommandSenderException ex) {
                rb = rb.tags("correct_sender", ex.getRequiredSender().getSimpleName());
            }
            return Text.fromKey(s, key, rb.build()).toComponent();
        };
    }

    /**
     * Loads everything required to operate menus. This may be expanded in the future.
     */
    private void loadMenus() {
        PaperInterfaceListeners.install(this);
    }

    /**
     * Loads everything required for event listeners. This may be expanded in the future.
     */
    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new Listeners(), this);
    }
}
