package games.cultivate.mcmmocredits.database;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public final class TestDatabase extends H2Database {
    private TestDatabase(final DataSource source) {
        super(source);
    }

    public static TestDatabase create(final String name) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1;MODE=MYSQL;IGNORECASE=TRUE".formatted(name));
        return new TestDatabase(ds);
    }

    public void delete() {
        this.executor.useHandle(handle -> handle.execute("DELETE FROM MCMMOCredits"));
    }
}
