package games.cultivate.mcmmocredits.config;

public enum Keys {
    DATABASE_ADD_MESSAGE(Config.SETTINGS, "database", "database-add-message"),
    DATABASE_ADAPTER(Config.SETTINGS, "database", "database-adapter"),
    DATABASE_HOST(Config.SETTINGS, "database", "mysql-credentials", "host"),
    DATABASE_PORT(Config.SETTINGS, "database","mysql-credentials", "port"),
    DATABASE_NAME(Config.SETTINGS, "database","mysql-credentials", "name"),
    DATABASE_USERNAME(Config.SETTINGS,  "database", "mysql-credentials", "username"),
    DATABASE_PASSWORD(Config.SETTINGS, "database", "mysql-credentials", "password"),

    USERCACHE_LOOKUP(Config.SETTINGS,  "general", "usercache-lookup"),
    PLAYER_TAB_COMPLETION(Config.SETTINGS, "general", "player-tab-completion"),
    SEND_LOGIN_MESSAGE(Config.SETTINGS, "general", "send-login-message"),

    PREFIX(Config.MESSAGES, "general", "prefix"),
    LOGIN_MESSAGE(Config.MESSAGES,"general", "login-message"),
    DATABASE_CONSOLE_MESSAGE(Config.MESSAGES, "general", "database-console-message"),

    INVALID_ARGUMENTS(Config.MESSAGES, "exceptions", "invalid-arguments"),
    MUST_BE_NUMBER(Config.MESSAGES, "exceptions", "must-be-number"),
    NO_PERMS(Config.MESSAGES, "exceptions", "no-perms"),
    PLAYER_DOES_NOT_EXIST(Config.MESSAGES, "exceptions", "player-does-not-exist"),

    CREDITS_BALANCE_SELF(Config.MESSAGES, "commands", "credits", "balance-self"),
    CREDITS_BALANCE_OTHER(Config.MESSAGES, "commands", "credits", "balance-other"),
    CREDITS_RELOAD_SUCCESSFUL(Config.MESSAGES, "commands", "credits", "reload-successful"),
    REDEEM_SKILL_CAP(Config.MESSAGES, "commands", "redeem", "skill-cap"),
    REDEEM_NOT_ENOUGH_CREDITS(Config.MESSAGES, "commands", "redeem", "not-enough-credits"),
    REDEEM_SUCCESSFUL_SELF(Config.MESSAGES, "commands", "redeem", "successful-self"),
    REDEEM_SUCCESSFUL_SENDER(Config.MESSAGES, "commands", "redeem", "successful-sender"),
    REDEEM_SUCCESSFUL_RECEIVER(Config.MESSAGES, "commands", "redeem", "successful-receiver"),
    MODIFY_CREDITS_ADD_SENDER(Config.MESSAGES, "commands", "modify-credits", "add-sender"),
    MODIFY_CREDITS_SET_SENDER(Config.MESSAGES, "commands", "modify-credits", "set-sender"),
    MODIFY_CREDITS_TAKE_SENDER(Config.MESSAGES, "commands", "modify-credits", "take-sender"),
    MODIFY_CREDITS_ADD_RECEIVER(Config.MESSAGES, "commands", "modify-credits", "add-receiver"),
    MODIFY_CREDITS_SET_RECEIVER(Config.MESSAGES, "commands", "modify-credits", "set-receiver"),
    MODIFY_CREDITS_TAKE_RECEIVER(Config.MESSAGES, "commands", "modify-credits", "take-receiver");

    private final Object[] path;
    private final Config location;

    Keys(Config location, Object... path) {
        this.path = path;
        this.location = location;
    }

    public Object[] path() {
        return path;
    }

    public Config location() {
        return location;
    }

    public String getString() {
            return this.location().root().node(this.path()).getString();
    }

    public int getInt() {
        return this.location().root().node(this.path()).getInt();
    }

    public boolean getBoolean() {
        return this.location().root().node(this.path()).getBoolean();
    }

}
