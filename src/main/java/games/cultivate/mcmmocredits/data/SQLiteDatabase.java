package games.cultivate.mcmmocredits.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.SettingsConfig;
import org.bukkit.Bukkit;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlite3.SQLitePlugin;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class SQLiteDatabase extends SQLDatabase {

    @Inject
    public SQLiteDatabase(SettingsConfig settings, MCMMOCredits plugin) {
        super(settings, plugin);
    }

    @Override
    Jdbi createJDBI() {
        return Jdbi.create(super.hikari).installPlugin(new SQLitePlugin());
    }

    @Override
    HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("MCMMOCredits SQLite");
        config.setDataSourceClassName("org.sqlite.SQLiteDataSource");
        String fileName = "database.db";
        File file = new File(super.plugin.getDataFolder().getAbsolutePath() + File.separator + fileName);
        try {
            if (file.getParentFile().mkdirs() && file.createNewFile()) {
                Bukkit.getLogger().log(Level.INFO, "[MCMMOCredits] Created " + fileName + " file!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        config.addDataSourceProperty("url", "jdbc:sqlite:" + file.toPath());
        return new HikariDataSource(config);
    }
}
