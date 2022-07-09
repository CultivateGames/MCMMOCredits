package games.cultivate.mcmmocredits.data;

import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.keys.StringKey;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static games.cultivate.mcmmocredits.data.SQLStatements.*;

public abstract sealed class SQLDatabase extends Database permits MYSQLDatabase, SQLiteDatabase {
    final JavaPlugin plugin;
    private final HikariDataSource hikari;
    private static final UUID ZERO_UUID = new UUID(0, 0);

    abstract HikariDataSource createDataSource();

    SQLDatabase() {
        this.plugin = JavaPlugin.getPlugin(MCMMOCredits.class);
        this.hikari = this.createDataSource();
        SQLStatements creation = StringKey.DATABASE_ADAPTER.get().equals("mysql") ? MYSQL_CREATE_TABLE : SQLITE_CREATE_TABLE;
        try (Connection connection = this.connection();
             PreparedStatement ps = connection.prepareStatement(creation.toString())) {
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection connection() throws SQLException {
        return this.hikari.getConnection();
    }

    @Override
    public void disable() {
        if (this.hikari != null) {
            this.hikari.close();
        }
    }

    @Override
    public void addPlayer(UUID uuid, String username, int credits) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = this.connection();
                 PreparedStatement ps = connection.prepareStatement(ADD_PLAYER.toString())) {
                ps.setString(1, uuid.toString());
                ps.setString(2, username);
                ps.setInt(3, credits);
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<UUID> getUUID(String username) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = this.connection();
                 PreparedStatement ps = connection.prepareStatement(GET_UUID.toString())) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                return rs.next() ? UUID.fromString(rs.getString(1)) : ZERO_UUID;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public boolean doesPlayerExist(UUID uuid) {
        if (uuid == null || uuid.equals(ZERO_UUID)) {
            return false;
        }
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = this.connection();
                 PreparedStatement ps = connection.prepareStatement(DOES_PLAYER_EXIST.toString())) {
                ps.setString(1, uuid.toString());
                ResultSet rows = ps.executeQuery();
                result.complete(rows.next());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return result.join();
    }

    @Override
    public void setUsername(UUID uuid, String username) {
        this.getUsername(uuid).thenAcceptAsync(i -> {
            if (!i.equalsIgnoreCase(username)) {
                try (Connection connection = this.connection();
                     PreparedStatement ps = connection.prepareStatement(SET_USERNAME.toString())) {
                    ps.setString(1, username);
                    ps.setString(2, uuid.toString());
                    ps.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public CompletableFuture<String> getUsername(UUID uuid) {
        CompletableFuture<String> result = new CompletableFuture<>();
        try (Connection connection = this.connection();
             PreparedStatement ps = connection.prepareStatement(GET_USERNAME.toString())) {
            ps.setString(1, uuid.toString());
            ResultSet rows = ps.executeQuery();
            String completion = rows.next() ? rows.getString(1) : null;
            result.complete(completion);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void setCredits(UUID uuid, int credits) {
        try (Connection connection = this.connection();
             PreparedStatement ps = connection.prepareStatement(SET_CREDITS.toString())) {
            ps.setInt(1, credits);
            ps.setString(2, uuid.toString());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCredits(UUID uuid) {
        CompletableFuture<Integer> result = new CompletableFuture<>();
        try (Connection connection = this.connection();
             PreparedStatement ps = connection.prepareStatement(GET_CREDITS.toString())) {
            ps.setString(1, uuid.toString());
            ResultSet rows = ps.executeQuery();
            result.complete(rows.next() ? rows.getInt(1) : 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.join();
    }
}
