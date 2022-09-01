package games.cultivate.mcmmocredits.menu;

import org.bukkit.entity.Player;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.type.ChestInterface;

/**
 * Interface which represents all Menus.
 */
public interface Menu {
    /**
     * Opens a menu for the {@link PlayerViewer}
     */
    default void open() {
        this.chest().open(this.viewer());
    }

    /**
     * Closes a menu for the {@link PlayerViewer}
     */
    default void close() {
        this.viewer().close();
    }

    /**
     * Loads the menu.
     */
    void load();

    /**
     * Provides the backing {@link ChestInterface} of a menu.
     *
     * @return the {@link ChestInterface}
     */
    ChestInterface chest();

    /**
     * Provides the backing {@link PlayerViewer} of a menu. Typically derived from a {@link Player}
     *
     * @return the {@link PlayerViewer}
     */
    PlayerViewer viewer();
}
