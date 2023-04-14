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
package games.cultivate.mcmmocredits.user;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ConsoleTest {

    private Console console;

    @BeforeEach
    void setUp() {
        //Arrange
        this.console = Console.INSTANCE;
    }

    @Test
    void isPlayer_ReturnsFalse() {
        // Act
        boolean isPlayer = this.console.isPlayer();

        // Assert
        assertFalse(isPlayer);
    }

    @Test
    void isConsole_ReturnsTrue() {
        // Act
        boolean isConsole = this.console.isConsole();

        // Assert
        assertTrue(isConsole);
    }

    @Test
    void sender_ReturnsConsoleSender() {
        //Arrange
        try (MockedStatic<Bukkit> mockedBukkit = Mockito.mockStatic(Bukkit.class)) {
            ConsoleCommandSender sender = mock(ConsoleCommandSender.class);
            mockedBukkit.when(Bukkit::getConsoleSender).thenReturn(sender);

            //Act
            CommandSender test = this.console.sender();

            //Assert
            assertNotNull(test);
            assertTrue(test instanceof ConsoleCommandSender);
        }
    }

    @Test
    void player_ThrowsUnsupportedOperationException() {
        // Act and Assert
        assertThrows(UnsupportedOperationException.class, this.console::player);
    }
}
