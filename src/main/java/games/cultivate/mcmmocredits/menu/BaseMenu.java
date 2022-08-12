package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.core.util.Vector2;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.type.ChestInterface;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseMenu implements Menus {
    protected final List<TransformContext<ChestPane, PlayerViewer>> transformations;
    protected final MenuConfig menu;
    protected final Player player;
    private final Component title;
    private final int rows;
    private ChestInterface chest;

    BaseMenu(final MenuConfig menu, final Player player, final Component title, final int rows) {
        this.transformations = new ArrayList<>();
        this.menu = menu;
        this.player = player;
        this.title = title;
        this.rows = rows;
    }

    BaseMenu(final MenuConfig menu, final Player player, final String path) {
        this(menu, player, Text.fromString(player, menu.string(path + ".info.title")).toComponent(), menu.integer(path + ".info.size") / 9);
    }

    public abstract void applySpecialItems();

    @Override
    public ChestInterface chest() {
        if (this.chest == null) {
            this.load();
        }
        return this.chest;
    }

    @Override
    public PlayerViewer viewer() {
        return PlayerViewer.of(this.player);
    }

    public void applyFillItems() {
        if (this.menu.bool("all.navigation")) {
            String path = "main.items.navigation";
            this.transformations.add(TransformContext.of(2, (pane, view) -> {
                pane = pane.element(ItemStackElement.of(this.menu.item(path, this.player), click -> {
                    if (click.click().leftClick()) {
                        Bukkit.dispatchCommand(this.player, "credits menu");
                    }
                }), this.menu.slot(path) % 9, this.menu.slot(path) / 9);
                return pane;
            }));
        }
        if (this.menu.bool("all.fill")) {
            ItemStackElement<ChestPane> filler = ItemStackElement.of(this.menu.item("main.items.fill", this.player));
            this.transformations.add(TransformContext.of(1, (pane, view) -> {
                for (var ele : pane.chestElements().entrySet()) {
                    if (ele.getValue().equals(ItemStackElement.empty())) {
                        Vector2 vector = ele.getKey();
                        pane = pane.element(filler, vector.x(), vector.y());
                    }
                }
                return pane;
            }));
        }
    }

    @Override
    public void load() {
        this.applySpecialItems();
        this.applyFillItems();
        this.chest = new ChestInterface(this.rows, this.title, this.transformations, List.of(), true, 10, ClickHandler.cancel());
    }
}
