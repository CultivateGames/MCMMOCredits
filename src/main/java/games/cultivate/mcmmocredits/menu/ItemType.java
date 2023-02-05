package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.util.Util;

import java.util.Arrays;
import java.util.List;

/**
 * Represents all possible Item obligations inside a {@link Menu}
 */
public enum ItemType {
    MAIN_MENU("navigation"),
    CONFIG_MENU("config"),
    REDEEM_MENU("redemption"),
    EDIT_MESSAGE("messages"),
    EDIT_SETTING("settings"),
    FILL("fill"),
    REDEEM(Util.getSkillNames());

    private final List<String> configSections;

    /**
     * Constructs the ItemType.
     *
     * @param configSections list of node paths that may contain the ItemType.
     */
    ItemType(final List<String> configSections) {
        this.configSections = configSections;
    }

    /**
     * Constructs the ItemType.
     *
     * @param configSections node path that contains the ItemType.
     */
    ItemType(final String configSections) {
        this.configSections = List.of(configSections);
    }

    /**
     * Finds the correct ItemType based on the provided String.
     *
     * @param data String representing a node path that correlates to an ItemType.
     * @return The matching ItemType, or FILL.
     */
    public static ItemType value(final String data) {
        return Arrays.stream(ItemType.values()).filter(x -> x.contains(data)).findAny().orElse(ItemType.FILL);
    }

    /**
     * Returns if the ItemType's data matches the provided String.
     *
     * @param data the provided String.
     * @return if the ItemType contains the String.
     */
    public boolean contains(final String data) {
        return this.configSections.contains(data);
    }
}
