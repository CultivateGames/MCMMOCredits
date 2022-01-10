package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.MCMMOCredits;
import net.kyori.adventure.audience.Audience;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.HeaderMode;

import java.nio.file.Paths;

/**
 * This class is responsible for all configuration management, and message parsing/sending.
 */
public final class ConfigHandler {

    public static CommentedConfigurationNode root;
    public static HoconConfigurationLoader loader;

    //Creating a loader based on the file. We can use this to load multiple files if need be.
    //We cannot cache here because we want to be able to create loaders for multiple files.
    public static HoconConfigurationLoader createHoconLoader(Config config) {
        return HoconConfigurationLoader.builder().path(Paths.get(config.createFile().getPath())).emitComments(true).headerMode(HeaderMode.PRESERVE).build();
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
        for (Config config : Config.getAllConfigs()) {
            loadConfig(config);
        }
    }

    public static void unloadAllConfigs() {
        for (Config config : Config.getAllConfigs()) {
            saveConfig(config);
        }
    }

    /**
     * This is responsible for actually sending the message to a user. All messages sent out are prepended with a prefix.
     */
    public static void sendMessage(Audience audience, String text) {
        audience.sendMessage(MCMMOCredits.getMM().parse(Keys.PREFIX.getString() + text));
    }
}
