package games.cultivate.mcmmocredits.config;

import java.util.Arrays;
import java.util.EnumSet;

public enum Keys {
    DATABASE_ADD_NOTIFICATION("settings", "database", "add-notification"),
    DATABASE_ADAPTER("settings", "database", "adapter"),
    DATABASE_HOST("settings", "database", "mysql-credentials", "host"),
    DATABASE_PORT("settings", "database","mysql-credentials", "port"),
    DATABASE_NAME("settings", "database","mysql-credentials", "name"),
    DATABASE_USERNAME("settings", "database", "mysql-credentials", "username"),
    DATABASE_PASSWORD("settings", "database", "mysql-credentials", "password"),

    USERCACHE_LOOKUP("settings", "general", "usercache-lookup"),
    PLAYER_TAB_COMPLETION("settings", "general", "player-tab-completion"),
    SEND_LOGIN_MESSAGE("settings", "general", "send-login-message"),

    PREFIX("messages", "general", "prefix"),
    LOGIN_MESSAGE("messages", "general", "login-message"),
    DATABASE_CONSOLE_MESSAGE("messages", "general", "database-console-message"),

    INVALID_ARGUMENTS("messages", "exceptions", "invalid-arguments"),
    MUST_BE_NUMBER("messages", "exceptions", "must-be-number"),
    NO_PERMS("messages", "exceptions", "no-perms"),
    PLAYER_DOES_NOT_EXIST("messages", "exceptions", "player-does-not-exist"),
    COMMAND_ERROR("messages", "exceptions", "command-error"),

    CREDITS_BALANCE_SELF("messages", "commands", "credits", "balance-self"),
    CREDITS_BALANCE_OTHER("messages", "commands", "credits", "balance-other"),
    CREDITS_RELOAD_SUCCESSFUL("messages", "commands", "credits", "reload-successful"),
    CREDITS_SETTING_CHANGE_SUCCESSFUL("messages", "commands", "credits", "setting-change-successful"),
    CREDITS_SETTING_CHANGE_FAILURE("messages", "commands", "credits", "setting-change-failure"),


    REDEEM_SKILL_CAP("messages", "commands", "redeem", "skill-cap"),
    REDEEM_NOT_ENOUGH_CREDITS("messages", "commands", "redeem", "not-enough-credits"),
    REDEEM_SUCCESSFUL_SELF("messages", "commands", "redeem", "successful-self"),
    REDEEM_SUCCESSFUL_SENDER("messages", "commands", "redeem", "successful-sender"),
    REDEEM_SUCCESSFUL_RECEIVER("messages", "commands", "redeem", "successful-receiver"),

    MODIFY_CREDITS_ADD_SENDER("messages", "commands", "modify-credits", "add-sender"),
    MODIFY_CREDITS_SET_SENDER("messages", "commands", "modify-credits", "set-sender"),
    MODIFY_CREDITS_TAKE_SENDER("messages", "commands", "modify-credits", "take-sender"),
    MODIFY_CREDITS_ADD_RECEIVER("messages", "commands", "modify-credits", "add-receiver"),
    MODIFY_CREDITS_SET_RECEIVER("messages", "commands", "modify-credits", "set-receiver"),
    MODIFY_CREDITS_TAKE_RECEIVER("messages", "commands", "modify-credits", "take-receiver");

    private final String[] path;

    public static final EnumSet<Keys> all = EnumSet.allOf(Keys.class);

    Keys(String... path) {
        this.path = path;
    }

    public String[] path() {
        return path;
    }

    @SuppressWarnings("unused")
    public int getInt() {
        return ConfigHandler.instance().root().node(Arrays.stream(this.path()).toList()).getInt();
    }

    public boolean getBoolean() {
        return ConfigHandler.instance().root().node(Arrays.stream(this.path()).toList()).getBoolean();
    }

    public String getString() {
        return ConfigHandler.instance().root().node(Arrays.stream(this.path()).toList()).getString();
    }

}
