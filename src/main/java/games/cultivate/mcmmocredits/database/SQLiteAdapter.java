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

public class SQLiteAdapter extends DatabaseAdapter {
    private String url;

    public SQLiteAdapter() {
        this.url = "jdbc:sqlite:" + MCMMOCredits.getInstance().getDataFolder().getAbsolutePath() + "\\database.db";
        Connection connection = this.getConnection();
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS mcmmoCredits(id INTEGER PRIMARY KEY AUTOINCREMENT, uuid VARCHAR NOT NULL, last_known_name VARCHAR NOT NULL, credits INT);");
            ps.execute();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    @Override
    public void enableAdapter() {
        this.url = "jdbc:sqlite:" + MCMMOCredits.getInstance().getDataFolder().getAbsolutePath() + "\\database.db";
        Connection connection = this.getConnection();
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS mcmmoCredits(id INTEGER PRIMARY KEY AUTOINCREMENT, uuid VARCHAR NOT NULL, last_known_name VARCHAR NOT NULL, credits INT);");
            ps.execute();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disableAdapter() {
        Connection conn = this.getConnection();
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPlayer(UUID uuid, String username, int credits) {
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

    @Override
    public void setUsername(UUID uuid, String username) {
        this.getUsername(uuid).thenAcceptAsync((i) -> {
            if (!i.equalsIgnoreCase(username)) {
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
            }
        });
    }

    @Override
    public CompletableFuture<String> getUsername(UUID uuid) {
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
        return result;
    }

    @Override
    public void setCredits(UUID uuid, int credits) {
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
    }

    @Override
    public void addCredits(UUID uuid, int amount) {
        setCredits(uuid, getCredits(uuid) + amount);
    }

    @Override
    public void takeCredits(UUID uuid, int amount) {
        setCredits(uuid, Math.max(0, getCredits(uuid) - amount));
    }

    @Override
    public boolean doesPlayerExist(UUID uuid) {
        if (uuid == null || uuid.equals(new UUID(0, 0))) {
            return false;
        }
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

    @Override
    public int getCredits(UUID uuid) {
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

    @Override
    public CompletableFuture<UUID> getUUID(String username) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM `mcmmoCredits` WHERE last_known_name= ?;");
                ps.setString(1, username);
                ResultSet rows = ps.executeQuery();
                UUID result;
                if (rows.isClosed()) {
                    result = new UUID(0, 0);
                } else {
                    result =  UUID.fromString(rows.getString(2));
                }
                connection.close();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }
}
