package games.cultivate.mcmmocredits;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.exceptions.InvalidCommandSenderException;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.exceptions.NoPermissionException;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import games.cultivate.mcmmocredits.commands.Credits;
import games.cultivate.mcmmocredits.commands.ModifyCredits;
import games.cultivate.mcmmocredits.commands.Redeem;
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.util.CreditsExpansion;
import games.cultivate.mcmmocredits.util.Listeners;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * This class is responsible for startup/shutdown logic, and command loading.
 */
public class MCMMOCredits extends JavaPlugin {
    private static boolean isPaper = false;
    private static MCMMOCredits instance;

    /**
     * This will tell us if we are in a Paper-based environment.
     * <p>
     * Moved from the Util class so that we are only checking once.
     */
    public static boolean isPaper() {
        return isPaper;
    }

    /**
     * Generates widely accessible instance of this plugin.
     * This is bad practice, but I don't really care. Project is not large enough for Guice.
     * @return MCMMOCredits plugin
     */
    public static MCMMOCredits getInstance() {
        return instance;
    }

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
        instance = this;
        this.dependCheck();
        new ConfigHandler().enable();
        Database.initDB();
        this.loadCommands();
        Bukkit.getPluginManager().registerEvents(new Listeners(), this);
    }

    /**
     * This handles all shutdown logic.
     * <p>
     * This includes shutting down the Database connection, and saving our Configs.
     */
    @Override
    public void onDisable() {
        ConfigHandler.instance().saveConfig(ConfigHandler.instance().root());
        Database.shutdownDB();
    }

    private void dependCheck() {
        try {
            Class.forName("com.destroystokyo.paper.MaterialSetTag");
            isPaper = true;
        } catch (Exception ignored) {
        }

        if (Bukkit.getPluginManager().getPlugin("mcMMO") != null) {
            this.getLogger().log(Level.INFO, "MCMMO has been found! Continuing to load...");
        } else {
            this.getLogger().log(Level.SEVERE, "MCMMO is not found, disabling plugin...");
            this.setEnabled(false);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CreditsExpansion().register();
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
        commandManager.getParserRegistry().registerSuggestionProvider("customPlayer", (context, input) ->
                Keys.PLAYER_TAB_COMPLETION.getBoolean() ? Bukkit.getOnlinePlayers().stream().map(Player::getName).toList() : List.of());

        AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(commandManager, CommandSender.class, parameters -> SimpleCommandMeta.empty());

        //TODO caption registry
        new MinecraftExceptionHandler<CommandSender>()
                .withDefaultHandlers()
                .withHandler(MinecraftExceptionHandler.ExceptionType.NO_PERMISSION, (sender, ex) -> ConfigHandler.exceptionMessage(sender, Keys.NO_PERMS, Util.createPlaceholder("required_permission", ((NoPermissionException) ex).getMissingPermission())))
                .withHandler(MinecraftExceptionHandler.ExceptionType.ARGUMENT_PARSING, (sender, ex) -> ConfigHandler.exceptionMessage(sender, Keys.INVALID_ARGUMENTS))
                .withHandler(MinecraftExceptionHandler.ExceptionType.COMMAND_EXECUTION, (sender, ex) -> ConfigHandler.exceptionMessage(sender, Keys.COMMAND_ERROR))
                .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SYNTAX, (sender, ex) -> ConfigHandler.exceptionMessage(sender, Keys.INVALID_ARGUMENTS, Util.createPlaceholder("correct_syntax", ((InvalidSyntaxException) ex).getCorrectSyntax())))
                .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SENDER, (sender, ex) -> ConfigHandler.exceptionMessage(sender, Keys.INVALID_ARGUMENTS, Util.createPlaceholder("correct_sender", ((InvalidCommandSenderException) ex).getRequiredSender().getSimpleName())))
                .apply(commandManager, sender -> sender);

        annotationParser.parse(new ModifyCredits());
        annotationParser.parse(new Credits());
        annotationParser.parse(new Redeem());

    }
}
