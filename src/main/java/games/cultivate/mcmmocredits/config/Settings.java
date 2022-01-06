package games.cultivate.mcmmocredits.config;

//TODO documentation
//TODO see if we can assign class types to the enum.
public enum Settings {
    DATABASE_ADD_MESSAGE("database-add-message"),
    PLAYER_TAB_COMPLETION("player-tab-completion"),
    SEND_LOGIN_MESSAGE("send-login-message"),
    USE_USERCACHE_LOOKUP("use-usercache-lookup");

    private final String key;

    Settings(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

    //TODO We have no clue if this works
    public Object value() {
        return ConfigHandler.settingsNode().node(key).rawScalar();
    }

}