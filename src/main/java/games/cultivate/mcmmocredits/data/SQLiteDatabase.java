package games.cultivate.mcmmocredits.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;

final class SQLiteDatabase extends SQLDatabase {
    private final String path = "jdbc:sqlite:" + super.plugin.getDataFolder().getAbsolutePath() + File.separator + "database.db";

    SQLiteDatabase() {
        super();
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
