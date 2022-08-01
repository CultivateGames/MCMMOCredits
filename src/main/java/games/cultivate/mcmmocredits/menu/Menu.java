package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.MenuConfig;
import org.bukkit.entity.Player;
import org.incendo.interfaces.core.transform.Transform;
import org.incendo.interfaces.core.util.Vector2;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.type.ChestInterface;

import java.util.Map;

//TODO override methods should not be called in the constructor.
public abstract class Menu {
    protected ChestInterface chest;
    protected PlayerViewer viewer;
    protected MCMMOCredits plugin;

    public abstract void open();

    public abstract void close();

    public abstract Transform<ChestPane, PlayerViewer> itemTransform();

    public abstract ChestInterface create();

    protected Transform<ChestPane, PlayerViewer> fillTransform(final MenuConfig config, final Player player) {
        return (pane, view) -> {
            for (Map.Entry<Vector2, ItemStackElement<ChestPane>> ele : pane.chestElements().entrySet()) {
                if (ele.getValue().equals(ItemStackElement.empty())) {
                    Vector2 vec = ele.getKey();
                    pane = pane.element(ItemStackElement.of(config.item("main.items.fill", player)), vec.x(), vec.y());
                }
            }
            return pane;
        };
    }
}
