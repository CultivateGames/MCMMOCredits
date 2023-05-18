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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class TextTest {
    private final String testContent = "Test content";
    private final Resolver resolver = new Resolver();
    private CommandSender audience;
    private CommandExecutor executor;

    @BeforeEach
    void setUp() {
        this.audience = mock(CommandSender.class);
        this.executor = spy(new User(UUID.randomUUID(), "testUsername", 100, 10));
        doReturn(this.audience).when(this.executor).sender();
    }

    @Test
    void fromString_AudienceContentResolver_SendsMessageToAudience() {
        Text.fromString(this.audience, this.testContent, this.resolver).send();
        verify(this.audience).sendMessage(any(Component.class));
    }

    @Test
    void fromString_CommandExecutorContentResolver_SendsMessageToAudience() {
        Text.fromString(this.executor, this.testContent, this.resolver).send();
        verify(this.audience).sendMessage(any(Component.class));
    }

    @Test
    void forOneUser_CommandExecutorContent_SendsMessageToAudience() {
        Text.forOneUser(this.executor, this.testContent).send();
        verify(this.audience).sendMessage(any(Component.class));
    }

    @Test
    void toComponent_AudienceContentResolver_ReturnsParsedComponent() {
        String content = "<sender> <credits> <redeemed>!";
        Component component = Text.fromString(this.audience, content, this.resolver).toComponent();
        Component expected = Component.empty().decoration(TextDecoration.ITALIC, false).append(Component.text("<sender> <credits> <redeemed>!"));
        assertEquals(expected, component);
    }

    @Test
    void send_AudienceContentResolver_SendsMessageToAudience() {
        Text.fromString(this.audience, this.testContent, this.resolver).send();
        verify(this.audience).sendMessage(any(Component.class));
    }
}
