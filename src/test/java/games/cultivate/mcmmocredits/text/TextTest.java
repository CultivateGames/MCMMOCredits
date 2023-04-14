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
    private final String testContent = "Test content";

    @BeforeEach
    void setUp() {
        this.audience = mock(CommandSender.class);
        this.resolver = new Resolver();
        //Mock the user to return our command sender.
        this.executor = mock(User.class);
        when(this.executor.credits()).thenReturn(0);
        when(this.executor.username()).thenReturn("testUsername");
        when(this.executor.redeemed()).thenReturn(0);
        when(this.executor.uuid()).thenReturn(UUID.randomUUID());
        when(this.executor.sender()).thenReturn(this.audience);
    }

    @Test
    void fromString_AudienceContentResolver_SendsMessageToAudience() {
        //Arrange
        Text text = Text.fromString(this.audience, this.testContent, this.resolver);

        //Act
        text.send();

        //Assert
        verify(this.audience).sendMessage(any(Component.class));
    }

    @Test
    void fromString_CommandExecutorContentResolver_SendsMessageToAudience() {
        //Arrange
        Text text = Text.fromString(this.executor, this.testContent, this.resolver);

        //Act
        text.send();

        //Assert
        verify(this.audience).sendMessage(any(Component.class));
    }

    @Test
    void forOneUser_CommandExecutorContent_SendsMessageToAudience() {
        //Arrange
        Text text = Text.forOneUser(this.executor, this.testContent);

        //Act
        text.send();

        //Assert
        verify(this.audience).sendMessage(any(Component.class));
    }

    @Test
    void toComponent_AudienceContentResolver_ReturnsComponent() {
        //Arrange
        Text text = Text.fromString(this.audience, this.testContent, this.resolver);

        //Act
        Component component = text.toComponent();

        //Assert
        Component expectedComponent = Component.empty().decoration(TextDecoration.ITALIC, false).append(Component.text(this.testContent));
        assertEquals(expectedComponent, component);
    }

    @Test
    void send_AudienceContentResolver_SendsMessageToAudience() {
        //Arrange
        Text text = Text.fromString(this.audience, this.testContent, this.resolver);

        //Act
        text.send();

        //Assert
        verify(this.audience).sendMessage(any(Component.class));
    }
}
