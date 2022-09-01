//
// MIT License
//
// Copyright (c) 2022 Cultivate Games
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package games.cultivate.mcmmocredits.data;

/**
 * Enum used to hold SQL-based queries used throughout the application.
 */
enum SQLStatement {
    ADD_PLAYER("INSERT INTO `MCMMOCredits`(UUID, username, credits) VALUES(?,?,?);"),
    SET_CREDITS("UPDATE `MCMMOCredits` SET credits= ? WHERE `UUID`= ?;"),
    ADD_CREDITS("UPDATE `MCMMOCredits` SET credits = credits + ? WHERE `UUID`= ?;"),
    TAKE_CREDITS("UPDATE `MCMMOCredits` SET credits = credits - ? WHERE `UUID`= ?;"),
    GET_CREDITS("SELECT `credits` FROM `MCMMOCredits` WHERE `UUID`= ? LIMIT 1;"),
    SET_USERNAME("UPDATE `MCMMOCredits` SET username= ? WHERE `UUID`= ?;"),
    GET_USERNAME("SELECT `username` FROM `MCMMOCredits` WHERE `UUID`= ? LIMIT 1;"),
    GET_UUID("SELECT `UUID` FROM `MCMMOCredits` WHERE `username` LIKE ? LIMIT 1;"),
    SQLITE_CREATE_TABLE("CREATE TABLE IF NOT EXISTS MCMMOCredits(id INTEGER PRIMARY KEY AUTOINCREMENT, UUID VARCHAR NOT NULL, username VARCHAR NOT NULL, credits INT CHECK(credits >= 0));"),
    MYSQL_CREATE_TABLE("CREATE TABLE IF NOT EXISTS `MCMMOCredits`(`id` int PRIMARY KEY AUTO_INCREMENT,`UUID` text NOT NULL,`username` text NOT NULL,`credits` int CHECK(credits >= 0));");

    private final String statement;

    SQLStatement(final String statement) {
        this.statement = statement;
    }

    /**
     * Used to obtain the query.
     *
     * @return the query.
     */
    @Override
    public String toString() {
        return this.statement;
    }
}
