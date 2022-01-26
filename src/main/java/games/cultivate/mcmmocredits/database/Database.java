package games.cultivate.mcmmocredits.database;

import games.cultivate.mcmmocredits.MCMMOCredits;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This class is responsible for all Database management. We currently only support SQLite.
 *
 * TODO: MySQL support - The transactions use a very similar syntax, would not be difficult to do.
 * TODO: More Async operations
 */
public class Database {
    private static String url;

    private static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * This is all the Database logic that runs on server startup.
     */
    public static void initDB() {
        url = "jdbc:sqlite:" + MCMMOCredits.getInstance().getDataFolder().getAbsolutePath() + "\\database.db";
        Connection connection = getConnection();
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS mcmmoCredits(id INTEGER PRIMARY KEY AUTOINCREMENT, uuid VARCHAR NOT NULL, last_known_name VARCHAR NOT NULL, credits INT);");
            ps.execute();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This is all the Database logic that runs on server shutdown.
     */
    public static void shutdownDB() {
        Connection conn = Database.getConnection();
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This executes when we need to add a player to our Database.
     * @param uuid UUID of the target.
     * @param username username of the target. We store this to avoid UUID lookups.
     * @param credits amount of credits to start the user with.
     */
    public static void addPlayer(UUID uuid, String username, int credits) {
        Bukkit.getScheduler().runTaskAsynchronously(MCMMOCredits.getInstance(), () -> {
            try {
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement("INSERT INTO `mcmmoCredits`(UUID, last_known_name, credits) VALUES(?,?,?);");
                ps.setObject(1, uuid);
                ps.setString(2, username);
                ps.setInt(3, credits);
                ps.execute();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * This executes when we need to modify someone's username. We try to update this on every login.
     * @param uuid UUID of the target. Used to query the DB row.
     * @param username new username to modify our row with.
     */
    public static void setUsername(UUID uuid, String username) {
        if (getUsername(uuid) == null || !getUsername(uuid).equalsIgnoreCase(username)) {
            Bukkit.getScheduler().runTaskAsynchronously(MCMMOCredits.getInstance(), () -> {
                try {
                    Connection connection = getConnection();
                    PreparedStatement ps = connection.prepareStatement("UPDATE `mcmmoCredits` SET last_known_name = ? WHERE UUID= ?;");
                    ps.setString(1, username);
                    ps.setObject(2, uuid);
                    ps.execute();
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * This is responsible for checking the MCMMO Credit balance of a user.
     * <p>
     * TODO: Remove usage of .join() (this is blocking)
     */
    public static String getUsername(UUID uuid) {
        CompletableFuture<String> result = new CompletableFuture<>();
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `mcmmoCredits` WHERE UUID= ?;");
            ps.setObject(1, uuid);
            ResultSet rows = ps.executeQuery();
            result.complete(rows.getString(3));
            connection.close();
        } catch (Exception e) {
            result.complete(null);
            e.printStackTrace();
        }
        return result.join();
    }

    /**
     * This is responsible for updating a user's MCMMO Credit balance.
     */
    public static void setCredits(UUID uuid, int credits) {
        Bukkit.getScheduler().runTaskAsynchronously(MCMMOCredits.getInstance(), () -> {
            try {
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement("UPDATE `mcmmoCredits` SET credits = ? WHERE UUID= ?;");
                ps.setInt(1, credits);
                ps.setObject(2, uuid);
                ps.execute();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Helper method to modify MCMMO Credits (add).
     *
     * @param uuid   UUID of user to modify.
     * @param amount amount of Credits to add to user.
     */
    public static void addCredits(UUID uuid, int amount) {
        setCredits(uuid, getCredits(uuid) + amount);
    }

    /**
     * Helper method to modify MCMMO Credits (take). Ensures that MCMMO Credits never go negative.
     *
     * @param uuid   UUID of user to modify.
     * @param amount amount of Credits to take away from user.
     */
    public static void takeCredits(UUID uuid, int amount) {
        setCredits(uuid, Math.max(0, getCredits(uuid) - amount));
    }

    /**
     * This is responsible for checking if a user exists in our Database.
     * <p>
     * TODO: Remove usage of .join() (this is blocking)
     */
    public static boolean doesPlayerExist(UUID uuid) {
        if (uuid == null) return false;
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(MCMMOCredits.getInstance(), () -> {
            try {
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM `mcmmoCredits` WHERE UUID= ?;");
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

    /**
     * Logic that executes when we need Credit balance from UUID
     * TODO: Remove usage of .join() (blocking operation).
     *
     * @param uuid UUID of target
     * @return Credit balance of Player
     */
    public static int getCredits(UUID uuid) {
        CompletableFuture<Integer> result = new CompletableFuture<>();
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `mcmmoCredits` WHERE UUID= ?;");
            ps.setObject(1, uuid);
            ResultSet rows = ps.executeQuery();
            result.complete(rows.getInt(4));
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.join();
    }

    /**
     * Logic that obtains a UUID from last known username.
     * @param username username we are querying
     * @return UUID of the user
     */
    public static UUID getUUID(String username) {
        CompletableFuture<UUID> result = new CompletableFuture<>();
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `mcmmoCredits` WHERE last_known_name= ?;");
            ResultSet rows = ps.executeQuery();
            result.complete((UUID) rows.getObject( 2));
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.join();
    }
}
