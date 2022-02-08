package games.cultivate.mcmmocredits.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.Keys;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class Database {
    private final HikariDataSource hikari;
    private final MCMMOCredits plugin;
    private static final String SQLITE_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS MCMMOCredits(id INTEGER PRIMARY KEY AUTOINCREMENT, UUID VARCHAR NOT NULL, last_known_name VARCHAR NOT NULL, credits INT);";
    private static final String MYSQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `MCMMOCredits`(`id` int PRIMARY KEY AUTO_INCREMENT,`UUID` text NOT NULL,`last_known_name` text NOT NULL,`credits` int);";
    private static final String ADD_PLAYER = "INSERT INTO `MCMMOCredits`(UUID, last_known_name, credits) VALUES(?,?,?);";
    private static final String SET_CREDITS = "UPDATE `MCMMOCredits` SET credits= ? WHERE `UUID`= ?;";
    private static final String GET_CREDITS = "SELECT `credits` FROM `MCMMOCredits` WHERE `UUID`= ? LIMIT 1;";
    private static final String SET_USERNAME = "UPDATE `MCMMOCredits` SET last_known_name= ? WHERE `UUID`= ?;";
    private static final String GET_USERNAME = "SELECT `last_known_name` FROM `MCMMOCredits` WHERE `UUID`= ? LIMIT 1;";
    private static final String GET_UUID = "SELECT `UUID` FROM `MCMMOCredits` WHERE `last_known_name`= ? LIMIT 1;";
    private static final String DOES_PLAYER_EXIST = "SELECT * FROM `MCMMOCredits` WHERE `UUID`= ? LIMIT 1;";
    private static final String adapter = Keys.DATABASE_ADAPTER.get();

    public Database(MCMMOCredits plugin) {
        this.plugin = plugin;
        HikariConfig config = new HikariConfig();
        switch (adapter.toLowerCase()) {
            case "sqlite" -> {
                config.setPoolName("MCMMOCredits SQLite");
                config.setDataSourceClassName("org.sqlite.SQLiteDataSource");
                config.addDataSourceProperty("url", "jdbc:sqlite:" + MCMMOCredits.path + "database.db");
            }
            case "mysql" -> {
                config.setPoolName("MCMMOCredits MySQL");
                config.setJdbcUrl("jdbc:mysql://" + Keys.DATABASE_HOST.get() + ":" + Keys.DATABASE_PORT.get() + "/" + Keys.DATABASE_NAME.get());
                config.setUsername(Keys.DATABASE_USERNAME.get());
                config.setPassword(Keys.DATABASE_PASSWORD.get());
                config.addDataSourceProperty("useSSL", Keys.DATABASE_SSL.get());
                config.addDataSourceProperty("maintainTimeStats", "false");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                config.addDataSourceProperty("rewriteBatchedStatements", "true");
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("useServerPrepStmts", "true");
                config.addDataSourceProperty("useLocalSessionState", "true");
                config.addDataSourceProperty("cacheResultSetMetadata", "true");
                config.addDataSourceProperty("cacheServerConfiguration", "true");
            }
            default -> {
                Bukkit.getLogger().log(Level.SEVERE, "INVALID DATABASE ADAPTER SET! Disabling plugin...");
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }

        hikari = new HikariDataSource(config);
        Connection connection = this.getConnection();
        try {
            connection.createStatement().execute(adapter.equalsIgnoreCase("sqlite") ? SQLITE_CREATE_TABLE : MYSQL_CREATE_TABLE);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            return hikari.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void disable() {
        if (hikari != null) {
            hikari.close();
        }
    }

    public void addPlayer(UUID uuid, String username, int credits) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(ADD_PLAYER);
                ps.setString(1, uuid.toString());
                ps.setString(2, username);
                ps.setInt(3, credits);
                ps.execute();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void setUsername(UUID uuid, String username) {
        this.getUsername(uuid).thenAcceptAsync((i) -> {
            if (!i.equalsIgnoreCase(username)) {
                try {
                    Connection connection = getConnection();
                    PreparedStatement ps = connection.prepareStatement(SET_USERNAME);
                    ps.setString(1, username);
                    ps.setString(2, uuid.toString());
                    ps.execute();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public CompletableFuture<String> getUsername(UUID uuid) {
        CompletableFuture<String> result = new CompletableFuture<>();
            try {
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(GET_USERNAME);
                ps.setString(1, uuid.toString());
                ResultSet rows = ps.executeQuery();
                if (rows.next()) {
                    result.complete(rows.getString(1));
                } else {
                    result.complete(null);
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return result;
    }

    public void setCredits(UUID uuid, int credits) {
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(SET_CREDITS);
            ps.setInt(1, credits);
            ps.setString(2, uuid.toString());
            ps.execute();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean doesPlayerExist(UUID uuid) {
        if (uuid == null || uuid.equals(new UUID(0, 0))) {
            return false;
        }
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(DOES_PLAYER_EXIST);
                ps.setString(1, uuid.toString());
                ResultSet rows = ps.executeQuery();
                if(rows.next()) {
                    result.complete(true);
                } else {
                    result.complete(false);
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return result.join();
    }

    public int getCredits(UUID uuid) {
        CompletableFuture<Integer> result = new CompletableFuture<>();
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(GET_CREDITS);
            ps.setString(1, uuid.toString());
            ResultSet rows = ps.executeQuery();
            if(rows.next()) {
                result.complete(rows.getInt(1));
            } else {
                result.complete(0);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.join();
    }

    public CompletableFuture<UUID> getUUID(String username) {
        return CompletableFuture.supplyAsync(() -> {
            UUID result = new UUID(0, 0);
            try {
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(GET_UUID);
                ps.setString(1, username);
                ResultSet rows = ps.executeQuery();
                if (rows.next()) {
                    result = UUID.fromString(rows.getString(1));
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return result;
        });
    }

    public void addCredits(UUID uuid, int amount) {
        setCredits(uuid, getCredits(uuid) + amount);
    }

    public void takeCredits(UUID uuid, int amount) {
        setCredits(uuid, Math.max(0, getCredits(uuid) - amount));
    }
}
