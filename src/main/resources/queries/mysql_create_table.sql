CREATE TABLE IF NOT EXISTS MCMMOCredits(id INTEGER PRIMARY KEY AUTO_INCREMENT, UUID VARCHAR NOT NULL, username VARCHAR NOT NULL, credits INT CHECK(credits >= 0), redeemed INT);