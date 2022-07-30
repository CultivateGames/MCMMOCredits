package games.cultivate.mcmmocredits.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.SettingsConfig;
import games.cultivate.mcmmocredits.util.FileUtil;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlite3.SQLitePlugin;

import javax.inject.Inject;
import javax.inject.Named;
import java.nio.file.Path;

public final class SQLiteDatabase extends SQLDatabase {

    @Inject
    public SQLiteDatabase(SettingsConfig settings, MCMMOCredits plugin, @Named("dir") Path dir) {
        super(settings, plugin, dir);
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
        Path path = super.dir.resolve("database.db");
        FileUtil.createFile(path);
        config.addDataSourceProperty("url", "jdbc:sqlite:" + path);
        return new HikariDataSource(config);
    }
}
