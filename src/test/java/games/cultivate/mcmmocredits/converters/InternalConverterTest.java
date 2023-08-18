//
// MIT License
//
// Copyright (c) 2023 Cultivate Games
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

import games.cultivate.mcmmocredits.database.AbstractDatabase;
import games.cultivate.mcmmocredits.database.DatabaseUtil;
import games.cultivate.mcmmocredits.user.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InternalConverterTest {
    private final AbstractDatabase oldDatabase = DatabaseUtil.create("test");
    private final AbstractDatabase currentDatabase = DatabaseUtil.create("test1");

    @Test
    void run_ValidUsers_ConvertsUsersCorrectly() {
        this.oldDatabase.addUser(new User(new UUID(0, 0), "tester0", 0, 0));
        this.oldDatabase.addUser(new User(new UUID(1, 1), "tester1", 10, 10));
        this.oldDatabase.addUser(new User(new UUID(2, 2), "tester2", 20, 20));
        List<User> users = this.oldDatabase.getAllUsers();
        Converter converter = new InternalConverter(this.currentDatabase, this.oldDatabase);
        assertTrue(converter.run());
        List<User> currentUsers = this.currentDatabase.getAllUsers();
        assertEquals(users.size(), currentUsers.size());
        assertTrue(currentUsers.containsAll(users));
    }
}
