package games.cultivate.mcmmocredits.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal, unused"})
public class SettingsConfig {
    @Comment("Database related settings.\n" + "Includes MySQL credential management.\n" + "These are not eligible for in-game configuration.")
    @Setting("database")
    private DatabaseSettings databaseSettings = new DatabaseSettings();

    @Comment("All other settings")
    @Setting("general")
    private GeneralSettings generalSettings = new GeneralSettings();

    @ConfigSerializable
    protected static final class GeneralSettings {
        @Comment("Toggles tab completion for Player based arguments.\n" + "Useful if you have other plugins which hide staff.")
        private boolean player_tab_completion = true;
        @Comment("Toggles sending a login message to the user.")
        private boolean send_login_message = true;
        @Comment("Toggles console message when a user\n" + "is added to the MCMMO Credits database")
        private boolean add_notification = true;
        @Comment("Enables debug")
        private boolean debug = false;

    }

    @ConfigSerializable
    protected static final class DatabaseSettings {
        @Comment("MySQL connection properties. All options will be ignored if SQLite is enabled. Use a separate MySQL user to manage this database.")
        @Setting("mysql-credentials")
        private SQLSettings sqlSettings = new SQLSettings();
        @Comment("Options: sqlite, mysql. Which database type should we use?\n" + "NOTE: There is not native support for changing between DB types.")
        private String adapter = "sqlite";
    }

    @ConfigSerializable
    protected static final class SQLSettings {
        @Comment("Host address of MySQL database.")
        private String host = "127.0.0.1";
        @Comment("Port for Host address of MySQL database.")
        private int port = 3306;
        @Comment("Name of Database to create.")
        private String name = "database";
        @Comment("MySQL Account Username.")
        private String username = "root";
        @Comment("MySQL Account Password.")
        private String password = "passw0rd+";
        @Comment("UseSSL. Should the connection use SSL?")
        private boolean ssl = true;
    }
}
