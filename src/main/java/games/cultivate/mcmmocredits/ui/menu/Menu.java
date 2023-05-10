package games.cultivate.mcmmocredits.ui.menu;

import games.cultivate.mcmmocredits.ui.ContextFactory;
import games.cultivate.mcmmocredits.ui.item.Item;
import games.cultivate.mcmmocredits.user.User;
import org.incendo.interfaces.paper.type.ChestInterface;

import java.util.Map;

public interface Menu {
    /**
     * Adds extra items if needed.
     */
    void addExtraItems();

    /**
     * Creates a new ChestInterface for the given user with the specified contextFactory.
     */
    ChestInterface build(final User user, final ContextFactory factory);

    /**
     * Obtain a Map of items in the Menu.
     * @return The map.
     */
    Map<String, Item> items();

    /**
     * Obtain the unparsed title of the inventory.
     * @return The title.
     */
    String title();

    /**
     * Get the size of the backing chest inventory.
     * @return The size.
     */
    int slots();

    /**
     * Whether the menu should be filled with border items.
     *
     * @return If the Menu is "filled".
     */
    boolean fill();

    /**
     * Whether the menu should be filled with border items.
     * @return If the Menu has a navigation item.
     */
    boolean navigation();
}
