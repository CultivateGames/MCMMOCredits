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
package games.cultivate.mcmmocredits.storage;

import games.cultivate.mcmmocredits.TestUtils;
import games.cultivate.mcmmocredits.user.CommandSourceData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StorageServiceTest {
    private CommandSourceData user;
    private final StorageService service = TestUtils.create();

    @BeforeEach
    void setUp() {
        this.user = TestUtils.createUser();
    }

    @AfterEach
    void tearDown() {
        TestUtils.deleteDatabase(this.service);
    }

    @Test
    void addUser_NewUser_UserAdded() {
        this.service.addUser(this.user);
        assertEquals(this.user, this.service.getUser(this.user.uuid()).orElseThrow());
    }

    @Test
    void addUsers_NewUsers_UsersAdded() {
        List<CommandSourceData> users = TestUtils.createUsers(2);
        var first = users.getFirst();
        var second = users.getLast();
        this.service.addUsers(users);
        assertEquals(first, this.service.getUser(first.uuid()).orElseThrow());
        assertEquals(second, this.service.getUser(second.uuid()).orElseThrow());
    }

    @Test
    void getAllUsers_ReturnAllUsers() {
        List<CommandSourceData> list = TestUtils.createUsers(2);
        this.service.addUsers(list);
        List<CommandSourceData> users = this.service.getAllUsers();
        assertTrue(users.containsAll(list));
    }

    @Test
    void getUser_ByUsername_UserFound() {
        this.service.addUser(this.user);
        Optional<CommandSourceData> ouser = this.service.getUser(this.user.username());
        assertEquals(this.user, ouser.orElseThrow());
    }

    @Test
    void getUser_MissingUser_ReturnsEmpty() {
        assertTrue(this.service.getUser(UUID.randomUUID()).isEmpty());
    }

    @Test
    void getUserGroup_ThreeUsers_UsersRetrieved() {
        CommandSourceData first = new CommandSourceData(UUID.randomUUID(), "firstPlace", 1000, 10);
        CommandSourceData second = new CommandSourceData(UUID.randomUUID(), "secondPlace", 100, 10);
        this.service.addUsers(List.of(first, second, this.user));
        List<CommandSourceData> users = this.service.getUserGroup(3, 0);
        assertTrue(users.containsAll(List.of(first, second, this.user)));
    }

    @Test
    void updateUser_ExistingUser_ReturnsUpdatedUser() {
        this.service.addUser(this.user);
        var updated = this.user.withCredits(10000);
        this.service.updateUser(updated);
        var fromStorage = this.service.getUser(this.user.uuid()).orElseThrow();
        assertEquals(10000, fromStorage.credits());
    }
}
