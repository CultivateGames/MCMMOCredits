package games.cultivate.mcmmocredits.keys;

import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.ConfigUtil;
import games.cultivate.mcmmocredits.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

public enum StringKey implements Key<String> {
    //SETTINGS
    DATABASE_ADAPTER("database.adapter"),
    DATABASE_HOST("database.mysql-credentials.host"),
    DATABASE_NAME("database.mysql-credentials.name"),
    DATABASE_USERNAME("database.mysql-credentials.username"),
    DATABASE_PASSWORD("database.mysql-credentials.password"),

    //MESSAGES
    CANCEL_PROMPT("general.cancel-prompt"),
    PREFIX("general.prefix"),
    LOGIN_MESSAGE("general.login-message"),
    ADD_PLAYER_MESSAGE("general.add-player-message"),

    INVALID_ARGUMENTS("exceptions.invalid-arguments"),
    MUST_BE_NUMBER("exceptions.must-be-number"),
    NO_PERMS("exceptions.no-perms"),
    PLAYER_DOES_NOT_EXIST("exceptions.player-does-not-exist"),
    COMMAND_ERROR("exceptions.command-error"),

    CREDITS_BALANCE_SELF("commands.credits.balance-self"),
    CREDITS_BALANCE_OTHER("commands.credits.balance-other"),
    CREDITS_RELOAD_SUCCESSFUL("commands.credits.reload-successful"),
    CREDITS_SETTING_CHANGE_SUCCESSFUL("commands.credits.setting-change-successful"),
    CREDITS_SETTING_CHANGE_FAILURE("commands.credits.setting-change-failure"),
    CREDITS_MENU_EDITING_PROMPT("commands.credits.menu-editing-prompt"),
    CREDITS_MENU_REDEEM_PROMPT("commands.credits.menu-redeem-prompt"),

    REDEEM_SKILL_CAP("commands.redeem.skill-cap"),
    REDEEM_NOT_ENOUGH_CREDITS("commands.redeem.not-enough-credits"),
    REDEEM_SUCCESSFUL_SELF("commands.redeem.successful-self"),
    REDEEM_SUCCESSFUL_SENDER("commands.redeem.successful-sender"),
    REDEEM_SUCCESSFUL_RECEIVER("commands.redeem.successful-receiver"),

    MODIFY_CREDITS_ADD_SENDER("commands.modify-credits.add-sender"),
    MODIFY_CREDITS_SET_SENDER("commands.modify-credits.set-sender"),
    MODIFY_CREDITS_TAKE_SENDER("commands.modify-credits.take-sender"),
    MODIFY_CREDITS_ADD_RECEIVER("commands.modify-credits.add-receiver"),
    MODIFY_CREDITS_SET_RECEIVER("commands.modify-credits.set-receiver"),
    MODIFY_CREDITS_TAKE_RECEIVER("commands.modify-credits.take-receiver"),

    //MENUS
    MENU_TITLE("main.inventory-title"),
    EDIT_MESSAGES_TITLE("editing.messages.inventory-title"),
    EDIT_SETTINGS_TITLE("editing.settings.inventory-title"),
    REDEEM_TITLE("redeem.inventory-title");

    private final String path;
    private final CommentedConfigurationNode root;

    StringKey(String path) {
        this.path = path;
        this.root = this.root();
    }

    @Override
    public @NotNull String path() {
        return path;
    }

    @Override
    public @NotNull String get() {
        return this.root.getString("");
    }

    @Override
    public @NotNull Config<?> config() {
        return null;
    }

    /**
     * Creates builder out of the StringKey
     *
     * @return a new Text.Builder with a populated StringKey.
     */
    public Text.Builder toBuilder() {
        return new Text.Builder().key(this);
    }

    public String get(boolean withPrefix) {
        String result = this.root.getString("");
        return withPrefix ? PREFIX.get() + result : result;
    }

    public CommentedConfigurationNode root() {
        String name = this.name();
        if (name.contains("TITLE")) {
            return ConfigUtil.MENU.root();
        }
        if (name.contains("DATABASE")) {
            return ConfigUtil.SETTINGS.root();
        }
        return ConfigUtil.MESSAGES.root();
    }
}
