package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.util.Util;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.placeholder.Placeholder;
import net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.HeaderMode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * This class is responsible for all configuration management, and message parsing/sending.
 */
public final class ConfigHandler {
    private static ConfigHandler instance;
    private CreditsConfig config;
    private CommentedConfigurationNode root;
    private HoconConfigurationLoader loader;
    private MCMMOCredits plugin;

    public ConfigHandler(MCMMOCredits plugin) {
        this.plugin = plugin;
        loadConfig();
        setInstance(this);
    }

    public static boolean changeConfigInGame(String[] path, Object change) {
        boolean canProceed = false;
        for (Keys key : Keys.modifiableKeys) {
            if (Arrays.equals(key.path(), path)) {
                canProceed = true;
                break;
            }
        }
        if (!canProceed)  return false;
        ConfigHandler handler = ConfigHandler.instance();
        try {
            switch (path[0]) {
                case "messages" -> handler.root().node(Arrays.asList(path)).set(String.class, change);
                case "settings" -> handler.root().node(Arrays.asList(path)).raw(change);
                default -> {
                    return false;
                }
            }
            handler.loader().save(handler.root());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Path createFilePath() {
        File file = new File(this.plugin.getDataFolder().getAbsolutePath() + "\\" + "config.conf");
        try {
            if (file.getParentFile().mkdirs() && file.createNewFile()) {
                Bukkit.getLogger().log(Level.INFO, "[MCMMOCredits] Created configuration file!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Paths.get(file.getPath());
    }

    /**
     * This is responsible for actually sending the message to a user. All messages sent out are prepended with a prefix.
     */
    public static void sendMessage(Audience audience, Keys key, @Nullable PlaceholderResolver resolver) {
        String send = Keys.PREFIX.getString() + key.getString();
        if (resolver == null) {
            audience.sendMessage(MiniMessage.miniMessage().deserialize(send));
            return;
        }
        if (audience instanceof Player player) {
            audience.sendMessage(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, send), resolver));
        } else {
            audience.sendMessage(MiniMessage.miniMessage().deserialize(send, resolver));
        }
    }

    public static Component exceptionMessage(CommandSender sender, Keys key, Placeholder<?>... placeholderList) {
        PlaceholderResolver pr = placeholderList.length != 0 && sender instanceof Player player ? Util.basicBuilder(player).placeholders(placeholderList).build() : PlaceholderResolver.placeholders(Util.createPlaceholder("sender", sender.getName()));
        return MiniMessage.miniMessage().deserialize(Keys.PREFIX.getString() + key.getString(), pr);
    }

    public HoconConfigurationLoader createHoconLoader() {
        HoconConfigurationLoader configLoader = HoconConfigurationLoader.builder().defaultOptions(opts -> opts.serializers(build -> build.register(ItemStack.class, ItemStackSerializer.INSTANCE))).path(this.createFilePath()).emitComments(true).prettyPrinting(true).headerMode(HeaderMode.PRESERVE).build();
        this.setLoader(configLoader);
        return this.loader();
    }

    public void loadConfig() {
        try {
            CommentedConfigurationNode root = loader().load();
            CreditsConfig config = root.get(CreditsConfig.class);
            loader().save(root);
            this.setConfig(config);
            this.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveConfig(CommentedConfigurationNode root) {
        try {
            loader().save(root);
            this.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ConfigHandler instance() {
        return instance;
    }

    public CreditsConfig config() {
        return config;
    }

    public CommentedConfigurationNode root() {
        return root;
    }

    public HoconConfigurationLoader loader() {
        return loader == null ? this.createHoconLoader() : loader;
    }

    public void setConfig(CreditsConfig config) {
        this.config = config;
    }

    public void setRoot(CommentedConfigurationNode root) {
        this.root = root;
    }

    public void setLoader(HoconConfigurationLoader loader) {
        this.loader = loader;
    }

    public void setInstance(ConfigHandler instance) {
        ConfigHandler.instance = instance;
    }
}
