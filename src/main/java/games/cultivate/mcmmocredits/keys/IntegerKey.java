package games.cultivate.mcmmocredits.keys;

import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.ConfigUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

public enum IntegerKey implements Key<Integer> {
    //SETTINGS
    DATABASE_PORT("database.mysql-credentials.port"),
    //MENU
    EDIT_SETTINGS_SIZE("editing.settings.inventory-size"),
    EDIT_MESSAGES_SIZE("editing.messages.inventory-size"),
    MENU_SIZE("main.inventory-size"),
    REDEEM_SIZE("redeem.inventory-size");

    private final String path;
    private final CommentedConfigurationNode root;
    private Config<?> config;

    IntegerKey(String path) {
        this.path = path;
        if (this.path.contains("database")) {
            this.config = ConfigUtil.SETTINGS;
            this.root = this.config.root();
        } else {
            this.config = ConfigUtil.MENU;
            this.root = this.config.root();
        }
    }

    @Override
    public @NotNull String path() {
        return path;
    }

    @Override
    public @NotNull Integer get() {
        return this.root.getInt(0);
    }

    @Override
    public @NotNull Config<?> config() {
        return this.config;
    }
}
