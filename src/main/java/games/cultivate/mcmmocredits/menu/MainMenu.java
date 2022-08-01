package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.core.transform.Transform;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.type.ChestInterface;

import java.util.ArrayList;
import java.util.List;

public final class MainMenu extends Menu {
    private final MenuConfig menu;
    private final Player player;

    public MainMenu(final MenuConfig menu, final MCMMOCredits plugin, final Player player) {
        this.menu = menu;
        this.plugin = plugin;
        this.player = player;
        this.viewer = PlayerViewer.of(player);
        this.chest = this.create();
    }

    @Override
    public void open() {
        if (this.chest == null) {
            throw new IllegalStateException("Chest Interface has not been created!");
        }
        this.chest.open(this.viewer);
    }

    @Override
    public void close() {
        if (this.chest == null) {
            throw new IllegalStateException("Chest Interface has not been created!");
        }
        this.viewer.close();
    }

    public Transform<ChestPane, PlayerViewer> itemTransform() {
        String redeemPath = "main.items.redeem";
        String messagesPath = "main.items.messages";
        String settingsPath = "main.items.settings";
        return (pane, view) -> {
            pane = this.createCommandTransform(pane, redeemPath, this.pathCommand(redeemPath));
            if (this.player.hasPermission("mcmmocredits.Menu.admin")) {
                pane = this.createCommandTransform(pane, messagesPath, this.pathCommand(messagesPath));
                pane = this.createCommandTransform(pane, settingsPath, this.pathCommand(settingsPath));
            }
            return pane;
        };
    }

    @Override
    public ChestInterface create() {
        Component title = Text.fromString(this.player, this.menu.string("main.info.title")).toComponent();
        int rows = this.menu.integer("main.info.size") / 9;
        List<TransformContext<ChestPane, PlayerViewer>> transforms = new ArrayList<>();
        transforms.add(TransformContext.of(1, this.itemTransform()));
        if (this.menu.bool("all.fill")) {
            transforms.add(TransformContext.of(0, this.fillTransform(this.menu, this.player)));
        }
        return new ChestInterface(rows, title, transforms, List.of(), true, 10, ClickHandler.cancel());
    }

    private String pathCommand(final String path) {
        return "credits menu " + path.substring(path.lastIndexOf('.') + 1);
    }

    private ChestPane createCommandTransform(final ChestPane pane, final String itemPath, final String command) {
        return pane.element(ItemStackElement.of(this.menu.item(itemPath, this.player), click -> {
            if (click.cause().isLeftClick()) {
                Bukkit.getScheduler().runTask(this.plugin, () -> Bukkit.dispatchCommand(this.player, command));
            }
        }), this.menu.slot(itemPath) % 9, this.menu.slot(itemPath) / 9);
    }
}
