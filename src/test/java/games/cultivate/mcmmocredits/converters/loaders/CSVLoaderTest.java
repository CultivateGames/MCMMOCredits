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

import games.cultivate.mcmmocredits.TestUtils;
import games.cultivate.mcmmocredits.converters.CSVLoader;
import games.cultivate.mcmmocredits.converters.UserLoader;
import games.cultivate.mcmmocredits.user.CommandSourceData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CSVLoaderTest {
    @TempDir(cleanup = CleanupMode.ALWAYS)
    private Path temporaryPath;
    private Path filePath;
    private List<CommandSourceData> users;

    @BeforeEach
    public void setUp() throws IOException {
        this.filePath = this.temporaryPath.resolve("database.csv");
        this.users = TestUtils.createUsers(500);
        Files.write(this.filePath, this.users.stream().map(CommandSourceData::toString).toList());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.delete(this.filePath);
    }

    @Test
    void getUsers_returnsAllUsers() {
        UserLoader loader = new CSVLoader(this.filePath);
        assertEquals(users, loader.getUsers());
    }
}
