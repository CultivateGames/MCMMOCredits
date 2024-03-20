//
// MIT License
//
// Copyright (c) 2024 Cultivate Games
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
package games.cultivate.mcmmocredits.converters;

import games.cultivate.mcmmocredits.converters.loaders.CSVLoader;
import games.cultivate.mcmmocredits.converters.loaders.UserLoader;
import games.cultivate.mcmmocredits.database.AbstractDatabase;
import games.cultivate.mcmmocredits.database.DatabaseUtil;
import games.cultivate.mcmmocredits.user.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CSVConverterTest {
    private final AbstractDatabase currentDatabase = DatabaseUtil.create("csv");
    private final Random random = new Random();

    @AfterEach
    void tearDown() {
        Path.of("src", "test", "resources", "database.csv").toFile().delete();
    }

    @Test
    void run_ValidUsers_ConvertsUsersCorrectly() throws IOException {
        List<User> newUsers = new ArrayList<>();
        Path csvPath = Path.of("src", "test", "resources", "database.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvPath.toFile()))) {
            for (int i = 0; i < 500; i++) {
                User user = new User(UUID.randomUUID(), RandomStringUtils.randomAlphanumeric(16), this.random.nextInt(0, Integer.MAX_VALUE), this.random.nextInt(0, Integer.MAX_VALUE));
                newUsers.add(user);
                writer.write(String.format("%s,%s,%d,%d", user.uuid(), user.username(), user.credits(), user.redeemed()));
                writer.newLine();
            }
        }
        UserLoader loader = new CSVLoader(csvPath);
        Converter converter = new DefaultConverter(this.currentDatabase, loader);
        assertTrue(converter.run());
        List<User> currentUsers = this.currentDatabase.getAllUsers().join();
        assertEquals(newUsers.size(), currentUsers.size());
        assertTrue(currentUsers.containsAll(newUsers));
    }
}
