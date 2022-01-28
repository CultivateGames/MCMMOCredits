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
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.database.DatabaseAdapter;
import games.cultivate.mcmmocredits.database.MySQLAdapter;
import games.cultivate.mcmmocredits.database.SQLiteAdapter;
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
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * This class is responsible for startup/shutdown logic, and command loading.
 */
public class MCMMOCredits extends JavaPlugin {
    public static NamespacedKey key;
    private static DatabaseAdapter adapter;

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
        key = Objects.requireNonNull(NamespacedKey.fromString("mcmmocredits"));
        new ConfigHandler(this);

        this.loadDatabase();
        this.loadCommands();

        PaperInterfaceListeners.install(this);
        this.dependCheck();
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
        getAdapter().disableAdapter();
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
                .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SYNTAX, (sender, ex) -> ConfigHandler.exceptionMessage(sender, Keys.INVALID_ARGUMENTS, Util.createPlaceholder("correct_syntax", "/" + ((InvalidSyntaxException) ex).getCorrectSyntax())))
                .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SENDER, (sender, ex) -> ConfigHandler.exceptionMessage(sender, Keys.INVALID_ARGUMENTS, Util.createPlaceholder("correct_sender", ((InvalidCommandSenderException) ex).getRequiredSender().getSimpleName())))
                .apply(commandManager, AudienceProvider.nativeAudience());

        annotationParser.parse(new ModifyCredits());
        annotationParser.parse(new Credits());
        annotationParser.parse(new Redeem());
    }

    private void loadDatabase() {
        switch (Keys.DATABASE_ADAPTER.getString().toLowerCase()) {
            case "sqlite" -> this.setAdapter(new SQLiteAdapter(this));
            case "mysql" -> this.setAdapter(new MySQLAdapter(this));
            default -> {
                Bukkit.getLogger().log(Level.SEVERE, "INVALID DATABASE ADAPTER SET! Disabling plugin...");
                this.setEnabled(false);
            }
        }
    }

    public static DatabaseAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(DatabaseAdapter adapter) {
        MCMMOCredits.adapter = adapter;
    }
}
