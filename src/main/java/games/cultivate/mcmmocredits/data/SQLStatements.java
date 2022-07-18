package games.cultivate.mcmmocredits.data;

enum SQLStatements {
    ADD_PLAYER("INSERT INTO `MCMMOCredits`(UUID, last_known_name, credits) VALUES(?,?,?);"),
    SET_CREDITS("UPDATE `MCMMOCredits` SET credits= ? WHERE `UUID`= ?;"),
    ADD_CREDITS("UPDATE `MCMMOCredits` SET credits = credits + ? WHERE `UUID`= ?;"),
    TAKE_CREDITS("UPDATE `MCMMOCredits` SET credits = credits - ? WHERE `UUID`= ?;"),
    GET_CREDITS("SELECT `credits` FROM `MCMMOCredits` WHERE `UUID`= ? LIMIT 1;"),
    SET_USERNAME("UPDATE `MCMMOCredits` SET last_known_name= ? WHERE `UUID`= ?;"),
    GET_USERNAME("SELECT `last_known_name` FROM `MCMMOCredits` WHERE `UUID`= ? LIMIT 1;"),
    GET_UUID("SELECT `UUID` FROM `MCMMOCredits` WHERE `last_known_name`= ? LIMIT 1;"),
    SQLITE_CREATE_TABLE("CREATE TABLE IF NOT EXISTS MCMMOCredits(id INTEGER PRIMARY KEY AUTOINCREMENT, UUID VARCHAR NOT NULL, last_known_name VARCHAR NOT NULL, credits INT CHECK(credits >= 0));"),
    MYSQL_CREATE_TABLE("CREATE TABLE IF NOT EXISTS `MCMMOCredits`(`id` int PRIMARY KEY AUTO_INCREMENT,`UUID` text NOT NULL,`last_known_name` text NOT NULL,`credits` int CHECK(credits >= 0));");

    private final String statement;

    SQLStatements(String statement) {
        this.statement = statement;
    }

    @Override
    public String toString() {
        return this.statement;
    }
}
