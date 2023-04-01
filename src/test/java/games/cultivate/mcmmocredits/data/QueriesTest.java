package games.cultivate.mcmmocredits.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QueriesTest {
    private static final Queries QUERIES = new Queries();

    @Test
    void testGetMySQLQuery() {
        assertEquals("CREATE TABLE IF NOT EXISTS MCMMOCredits(id INTEGER PRIMARY KEY AUTO_INCREMENT, UUID VARCHAR NOT NULL, username VARCHAR NOT NULL, credits INT CHECK(credits >= 0), redeemed INT);", QUERIES.query("CREATE-TABLE-MYSQL").stripTrailing());
    }

    @Test
    void testGetSQLiteQuery() {
        assertEquals("CREATE TABLE IF NOT EXISTS MCMMOCredits(id INTEGER PRIMARY KEY AUTOINCREMENT, UUID VARCHAR NOT NULL, username VARCHAR NOT NULL, credits INT CHECK(credits >= 0), redeemed INT);", QUERIES.query("CREATE-TABLE-SQLITE").stripTrailing());
    }
}
