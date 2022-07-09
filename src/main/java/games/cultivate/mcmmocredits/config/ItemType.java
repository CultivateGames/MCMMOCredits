package games.cultivate.mcmmocredits.config;

import org.jetbrains.annotations.NotNull;

public enum ItemType {
    MENU_FILL_ITEM("main.fill-item"),
    MENU_NAVIGATION_ITEM("main.navigation-item"),
    MENU_MESSAGES_ITEM("main.messages"),
    MENU_REDEEM_ITEM("main.redeem"),
    MENU_SETTINGS_ITEM("main.settings"),

    EDIT_MESSAGES_ITEM("editing.messages.item"),
    EDIT_SETTINGS_ITEM("editing.settings.item"),

    ACROBATICS_ITEM("redeem.items.acrobatics"),
    ALCHEMY_ITEM("redeem.items.alchemy"),
    ARCHERY_ITEM("redeem.items.archery"),
    AXES_ITEM("redeem.items.axes"),
    EXCAVATION_ITEM("redeem.items.excavation"),
    FISHING_ITEM("redeem.items.fishing"),
    HERBALISM_ITEM("redeem.items.herbalism"),
    MINING_ITEM("redeem.items.mining"),
    REPAIR_ITEM("redeem.items.repair"),
    SWORDS_ITEM("redeem.items.swords"),
    TAMING_ITEM("redeem.items.taming"),
    UNARMED_ITEM("redeem.items.unarmed"),
    WOODCUTTING_ITEM("redeem.items.woodcutting");

    private final String path;

    ItemType(String path) {
        this.path = path;
    }

    public @NotNull String path() {
        return path;
    }
}
