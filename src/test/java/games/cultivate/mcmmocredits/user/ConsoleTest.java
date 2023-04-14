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
            mockedBukkit.when(() -> Bukkit.getConsoleSender()).thenReturn(sender);

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