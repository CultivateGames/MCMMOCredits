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

import games.cultivate.mcmmocredits.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommandSourceDataTest {
    @Test
    void new_InvalidState_ThrowsException() {
        var badName = "888888888888888888888";
        var badUUID = new UUID(0, 0);
        var badCredits = -1;
        var badRedeemed = -2;
        var badNameMessage = "Username is malformed! Username: " + badName;
        var badUUIDMessage = "UUID is malformed! UUID: " + badUUID;
        var badCreditsMessage = "Credits should never be negative! Credits: " + badCredits;
        var badRedeemedMessage = "Redeemed Credits should never be negative! Redeemed Credits: " + badRedeemed;
        assertThrows(MalformedUserException.class, () -> new CommandSourceData(badUUID, "111", 0, 0), badUUIDMessage);
        CommandSourceData user = TestUtils.createUser();
        assertThrows(MalformedUserException.class, () -> user.withName(badName), badNameMessage);
        assertThrows(MalformedUserException.class, () -> user.withCredits(badCredits), badCreditsMessage);
        assertThrows(MalformedUserException.class, () -> new CommandSourceData(user.uuid(), "111", 0, badRedeemed), badRedeemedMessage);
    }


    @Test
    void withCredits_ReturnsSameUserWithUpdatedCredits() {
        CommandSourceData user = TestUtils.createUser();
        int newCredits = 200;
        CommandSourceData updatedUser = user.withCredits(newCredits);
        assertNotEquals(user, updatedUser);
        assertEquals(newCredits, updatedUser.credits());
        assertEquals(user.uuid(), updatedUser.uuid());
        assertEquals(user.username(), updatedUser.username());
        assertEquals(user.redeemed(), updatedUser.redeemed());
    }

    @Test
    void redeemedCredits_ReturnsSameUserWithUpdatedCredits() {
        CommandSourceData user = TestUtils.createUser();
        int newCredits = user.credits() - 200;
        int newRedeemedCredits = user.redeemed() + 200;
        CommandSourceData updatedUser = user.redeemCredits(200);
        assertNotEquals(user, updatedUser);
        assertEquals(newCredits, updatedUser.credits());
        assertEquals(newRedeemedCredits, updatedUser.redeemed());
        assertEquals(user.uuid(), updatedUser.uuid());
        assertEquals(user.username(), updatedUser.username());
    }

    @Test
    void withName_ReturnsSameUserWithUpdatedUsername() {
        CommandSourceData user = TestUtils.createUser();
        String newUsername = "UpdatedUser";
        CommandSourceData updatedUser = user.withName(newUsername);
        assertNotEquals(user, updatedUser);
        assertEquals(newUsername, updatedUser.username());
        assertEquals(user.uuid(), updatedUser.uuid());
        assertEquals(user.credits(), updatedUser.credits());
        assertEquals(user.redeemed(), updatedUser.redeemed());
    }
}
