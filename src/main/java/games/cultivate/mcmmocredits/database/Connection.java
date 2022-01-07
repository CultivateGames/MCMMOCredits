package games.cultivate.mcmmocredits.database;

/**
 * This is an alternative method to creating a MySQL connection. This is a proof of concept and will likely be deleted.
 */
public final class Connection {
    private String host = "127.0.0.1";
    private int port = 3306;
    private String database = "database";
    private String username = "root";
    private String password = "";

    public Connection(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

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
