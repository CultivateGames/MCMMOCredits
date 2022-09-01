package games.cultivate.mcmmocredits;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.exceptions.CommandExecutionException;
import cloud.commandframework.exceptions.InvalidCommandSenderException;
import cloud.commandframework.exceptions.InvalidSyntaxException;
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
import games.cultivate.mcmmocredits.data.Database;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Main class of the application. Handles startup and shutdown logic.
 */
public final class MCMMOCredits extends JavaPlugin {
    private Injector injector;
    private GeneralConfig config;

    /**
     * Called when the application starts up. Handles injection, checks for required dependencies (Paper, MCMMO etc.), and loads configurations/commands.
     * <p>
     * If debug is enabled in {@link GeneralConfig}, we also track the startup time of the application and print it to console.
     */
    @Override
    public void onEnable() {
        long start = System.nanoTime();
        this.injector = Guice.createInjector(new PluginModule(this));
        this.config = this.injector.getInstance(GeneralConfig.class);
        this.loadConfiguration();
        this.checkForDependencies();
        this.loadCommands();
        Bukkit.getPluginManager().registerEvents(new PaperInterfaceListeners(this, 10L), this);
        Bukkit.getPluginManager().registerEvents(this.injector.getInstance(Listeners.class), this);
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
     * Handles all logic required to check for required software (Paper, MCMMO). Registers {@link PlaceholderExpansion} if PlaceholderAPI is present.
     */
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

        if (manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            manager.registerBrigadier();
            manager.brigadierManager().setNativeNumberSuggestions(false);
        }

        if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
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
        manager.parserRegistry().registerSuggestionProvider("ops", (c, i) -> List.of("add", "set", "take"));

        AnnotationParser<CommandSender> parser = new AnnotationParser<>(manager, CommandSender.class, p -> SimpleCommandMeta.empty());
        parser.parse(this.injector.getInstance(Credits.class));

        MinecraftExceptionHandler<CommandSender> handler = new MinecraftExceptionHandler<>();
        EnumSet.allOf(ExceptionType.class).forEach(x -> handler.withHandler(x, this.buildError(x)));
        handler.apply(manager, AudienceProvider.nativeAudience());
    }

    /**
     * Called when the application shuts down. Saves all valuable data (configurations, database).
     */
    @Override
    public void onDisable() {
        this.injector.getInstance(GeneralConfig.class).save();
        this.injector.getInstance(MenuConfig.class).save();
        this.injector.getInstance(Database.class).disable();
    }

    private BiFunction<CommandSender, Exception, Component> buildError(final ExceptionType exType) {
        String path = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, exType.name());
        return (sender, ex) -> {
            if (this.config.bool("debug") || ex instanceof CommandExecutionException) {
                ex.printStackTrace();
            }
            Resolver.Builder rb = this.injector.getInstance(ResolverFactory.class).builder().users(sender);
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
            return Text.fromString(sender, this.config.string(path), rb.tags(tags).build()).toComponent();
        };
    }
}
