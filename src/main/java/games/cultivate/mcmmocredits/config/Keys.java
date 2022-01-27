package games.cultivate.mcmmocredits.config;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

public enum Keys {
    DATABASE_ADD_NOTIFICATION(true, "settings", "database", "add-notification"),
    DATABASE_ADAPTER(false, "settings", "database", "adapter"),
    DATABASE_HOST(false, "settings", "database", "mysql-credentials", "host"),
    DATABASE_PORT(false, "settings", "database","mysql-credentials", "port"),
    DATABASE_NAME(false, "settings", "database","mysql-credentials", "name"),
    DATABASE_USERNAME(false, "settings", "database", "mysql-credentials", "username"),
    DATABASE_PASSWORD(false, "settings", "database", "mysql-credentials", "password"),

    USERCACHE_LOOKUP(true,"settings", "general", "usercache-lookup"),
    UNSAFE_LOOKUP(true, "settings", "general", "unsafe-lookup"),
    PLAYER_TAB_COMPLETION(true,"settings", "general", "player-tab-completion"),
    SEND_LOGIN_MESSAGE(true,"settings", "general", "send-login-message"),

    GUI_TITLE(true, "settings", "gui", "title"),
    GUI_SIZE(true, "settings", "gui", "size"),
    GUI_FILL(true, "settings", "gui", "fill"),
    GUI_FILL_ITEM(false, "settings", "gui", "fill-item"),
    GUI_SETTING_CHANGE(false, "settings", "gui", "setting-change"),
    GUI_MESSAGE_CHANGE(false, "settings", "gui", "message-change"),
    GUI_REDEMPTION(false, "settings", "gui", "redemption"),

    PREFIX(true, "messages", "general", "prefix"),
    LOGIN_MESSAGE(true, "messages", "general", "login-message"),
    DATABASE_CONSOLE_MESSAGE(true, "messages", "general", "database-console-message"),

    INVALID_ARGUMENTS(true, "messages", "exceptions", "invalid-arguments"),
    MUST_BE_NUMBER(true, "messages", "exceptions", "must-be-number"),
    NO_PERMS(true, "messages", "exceptions", "no-perms"),
    PLAYER_DOES_NOT_EXIST(true, "messages", "exceptions", "player-does-not-exist"),
    COMMAND_ERROR(true, "messages", "exceptions", "command-error"),

    CREDITS_BALANCE_SELF(true, "messages", "commands", "credits", "balance-self"),
    CREDITS_BALANCE_OTHER(true, "messages", "commands", "credits", "balance-other"),
    CREDITS_RELOAD_SUCCESSFUL(true, "messages", "commands", "credits", "reload-successful"),
    CREDITS_SETTING_CHANGE_SUCCESSFUL(true, "messages", "commands", "credits", "setting-change-successful"),
    CREDITS_SETTING_CHANGE_FAILURE(true, "messages", "commands", "credits", "setting-change-failure"),

    REDEEM_SKILL_CAP(true, "messages", "commands", "redeem", "skill-cap"),
    REDEEM_NOT_ENOUGH_CREDITS(true, "messages", "commands", "redeem", "not-enough-credits"),
    REDEEM_SUCCESSFUL_SELF(true, "messages", "commands", "redeem", "successful-self"),
    REDEEM_SUCCESSFUL_SENDER(true, "messages", "commands", "redeem", "successful-sender"),
    REDEEM_SUCCESSFUL_RECEIVER(true, "messages", "commands", "redeem", "successful-receiver"),

    MODIFY_CREDITS_ADD_SENDER(true, "messages", "commands", "modify-credits", "add-sender"),
    MODIFY_CREDITS_SET_SENDER(true, "messages", "commands", "modify-credits", "set-sender"),
    MODIFY_CREDITS_TAKE_SENDER(true, "messages", "commands", "modify-credits", "take-sender"),
    MODIFY_CREDITS_ADD_RECEIVER(true, "messages", "commands", "modify-credits", "add-receiver"),
    MODIFY_CREDITS_SET_RECEIVER(true, "messages", "commands", "modify-credits", "set-receiver"),
    MODIFY_CREDITS_TAKE_RECEIVER(true, "messages", "commands", "modify-credits", "take-receiver");

    private final String[] path;
    private final boolean canChange;

    public static final EnumSet<Keys> all = EnumSet.allOf(Keys.class);
    public static final List<Keys> modifiableKeys = all.stream().filter(Keys::canChange).toList();
    public static final List<Keys> messageKeys = Keys.all.stream().filter(i -> i.path()[0].equalsIgnoreCase("messages")).toList();
    public static final List<Keys> settingKeys = Keys.all.stream().filter(i -> i.path()[0].equalsIgnoreCase("settings")).toList();

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
