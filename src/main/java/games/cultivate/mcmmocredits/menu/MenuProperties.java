package games.cultivate.mcmmocredits.menu;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * Properties that are common among all Menus.
 *
 * @param title      Title of the menu. Does not update.
 * @param slots      Size of the menu.
 * @param fill       Whether the Menu's empty spaces should be filled.
 * @param navigation If navigation item should be added to menu.
 */
@ConfigSerializable
public record MenuProperties(String title, int slots, boolean fill, boolean navigation) {
}
