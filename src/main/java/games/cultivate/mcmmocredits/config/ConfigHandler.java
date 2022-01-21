package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.MCMMOCredits;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.HeaderMode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;

/**
 * This class is responsible for all configuration management, and message parsing/sending.
 */
public final class ConfigHandler {

    public static CommentedConfigurationNode root;
    public static HoconConfigurationLoader loader;

    //Creating a loader based on the file. We can use this to load multiple files if need be.
    //We cannot cache here because we want to be able to create loaders for multiple files.
    public static HoconConfigurationLoader createHoconLoader(Config config) {
        return HoconConfigurationLoader.builder().path(Paths.get(createFile(config).getPath())).emitComments(true).prettyPrinting(true).headerMode(HeaderMode.PRESERVE).build();
    }

    public static void loadConfig(Config config) {
        loader = createHoconLoader(config);
        try {
            root = loader.load();
            config.load(root);
            loader.save(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig(Config config) {
        loader = createHoconLoader(config);
        try {
            config.save(root);
            loader.save(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //An attempt to load multiple Config within one method. Not sure why I am writing this.
    public static void loadAllConfigs() {
        for (Config config : Config.values()) {
            loadConfig(config);
        }
    }

    public static File createFile(Config config) {
        String fileName = config.toString().toLowerCase() + ".conf";
        File file = new File(MCMMOCredits.getInstance().getDataFolder().getAbsolutePath() + "\\" + fileName);
        try {
            if (file.getParentFile().mkdirs() && file.createNewFile()) {
                Bukkit.getLogger().log(Level.INFO, "[MCMMOCredits] Created " + fileName + " file!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * This is responsible for actually sending the message to a user. All messages sent out are prepended with a prefix.
     */
    public static void sendMessage(Audience audience, String text, @Nullable PlaceholderResolver resolver) {
        String send = Keys.PREFIX.getString() + text;
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
}
