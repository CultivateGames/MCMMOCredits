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
package games.cultivate.mcmmocredits.converters.loaders;

import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CSVLoaderTest {
    private static final Path FILE_PATH = Path.of("src", "test", "resources", "database.csv");

    @AfterEach
    void tearDown() throws IOException {
        Files.delete(FILE_PATH);
    }

    @Test
    void getUsers_returnsAllUsers() throws IOException {
        List<User> users = UserCreator.createUsers(500);
        List<String> lines = users.stream().map(u -> u.uuid() + "," + u.username() + "," + u.credits() + "," + u.redeemed()).toList();
        Files.write(FILE_PATH, lines);
        UserLoader loader = new CSVLoader(FILE_PATH);
        assertEquals(users, loader.getUsers());
    }
}
