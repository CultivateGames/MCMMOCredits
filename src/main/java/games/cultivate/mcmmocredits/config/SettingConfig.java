package games.cultivate.mcmmocredits.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

/**
 * This class is responsible for generating the default settings.conf file. All values here represent defaults.
 */
@ConfigSerializable
public final class SettingConfig {
    @Comment("MySQL connection properties. All options will be ignored if SQLite is enabled. Use a separate MySQL user to manage this database.")
    @Setting("mysql-credentials")
    private MySQL mysql = new MySQL();

    @Comment("Perform offline player lookups with usercache. PAPER ONLY. Disable if you are having problems.")
    private boolean use_usercache_lookup = false;

    @Comment("Toggles tab completion for Player based arguments. Useful if you have other plugins which hide staff.")
    private boolean player_tab_completion = true;

    @Comment("Toggles sending a login message to the user indicating how many MCMMO Credits they have. Message can be configured in messages.conf.")
    private boolean send_login_message = true;

    @Comment("Toggles console message when a user is added to the MCMMO Credits database")
    private boolean database_add_message = true;

    @Comment("Options: sqlite, mysql")
    private String database_adapter = "sqlite";

    @ConfigSerializable
    protected static final class MySQL {
        @Comment("This should be the host address of the MySQL database.")
        private String host = "127.0.0.1";
        @Comment("This should be the host address port of the MySQL database.")
        private int port = 3306;
        @Comment("This is the name of the relevant database.")
        private String database = "database";
        @Comment("This is the username to login to the database.")
        private String username = "root";
        @Comment("This is the password used to login to the database.")
        private String password = "";
    }
}