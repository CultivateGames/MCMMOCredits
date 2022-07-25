package games.cultivate.mcmmocredits.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal, unused"})
public class SettingsConfig extends Config {
    @Comment("General plugin settings.")
    @Setting("general")
    private GeneralSettings general = new GeneralSettings();
    @Comment("Database related settings, that are not eligible for in-game configuration.")
    @Setting("mysql")
    private MYSQLSettings mysql = new MYSQLSettings();

    SettingsConfig() {
        super(SettingsConfig.class, "settings.conf");
    }

    public boolean isMYSQL() {
        return general.databaseType.equalsIgnoreCase("mysql");
    }

    @ConfigSerializable
    static class GeneralSettings {
        @Comment("Toggles tab completion for Player based arguments.\n" + "Useful if you have other plugins which hide staff.")
        @Setting("playerTabCompletion")
        private boolean playerTabCompletion = true;
        @Comment("Toggles sending a login message to the user.")
        @Setting("sendLoginMessage")
        private boolean sendLoginMessage = true;
        @Comment("Toggles console message when a user\n" + "is added to the MCMMO Credits database")
        @Setting("addPlayerNotification")
        private boolean addPlayerNotification = true;
        @Comment("Enables debug")
        @Setting("debug")
        private boolean debug = false;
        @Comment("Options: sqlite, mysql. Which database type should we use?\n" + "NOTE: There is not native support for changing between DB types.")
        @Setting("databaseType")
        private String databaseType = "sqlite";
    }

    @ConfigSerializable
    static class MYSQLSettings {
        @Comment("Host address of MySQL database.")
        @Setting("host")
        private String host = "127.0.0.1";
        @Comment("Port for Host address of MySQL database.")
        @Setting("port")
        private int port = 3306;
        @Comment("Name of Database to create.")
        @Setting("database")
        private String database = "database";
        @Comment("MySQL Account Username.")
        @Setting("username")
        private String username = "root";
        @Comment("MySQL Account Password.")
        @Setting("password")
        private String password = "passw0rd+";
        @Comment("UseSSL connection property. Should the connection use SSL?")
        @Setting("useSSL")
        private boolean useSSL = true;
    }
}
