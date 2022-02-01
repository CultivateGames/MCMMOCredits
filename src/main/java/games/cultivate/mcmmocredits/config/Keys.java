package games.cultivate.mcmmocredits.config;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

public enum Keys {
    //Settings
    ADD_NOTIFICATION(true, "configuration-settings", "add-notification"),
    DATABASE_ADAPTER(false, "configuration-settings", "database", "adapter"),
    DATABASE_HOST(false, "configuration-settings", "database", "mysql-credentials", "host"),
    DATABASE_PORT(false, "configuration-settings", "database","mysql-credentials", "port"),
    DATABASE_NAME(false, "configuration-settings", "database","mysql-credentials", "name"),
    DATABASE_USERNAME(false, "configuration-settings", "database", "mysql-credentials", "username"),
    DATABASE_PASSWORD(false, "configuration-settings", "database", "mysql-credentials", "password"),
    DATABASE_USE_SSL(false, "configuration-settings", "database", "mysql-credentials", "use-ssl"),
    PLAYER_TAB_COMPLETION(true,"configuration-settings", "general", "player-tab-completion"),
    SEND_LOGIN_MESSAGE(true,"configuration-settings", "general", "send-login-message"),

    //Messages
    PREFIX(true, "configuration-messages", "general", "prefix"),
    LOGIN_MESSAGE(true, "configuration-messages", "general", "login-message"),
    DATABASE_CONSOLE_MESSAGE(true, "configuration-messages", "general", "database-console-message"),
    INVALID_ARGUMENTS(true, "configuration-messages", "exceptions", "invalid-arguments"),
    MUST_BE_NUMBER(true, "configuration-messages", "exceptions", "must-be-number"),
    NO_PERMS(true, "configuration-messages", "exceptions", "no-perms"),
    PLAYER_DOES_NOT_EXIST(true, "configuration-messages", "exceptions", "player-does-not-exist"),
    COMMAND_ERROR(true, "configuration-messages", "exceptions", "command-error"),
    CREDITS_BALANCE_SELF(true, "configuration-messages", "commands", "credits", "balance-self"),
    CREDITS_BALANCE_OTHER(true, "configuration-messages", "commands", "credits", "balance-other"),
    CREDITS_RELOAD_SUCCESSFUL(true, "configuration-messages", "commands", "credits", "reload-successful"),
    CREDITS_SETTING_CHANGE_SUCCESSFUL(true, "configuration-messages", "commands", "credits", "setting-change-successful"),
    CREDITS_SETTING_CHANGE_FAILURE(true, "configuration-messages", "commands", "credits", "setting-change-failure"),
    CREDITS_MENU_EDITING_PROMPT(true, "configuration-messages", "commands", "credits", "menu-editing-prompt"),
    CREDITS_MENU_REDEEM_PROMPT(true, "configuration-messages", "commands", "credits", "menu-redeem-prompt"),
    REDEEM_SKILL_CAP(true, "configuration-messages", "commands", "redeem", "skill-cap"),
    REDEEM_NOT_ENOUGH_CREDITS(true, "configuration-messages", "commands", "redeem", "not-enough-credits"),
    REDEEM_SUCCESSFUL_SELF(true, "configuration-messages", "commands", "redeem", "successful-self"),
    REDEEM_SUCCESSFUL_SENDER(true, "configuration-messages", "commands", "redeem", "successful-sender"),
    REDEEM_SUCCESSFUL_RECEIVER(true, "configuration-messages", "commands", "redeem", "successful-receiver"),
    MODIFY_CREDITS_ADD_SENDER(true, "configuration-messages", "commands", "modify-credits", "add-sender"),
    MODIFY_CREDITS_SET_SENDER(true, "configuration-messages", "commands", "modify-credits", "set-sender"),
    MODIFY_CREDITS_TAKE_SENDER(true, "configuration-messages", "commands", "modify-credits", "take-sender"),
    MODIFY_CREDITS_ADD_RECEIVER(true, "configuration-messages", "commands", "modify-credits", "add-receiver"),
    MODIFY_CREDITS_SET_RECEIVER(true, "configuration-messages", "commands", "modify-credits", "set-receiver"),
    MODIFY_CREDITS_TAKE_RECEIVER(true, "configuration-messages", "commands", "modify-credits", "take-receiver"),

    //Menu Settings
    MENU_FILL(true, "menu-main", "menu-fill"),
    MENU_FILL_ITEM(false, "menu-main", "menu-fill-item"),
    MENU_NAVIGATION(true, "menu-main", "menu-navigation"),
    MENU_NAVIGATION_ITEM(false, "menu-main", "menu-navigation-item"),
    MENU_SIZE(true, "menu-main", "inventory-size"),
    MENU_TITLE(true, "menu-main", "inventory-title"),
    MENU_MESSAGES_ITEM(false, "menu-main", "messages-item"),
    MENU_REDEEM_ITEM(false, "menu-main", "redeem-item"),
    MENU_SETTINGS_ITEM(false, "menu-main", "settings-item"),

    EDIT_MESSAGES_SIZE(true, "menu-edit-messages", "inventory-size"),
    EDIT_MESSAGES_TITLE(true, "menu-edit-messages", "inventory-title"),
    EDIT_MESSAGES_ITEM(false, "menu-edit-messages", "item"),

    EDIT_SETTINGS_SIZE(true, "menu-edit-messages", "inventory-size"),
    EDIT_SETTINGS_TITLE(true, "menu-edit-settings", "inventory-title"),
    EDIT_SETTINGS_ITEM(false, "menu-edit-settings", "item"),

    REDEEM_SIZE(true, "menu-redeem", "inventory-size"),
    REDEEM_TITLE(true, "menu-redeem", "inventory-title"),
    REDEEM_ACROBATICS(true, "menu-redeem", "item-acrobatics"),
    REDEEM_ALCHEMY(true, "menu-redeem", "item-alchemy"),
    REDEEM_ARCHERY(true, "menu-redeem", "item-archery"),
    REDEEM_AXES(true, "menu-redeem", "item-axes"),
    REDEEM_EXCAVATION(true, "menu-redeem", "item-excavation"),
    REDEEM_FISHING(true, "menu-redeem", "item-fishing"),
    REDEEM_HERBALISM(true, "menu-redeem", "item-herbalism"),
    REDEEM_MINING(true, "menu-redeem", "item-mining"),
    REDEEM_REPAIR(true, "menu-redeem", "item-repair"),
    REDEEM_SWORDS(true, "menu-redeem", "item-swords"),
    REDEEM_TAMING(true, "menu-redeem", "item-taming"),
    REDEEM_UNARMED(true, "menu-redeem", "item-unarmed"),
    REDEEM_WOODCUTTING(true, "menu-redeem", "item-woodcutting");


    private final String[] path;
    private final boolean canChange;

    public static final EnumSet<Keys> all = EnumSet.allOf(Keys.class);
    public static final List<Keys> messageKeys = Keys.all.stream().filter(i -> i.path()[0].equalsIgnoreCase("configuration-messages")).toList();
    public static final List<Keys> settingKeys = Keys.all.stream().filter(i -> i.path()[0].equalsIgnoreCase("configuration-settings")).toList();

    Keys (boolean canChange, String... path) {
        this.path = path;
        this.canChange = canChange;
    }

    public String[] path() {
        return path;
    }

    public CommentedConfigurationNode node() {
        return ConfigHandler.instance().root().node(Stream.of(this.path()).toList());
    }

    public boolean canChange() {
        return canChange;
    }

    @SuppressWarnings("unused")
    public ItemStack getItemStack() {
        return ItemStackSerializer.INSTANCE.deserialize(ItemStack.class, this.node());
    }

    public ItemStack getItemStack(Player player) {
        return ItemStackSerializer.INSTANCE.deserializePlayer(ItemStack.class, this.node(), player);
    }

    public ItemStack partialItemStack() {
        return ItemStackSerializer.INSTANCE.deserializeConfig(this.node());
    }

    public int getInt() {
        return this.node().getInt();
    }

    public boolean getBoolean() {
        return this.node().getBoolean();
    }

    public String getString() {
        return this.node().getString();
    }
}
