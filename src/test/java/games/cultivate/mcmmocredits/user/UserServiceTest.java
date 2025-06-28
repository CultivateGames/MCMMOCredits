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
package games.cultivate.mcmmocredits.user;

import com.google.gson.JsonParser;
import games.cultivate.mcmmocredits.storage.StorageService;
import games.cultivate.mcmmocredits.TestUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private StorageService storageService;
    private CommandSourceData user;
    private UserService service;
    @Mock
    private MockedStatic<Bukkit> mockBukkit;
    @Mock
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        this.user = TestUtils.createUser();
        this.storageService = TestUtils.create();
        this.service = new UserService(this.storageService);
    }

    @AfterEach
    void tearDown() {
        TestUtils.deleteDatabase(this.storageService);
    }

    @Test
    void getUser_CachedUser_ReturnsUser() {
        this.service.addUser(this.user);
        Optional<CommandSourceData> result = this.service.getUser(this.user.uuid());
        assertEquals(this.user, result.orElseThrow());
    }

    @Test
    void getUser_NotCachedUser_ReturnsUser() {
        this.storageService.addUser(this.user);
        Optional<CommandSourceData> result = this.service.getUser(this.user.uuid());
        assertEquals(this.user, result.orElseThrow());
    }

    @Test
    void getUser_NonExistentUser_ReturnsEmptyOptional() {
        Optional<CommandSourceData> result = this.service.getUser(UUID.randomUUID());
        assertFalse(result.isPresent());
    }

    @Test
    void getCredits_UserIsCached_ReturnsCredits() {
        this.service.addUser(this.user);
        Optional<CommandSourceData> result = this.service.getUser(this.user.uuid());
        assertTrue(result.isPresent());
        assertEquals(this.user.credits(), result.orElseThrow().credits());
    }

    @Test
    void getCredits_UserIsNotCached_ReturnsCredits() {
        this.storageService.addUser(this.user);
        Optional<CommandSourceData> result = this.service.getUser(this.user.uuid());
        assertTrue(result.isPresent());
        assertEquals(this.user.credits(), result.orElseThrow().credits());
    }

    @Test
    void getCredits_UserDoesNotExist_ReturnsZero() {
        assertEquals(0, this.service.getUser(this.user.uuid()).map(CommandSourceData::credits).orElse(0));
    }

    @Test
    void setCredits_UserExists_ReturnsUpdatedUser() {
        this.service.addUser(this.user);
        this.service.updateUser(this.user.withCredits(200));
        assertEquals(200, this.service.getUser(this.user.uuid()).orElseThrow().credits());
    }

    @Test
    void getUserGroup_ReturnsPageOfUsers() {
        List<CommandSourceData> users = TestUtils.createUsers(2);
        users.forEach(user -> this.service.addUser(user));
        List<CommandSourceData> result = this.service.getUserGroup(2, 0);
        assertEquals(users, result);
    }

    @Test
    void getOnlineUsers_ValidPlayers_GetsAll() {
        this.service.addUser(this.user);
        this.mockBukkit.when(() -> Bukkit.getPlayer(this.user.uuid())).thenReturn(this.mockPlayer);
        this.mockBukkit.when(Bukkit::getOnlinePlayers).thenReturn(List.of(this.mockPlayer));
        when(this.mockPlayer.getUniqueId()).thenReturn(this.user.uuid());
        List<CommandSourceData> users = new ArrayList<>(this.service.getOnlineUsers());
        assertEquals(this.user.uuid(), users.getFirst().uuid());
    }

    @Test
    void setUsername_UsernameConflict_OlderUserReplaced() {
        //jeb_: 853c80ef-3c37-49fd-aa49-938b674adae6
        //Notch: 069a79f4-44e9-4726-a5be-fca90e38aaf5
        CommandSourceData notch = new CommandSourceData(UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5"), "jeb_", 100, 100);
        CommandSourceData jeb = new CommandSourceData(UUID.fromString("853c80ef-3c37-49fd-aa49-938b674adae6"), "Notch", 500, 500);
        this.storageService.addUser(notch);
        this.storageService.addUser(jeb);
        this.service.updateUser(jeb.withName("jeb_"));
        this.service.updateUser(notch.withName("Notch"));
        assertEquals("Notch", this.service.getUser(notch.uuid()).map(CommandSourceData::username).orElse(""));
        assertEquals("jeb_", this.service.getUser(jeb.uuid()).map(CommandSourceData::username).orElse(""));
    }

    @Test
    void setUsername_UsernameUpdated_ReturnsUpdatedUser() {
        this.storageService.addUser(this.user);
        this.service.updateUser(this.user.withName("newUsername"));
        assertEquals("newUsername", this.service.getUser(this.user.uuid()).orElseThrow().username());
    }

    @Test
    void setUsername_CaseSensitivityIssue_OlderUserUpdated() throws IOException, InterruptedException {
        UUID fakeUUID = UUID.fromString("853c80ef-3c37-49fd-aa49-938b674adae6");
        UUID originalUUID = UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5");
        CommandSourceData fakeNotch = new CommandSourceData(fakeUUID, "nOtCh", 100, 100);
        CommandSourceData notNotch =new CommandSourceData(originalUUID, "notNotch", 100, 10);
        this.storageService.addUser(fakeNotch);
        this.storageService.addUser(notNotch);
        this.service.updateUser(notNotch.withName("Notch"));
        Optional<CommandSourceData> updatedFakeUser = this.service.getUser(fakeUUID);
        String username = this.getUsername(fakeUUID).orElseThrow();
        assertEquals(username, updatedFakeUser.orElseThrow().username());
        Optional<CommandSourceData> originalUser = this.service.getUser(originalUUID);
        assertEquals("Notch", originalUser.orElseThrow().username());
    }

    private Optional<String> getUsername(final UUID uuid) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid)).build();
        try (var client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return Optional.of(JsonParser.parseString(response.body()).getAsJsonObject().get("name").getAsString());
            }
        }
        return Optional.empty();
    }
}
