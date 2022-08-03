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
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

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

    public Transform<ChestPane, PlayerViewer> itemTransform() {
        Set<MenuType> set = EnumSet.allOf(MenuType.class);
        set.remove(MenuType.MAIN);
        return (pane, view) -> {
            for (MenuType type : set) {
                if (type.canOpen(this.player)) {
                    pane = this.createCommandTransform(pane, type);
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

    private ChestPane createCommandTransform(final ChestPane pane, final MenuType menuType) {
        String menuPath = "main.items" + menuType.name().toLowerCase();
        return pane.element(ItemStackElement.of(this.menu.item(menuPath, this.player), click -> {
            if (click.cause().isLeftClick()) {
                Bukkit.getScheduler().runTask(this.plugin, () -> Bukkit.dispatchCommand(this.player, "credits menu " + menuType));
            }
        }), this.menu.slot(menuPath) % 9, this.menu.slot(menuPath) / 9);
    }
}
