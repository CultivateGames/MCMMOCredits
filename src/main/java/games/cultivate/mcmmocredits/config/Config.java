package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.MCMMOCredits;
import org.bukkit.Bukkit;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.logging.Level;

public enum Config {
    SETTINGS {
        private CommentedConfigurationNode instance;
        private SettingConfig settings;

        public void load(CommentedConfigurationNode root) {
            try {
                settings = root.get(SettingConfig.class);
                instance = root;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void save(CommentedConfigurationNode root) {
            try {
                root.set(SettingConfig.class, settings);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public CommentedConfigurationNode root() {
            return instance;
        }

    }, MESSAGES {
        private CommentedConfigurationNode instance;
        private MessageConfig messages;

        public void load(CommentedConfigurationNode root) {
            try {
                messages = root.get(MessageConfig.class);
                instance = root;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void save(CommentedConfigurationNode root) {
            try {
                root.set(MessageConfig.class, messages);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public CommentedConfigurationNode root()  {
            return instance;
        }

    };

    private static final EnumSet<Config> configs = EnumSet.allOf(Config.class);

    public static EnumSet<Config> getAllConfigs() {
        return configs;
    }

    public abstract void load(CommentedConfigurationNode root);

    public abstract void save(CommentedConfigurationNode root);

    public abstract CommentedConfigurationNode root();

    public File createFile() {
        String fileName = this.toString().toLowerCase() + ".conf";
        File file = new File(MCMMOCredits.getInstance().getDataFolder().getAbsolutePath() + "\\" + fileName);
        try {
            if (file.getParentFile().mkdirs() && file.createNewFile()) {
                Bukkit.getLogger().log(Level.INFO, "[MCMMOCredits] Created " + fileName + " file!");
            } else {
                Bukkit.getLogger().log(Level.INFO, "[MCMMOCredits] " + fileName + " already exists... skipping.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
