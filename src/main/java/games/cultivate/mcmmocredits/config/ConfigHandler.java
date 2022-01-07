package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.database.ConnectionSerializer;
import games.cultivate.mcmmocredits.database.Connection;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Level;

/**
 * This class is responsible for all configuration management, and message parsing/sending.
 */
public final class ConfigHandler {

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
            Settings config;
            try {
                node = loader.load();
                config = node.get(Settings.class);
                Objects.requireNonNull(node).set(Settings.class, config);
                loader.save(node);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        if (fileType.equalsIgnoreCase("messages")) {
            Messages config;
            try {
                node = loader.load();
                config = node.get(Messages.class);
                Objects.requireNonNull(node).set(Messages.class, config);
                loader.save(node);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * POC for Configurate Type Serializer
     */
    public static void alternativeFileLoad(String fileType) {
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .defaultOptions(opts -> opts.serializers(build -> build.register(Connection.class, ConnectionSerializer.connection)))
                .prettyPrinting(true)
                .path(Paths.get(createFile("settings").getPath()))
                .build();

        CommentedConfigurationNode node;
        Settings config;
            try {
                node = loader.load();
                config = node.get(Settings.class);
                Objects.requireNonNull(node).set(Settings.class, config);
                loader.save(node);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }


    /**
     * This is responsible for actually sending the message to a user. All messages sent out are prepended with a prefix.
     */
    public static void sendMessage(Audience audience, String text) {
        audience.sendMessage(MCMMOCredits.getMM().parse(MCMMOCredits.messages().getPrefix() + text));
    }
}
