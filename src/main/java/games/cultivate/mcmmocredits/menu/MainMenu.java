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

    //TODO remove string type param
    public Transform<ChestPane, PlayerViewer> itemTransform() {
        List<String> menus = List.of("messages", "settings", "redeem");
        return (pane, view) -> {
            for (String type : menus) {
                if (this.player.hasPermission("mcmmocredits.menu." + type)) {
                    String menuPath = "main.items" + type;
                    int slot = this.menu.slot(menuPath);
                    pane = pane.element(ItemStackElement.of(this.menu.item(menuPath, this.player), click -> {
                        if (click.cause().isLeftClick()) {
                            String command = "credits menu " + type;
                            Bukkit.getScheduler().runTask(this.plugin, () -> Bukkit.dispatchCommand(this.player, command));
                        }
                    }), slot % 9, slot / 9);
                }
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

    //TODO remove String type param
    private ChestPane createCommandTransform(final ChestPane pane, String type) {
        String menuPath = "main.items" + type;
        return pane.element(ItemStackElement.of(this.menu.item(menuPath, this.player), click -> {
            if (click.cause().isLeftClick()) {
                Bukkit.getScheduler().runTask(this.plugin, () -> Bukkit.dispatchCommand(this.player, "credits menu " + type));
            }
        }), this.menu.slot(menuPath) % 9, this.menu.slot(menuPath) / 9);
    }
}
