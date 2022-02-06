package games.cultivate.mcmmocredits.config;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public enum Keys {
    //Settings
    DATABASE_ADAPTER(Config.SETTINGS, String.class, "database", "adapter"),
    DATABASE_HOST(Config.SETTINGS, String.class, "database", "mysql-credentials", "host"),
    DATABASE_PORT(Config.SETTINGS, Integer.class, "database","mysql-credentials", "port"),
    DATABASE_NAME(Config.SETTINGS, String.class, "database","mysql-credentials", "name"),
    DATABASE_USERNAME(Config.SETTINGS, String.class, "database", "mysql-credentials", "username"),
    DATABASE_PASSWORD(Config.SETTINGS, String.class, "database", "mysql-credentials", "password"),
    DATABASE_USE_SSL(Config.SETTINGS, Boolean.class, "database", "mysql-credentials", "ssl"),

    ADD_NOTIFICATION(Config.SETTINGS, Boolean.class, "general", "add-notification"),
    PLAYER_TAB_COMPLETION(Config.SETTINGS, Boolean.class, "general", "player-tab-completion"),
    SEND_LOGIN_MESSAGE(Config.SETTINGS, Boolean.class, "general", "send-login-message"),
    SETTINGS_DEBUG(Config.SETTINGS, Boolean.class, "general", "debug"),

    //Messages
    CANCEL_PROMPT(Config.MESSAGES, String.class, "general", "cancel-prompt"),
    PREFIX(Config.MESSAGES, String.class, "general", "prefix"),
    LOGIN_MESSAGE(Config.MESSAGES, String.class, "general", "login-message"),
    ADD_PLAYER_MESSAGE(Config.MESSAGES, String.class, "general", "add-player-message"),

    INVALID_ARGUMENTS(Config.MESSAGES, String.class, "exceptions", "invalid-arguments"),
    MUST_BE_NUMBER(Config.MESSAGES, String.class, "exceptions", "must-be-number"),
    NO_PERMS(Config.MESSAGES, String.class, "exceptions", "no-perms"),
    PLAYER_DOES_NOT_EXIST(Config.MESSAGES, String.class, "exceptions", "player-does-not-exist"),
    COMMAND_ERROR(Config.MESSAGES, String.class, "exceptions", "command-error"),

    CREDITS_BALANCE_SELF(Config.MESSAGES, String.class, "commands", "credits", "balance-self"),
    CREDITS_BALANCE_OTHER(Config.MESSAGES, String.class, "commands", "credits", "balance-other"),
    CREDITS_RELOAD_SUCCESSFUL(Config.MESSAGES, String.class, "commands", "credits", "reload-successful"),
    CREDITS_SETTING_CHANGE_SUCCESSFUL(Config.MESSAGES, String.class, "commands", "credits", "setting-change-successful"),
    CREDITS_SETTING_CHANGE_FAILURE(Config.MESSAGES, String.class, "commands", "credits", "setting-change-failure"),
    CREDITS_MENU_EDITING_PROMPT(Config.MESSAGES, String.class, "commands", "credits", "menu-editing-prompt"),
    CREDITS_MENU_REDEEM_PROMPT(Config.MESSAGES, String.class, "commands", "credits", "menu-redeem-prompt"),

    REDEEM_SKILL_CAP(Config.MESSAGES, String.class, "commands", "redeem", "skill-cap"),
    REDEEM_NOT_ENOUGH_CREDITS(Config.MESSAGES, String.class, "commands", "redeem", "not-enough-credits"),
    REDEEM_SUCCESSFUL_SELF(Config.MESSAGES, String.class, "commands", "redeem", "successful-self"),
    REDEEM_SUCCESSFUL_SENDER(Config.MESSAGES, String.class, "commands", "redeem", "successful-sender"),
    REDEEM_SUCCESSFUL_RECEIVER(Config.MESSAGES, String.class, "commands", "redeem", "successful-receiver"),

    MODIFY_CREDITS_ADD_SENDER(Config.MESSAGES, String.class, "commands", "modify-credits", "add-sender"),
    MODIFY_CREDITS_SET_SENDER(Config.MESSAGES, String.class, "commands", "modify-credits", "set-sender"),
    MODIFY_CREDITS_TAKE_SENDER(Config.MESSAGES, String.class, "commands", "modify-credits", "take-sender"),
    MODIFY_CREDITS_ADD_RECEIVER(Config.MESSAGES, String.class, "commands", "modify-credits", "add-receiver"),
    MODIFY_CREDITS_SET_RECEIVER(Config.MESSAGES, String.class, "commands", "modify-credits", "set-receiver"),
    MODIFY_CREDITS_TAKE_RECEIVER(Config.MESSAGES, String.class, "commands", "modify-credits", "take-receiver"),

    //Menu Settings
    MENU_FILL(Config.MENU, Boolean.class, "main", "fill"),
    MENU_FILL_ITEM(Config.MENU, ItemStack.class, "main", "fill-item"),
    MENU_NAVIGATION(Config.MENU, Boolean.class, "main", "navigation"),
    MENU_NAVIGATION_ITEM(Config.MENU, ItemStack.class, "main", "navigation-item"),
    MENU_SIZE(Config.MENU, Integer.class, "main", "inventory-size"),
    MENU_TITLE(Config.MENU, String.class, "main",  "inventory-title"),
    MENU_MESSAGES_ITEM(Config.MENU, ItemStack.class, "main",  "messages"),
    MENU_REDEEM_ITEM(Config.MENU, ItemStack.class, "main",  "redeem"),
    MENU_SETTINGS_ITEM(Config.MENU, ItemStack.class, "main",  "settings"),

    EDIT_MESSAGES_SIZE(Config.MENU, Integer.class, "editing", "messages", "inventory-size"),
    EDIT_MESSAGES_TITLE(Config.MENU, String.class, "editing", "messages", "inventory-title"),
    EDIT_MESSAGES_ITEM(Config.MENU, ItemStack.class, "editing", "messages", "item"),

    EDIT_SETTINGS_SIZE(Config.MENU, Integer.class, "editing", "settings", "inventory-size"),
    EDIT_SETTINGS_TITLE(Config.MENU, String.class, "editing", "settings", "inventory-title"),
    EDIT_SETTINGS_ITEM(Config.MENU, ItemStack.class, "editing", "settings", "item"),

    REDEEM_SIZE(Config.MENU, Integer.class, "redeem", "inventory-size"),
    REDEEM_TITLE(Config.MENU, String.class, "redeem", "inventory-title"),
    ACROBATICS_ITEM(Config.MENU, ItemStack.class, "redeem", "items", "acrobatics"),
    ALCHEMY_ITEM(Config.MENU, ItemStack.class, "redeem", "items", "alchemy"),
    ARCHERY_ITEM(Config.MENU, ItemStack.class, "redeem", "items", "archery"),
    AXES_ITEM(Config.MENU, ItemStack.class, "redeem", "items", "axes"),
    EXCAVATION_ITEM(Config.MENU, ItemStack.class, "redeem", "items", "excavation"),
    FISHING_ITEM(Config.MENU, ItemStack.class, "redeem", "items", "fishing"),
    HERBALISM_ITEM(Config.MENU, ItemStack.class, "redeem", "items", "herbalism"),
    MINING_ITEM(Config.MENU, ItemStack.class, "redeem", "items", "mining"),
    REPAIR_ITEM(Config.MENU, ItemStack.class, "redeem", "items", "repair"),
    SWORDS_ITEM(Config.MENU, ItemStack.class, "redeem", "items", "swords"),
    TAMING_ITEM(Config.MENU, ItemStack.class, "redeem", "items", "taming"),
    UNARMED_ITEM(Config.MENU, ItemStack.class, "redeem", "items", "unarmed"),
    WOODCUTTING_ITEM(Config.MENU, ItemStack.class, "redeem", "items", "woodcutting");

    private final Config<?> CONFIG;
    private final Class<?> TYPE;
    private final List<String> PATH;

    /**
     * An Enum Set that contains all Keys.
     */
    public static final EnumSet<Keys> ALL = EnumSet.allOf(Keys.class);

    /**
     * A Set of Keys that can be changed via /credits settings.
     * There is no support for changing Database settings without a restart,
     * and there is no support for transferring data between adapters.
     *
     * Item settings can be changed in config, but NOT in game. This is supported with /credits reload.
     */
    public static final Set<Keys> CAN_CHANGE = ALL.stream().filter(Keys::canChange).collect(Collectors.toSet());

    /**
     * Lists which contain Keys for each Configuration.
     */
    public static final List<Keys> MESSAGE_KEYS = ALL.stream().filter(key -> key.config().equals(Config.MESSAGES)).toList();
    public static final List<Keys> SETTING_KEYS = ALL.stream().filter(key -> key.config().equals(Config.SETTINGS)).toList();
    public static final List<Keys> MENU_KEYS = ALL.stream().filter(key -> key.config().equals(Config.MENU)).toList();

    Keys (Config<?> config, Class<?> type, String... path) {
        this.CONFIG = config;
        this.TYPE = type;
        this.PATH = Arrays.asList(path);
    }

    public boolean canChange() {
        String name = this.name();
        return !name.contains("DATABASE") && !name.contains("ITEM");
    }

    public ItemStack getItemStack(Player player) {
        return ItemStackSerializer.INSTANCE.deserializePlayer(node(), player);
    }

    public ItemStack partialItemStack() {
        return ItemStackSerializer.INSTANCE.deserializeConfig(node());
    }

    public CommentedConfigurationNode node() {
        return CONFIG.root().node(PATH);
    }

    public List<String> path() {
        return PATH;
    }

    public Config<?> config() {
        return CONFIG;
    }

    public Class<?> type() {
        return TYPE;
    }

    @SuppressWarnings("unchecked")
    public <T> T get() {
        return (T) CONFIG.get(TYPE, PATH);
    }
}
