package games.cultivate.mcmmocredits.actions;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.MainConfig;
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
    private MainConfig mockConfig;
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
        ConfigAction action = new ConfigAction(NodePath.of(List.of("settings", "debug")));
        action.execute(this.mockContext);
        verify(this.mockQueue).act(any(UUID.class), any(Consumer.class));
    }

    @Test
    void execute_executesCommand() {
        CommandAction action = new CommandAction("help");
        action.execute(this.mockContext);
        verify(this.mockPlugin).execute(any(Runnable.class));
    }

    @Test
    void execute_executesTransaction() {
        when(this.mockUser.uuid()).thenReturn(new UUID(0, 0));
        RedeemAction action = new RedeemAction(PrimarySkillType.HERBALISM);
        action.execute(this.mockContext);
        verify(this.mockQueue).act(any(UUID.class), any(Consumer.class));
    }
}