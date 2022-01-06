package games.cultivate.mcmmocredits.config;

//TODO: Documentation + use parse placeholders boolean
public enum Messages {
    CREDITS_CHECK_OTHER("credits-check-other", true),
    CREDITS_CHECK_SELF("credits-check-self", true),
    INVALID_ARGUMENTS("invalid-arguments", false),
    LOGIN_MESSAGE("login-message", true),
    MODIFY_CREDITS_ADD("modify-credits-add", true),
    MODIFY_CREDITS_SET("modify-credits-set", true),
    MODIFY_CREDITS_TAKE("modify-credits-take", true),
    MUST_BE_NUMBER("must-be-number", false),
    NO_PERMS("no-perms", false),
    PLAYER_DOES_NOT_EXIST("player-does-not-exist", false),
    PREFIX("prefix", true),
    RELOAD_SUCCESSFUL("reload-successful", true),
    REDEEM_NOT_ENOUGH_CREDITS("redeem-not-enough-credits", true),
    REDEEM_SKILL_CAP("redeem-skill-cap", true),
    REDEEM_SUCCESSFUL("redeem-successful", true),
    REDEEM_SUCCESSFUL_OTHER("redeem-successful-other", true);

    private final String key;
    private final boolean parsePlaceholders;

    Messages(String key, boolean parsePlaceholders) {
            this.key = key;
            this.parsePlaceholders = parsePlaceholders;
    }
    /**
     * This is used to access values from messages.conf as a String.
     */
    public String message() {
        return ConfigHandler.messageNode().node(key).getString();
    }

    public String key() {
        return key;
    }

    public boolean parsePlaceholders() {
        return parsePlaceholders;
    }
}

