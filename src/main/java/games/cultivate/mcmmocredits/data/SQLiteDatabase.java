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
    private final Path dir;

    @Inject
    public SQLiteDatabase(final SettingsConfig settings, final MCMMOCredits plugin, final @Named("dir") Path dir) {
        super(settings, plugin);
        this.dir = dir;
    }

    @Override
    Jdbi createJDBI() {
        return Jdbi.create(this.hikari()).installPlugin(new SQLitePlugin());
    }

    @Override
    HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("MCMMOCredits SQLite");
        config.setDataSourceClassName("org.sqlite.SQLiteDataSource");
        Path path = this.dir.resolve("database.db");
        FileUtil.createFile(path);
        config.addDataSourceProperty("url", "jdbc:sqlite:" + path);
        return new HikariDataSource(config);
    }
}
