package games.cultivate.mcmmocredits.config;

public enum Keys {
    DATABASE_HOST(Config.SETTINGS, "mysql-credentials", "host"),
    DATABASE_PORT(Config.SETTINGS, "mysql-credentials", "port"),
    DATABASE_NAME(Config.SETTINGS, "mysql-credentials", "database"),
    DATABASE_USERNAME(Config.SETTINGS,  "mysql-credentials", "username"),
    DATABASE_PASSWORD(Config.SETTINGS, "mysql-credentials", "password"),
    USERCACHE_LOOKUP(Config.SETTINGS,  "use-usercache-lookup"),
    PLAYER_TAB_COMPLETION(Config.SETTINGS, "player-tab-completion"),
    SEND_LOGIN_MESSAGE(Config.SETTINGS, "send-login-message"),
    DATABASE_ADD_MESSAGE(Config.SETTINGS, "database-add-message"),
    USE_SQLITE(Config.SETTINGS, "use-sqlite"),
    PREFIX(Config.MESSAGES, "prefix"),
    INVALID_ARGUMENTS(Config.MESSAGES, "invalid-arguments"),
    MUST_BE_NUMBER(Config.MESSAGES, "must-be-number"),
    NO_PERMS(Config.MESSAGES, "no-perms"),
    CREDITS_CHECK_SELF(Config.MESSAGES, "credits-check-self"),
    CREDITS_CHECK_OTHER(Config.MESSAGES, "credits-check-other"),
    PLAYER_DOES_NOT_EXIST(Config.MESSAGES, "player-does-not-exist"),
    RELOAD_SUCCESSFUL(Config.MESSAGES, "reload-successful"),
    REDEEM_SKILL_CAP(Config.MESSAGES, "redeem-skill-cap"),
    REDEEM_NOT_ENOUGH_CREDITS(Config.MESSAGES, "redeem-not-enough-credits"),
    REDEEM_SUCCESSFUL(Config.MESSAGES, "redeem-successful"),
    REDEEM_SUCCESSFUL_OTHER(Config.MESSAGES, "redeem-successful-other"),
    LOGIN_MESSAGE(Config.MESSAGES, "login-message"),
    MODIFY_CREDITS_ADD(Config.MESSAGES, "modify-credits-add"),
    MODIFY_CREDITS_SET(Config.MESSAGES, "modify-credits-set"),
    MODIFY_CREDITS_TAKE(Config.MESSAGES, "modify-credits-take"),
    DATABASE_CONSOLE_MESSAGE(Config.MESSAGES, "database-console-message");

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
