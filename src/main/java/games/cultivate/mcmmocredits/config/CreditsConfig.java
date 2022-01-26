package games.cultivate.mcmmocredits.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Arrays;
import java.util.List;

@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal, unused"})
public class CreditsConfig {
    @Comment("All messages are prepended with messages.general.prefix")
    @Setting("messages")
    private AllMessages allMessages = new AllMessages();

    @Comment("All plugin settings are found here.")
    @Setting("settings")
    private AllSettings allSettings = new AllSettings();

    @ConfigSerializable
    protected static final class AllMessages {
        @Comment("Sender output for command exceptions.")
        @Setting("exceptions")
        private ExceptionMessages exceptionMessages = new ExceptionMessages();

        @Comment("Output for command execution.\n" + "Includes command-specific errors.")
        @Setting("commands")
        private CommandMessages commandMessages = new CommandMessages();

        @Comment("Output for all other messages.")
        @Setting("general")
        private GeneralMessages generalMessages = new GeneralMessages();
    }

    @ConfigSerializable
    protected static final class AllSettings {
        @Comment("Database related settings.\n" + "Includes MySQL credential management.\n" + "These are not eligible for in-game configuration.")
        @Setting("database")
        private DatabaseSettings databaseSettings = new DatabaseSettings();

        @Comment("All other settings.")
        @Setting("general")
        private GeneralSettings generalSettings = new GeneralSettings();

        @Comment("GUI Settings for /credits gui.\n" + "CONFIGURE THE GUI BEFORE PLUGIN USAGE.\n" + "It is configured as an example to show many usable features of the plugin.")
        @Setting("gui")
        private GUISettings gui = new GUISettings();
    }

    @ConfigSerializable
    protected static final class CommandMessages {
        @Comment("All output for /credits.\n" + "Placeholders: <player>, <credits>, PAPI")
        @Setting("credits")
        private CreditsCommandMessages credits = new CreditsCommandMessages();

        @Comment("All output for /modifycredits\n" + "Placeholders: <player>, <credits>, <amount>, <sender>, <sender_credits>, PAPI")
        @Setting("modify-credits")
        private ModifyCreditsCommandMessages modify_credits = new ModifyCreditsCommandMessages();

        @Comment("All output for /redeem\n" + "Placeholders: <player>, <credits>, <amount>, <sender>, <sender_credits>, <skill>, <cap>, PAPI")
        @Setting("redeem")
        private RedeemCommandMessages redeem = new RedeemCommandMessages();
    }

    @ConfigSerializable
    protected static final class GUISettings {
        @Comment("GUI Title.\n" + "Placeholders: <player>, <credits>, PAPI")
        private String title = "<#ff253c>MCMMO Credits Main Menu";

        @Comment("Size of the GUI.\n" + "Choices: 9, 18, 27, 36, 45, 54")
        private int size = 45;

        @Comment("Toggle for use of GUI fill item.")
        private boolean fill = false;

        @Comment("Item config for GUI filling.")
        @Setting("fill-item")
        private FillItemStack fill_item = new FillItemStack();

        @Comment("Item config for Settings button.\n" + "If users do not have perm, this will be hidden in GUI.")
        @Setting("setting-change")
        private SettingItemStack settings_item = new SettingItemStack();

        @Comment("Item config for Messages button.\n" + "If users do not have perm, this will be hidden in GUI.")
        @Setting("message-change")
        private MessageItemStack messages_item = new MessageItemStack();

        @Comment("Shown to user in the GUI main menu for credit redemption.\n" + "Shown to all users who can open GUI.")
        @Setting("redemption")
        private RedeemItemStack redeem_item = new RedeemItemStack();
    }

    @ConfigSerializable
    protected static final class RedeemItemStack {
        @Comment("Material of GUI item.\n" + "Use all caps (ex. STONE).")
        private String material = "SUGAR_CANE";
        @Comment("Quantity of GUI item.\n" + "Must be valid for given material")
        private int amount = 1;
        @Comment("Durability of the item.\n" + "For PLAYER_HEAD, set to 3.")
        private int durability = 0;
        @Comment("Item Name.\n" + "Placeholders: <credits>, <player>, PAPI")
        private String name = "<!italic><#FF253C><bold>Redeem Credits:</bold><credits> Credits available...";
        @Comment("Item Lore.\n" + "Placeholders: <credits>, <player>, PAPI.\n" + "If you do not want italics here, use the <!italic> tag")
        private List<String> lore = Arrays.asList("<!italic><gray><player>, Click here to redeem your Credits!", "<!italic><gradient:#666666:#FFFFFF>This server has been online for: %server_uptime%");
        @Comment("Toggle for enchantment glint.")
        private boolean glow = true;
        @Comment("Location of item in GUI.\n" + "Starts at 0, and cannot exceed inventory size.")
        private int inventory_slot = 22;
    }

    @ConfigSerializable
    protected static final class MessageItemStack {
        @Comment("Material of GUI item.\n" + "Use all caps (ex. STONE).")
        private String material = "WRITABLE_BOOK";
        @Comment("Quantity of GUI item.\n" + "Must be valid for given material")
        private int amount = 1;
        @Comment("Durability of the item.\n" + "For PLAYER_HEAD, set to 3.")
        private int durability = 0;
        @Comment("Item Name.\n" + "Placeholders: <credits>, <player>, PAPI")
        private String name = "<red>Edit Messages";
        @Comment("Item Lore.\n" + "Placeholders: <credits>, <player>, PAPI.\n" + "If you do not want italics here, use the <!italic> tag")
        private List<String> lore = Arrays.asList("<!italic><gray>Click here to edit plugin messages.", "<blue>Server TPS:</blue> %server_tps%");
        @Comment("Toggle for enchantment glint.")
        private boolean glow = false;
        @Comment("Location of item in GUI.\n" + "Starts at 0, and cannot exceed inventory size.")
        private int inventory_slot = 23;
    }

    @ConfigSerializable
    protected static final class SettingItemStack {
        @Comment("Material of GUI item.\n" + "Use all caps (ex. STONE).")
        private String material = "COMPASS";
        @Comment("Quantity of GUI item.\n" + "Must be valid for given material")
        private int amount = 1;
        @Comment("Durability of the item.\n" + "For PLAYER_HEAD, set to 3.")
        private int durability = 0;
        @Comment("Item Name.\n" + "Placeholders: <credits>, <player>, PAPI")
        private String name = "<rainbow>Edit Settings";
        @Comment("Item Lore.\n" + "Placeholders: <credits>, <player>, PAPI.\n" + "If you do not want italics here, use the <!italic> tag")
        private List<String> lore = Arrays.asList("<!italic><gray>Click here to edit plugin messages.", "<!italic><yellow>Current Time: %server_time_h:mm a z%");
        @Comment("Toggle for enchantment glint.")
        private boolean glow = false;
        @Comment("Location of item in GUI.\n" + "Starts at 0, and cannot exceed inventory size.")
        private int inventory_slot = 21;
    }

    @ConfigSerializable
    protected static final class FillItemStack {
        @Comment("Material of GUI item.\n" + "Use all caps (ex. STONE).")
        private String material = "BLACK_STAINED_GLASS_PANE";
        @Comment("Quantity of GUI item.\n" + "Must be valid for given material")
        private int amount = 1;
        @Comment("Durability of the item.\n" + "For PLAYER_HEAD, set to 3.")
        private int durability = 0;
        @Comment("Item Name.\n" + "Placeholders: <credits>, <player>, PAPI")
        private String name = "";
        @Comment("Item Lore.\n" + "Placeholders: <credits>, <player>, PAPI.\n" + "If you do not want italics here, use the <!italic> tag")
        private List<String> lore = List.of("");
        @Comment("Toggle for enchantment glint.")
        private boolean glow = false;
    }

    @ConfigSerializable
    protected static final class CreditsCommandMessages {
        @Comment("Sender output for /credits.")
        private String balance_self = "<green>You have <credits> MCMMO Credits!";
        @Comment("Sender output for /credits <player>.")
        private String balance_other = "<green><player> has <credits> MCMMO Credits.";
        @Comment("Sender output for /credits reload.")
        private String reload_successful = "<green>The configuration file has been reloaded.";
        @Comment("Sender output for /credits <setting> <change> success.")
        private String setting_change_successful = "<green>You have changed <gray><setting> <green>to <gray><change>.";
        @Comment("Sender output for /credits <setting> <change> failure.")
        private String setting_change_failure = "<red>There was an error while changing settings, operation aborted.";
    }

    @ConfigSerializable
    protected static final class ModifyCreditsCommandMessages {
        @Comment("Sender output for /modifycredits add.")
        private String add_sender = "<green>You have given <amount> Credits to <player>.";
        @Comment("Sender output for /modifycredits set.")
        private String set_sender = "<yellow>You have set <player>'s Credits to <amount>.";
        @Comment("Sender output for /modifycredits take.")
        private String take_sender = "<red>You have taken <amount> Credits from <player>.";
        @Comment("Receiver output for /modifycredits add.\n" + "Requires --silent flag to be used.")
        private String add_receiver = "<green><amount> Credits have been added to your balance by <sender>. You now have <credits> Credits.";
        @Comment("Receiver output for /modifycredits set.\n" + "Requires --silent flag to be used.")
        private String set_receiver = "<yellow>Your MCMMO Credit balance has been set to <amount> by <sender>.";
        @Comment("Receiver output for /modifycredits take.\n" + "Requires --silent flag to be used.")
        private String take_receiver = "<red>You had <amount> taken out of your Credit balance by <sender>. You now have <credits> Credits.";
    }

    @ConfigSerializable
    protected static final class RedeemCommandMessages {
        @Comment("Sender output for /redeem failure.\n" + "Sent when skill cap would be exceeded.")
        private String skill_cap = "<red>You cannot redeem this many MCMMO Credits into <skill>, due to the Level Cap (<cap>).";
        @Comment("Sender output for /redeem failure.\n" + "Sent when user does not have enough credits.")
        private String not_enough_credits = "<red>You do not have enough MCMMO Credits to do this!";
        @Comment("Sender output for /redeem success.")
        private String successful_self = "<green>Redemption Successful! You have redeemed <amount> Credits into <skill>. You have <credits> Credits remaining.";
        @Comment("Sender output for /redeem success,\n" + "when used on another player.\n" + "Requires --silent flag to be used.")
        private String successful_sender = "<green>Redemption Successful! You have redeemed <amount> Credits into <skill> for <player>. They have <credits> Credits remaining.";
        @Comment("Receiver output for /redeem success,\n" + "when used on another player.\n" + "Requires --silent flag to be used.")
        private String successful_receiver = "<green>Redemption Successful! <sender> has redeemed <amount> Credits into <skill> for you! You have <credits> Credits remaining.";
    }

    @ConfigSerializable
    protected static final class ExceptionMessages {
        @Comment("Sender output for invalid command argument input.\n"+ "Placeholders: <sender>, <correct_syntax> (if syntax is wrong)")
        private String invalid_arguments = "<red>Invalid arguments! Correct syntax (if applicable): <gray><correct_syntax>";
        @Comment("Sender output for invalid number input.")
        private String must_be_number = "<red>You need to specify a valid number.";
        @Comment("Sender output for missing permission.\n" + "Placeholders: <sender>, <required_permission>")
        private String no_perms = "<hover:show_text:'<red>Required permission: <required_permission>'><red>You do not have permission to do this!";
        @Comment("Sender output when a player does not exist.")
        private String player_does_not_exist = "<red>This player does not exist in our database!";
        @Comment("Sender output for general command errors.\n" + "Placeholder <sender>")
        private String command_error = "<red>There was an error executing this command!";
    }

    @ConfigSerializable
    protected static final class GeneralMessages {
        @Comment("Prefix for all plugin messages.")
        private String prefix = "<hover:show_text:'<green><player>: <credits> Credits'><gold><bold>CREDITS</bold> ";
        @Comment("Output for player joining.\n" + "Requires send-login-message: true.")
        private String login_message = "<hover:show_text:'<green>You have <credits> MCMMO Credits!'><yellow>Hover here to see how many MCMMO Credits you have!";
        @Comment("Output for adding new users to database.\n" + "Required database-add-message: true.")
        private String database_console_message = "<player> has been added to the database!";
    }

    @ConfigSerializable
    protected static final class GeneralSettings {
        @Comment("Perform offline player lookups with usercache.\n" + "Only disable if you are facing unsolvable issues.")
        private boolean usercache_lookup = true;
        @Comment("Explicitly allow unsafe player lookups.\n" + "This is NOT recommended and may hang the server.")
        private boolean unsafe_lookup = false;
        @Comment("Toggles tab completion for Player based arguments.\n" + "Useful if you have other plugins which hide staff.")
        private boolean player_tab_completion = true;
        @Comment("Toggles sending a login message to the user.")
        private boolean send_login_message = true;
    }

    @ConfigSerializable
    protected static final class DatabaseSettings {
        @Comment("MySQL connection properties. All options will be ignored if SQLite is enabled. Use a separate MySQL user to manage this database.")
        @Setting("mysql-credentials")
        private SQLSettings sqlSettings = new SQLSettings();

        @Comment("Toggles console message when a user\n" + "is added to the MCMMO Credits database")
        private boolean add_notification = true;
        @Comment("Options: sqlite, mysql")
        private String adapter = "sqlite";
    }

    @ConfigSerializable
    protected static final class SQLSettings {
        @Comment("Host address of MySQL database.")
        private String host = "127.0.0.1";
        @Comment("Port for Host address of MySQL database.")
        private int port = 3306;
        @Comment("Name of Database to create.")
        private String name = "database";
        @Comment("MySQL Account Username.")
        private String username = "root";
        @Comment("MySQL Account Password.")
        private String password = "passw0rd+";
    }
}
