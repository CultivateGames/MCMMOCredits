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
import games.cultivate.mcmmocredits.converters.StorageLoader;
import games.cultivate.mcmmocredits.converters.UserLoader;
import games.cultivate.mcmmocredits.storage.StorageService;
import games.cultivate.mcmmocredits.user.CommandSourceData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StorageLoaderTest {
    private final StorageService service = TestUtils.create();

    @AfterEach
    void tearDown() {
        TestUtils.deleteDatabase(this.service);
        this.service.disable();
    }

    @Test
    void getUsers_ValidData_ReturnsAllAndDisablesConnection() {
        List<CommandSourceData> users = TestUtils.createUsers(10);
        this.service.addUsers(users);
        UserLoader loader = new StorageLoader(this.service);
        assertEquals(users, loader.getUsers());
        assertThrows(CompletionException.class, loader::getUsers);
    }
}
