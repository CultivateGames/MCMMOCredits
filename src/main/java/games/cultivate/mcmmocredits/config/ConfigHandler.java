package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.util.Database;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for all configuration management, and message parsing/sending.
 */
public final class ConfigHandler {
    public static CommentedConfigurationNode messages;
    public static CommentedConfigurationNode settings;

    public static CommentedConfigurationNode messageNode() {
        return messages;
    }

    public static CommentedConfigurationNode settingsNode() {
        return settings;
    }

    /**
     * This is responsible for creating configuration files if we believe they do not exist.
     */
    public static File createFile(String fileName) {
        File file = new File(MCMMOCredits.getInstance().getDataFolder().getAbsolutePath() + "\\" + fileName + ".conf");
        try {
            if (file.getParentFile().mkdirs() && file.createNewFile()) {
                Bukkit.getLogger().log(Level.INFO, "[MCMMOCredits] Created" + fileName + ".conf file!");
            } else {
                Bukkit.getLogger().log(Level.INFO, "[MCMMOCredits] " + fileName + ".conf already exists... skipping.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * This is responsible for attempting to load configuration files, and then creating an instance of the result.
     * TODO: de-duplicate
     */
    public static void loadFile(String fileType) {
        File file = createFile(fileType);
        Path path = Paths.get(file.getPath());
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().prettyPrinting(true).path(path).build();
        CommentedConfigurationNode node;
        if (fileType.equalsIgnoreCase("settings")) {
            SettingsConfig config;
            try {
                node = loader.load();
                config = node.get(SettingsConfig.class);
                Objects.requireNonNull(node).set(SettingsConfig.class, config);
                loader.save(node);
                settings = node;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        if (fileType.equalsIgnoreCase("messages")) {
            MessageConfig config;
            try {
                node = loader.load();
                config = node.get(MessageConfig.class);
                Objects.requireNonNull(node).set(MessageConfig.class, config);
                loader.save(node);
                messages = node;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * This is responsible for actually sending the message to a user. All messages sent out are prepended with a prefix.
     */
    public static void sendMessage(Audience audience, String text) {
        audience.sendMessage(MCMMOCredits.getMM().parse(Messages.PREFIX.message() + text));
    }
}
