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
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS mcmmoCredits(id INTEGER PRIMARY KEY AUTOINCREMENT, uuid VARCHAR NOT NULL, last_known_name VARCHAR NOT NULL, credits INT);";
    private static final String ADD_PLAYER = "INSERT INTO `mcmmoCredits`(UUID, last_known_name, credits) VALUES(?,?,?);";
    private static final String SET_CREDITS = "UPDATE `mcmmoCredits` SET credits = ? WHERE UUID= ?;";
    private static final String GET_CREDITS = "SELECT * FROM `mcmmoCredits` WHERE UUID= ?;";
    private static final String SET_USERNAME = "UPDATE `mcmmoCredits` SET last_known_name = ? WHERE UUID= ?;";
    private static final String GET_USERNAME = "SELECT * FROM `mcmmoCredits` WHERE UUID= ?;";
    private static final String GET_UUID = "SELECT * FROM `mcmmoCredits` WHERE last_known_name= ?;";
    private static final String DOES_PLAYER_EXIST = "SELECT 1 FROM `mcmmoCredits` WHERE UUID= ?;";

    public Database(MCMMOCredits plugin) {
        this.plugin = plugin;
        HikariConfig config = new HikariConfig();
        String adapter = Keys.DATABASE_ADAPTER.get();
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
            connection.createStatement().execute(CREATE_TABLE);
            connection.close();
        } catch (Exception e) {
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
                ps.setObject(1, uuid);
                ps.setString(2, username);
                ps.setInt(3, credits);
                ps.execute();
                ps.close();
                connection.close();
            } catch (Exception e) {
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
                    ps.setObject(2, uuid);
                    ps.execute();
                    connection.close();
                } catch (Exception e) {
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
            ps.setObject(1, uuid);
            ResultSet rows = ps.executeQuery();
            result.complete(rows.getString(3));
            connection.close();
        } catch (Exception e) {
            result.complete(null);
            e.printStackTrace();
        }
        return result;
    }

    public void setCredits(UUID uuid, int credits) {
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(SET_CREDITS);
            ps.setInt(1, credits);
            ps.setObject(2, uuid);
            ps.execute();
            ps.close();
            connection.close();
        } catch (Exception e) {
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
                ps.setObject(1, uuid);
                ResultSet rows = ps.executeQuery();
                result.complete(rows.isBeforeFirst());
                connection.close();
            } catch (Exception e) {
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
            ps.setObject(1, uuid);
            ResultSet rows = ps.executeQuery();
            result.complete(rows.getInt(4));
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.join();
    }

    public CompletableFuture<UUID> getUUID(String username) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(GET_UUID);
                ps.setString(1, username);
                ResultSet rows = ps.executeQuery();
                UUID result;
                if (rows.isClosed()) {
                    result = new UUID(0, 0);
                } else {
                    result =  UUID.fromString(rows.getString(2));
                }
                rows.close();
                ps.close();
                connection.close();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public void addCredits(UUID uuid, int amount) {
        setCredits(uuid, getCredits(uuid) + amount);
    }

    public void takeCredits(UUID uuid, int amount) {
        setCredits(uuid, Math.max(0, getCredits(uuid) - amount));
    }
}
