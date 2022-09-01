package games.cultivate.mcmmocredits.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.util.FileUtil;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlite3.SQLitePlugin;

import javax.inject.Inject;
import javax.inject.Named;
import java.nio.file.Path;

/**
 * {@link Database} instance that utilizes a SQLite database.
 */
public final class SQLiteDatabase extends SQLDatabase {

    @Inject
    public SQLiteDatabase(final MCMMOCredits plugin, final @Named("dir") Path dir) {
        super(plugin);
        HikariConfig config = new HikariConfig();
        config.setPoolName("MCMMOCredits SQLite");
        config.setDataSourceClassName("org.sqlite.SQLiteDataSource");
        FileUtil.createFile(dir, "database.db");
        config.addDataSourceProperty("url", "jdbc:sqlite:" + dir.resolve("database.db"));
        this.hikari = new HikariDataSource(config);
        this.jdbi = Jdbi.create(this.hikari).installPlugin(new SQLitePlugin());
        this.jdbi.useHandle(x -> x.execute(SQLStatement.SQLITE_CREATE_TABLE.toString()));
    }
}
