package games.cultivate.mcmmocredits.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.SettingsConfig;

import javax.inject.Inject;
import java.io.File;

public final class SQLiteDatabase extends SQLDatabase {
    private final String path;

    @Inject
    public SQLiteDatabase(SettingsConfig settings, MCMMOCredits plugin) {
        super(settings, plugin);
        this.path = "jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + File.separator + "database.db";
    }

    @Override
    HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("MCMMOCredits SQLite");
        config.setDataSourceClassName("org.sqlite.SQLiteDataSource");
        config.addDataSourceProperty("url", path);
        return new HikariDataSource(config);
    }
}
