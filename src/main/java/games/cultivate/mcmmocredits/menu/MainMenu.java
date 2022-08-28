package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.placeholders.ResolverFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;

public final class MainMenu extends BaseMenu {

    MainMenu(final MenuConfig menu, final ResolverFactory resolverFactory, final Player player, final MCMMOCredits plugin) {
        super(menu, resolverFactory, player, plugin, "main");
    }

    @Override
    public void applySpecialItems() {
        this.transformations.add(this.mainTransformContext("config"));
        this.transformations.add(this.mainTransformContext("redeem"));
    }

    private TransformContext<ChestPane, PlayerViewer> mainTransformContext(final String type) {
        return TransformContext.of(0, (pane, view) -> {
            if (this.player.hasPermission("mcmmocredits.menu." + type)) {
                String menuPath = "main.items." + type;
                int slot = this.menu.slot(menuPath);
                pane = pane.element(ItemStackElement.of(this.menu.item(menuPath, this.player, this.resolverFactory), click -> {
                    if (click.cause().isLeftClick()) {
                        Bukkit.getScheduler().callSyncMethod(this.plugin, () -> {
                            Bukkit.dispatchCommand(this.player, "credits menu " + type);
                            return this;
                        });
                    }
                }), slot % 9, slot / 9);
            }
            return pane;
        });
    }
}
