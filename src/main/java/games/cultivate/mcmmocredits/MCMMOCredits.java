package games.cultivate.mcmmocredits;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import com.gmail.nossr50.mcMMO;
import games.cultivate.mcmmocredits.commands.CheckCredits;
import games.cultivate.mcmmocredits.commands.ModifyCredits;
import games.cultivate.mcmmocredits.commands.Redeem;
import games.cultivate.mcmmocredits.commands.Reload;
import games.cultivate.mcmmocredits.util.ConfigHandler;
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
 * TODO: For the whole plugin, fix Static Abuse?
 * <p>This is the main class of the plugin. This is minimally used, except to handle processes which need to occur
 * during plugin enable and disable.</p>
 *
 * @see MCMMOCredits#onEnable()
 * @see MCMMOCredits#onDisable()
 * @see MCMMOCredits#dependCheck()
 * @see MCMMOCredits#loadCommands()
 */
public final class MCMMOCredits extends JavaPlugin {
    /**
     * <p>An Instance of the plugin.</p>
     * @see MCMMOCredits#getInstance()
     */
    private static JavaPlugin instance;
    /**
     * <p>String which represents the Database URL</p>
     * @see MCMMOCredits#getDBURL()
     */
    private static String url;
    /**
     * An instance of MiniMessage.
     * @see MCMMOCredits#getMM()
     */
    private static MiniMessage mm;

    /**
     * <p>This method is here to provide an instance of the plugin.</p>
     *
     * @return An instance of the {@link JavaPlugin}
     */
    public static JavaPlugin getInstance() {
        return instance;
    }

    /**
     * <p>This method is here to provide the {@link Database} class a copy of the DB URL.</p>
     *
     * @return A string which represents the Database Connection URL.
     */
    public static String getDBURL() {
        return url;
    }

    /**
     * <p>This method is here to provide an instance of MiniMessage to send out.</p>
     * {@link net.kyori.adventure.text.Component} objects when necessary.
     *
     * @return An instance of {@link MiniMessage}.
     * @see <a href="https://docs.adventure.kyori.net/minimessage" target="_top">This plugin uses Mini Message!</a>
     */
    public static MiniMessage getMM() {
        return mm;
    }

    /**
     * TODO: Database - Is there a better way to to do the URL?
     * <p>This method is called when the plugin is being enabled. Here, we handle the following tasks:</p>
     * 1. Instances of the plugin and MiniMessage.
     * <br>
     * 2. mcMMO Dependency Check.
     * <br>
     * 3. Load/Create Configuration files (settings.conf/messages.conf).
     * <br>
     * 4. SQLite Database initialization.
     * <br>
     * 5. Command Loading and Registering.
     * <br>
     * 6. Registering Event Listeners.
     */
    @Override
    public void onEnable() {
        instance = this;
        mm = MiniMessage.miniMessage();

        this.dependCheck();

        ConfigHandler.loadFile("settings");
        ConfigHandler.loadFile("messages");

        url = "jdbc:sqlite:" + getDataFolder().getAbsolutePath() + "\\database.db";
        Database.initDB();

        this.loadCommands();
        Bukkit.getPluginManager().registerEvents(new Listeners(), this);

        // Small check to make sure that PlaceholderAPI is installed
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CreditsExpansion(this).register();
        }
    }

    /**
     * <p>This method is called when the plugin is being disabled. Here, we handle the following tasks:</p>
     * 1. Shutdown Database connection.
     */
    @Override
    public void onDisable() {
        Database.shutdownDB(Database.getConnection(url));
    }

    /**
     * <p>This method is here to check if {@link mcMMO} is enabled.
     * If it is not, then we also disable our Plugin.</p>
     */
    public void dependCheck() {
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("mcMMO")) {
            this.getLogger().log(Level.SEVERE, "MCMMO is not found, disabling plugin...");
            this.setEnabled(false);
        }
    }

    /**
     * <p>This method is here to handle the loading and registering of Commands. Here, we handle the following tasks:</p>
     * 1. Instantiating a {@link PaperCommandManager}.
     * <br>
     * 2. Registering capabilities if they can be used on the particular server.
     * <br>
     * 3. Creating the {@link AnnotationParser} and registering our commands.
     *
     * @see <a href="https://github.com/Incendo/cloud" target="_top">Incendo's Cloud Command Framework!</a>
     */
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
        annotationParser.parse(new CheckCredits());
        annotationParser.parse(new Reload());
        annotationParser.parse(new Redeem());
    }
}
