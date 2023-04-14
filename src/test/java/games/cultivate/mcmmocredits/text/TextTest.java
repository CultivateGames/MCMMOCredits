package games.cultivate.mcmmocredits.text;

import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TextTest {
    private CommandSender audience;
    private Resolver resolver;
    private CommandExecutor executor;

    @BeforeEach
    void setUp() {
        this.audience = mock(CommandSender.class);
        this.resolver = new Resolver();
        this.executor = mock(User.class);
        when(this.executor.credits()).thenReturn(0);
        when(this.executor.username()).thenReturn("testUsername");
        when(this.executor.redeemed()).thenReturn(0);
        when(this.executor.uuid()).thenReturn(UUID.randomUUID());
        when(this.executor.sender()).thenReturn(this.audience);
    }

    @Test
    void fromString_AudienceContentResolver_SendsMessageToAudience() {
        // Arrange
        Text text = Text.fromString(this.audience, "Test content", this.resolver);

        // Act
        text.send();

        // Assert
        verify(this.audience).sendMessage(any(Component.class));
    }

    @Test
    void fromString_CommandExecutorContentResolver_SendsMessageToAudience() {
        // Arrange
        Text text = Text.fromString(this.executor, "Test content", this.resolver);

        // Act
        text.send();

        // Assert
        verify(this.audience).sendMessage(any(Component.class));
    }

    @Test
    void forOneUser_CommandExecutorContent_SendsMessageToAudience() {
        // Arrange
        Text text = Text.forOneUser(this.executor, "Test content");

        // Act
        text.send();

        // Assert
        verify(this.audience).sendMessage(any(Component.class));
    }

    @Test
    void toComponent_AudienceContentResolver_ReturnsComponent() {
        // Arrange
        Text text = Text.fromString(this.audience, "Test content", this.resolver);

        // Act
        Component component = text.toComponent();

        // Assert
        Component expectedComponent = Component.empty()
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text("Test content"));
        assertEquals(expectedComponent, component);
    }

    @Test
    void send_AudienceContentResolver_SendsMessageToAudience() {
        // Arrange
        Text text = Text.fromString(this.audience, "Test content", this.resolver);

        // Act
        text.send();

        // Assert
        verify(this.audience).sendMessage(any(Component.class));
    }
}
