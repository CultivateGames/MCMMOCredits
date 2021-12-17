package games.cultivate.mcmmocredits.util;

import games.cultivate.mcmmocredits.MCMMOCredits;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * TODO: MySQL support - The transactions use a very similar syntax, would not be difficult to do.
 * TODO: More Async operations
 * <p>This class is responsible for all Database management. We currently only support SQLite.</p>
 *
 * @see Database#getConnection(String)
 * @see Database#initDB()
 * @see Database#shutdownDB(Connection)
 * @see Database#add(UUID, int)
 * @see Database#update(UUID, int)
 * @see Database#doesPlayerExist(UUID)
 * @see Database#getCredits(UUID)
 */
public class Database {
    /**
     * <p>This is a method used to access a Database connection for other operations.</p>
     *
     * @param url Database URL
     * @return The {@link Connection} that we would like to use.
     */
    public static Connection getConnection(String url) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * <p>This method is called when the plugin is enabling, in order to make sure a Database is ready for our modifications.</p>
     *
     * <p>First, we will establish our connection. Next, we will create a SQL table
     * using a {@link PreparedStatement} if one does not exist for us to use.</p>
     *
     * <p>Finally, we will close the connection to make sure that not too many connections are active at once.</p>
     */
    public static void initDB() {
        Connection connection = getConnection(MCMMOCredits.getDBURL());
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
     * <p>This method is used for when the plugin is disabling, and we need to close all active connections to the Database.</p>
     *
     * <p>Simply, we check if the {@link Connection} is not null, and not closed.
     * If this is the current state of the connection, then we will attempt to close the connection.</p>
     *
     * @param conn The {@link Connection} that needs to be closed.
     */
    public static void shutdownDB(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>This method is called when we need to add a user to the MCMMO Credits Database.</p>
     *
     * <p>First, we prepare our {@link Connection} and our {@link PreparedStatement} to make an entry into the Database.</p>
     *
     * <p>Next, we supply the information that needs to be added, and then it is added.</p>
     *
     * <p>Finally, we just close the connection.</p>
     *
     * <p>We call this method asynchronously because it is an I/O operation. We do not want to hang the server,
     * and this can be done safely on other threads.</p>
     *
     * @param uuid    The {@link UUID} of the {@link org.bukkit.entity.Player} we are trying to add to our database.
     * @param credits The amount of MCMMO Credits to give the user upon entry creation. Currently, this is always 0.
     * @see Listeners#onPlayerPreLogin(AsyncPlayerPreLoginEvent)
     */
    public static void add(UUID uuid, int credits) {
        Bukkit.getScheduler().runTaskAsynchronously(MCMMOCredits.getInstance(), () -> {
            try {
                Connection connection = getConnection(MCMMOCredits.getDBURL());
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
     * <p>This method is called when we need to update the amount of MCMMO Credits a user currently has.</p>
     *
     * <p>First, we prepare our {@link Connection} and our {@link PreparedStatement} to make an entry into the Database.</p>
     *
     * <p>Next, we supply the information that we need to update for the user, and then it is performed.</p>
     *
     * <p>Finally, we just close the connection.</p>
     *
     * <p>We call this method asynchronously because it is an I/O operation. We do not want to hang the server,
     * and this can be done safely.</p>
     *
     * @param uuid    The {@link UUID} of the {@link org.bukkit.entity.Player} we are trying to add to our database.
     * @param credits The amount of MCMMO Credits the user should have.
     * @see games.cultivate.mcmmocredits.commands.ModifyCredits
     * @see games.cultivate.mcmmocredits.commands.Redeem
     */
    public static void update(UUID uuid, int credits) {
        Bukkit.getScheduler().runTaskAsynchronously(MCMMOCredits.getInstance(), () -> {
            try {
                Connection connection = getConnection(MCMMOCredits.getDBURL());
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
     * <p>This method is responsible for checking if a user exists in our Database.
     * We will typically skip processing due to the result.</p>
     *
     * <p>First, we prepare our {@link Connection} and our {@link PreparedStatement} to search the Database.</p>
     *
     * <p>Next, we supply the {@link UUID} that we want to search with. After executing the query,
     * we check the position of the cursor in the {@link ResultSet}.</p>
     *
     * <p>Based on the result of the cursor, we complete our {@link CompletableFuture<Boolean>}.</p>
     *
     * <p>Finally, we just close the connection.</p>
     *
     * <p>We call this method asynchronously because it is an I/O operation. We do not want to hang the server,
     * and this can be done safely on other threads. However, .join() is a blocking call, so hanging is not completely mitigated.</p>
     *
     * @param uuid The {@link UUID} of the user we are trying to search for in our database.
     * @return Whether the user exists in our Database.
     */
    public static boolean doesPlayerExist(UUID uuid) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(MCMMOCredits.getInstance(), () -> {
            try {
                Connection connection = getConnection(MCMMOCredits.getDBURL());
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
     * <p>This method is responsible for checking if a user's MCMMO Credit balance.</p>
     *
     * <p>First, we prepare our {@link Connection} and our {@link PreparedStatement} to search the Database.</p>
     *
     * <p>Next, we supply the {@link UUID} that we want to search with. After executing the query,
     * we attempt to grab the Credit balance from our {@link ResultSet}.</p>
     *
     * <p>Based on the result of the query, we complete our {@link CompletableFuture<Integer>}.</p>
     *
     * <p>Finally, we just close the connection.</p>
     *
     * <p>This method exists for areas where using a Completable Future would be inconvenient,
     * and we are sure the data exists.</p>
     *
     * @param uuid The {@link UUID} of the user we are trying to search for in our database.
     * @return The amount of MCMMO Credits a user has (or 0).
     */
    public static int getCredits(UUID uuid) {
        CompletableFuture<Integer> result = new CompletableFuture<>();
        try {
            Connection connection = getConnection(MCMMOCredits.getDBURL());
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
