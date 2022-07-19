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
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.config.MessagesConfig;
import games.cultivate.mcmmocredits.config.SettingsConfig;
import games.cultivate.mcmmocredits.data.Database;
import games.cultivate.mcmmocredits.inject.PluginModule;
import games.cultivate.mcmmocredits.placeholders.CreditsExpansion;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.util.Listeners;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.interfaces.paper.PaperInterfaceListeners;

import java.util.List;
import java.util.function.BiFunction;
import java.util.logging.Level;

/**
 * This class is responsible for startup/shutdown logic, and command loading.
 */
public class MCMMOCredits extends JavaPlugin {
    private Injector injector;
    private SettingsConfig settings;

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
        this.injector = Guice.createInjector(new PluginModule(this));
        this.checkForDependencies();
        this.loadConfiguration();
        this.settings = this.injector.getInstance(SettingsConfig.class);
        this.injector.getInstance(Database.class);
        this.loadCommands();
        PaperInterfaceListeners.install(this);
        Bukkit.getPluginManager().registerEvents(this.injector.getInstance(Listeners.class), this);
    }

    public void loadConfiguration() {
        this.injector.getInstance(MessagesConfig.class).load();
        this.injector.getInstance(MenuConfig.class).load();
        this.injector.getInstance(SettingsConfig.class).load();
    }

    /**
     * This handles all shutdown logic.
     * <p>
     * This includes shutting down the Database connection, and saving our Configs.
     */
    @Override
    public void onDisable() {
        this.injector.getInstance(SettingsConfig.class).save();
        this.injector.getInstance(MessagesConfig.class).save();
        this.injector.getInstance(MenuConfig.class).save();
        this.injector.getInstance(Database.class).disable();
    }

    private void checkForDependencies() {
        try {
            Class.forName("com.destroystokyo.paper.MaterialSetTag");
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Not running Paper, disabling plugin...");
            this.setEnabled(false);
        }
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.getPlugin("mcMMO") == null) {
            this.getLogger().log(Level.SEVERE, "mcMMO is not found, disabling plugin...");
            this.setEnabled(false);
            return;
        }
        this.getLogger().log(Level.INFO, "mcMMO has been found! Continuing to load...");

        if (pluginManager.getPlugin("PlaceholderAPI") != null) {
            this.injector.getInstance(CreditsExpansion.class).register();
        }
    }

    private void loadCommands() {
        PaperCommandManager<CommandSender> manager;
        try {
            manager = new PaperCommandManager<>(this, AsynchronousCommandExecutionCoordinator.<CommandSender>newBuilder().withAsynchronousParsing().build(), t -> t, t -> t);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        manager.registerBrigadier();
        manager.registerAsynchronousCompletions();

        manager.parserRegistry().registerSuggestionProvider("user", (c, i) -> {
            if (this.settings.bool("playerTabCompletion", true)) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            }
            return List.of();
        });

        AnnotationParser<CommandSender> parser = new AnnotationParser<>(manager, CommandSender.class, parameters -> SimpleCommandMeta.empty());
        parser.parse(this.injector.getInstance(Credits.class));
        parser.parse(this.injector.getInstance(ModifyCredits.class));
        parser.parse(this.injector.getInstance(Redeem.class));

        MessagesConfig messages = this.injector.getInstance(MessagesConfig.class);
        MinecraftExceptionHandler<CommandSender> handler = new MinecraftExceptionHandler<>();
        handler.withHandler(ExceptionType.NO_PERMISSION, this.exceptionFunction(messages.string("noPermission")));
        handler.withHandler(ExceptionType.ARGUMENT_PARSING, this.exceptionFunction(messages.string("invalidArguments")));
        handler.withHandler(ExceptionType.COMMAND_EXECUTION, this.exceptionFunction(messages.string("commandError")));
        handler.withHandler(ExceptionType.INVALID_SYNTAX, this.exceptionFunction(messages.string("invalidArguments")));
        handler.withHandler(ExceptionType.INVALID_SENDER, this.exceptionFunction(messages.string("commandError")));
        handler.apply(manager, AudienceProvider.nativeAudience());
    }

    private BiFunction<CommandSender, Exception, Component> exceptionFunction(String string) {
        return (sender, exception) -> {
            if (this.settings.bool("debug", false)) {
                exception.printStackTrace();
            }
            Resolver.Builder rb = Resolver.builder().sender(sender);
            if (exception instanceof InvalidSyntaxException ex) {
                rb = rb.tags("correct_syntax", "/" + ex.getCorrectSyntax());
            }
            if (exception instanceof InvalidCommandSenderException ex) {
                rb = rb.tags("correct_sender", ex.getRequiredSender().getSimpleName());
            }
            return Text.fromString(sender, string, rb.build()).toComponent();
        };
    }
}
