package games.cultivate.mcmmocredits;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import games.cultivate.mcmmocredits.commands.Credits;
import games.cultivate.mcmmocredits.commands.ModifyCredits;
import games.cultivate.mcmmocredits.commands.Redeem;
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.util.CreditsExpansion;
import games.cultivate.mcmmocredits.injection.CreditsModule;
import games.cultivate.mcmmocredits.util.Listeners;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * This class is responsible for startup/shutdown logic, and command loading.
 */
@Singleton
public final class MCMMOCredits extends JavaPlugin {
    private static boolean isPaper = false;

    //TODO wtf is this
    public static String path;

    //Guice
    private final Database database = new Database(this);
    @Inject private Listeners listener;

    /**
     * This will tell us if we are in a Paper-based environment.
     * <p>
     * Moved from the Util class so that we are only checking once.
     */
    public static boolean isPaper() {
        return isPaper;
    }

    public static String path(){
        return path;
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
        //Dependency Injection (Guice)
        CreditsModule module = new CreditsModule(this, database);
        Injector injector = module.createInjector();
        injector.injectMembers(this);

        this.dependCheck();
        path = getDataFolder().getAbsolutePath();
        ConfigHandler.loadAllConfigs();

        database.initDB();
        this.loadCommands();
        Bukkit.getPluginManager().registerEvents(this.listener, this);
    }

    /**
     * This handles all shutdown logic.
     * <p>
     * This includes shutting down the Database connection, and disabling our Adventure audience.
     */
    @Override
    public void onDisable() {
        Database.shutdownDB();
        for (Config config : Config.values()) {
            ConfigHandler.saveConfig(config);
        }
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
        annotationParser.parse(new ModifyCredits(database));
        annotationParser.parse(new Credits(database));
        annotationParser.parse(new Redeem(database));
    }

    /**
     * This is responsible for creating a Suggestions provider for Commands.
     * <p>
     * TODO Figure out if I can just leave this here.
     */
    @Suggestions("player")
    public List<String> playerSuggestions(CommandContext<CommandSender> context, String input) {
        List<String> list = new ArrayList<>();
        if (Keys.PLAYER_TAB_COMPLETION.getBoolean()) {
            Bukkit.getOnlinePlayers().forEach(p -> list.add(p.getName()));
            return list;
        }
        return list;
    }

    /**
     * This is responsible for creating an Argument Parser for Commands.
     * <p>
     * TODO Figure out if I can just leave this here.
     */
    @Parser(suggestions = "player")
    public String playerParser(CommandContext<CommandSender> sender, Queue<String> inputQueue) {
        final String input = inputQueue.peek();
        inputQueue.poll();
        return input;
    }
}
