package games.cultivate.mcmmocredits;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.captions.CaptionRegistry;
import cloud.commandframework.exceptions.CommandExecutionException;
import cloud.commandframework.exceptions.InvalidCommandSenderException;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
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
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.interfaces.paper.PaperInterfaceListeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static cloud.commandframework.minecraft.extras.MinecraftExceptionHandler.ExceptionType.*;
import static java.util.Objects.requireNonNull;

/**
 * This class is responsible for startup/shutdown logic, and command loading.
 */
public final class MCMMOCredits extends JavaPlugin {
    public static final NamespacedKey NAMESPACED_KEY = requireNonNull(NamespacedKey.fromString("mcmmocredits"));
    private Injector injector;
    private MessagesConfig messages;
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
        long start = System.nanoTime();
        this.injector = Guice.createInjector(new PluginModule(this));
        this.checkForDependencies();
        this.loadConfiguration();
        this.loadCommands();
        Bukkit.getPluginManager().registerEvents(new PaperInterfaceListeners(this, 10L), this);
        Bukkit.getPluginManager().registerEvents(this.injector.getInstance(Listeners.class), this);
        long end = System.nanoTime();
        if (this.settings.bool("debug")) {
            this.getSLF4JLogger().info("Plugin enabled! Startup took: {} s.", (double) (end - start) / 1000000000);
        }
    }

    public void loadConfiguration() {
        this.injector.getInstance(MessagesConfig.class).load();
        this.injector.getInstance(MenuConfig.class).load();
        this.injector.getInstance(SettingsConfig.class).load();
        this.messages = this.injector.getInstance(MessagesConfig.class);
        this.settings = this.injector.getInstance(SettingsConfig.class);
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

    private void loadCommands() {
        PaperCommandManager<CommandSender> manager;
        try {
            manager = new PaperCommandManager<>(this, CommandExecutionCoordinator.simpleCoordinator(), t -> t, t -> t);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            manager.registerBrigadier();
        }

        if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            manager.registerAsynchronousCompletions();
        }

        manager.parserRegistry().registerSuggestionProvider("user", (c, i) -> {
            if (this.settings.bool("playerTabCompletion", true)) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            }
            return List.of();
        });

        AnnotationParser<CommandSender> parser = new AnnotationParser<>(manager, CommandSender.class, p -> SimpleCommandMeta.empty());
        parser.parse(this.injector.getInstance(Credits.class));
        parser.parse(this.injector.getInstance(ModifyCredits.class));
        parser.parse(this.injector.getInstance(Redeem.class));
        parser.parse(this);

        MinecraftExceptionHandler<CommandSender> handler = new MinecraftExceptionHandler<>();
        handler.withHandler(NO_PERMISSION, this.buildError("noPermission"));
        handler.withHandler(ARGUMENT_PARSING, this.buildError("invalidArguments"));
        handler.withHandler(COMMAND_EXECUTION, this.buildError("commandError"));
        handler.withHandler(INVALID_SYNTAX, this.buildError("invalidSyntax"));
        handler.withHandler(INVALID_SENDER, this.buildError("invalidSender"));
        handler.apply(manager, AudienceProvider.nativeAudience());

        //TODO caption system
        CaptionRegistry<CommandSender> registry = manager.captionRegistry();
    }

    private BiFunction<CommandSender, Exception, Component> buildError(final String path) {
        return (sender, ex) -> {
            if (this.settings.bool("debug") || ex instanceof CommandExecutionException) {
                ex.printStackTrace();
            }
            Resolver.Builder rb = Resolver.builder().sender(sender);
            Map<String, String> tags = new HashMap<>();
            switch (ex.getClass().getSimpleName()) {
                case "ArgumentParseException" -> tags.put("argument_error", ex.getCause().getMessage());
                case "InvalidSyntaxException" ->
                        tags.put("correct_syntax", "/" + ((InvalidSyntaxException) ex).getCorrectSyntax());
                case "InvalidCommandSenderException" ->
                        tags.put("correct_sender", ((InvalidCommandSenderException) ex).getRequiredSender().getSimpleName());
                default -> { //do nothing if no error
                }
            }
            return Text.fromString(sender, this.messages.string(path), rb.tags(tags).build()).toComponent();
        };
    }
}
