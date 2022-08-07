package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.config.MenuConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.paper.element.ItemStackElement;

import java.util.List;

public final class MainMenu extends BaseMenu {
    public MainMenu(final MenuConfig menu, final Player player) {
        super(menu, player, "main");
    }

    @Override
    public void applySpecialItems() {
        List<String> menus = List.of("messages", "settings", "redeem");
        this.transformations.add(TransformContext.of(0, (pane, view) -> {
            for (String type : menus) {
                if (this.player.hasPermission("mcmmocredits.menu." + type)) {
                    String menuPath = "main.items." + type;
                    int slot = this.menu.slot(menuPath);
                    pane = pane.element(ItemStackElement.of(this.menu.item(menuPath, this.player), click -> {
                        if (click.cause().isLeftClick()) {
                            Bukkit.dispatchCommand(this.player, "credits menu " + type);
                        }
                    }), slot % 9, slot / 9);
                }
            }
            return pane;
        }));
    }
}
