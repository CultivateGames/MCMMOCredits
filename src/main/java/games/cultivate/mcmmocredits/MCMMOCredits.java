package games.cultivate.mcmmocredits;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.exceptions.InvalidCommandSenderException;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.exceptions.NoPermissionException;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import games.cultivate.mcmmocredits.commands.Credits;
import games.cultivate.mcmmocredits.commands.ModifyCredits;
import games.cultivate.mcmmocredits.commands.Redeem;
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.util.CreditsExpansion;
import games.cultivate.mcmmocredits.util.Listeners;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.interfaces.paper.PaperInterfaceListeners;

import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * This class is responsible for startup/shutdown logic, and command loading.
 */
public class MCMMOCredits extends JavaPlugin {
    public static NamespacedKey key;
    public static String path;
    private Database database;

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
        key = NamespacedKey.fromString("mcmmocredits");
        path = this.getDataFolder().getAbsolutePath() + "\\";
        this.dependCheck();
        Config.MESSAGES.load("messages.conf");
        Config.SETTINGS.load("settings.conf");
        Config.MENU.load("menus.conf");
        database = new Database(this);
        Util.setDatabase(database);
        this.loadCommands();
        PaperInterfaceListeners.install(this);
        Bukkit.getPluginManager().registerEvents(new Listeners(database), this);
    }

    /**
     * This handles all shutdown logic.
     * <p>
     * This includes shutting down the Database connection, and saving our Configs.
     */
    @Override
    public void onDisable() {
        Config.MENU.save(Config.MENU.root());
        Config.MESSAGES.save(Config.MESSAGES.root());
        Config.SETTINGS.save(Config.SETTINGS.root());
        database.disable();
    }

    private void dependCheck() {
        try {
            Class.forName("com.destroystokyo.paper.MaterialSetTag");
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Not running Paper, disabling plugin...");
            this.setEnabled(false);
        }

        if (Bukkit.getPluginManager().getPlugin("mcMMO") != null) {
            this.getLogger().log(Level.INFO, "MCMMO has been found! Continuing to load...");
        } else {
            this.getLogger().log(Level.SEVERE, "MCMMO is not found, disabling plugin...");
            this.setEnabled(false);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CreditsExpansion(database).register();
        }
    }

    private void loadCommands() {
        PaperCommandManager<CommandSender> commandManager;
        try {
            commandManager = new PaperCommandManager<>(this, AsynchronousCommandExecutionCoordinator.<CommandSender>newBuilder().withAsynchronousParsing().build(), Function.identity(), Function.identity());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (commandManager.queryCapability(CloudBukkitCapabilities.BRIGADIER)) {
            commandManager.registerBrigadier();
        }
        if (commandManager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            commandManager.registerAsynchronousCompletions();
        }
        commandManager.getParserRegistry().registerSuggestionProvider("customPlayer", (context, input) -> Keys.PLAYER_TAB_COMPLETION.get() ? Bukkit.getOnlinePlayers().stream().map(Player::getName).toList() : List.of());

        AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(commandManager, CommandSender.class, parameters -> SimpleCommandMeta.empty());
        annotationParser.parse(new ModifyCredits(database));
        annotationParser.parse(new Credits(database));
        annotationParser.parse(new Redeem(database));

        new MinecraftExceptionHandler<CommandSender>()
                .withDefaultHandlers()
                .withHandler(MinecraftExceptionHandler.ExceptionType.NO_PERMISSION, (sender, ex) -> {
                    if (Keys.SETTINGS_DEBUG.get()) {
                        ex.printStackTrace();
                    }
                    return Util.exceptionMessage(sender, Keys.NO_PERMS.get(), Util.createPlaceholder("required_permission", ((NoPermissionException) ex).getMissingPermission()));
                })
                .withHandler(MinecraftExceptionHandler.ExceptionType.ARGUMENT_PARSING, (sender, ex) -> {
                    if (Keys.SETTINGS_DEBUG.get()) {
                        ex.printStackTrace();
                    }
                    return Util.exceptionMessage(sender, Keys.INVALID_ARGUMENTS.get());
                })
                .withHandler(MinecraftExceptionHandler.ExceptionType.COMMAND_EXECUTION, (sender, ex) -> {
                    if (Keys.SETTINGS_DEBUG.get()) {
                        ex.printStackTrace();
                    }
                    return Util.exceptionMessage(sender, Keys.COMMAND_ERROR.get());
                })
                .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SYNTAX, (sender, ex) -> {
                    if (Keys.SETTINGS_DEBUG.get()) {
                        ex.printStackTrace();
                    }
                    return Util.exceptionMessage(sender, Keys.INVALID_ARGUMENTS.get(), Util.createPlaceholder("correct_syntax", "/" + ((InvalidSyntaxException) ex).getCorrectSyntax()));
                })
                .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SENDER, (sender, ex) -> {
                    if (Keys.SETTINGS_DEBUG.get()) {
                        ex.printStackTrace();
                    }
                    return Util.exceptionMessage(sender, Keys.INVALID_ARGUMENTS.get(), Util.createPlaceholder("correct_sender", ((InvalidCommandSenderException) ex).getRequiredSender().getSimpleName()));
                })
                .apply(commandManager, AudienceProvider.nativeAudience());
    }

    public void runRedemption(Player player, String... args) {
        Bukkit.getScheduler().runTask(this, () -> player.chat("/redeem " + args[0] + " " + args[1]));
    }
}
