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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * This is responsible for all Database startup logic.
     */
    public static void initDB() {
        url = "jdbc:sqlite:" + MCMMOCredits.getInstance().getDataFolder().getAbsolutePath() + "\\database.db";
        Connection connection = getConnection();
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS mcmmoCredits(id INTEGER PRIMARY KEY AUTOINCREMENT, uuid VARCHAR NOT NULL, credits INT);");
            ps.execute();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is responsible for all Database shutdown logic.
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
     * This is responsible for adding users to the MCMMO Credits Database.
     */
    public static void addPlayer(UUID uuid, int credits) {
        Bukkit.getScheduler().runTaskAsynchronously(MCMMOCredits.getInstance(), () -> {
            try {
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement("INSERT INTO `mcmmoCredits`(UUID, credits) VALUES(?,?);");
                ps.setObject(1, uuid);
                ps.setInt(2, credits);
                ps.execute();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * This is responsible for checking if a user exists in our Database.
     *
     * TODO: Remove usage of .join() (this is blocking)
     */
    public static boolean doesPlayerExist(UUID uuid) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(MCMMOCredits.getInstance(), () -> {
            try {
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM `mcmmoCredits` WHERE UUID= ?;");
                ps.setObject(1, uuid);
                ResultSet rows = ps.executeQuery();
                result.complete(rows.isBeforeFirst());
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return result.join();
    }

    /**
     * This is responsible for checking the MCMMO Credit balance of a user.
     *
     * TODO: Remove usage of .join() (this is blocking)
     */
    public static int getCredits(UUID uuid) {
        CompletableFuture<Integer> result = new CompletableFuture<>();
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `mcmmoCredits` WHERE UUID= ?;");
            ps.setObject(1, uuid);
            ResultSet rows = ps.executeQuery();
            result.complete(rows.getInt(3));
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.join();
    }
}
