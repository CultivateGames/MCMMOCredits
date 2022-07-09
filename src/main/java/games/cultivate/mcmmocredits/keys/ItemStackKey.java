package games.cultivate.mcmmocredits.keys;

import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.ConfigUtil;
import games.cultivate.mcmmocredits.serializers.ItemStackSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

public enum ItemStackKey implements Key<ItemStack> {
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
    private final CommentedConfigurationNode root;

    ItemStackKey(String path) {
        this.path = path;
        this.root = this.root();
    }

    @Override
    public @NotNull String path() {
        return path;
    }

    @Override
    public @NotNull ItemStack get() {
        return ItemStackSerializer.INSTANCE.deserialize(ItemStack.class, this.root.node(path));
    }

    @Override
    public @NotNull Config<?> config() {
        return ConfigUtil.MENU;
    }

    public ItemStack get(Player player) {
        return ItemStackSerializer.INSTANCE.deserializePlayer(this.root.node(path), player);
    }

    public ItemStackElement<ChestPane> element(Player player) {
        return ItemStackElement.of(get(player));
    }

    public int slot() {
        return this.root.node(path + ".inventory-slot").getInt();
    }

    public CommentedConfigurationNode root()  {
        return ConfigUtil.MENU.root();
    }
}
