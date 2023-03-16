package games.cultivate.mcmmocredits.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QueriesTest {
    @Test
    void testGetMySQLQuery() {
        assertEquals("CREATE TABLE IF NOT EXISTS MCMMOCredits(id INTEGER PRIMARY KEY AUTO_INCREMENT, UUID VARCHAR NOT NULL, username VARCHAR NOT NULL, credits INT CHECK(credits >= 0), redeemed INT);", new Queries().query("CREATE-TABLE-MYSQL"));
    }

    @Test
    void testGetSQLiteQuery() {
        assertEquals("CREATE TABLE IF NOT EXISTS MCMMOCredits(id INTEGER PRIMARY KEY AUTOINCREMENT, UUID VARCHAR NOT NULL, username VARCHAR NOT NULL, credits INT CHECK(credits >= 0), redeemed INT);", new Queries().query("CREATE-TABLE-SQLITE"));
    }
}