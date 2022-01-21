package games.cultivate.mcmmocredits.config;

import org.spongepowered.configurate.CommentedConfigurationNode;

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

    public abstract void load(CommentedConfigurationNode root);

    public abstract void save(CommentedConfigurationNode root);

    public abstract CommentedConfigurationNode root();
}
