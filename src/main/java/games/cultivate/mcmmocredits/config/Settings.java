package games.cultivate.mcmmocredits.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
/**
 * This class is responsible for generating the default settings.conf file. All values here represent defaults.
 */
@ConfigSerializable
public final class Settings {
    @Comment("This is where MySQL connection properties are managed.")
    private final MySQL mysql = new MySQL();

    @Comment("Perform offline player lookups with usercache. PAPER ONLY. Disable if you are having problems.")
    private boolean use_usercache_lookup = false;

    @Comment("Toggles tab completion for Player based arguments. Useful if you have other plugins which hide staff.")
    private boolean player_tab_completion = true;

    @Comment("Toggles sending a login message to the user indicating how many MCMMO Credits they have. Message can be configured in messages.conf.")
    private boolean send_login_message = true;

    @Comment("Toggles console message when a user is added to the MCMMO Credits database")
    private boolean database_add_message = true;

    @Comment("Set to 'false' to use MySQL")
    private boolean use_sqlite = true;

    public MySQL getMYSQL() {
        return this.mysql;
    }

    public boolean getUsercacheLookup() {
        return this.use_usercache_lookup;
    }

    public void setUsercacheLookup(boolean bool) {
        this.use_usercache_lookup = bool;
    }

    public boolean getPlayerTabCompletion() {
        return this.player_tab_completion;
    }

    public void setPlayerTabCompletion(boolean bool) {
        this.player_tab_completion = bool;
    }

    public boolean getSendLoginMessage() {
        return send_login_message;
    }

    public void setSendLoginMessage(boolean bool) {
        this.send_login_message = bool;
    }

    public boolean getDatabaseAddMessage() {
        return this.database_add_message;
    }

    public void setDatabaseAddMessage(boolean bool) {
        this.database_add_message = bool;
    }

    public boolean getUseSQLite() {
        return this.use_sqlite;
    }

    public void setUseSQLite(boolean bool) {
        this.use_sqlite = bool;
    }

    @ConfigSerializable
    protected static final class MySQL {
        @Comment("This should be the host address of the MySQL database. Will be ignored if MySQL is disabled.")
        private String host = "127.0.0.1";
        @Comment("This should be the host address port of the MySQL database. Will be ignored if MySQL is disabled.")
        private int port = 3306;
        @Comment("This is the name of the relevant database. Will be ignored if MySQL is disabled.")
        private String database = "database";
        @Comment("This is the username to login to the database. Use a separate MySQL user to manage this database.")
        private String username = "root";
        @Comment("This is the password used to login to the database. Use a separate MySQL user to manage this database.")
        private String password = "";

        public String getHost() {
            return this.host;
        }

        //Not sure if this should be here.
        private void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return this.port;
        }

        //Not sure if this should be here.
        private void setPort(int port) {
            this.port = port;
        }

        public String getDatabaseName() {
            return this.database;
        }

        //Not sure if this should be here.
        private void setDatabaseName(String database) {
            this.database = database;
        }

        public String getUsername() {
            return this.username;
        }

        //Not sure if this should be here.
        private void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return this.password;
        }

        //Not sure if this should be here.
        private void setPassword(String password) {
            this.password = password;
        }
    }
}