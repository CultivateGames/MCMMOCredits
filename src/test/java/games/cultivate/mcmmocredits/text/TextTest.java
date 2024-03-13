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
package games.cultivate.mcmmocredits.text;

import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TextTest {
    private final String testContent = "Test content";
    private final Resolver resolver = new Resolver();
    @Spy
    private final CommandExecutor executor = new User(UUID.randomUUID(), "testUsername", 100, 10);
    @Mock
    private CommandSender audience;

    @Test
    void fromString_AudienceContentResolver_SendsMessageToAudience() {
        Text.fromString(this.audience, this.testContent, this.resolver).send();
        verify(this.audience).sendMessage(any(Component.class));
    }

    @Test
    void fromString_CommandExecutorContentResolver_SendsMessageToAudience() {
        doReturn(this.audience).when(this.executor).sender();
        Text.fromString(this.executor, this.testContent, this.resolver).send();
        verify(this.audience).sendMessage(any(Component.class));
    }

    @Test
    void forOneUser_CommandExecutorContent_SendsMessageToAudience() {
        doReturn(this.audience).when(this.executor).sender();
        Text.forOneUser(this.executor, this.testContent).send();
        verify(this.audience).sendMessage(any(Component.class));
    }

    @Test
    void forOneUser_CustomizedResolver_CorrectMessage() {
        doReturn(this.audience).when(this.executor).sender();
        Text.forOneUser(this.executor, this.testContent + "<test_resolver>", r -> r.addTag("test_resolver", 69));
        Component component = Text.forOneUser(this.executor, this.testContent + "<test_resolver>", r -> r.addTag("test_resolver", 69)).toComponent();
        Component expected = Component.empty().decoration(TextDecoration.ITALIC, false).append(Component.text(this.testContent + 69));
        assertEquals(expected, component);
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
