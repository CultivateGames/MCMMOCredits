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
package games.cultivate.mcmmocredits.menu;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.MainData;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.util.ChatQueue;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.incendo.interfaces.core.arguments.ArgumentKey;
import org.incendo.interfaces.core.arguments.HashMapInterfaceArguments;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.core.view.InterfaceView;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.configurate.NodePath;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActionTest {
    @Mock
    private Player mockPlayer;
    @Mock
    private ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> mockContext;
    @Mock
    private Config<MainData> mockConfig;
    @Mock
    private ChatQueue mockQueue;
    @Mock
    private User mockUser;
    @Mock
    private MCMMOCredits mockPlugin;
    @Mock
    private InterfaceView<ChestPane, PlayerViewer> mockView;

    @BeforeEach
    void setUp() {
        HashMapInterfaceArguments args = HashMapInterfaceArguments.with(ArgumentKey.of("config"), this.mockConfig)
                .with(ArgumentKey.of("queue"), this.mockQueue)
                .with(ArgumentKey.of("plugin"), this.mockPlugin)
                .with(ArgumentKey.of("user"), this.mockUser)
                .build();
        when(this.mockContext.view()).thenReturn(this.mockView);
        when(this.mockContext.view().arguments()).thenReturn(args);
        when(this.mockContext.viewer()).thenReturn(PlayerViewer.of(this.mockPlayer));
    }

    @Test
    void execute_executesQueueAction() {
        when(this.mockUser.uuid()).thenReturn(new UUID(0, 0));
        when(this.mockUser.credits()).thenReturn(0);
        when(this.mockUser.redeemed()).thenReturn(0);
        when(this.mockUser.username()).thenReturn("tester");
        Action action = Action.editConfig(NodePath.of(List.of("settings", "debug")));
        action.accept(this.mockContext);
        verify(this.mockQueue).act(any(UUID.class), any(Consumer.class));
    }

    @Test
    void execute_executesTransaction() {
        when(this.mockUser.uuid()).thenReturn(new UUID(0, 0));
        Action action = Action.redeem(PrimarySkillType.HERBALISM);
        action.accept(this.mockContext);
        verify(this.mockQueue).act(any(UUID.class), any(Consumer.class));
    }
}