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
package games.cultivate.mcmmocredits.converter;

import games.cultivate.mcmmocredits.converters.Converter;
import games.cultivate.mcmmocredits.converters.ConverterProperties;
import games.cultivate.mcmmocredits.converters.DataLoadingStrategy;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.database.DatabaseProperties;
import games.cultivate.mcmmocredits.database.DatabaseUtil;
import games.cultivate.mcmmocredits.user.User;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InternalConverterTest {
    private final Database oldDatabase = DatabaseUtil.create();
    private final Database currentDatabase = DatabaseUtil.create("jdbc:h2:mem:testdb2;DB_CLOSE_DELAY=-1;MODE=MYSQL");

    @Test
    void run_ValidUsers_ConvertsUsersCorrectly() {
        DatabaseProperties dproperties = mock(DatabaseProperties.class);
        when(dproperties.create(any())).thenReturn(this.oldDatabase);
        this.oldDatabase.addUser(new User(new UUID(0, 0), "tester0", 0, 0));
        this.oldDatabase.addUser(new User(new UUID(1, 1), "tester1", 10, 10));
        this.oldDatabase.addUser(new User(new UUID(2, 2), "tester2", 20, 20));
        List<User> users = this.oldDatabase.getAllUsers();
        ConverterProperties properties = new ConverterProperties(DataLoadingStrategy.INTERNAL, dproperties, 60000L, 300L, false);
        Converter converter = new Converter(properties, this.currentDatabase, Path.of(""));
        assertTrue(converter.run());
        assertEquals(users.size(), this.currentDatabase.getAllUsers().size());
        assertTrue(this.currentDatabase.getAllUsers().containsAll(users));
    }
}
