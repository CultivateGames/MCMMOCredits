package games.cultivate.mcmmocredits.keys;

import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.ConfigUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

public enum BooleanKey implements Key<Boolean> {
    //SETTINGS
    DATABASE_SSL("database.mysql-credentials.ssl"),
    ADD_NOTIFICATION("general.add-notification"),
    PLAYER_TAB_COMPLETION("general.player-tab-completion"),
    SEND_LOGIN_MESSAGE("general.send-login-message"),
    SETTINGS_DEBUG("general.debug"),

    //MESSAGES

    //MENUS
    MENU_NAVIGATION("main.navigation"),
    MENU_FILL("main.fill");

    private final String path;
    private final CommentedConfigurationNode root;

    BooleanKey(String path) {
        this.path = path;
        this.root = this.root();
    }

    @Override
    public @NotNull String path() {
        return path;
    }

    @Override
    public @NotNull Boolean get() {
        return this.root.getBoolean(false);
    }

    @Override
    public @NotNull Config<?> config() {
        return null;
    }

    public CommentedConfigurationNode root() {
        if (this.equals(MENU_FILL) || this.equals(MENU_NAVIGATION)) {
            return ConfigUtil.MENU.root();
        }
        return ConfigUtil.SETTINGS.root();
    }
}
