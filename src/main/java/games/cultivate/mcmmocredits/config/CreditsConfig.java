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
    @Setting("configuration-messages")
    private AllMessages allMessages = new AllMessages();
    @Comment("All plugin settings are found here.")
    @Setting("configuration-settings")
    private AllSettings allSettings = new AllSettings();
    @Comment("Main Menu configuration (/credits gui)")
    @Setting("menu-main")
    private MainMenu menu_main = new MainMenu();
    @Comment("Edit Messages Menu configuration (/credits gui --location messages)")
    @Setting("menu-edit-messages")
    private MessagesMenu menu_messages = new MessagesMenu();
    @Comment("Edit Settings Menu configuration (/credits gui --location settings)")
    @Setting("menu-edit-settings")
    private SettingsMenu menu_settings = new SettingsMenu();
    @Comment("Redeem Menu configuration (/credits gui --location redeem")
    @Setting("menu-redeem")
    private RedeemMenu menu_redeem = new RedeemMenu();

    @ConfigSerializable
    protected static final class MainMenu {
        private String inventory_title = "<#ff253c><bold>MCMMO Credits";
        private int inventory_size = 54;
        @Comment("This will apply to all menus.")
        private boolean menu_fill = false;
        @Comment("This will apply to all menus.")
        private ShortcutItem menu_fill_item = new ShortcutItem();
        @Comment("This will apply to all menus.")
        private boolean menu_navigation = false;
        @Comment("This will apply to all menus.")
        private ShortcutItem menu_navigation_item = new ShortcutItem();
        @Comment("Appearance of Edit Messages shortcut.")
        @Setting("messages-item")
        private ShortcutItem messages_item = new ShortcutItem();
        @Comment("Appearance of Redeem shortcut.")
        @Setting("redeem-item")
        private ShortcutItem redeem_item = new ShortcutItem();
        @Comment("Appearance of Edit Settings shortcut.")
        @Setting("settings-item")
        private ShortcutItem settings_item = new ShortcutItem();
    }

    @ConfigSerializable
    protected static final class RedeemMenu {
        private String inventory_title = "<dark_gray>Redeem Your Credits...";
        @Comment("Must be at least 18")
        private int inventory_size = 45;
        private ShortcutItem redeem_acrobatics = new ShortcutItem();
        private ShortcutItem redeem_alchemy = new ShortcutItem();
        private ShortcutItem redeem_archery = new ShortcutItem();
        private ShortcutItem redeem_axes = new ShortcutItem();
        private ShortcutItem redeem_excavation = new ShortcutItem();
        private ShortcutItem redeem_fishing = new ShortcutItem();
        private ShortcutItem redeem_herbalism = new ShortcutItem();
        private ShortcutItem redeem_mining = new ShortcutItem();
        private ShortcutItem redeem_repair = new ShortcutItem();
        private ShortcutItem redeem_swords = new ShortcutItem();
        private ShortcutItem redeem_taming = new ShortcutItem();
        private ShortcutItem redeem_unarmed = new ShortcutItem();
        private ShortcutItem redeem_woodcutting = new ShortcutItem();
    }

    @ConfigSerializable
    protected static final class MessagesMenu {
        private String inventory_title = "<dark_gray>Edit Messages";
        @Comment("Must be at least 27")
        private int inventory_size = 45;
        @Comment("Change item appearance. Placeholders: <value>, <comment>")
        private ShortcutItem messages_item = new ShortcutItem();
    }

    @ConfigSerializable
    protected static final class SettingsMenu {
        private String inventory_title = "<dark_gray>Edit Settings";
        private int inventory_size = 45;
        @Comment("Change item appearance. Placeholders: <value>, <comment>")
        private ShortcutItem settings_item = new ShortcutItem();

    }

    @ConfigSerializable
    protected static final class ShortcutItem {
        private String material = "STONE";
        private int amount = 1;
        private int durability = 0;
        private String name = "<gray>Menu Item!";
        private List<String> lore = Arrays.asList("<gray><player>, welcome to MCMMO Credits!", "<gradient:#666666:#FFFFFF>Configure this menu in config.conf!");
        private boolean glow = true;
        private int inventory_slot;
    }

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
    protected static final class AllSettings {
        @Comment("Toggles tab completion for Player based arguments.\n" + "Useful if you have other plugins which hide staff.")
        private boolean player_tab_completion = true;
        @Comment("Toggles sending a login message to the user.")
        private boolean send_login_message = true;
        @Comment("Toggles console message when a user\n" + "is added to the MCMMO Credits database")
        private boolean add_notification = true;
        @Comment("Database related settings.\n" + "Includes MySQL credential management.\n" + "These are not eligible for in-game configuration.")
        @Setting("database")
        private DatabaseSettings databaseSettings = new DatabaseSettings();
    }

    @ConfigSerializable
    protected static final class DatabaseSettings {
        @Comment("MySQL connection properties. All options will be ignored if SQLite is enabled. Use a separate MySQL user to manage this database.")
        @Setting("mysql-credentials")
        private SQLSettings sqlSettings = new SQLSettings();
        @Comment("Options: sqlite, mysql. Which database type should we use?\n" + "NOTE: There is not native support for changing between DB types.")
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
        @Comment("UseSSL. Should the connection use SSL?")
        private boolean use_ssl = true;
    }
}
