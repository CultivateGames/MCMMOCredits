package games.cultivate.mcmmocredits;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import games.cultivate.mcmmocredits.commands.Credits;
import games.cultivate.mcmmocredits.commands.ModifyCredits;
import games.cultivate.mcmmocredits.commands.Redeem;
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.util.CreditsExpansion;
import games.cultivate.mcmmocredits.util.Database;
import games.cultivate.mcmmocredits.util.Listeners;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;
import java.util.logging.Level;

/**
 * This class is responsible for startup/shutdown logic, and command loading.
 */
public final class MCMMOCredits extends JavaPlugin {
    private static JavaPlugin instance;
    private static MiniMessage mm;
    private static boolean isPaper = false;

    /**
     * This provides a usable instance of the plugin.
     */
    public static JavaPlugin getInstance() {
        return instance;
    }

    /**
     * This provides an instance of Kyori + MiniDigger's MiniMessage for message parsing.
     */
    public static MiniMessage getMM() {
        return mm;
    }

    /**
     * This will tell us if we are in a Paper-based environment.
     * <p>
     * Moved from the Util class so that we are only checking once.
     */
    public static boolean isPaper() {
        return isPaper;
    }

    /**
     * This handles all startup logic.
     * <p>
     * This includes creating any necessary instances and dependency checks.
     * <p>
     * This is also responsible for loading configurations,
     * audiences, and registering our event listeners/commands.
     * TODO: Possibly rethink order of events.
     */
    @Override
    public void onEnable() {
        instance = this;
        mm = MiniMessage.miniMessage();

        ConfigHandler.loadFile("settings");
        ConfigHandler.loadFile("messages");

        Database.initDB();

        this.loadCommands();
        this.dependCheck();

        Bukkit.getPluginManager().registerEvents(new Listeners(), this);
    }

    /**
     * This handles all shutdown logic.
     * <p>
     * This includes shutting down the Database connection, and disabling our Adventure audience.
     */
    @Override
    public void onDisable() {
        Database.shutdownDB();
    }

    private void dependCheck() {
        try {
            Class.forName("com.destroystokyo.paper.MaterialSetTag");
            isPaper = true;
        } catch (Exception ignored) {
        }

        if (Bukkit.getPluginManager().getPlugin("mcMMO") != null) {
            this.getLogger().log(Level.SEVERE, "MCMMO is not found, disabling plugin...");
            this.setEnabled(false);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CreditsExpansion(this).register();
        }
    }

    private void loadCommands() {
        PaperCommandManager<CommandSender> commandManager;
        try {
            commandManager = new PaperCommandManager<>(this, AsynchronousCommandExecutionCoordinator.<CommandSender>newBuilder().build(), Function.identity(), Function.identity());
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
        AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(commandManager, CommandSender.class, parameters -> SimpleCommandMeta.empty());
        annotationParser.parse(new ModifyCredits());
        annotationParser.parse(new Credits());
        annotationParser.parse(new Redeem());
    }
}
