package games.cultivate.mcmmocredits.data;

import com.zaxxer.hikari.HikariConfig;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.util.Util;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DataSourceFactoryTest {
    private MCMMOCredits plugin;
    private DatabaseProperties sqlProperties;
    private DatabaseProperties sqliteProperties;

    @BeforeEach
    public void setUp() {
        this.sqliteProperties = DatabaseProperties.defaults();
        this.sqlProperties = new DatabaseProperties(DatabaseType.MYSQL, "localhost", "database", "user", "password", 3306, false);
        this.plugin = mock(MCMMOCredits.class);
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Path.of("src", "test", "resources", "database.db"));
    }

    @Test
    void createSQLite_ValidProperties_ReturnsValidConfig() {
        try (MockedStatic<Util> util = Mockito.mockStatic(Util.class)) {
            util.when(Util::getPluginPath).thenReturn(Path.of("src", "test", "resources"));
            HikariConfig config = DataSourceFactory.createSQLite(this.sqliteProperties);

            assertEquals("org.sqlite.SQLiteDataSource", config.getDataSourceClassName());
            assertEquals("jdbc:sqlite:src\\test\\resources\\database.db", config.getDataSourceProperties().get("url"));
        }
    }

    @Test
    void createMySQL_ValidProperties_ReturnsValidConfig() {
        //Arrange
        when(this.plugin.getResource("hikari.properties")).thenReturn(this.getClass().getClassLoader().getResourceAsStream("hikari.properties"));
        //Act
        HikariConfig config = DataSourceFactory.createMySQL(this.sqlProperties, this.plugin);
        //Assert
        assertNotNull(config);
        assertEquals("com.mysql.cj.jdbc.MysqlDataSource", config.getDataSourceClassName());
        assertEquals("jdbc:mysql://localhost:3306/database?useSSL=false", config.getJdbcUrl());
        assertEquals("user", config.getUsername());
        assertEquals("password", config.getPassword());
    }
}