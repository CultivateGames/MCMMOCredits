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
package games.cultivate.mcmmocredits;

import games.cultivate.mcmmocredits.storage.StorageService;
import games.cultivate.mcmmocredits.user.CommandSourceData;
import games.cultivate.mcmmocredits.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MCMMOCreditsAPITest {
    private MCMMOCreditsAPI api;
    private CommandSourceData user;
    private StorageService storageService;
    private UserService service;
    @TempDir(cleanup = CleanupMode.ALWAYS)
    private Path temporaryPath;

    @BeforeEach
    void setUp() {
        this.storageService = TestUtils.create();
        this.service = new UserService(this.storageService);
        this.api = new MCMMOCreditsAPI(this.service);
        this.user = TestUtils.createUser();
    }

    @AfterEach
    void tearDown() {
        TestUtils.deleteDatabase(this.storageService);
    }

    @Test
    void getCredits_ValidUser_ReturnsCredits() {
        this.service.addUser(this.user);
        assertEquals(this.user.credits(), this.api.getCredits(this.user.uuid()));
    }

    @Test
    void getCredits_InvalidUser_ReturnsZero() {
        assertEquals(0, this.api.getCredits(UUID.randomUUID()));
    }

    @Test
    void addCredits_ValidUser_ReturnsUpdatedCredits() {
        this.service.addUser(this.user);
        assertTrue(this.api.addCredits(this.user.uuid(), 200));
        assertEquals(this.user.credits() + 200, this.api.getCredits(this.user.uuid()));
    }

    @Test
    void addCredits_InvalidUser_ReturnsFalse() {
        UUID ruuid = UUID.randomUUID();
        assertFalse(this.api.addCredits(ruuid, 2030));
        assertEquals(0, this.api.getCredits(ruuid));
    }

    @Test
    void setCredits_ValidUser_ReturnsUpdatedCredits() {
        this.service.addUser(this.user);
        assertTrue(this.api.setCredits(this.user.uuid(), 250));
        assertEquals(250, this.api.getCredits(this.user.uuid()));
    }

    @Test
    void setCredits_InvalidUser_ReturnsFalse() {
        UUID ruuid = UUID.randomUUID();
        assertFalse(this.api.setCredits(ruuid, 2030));
        assertEquals(0, this.api.getCredits(ruuid));
    }

    @Test
    void takeCredits_ValidUser_ReturnsUpdatedCredits() {
        this.service.addUser(this.user);
        assertTrue(this.api.takeCredits(this.user.uuid(), 20));
        assertEquals(this.user.credits() - 20, this.api.getCredits(this.user.uuid()));
    }

    @Test
    void takeCredits_InvalidUser_ReturnsFalse() {
        UUID ruuid = UUID.randomUUID();
        assertFalse(this.api.takeCredits(ruuid, 10));
        assertEquals(0, this.api.getCredits(ruuid));
    }
}
