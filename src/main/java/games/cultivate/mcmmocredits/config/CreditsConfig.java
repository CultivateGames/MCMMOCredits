package games.cultivate.mcmmocredits.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.List;

@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class CreditsConfig {
    @Comment("All messages are located here.")
    @Setting("messages")
    private AllMessages allMessages = new AllMessages();

    @Comment("All settings are located here.")
    @Setting("settings")
    private AllSettings allSettings = new AllSettings();

    @ConfigSerializable
    protected static final class AllMessages {
        @Comment("Messages sent when there are general exceptions when running commands.")
        @Setting("exceptions")
        private ExceptionMessages exceptionMessages = new ExceptionMessages();

        @Comment("Messages sent during command execution. Includes command-specific errors.")
        @Setting("commands")
        private CommandMessages commandMessages = new CommandMessages();

        @Comment("Messages sent at various times.")
        @Setting("general")
        private GeneralMessages generalMessages = new GeneralMessages();
    }

    @ConfigSerializable
    protected static final class AllSettings {
        @Comment("Database related settings. Includes MySQL credential management.")
        @Setting("database")
        private DatabaseSettings databaseSettings = new DatabaseSettings();

        @Comment("General settings, all used at various times.")
        @Setting("general")
        private GeneralSettings generalSettings = new GeneralSettings();

        @Comment("GUI Settings for the /credits gui command. YOU MUST CONFIGURE THE GUI BEFORE USING.")
        @Setting("gui")
        private GUISettings gui = new GUISettings();
    }

    @ConfigSerializable
    protected static final class CommandMessages {
        @Comment("Messages sent during execution of /credits. Placeholders: <player>, <credits>, external")
        @Setting("credits")
        private CreditsCommandMessages credits = new CreditsCommandMessages();

        @Comment("Messages sent during execution of /modifycredits. Placeholders: <player>, <credits>, <amount>, <sender>, <sender_credits>, external")
        @Setting("modify-credits")
        private ModifyCreditsCommandMessages modify_credits = new ModifyCreditsCommandMessages();

        @Comment("Messages sent during execution of /redeem. Placeholders: <player>, <credits>, <amount>, <sender>, <sender_credits>, <skill>, <cap>, external")
        @Setting("redeem")
        private RedeemCommandMessages redeem = new RedeemCommandMessages();
    }

    @ConfigSerializable
    protected static final class GUISettings {
        @Comment("Title of the GUI. Placeholders: <player>, <credits>, external")
        private String title = "<#ff253c>MCMMO Credits Main Menu";

        @Comment("Size of the GUI. Choices: 9, 18, 27, 36, 45, 54")
        private int size = 27;

        @Comment("Determines if the GUI will fill empty slots with an item.")
        private boolean fill = false;

        @Comment("Item to fill the GUI with if gui fill is enabled.")
        @Setting("fill-item")
        private ItemStackSettings fill_item = new ItemStackSettings();

        @Comment("Shown to users in the GUI main menu for Setting Changes. If users do not have perm, this will be hidden.")
        @Setting("setting-change")
        private ItemStackSettings settings_item = new ItemStackSettings();

        @Comment("Shown to users in the GUI main menu for Message Changes. If users do not have perm, this will be hidden.")
        @Setting("message-change")
        private ItemStackSettings messages_item = new ItemStackSettings();

        @Comment("Shown to user in the GUI main menu for credit redemption. This will show to all users who have GUI permission.")
        @Setting("redemption")
        private ItemStackSettings redeem_item = new ItemStackSettings();
    }

    @ConfigSerializable
    protected static final class ItemStackSettings {
        @Comment("Material of the GUI button. Use all caps (ex. STONE).")
        private String material = "STONE";
        @Comment("quantity of the item stack. Must be a valid number for the relevant material.")
        private int amount = 1;
        @Comment("Durability of the item. For Skulls, set to 3.")
        private int durability = 0;
        @Comment("Name of the item. Placeholders: <credits>, <player>, external")
        private String name = "GUI Item";
        @Comment("Lore of the item. Placeholders: <credits>, <player>, external. If you do not want italics here, use the <!italic> tag")
        private List<String> lore = List.of("<!italic>This is a test!", "<!italic>I love MCMMO Credits!");
        @Comment("Determines if the item will have an enchantment glint.")
        private boolean glow = false;
        @Comment("Determines slot where the item will be placed (starts at 0, cannot exceed inventory size")
        private int inventory_slot = 27;
    }

    @ConfigSerializable
    protected static final class CreditsCommandMessages {
        @Comment("Shown to user when they check their own MCMMO Credits amount with /credits.")
        private String balance_self = "<green>You have <credits> MCMMO Credits!";
        @Comment("Shown to user when they check the MCMMO Credit balance of another user with /credits <player>.")
        private String balance_other = "<green><player> has <credits> MCMMO Credits.";
        @Comment("Shown to user when they successfully use /credits reload.")
        private String reload_successful = "<green>The configuration file has been reloaded.";
        @Comment("Shown to user when they successfully use /credits <setting> <change>")
        private String setting_change_successful = "<green>You have changed <gray><setting> <green>to <gray><change>.";
        @Comment("Shown to user when there is an error while using /credits <setting> <change>")
        private String setting_change_failure = "<red>There was an error while changing settings, operation aborted.";
    }

    @ConfigSerializable
    protected static final class ModifyCreditsCommandMessages {
        @Comment("Shown to user when they use the /modifycredits add command.")
        private String add_sender = "<green>You have given <amount> Credits to <player>.";
        @Comment("Shown to user when they use the /modifycredits set command.")
        private String set_sender = "<yellow>You have set <player>'s Credits to <amount>.";
        @Comment("Shown to user when they use the /modifycredits take command.")
        private String take_sender = "<red>You have taken <amount> Credits from <player>.";
        @Comment("Shown to user when they are the target of a /modifycredits add command, and command feedback is enabled.")
        private String add_receiver = "<green><amount> Credits have been added to your balance by <sender>. You now have <credits> Credits.";
        @Comment("Shown to user when they are the target of a /modifycredits set command, and command feedback is enabled.")
        private String set_receiver = "<yellow>Your MCMMO Credit balance has been set to <amount> by <sender>.";
        @Comment("Shown to user when they are the target of a /modifycredits take command, and command feedback is enabled.")
        private String take_receiver = "<red>You had <amount> taken out of your Credit balance by <sender>. You now have <credits> Credits.";
    }

    @ConfigSerializable
    protected static final class RedeemCommandMessages {
        @Comment("Shown to user when they try to redeem MCMMO Credits to go over a skill's level cap.")
        private String skill_cap = "<red>You cannot redeem this many MCMMO Credits into <skill>, due to the Level Cap (<cap>).";
        @Comment("Shown to user when they try to redeem more MCMMO Credits than they have available.")
        private String not_enough_credits = "<red>You do not have enough MCMMO Credits to do this!";
        @Comment("Shown to user when they successfully redeem MCMMO Credits into a skill.")
        private String successful_self = "<green>Redemption Successful! You have redeemed <amount> Credits into <skill>. You have <credits> Credits remaining.";
        @Comment("Shown to user when they successfully redeem MCMMO Credits into a skill for another user.")
        private String successful_sender = "<green>Redemption Successful! You have redeemed <amount> Credits into <skill> for <player>. They have <credits> Credits remaining.";
        @Comment("Shown to user when they successfully have MCMMO Credits redeemed for them, and command feedback is not silent.")
        private String successful_receiver = "<green>Redemption Successful! <sender> has redeemed <amount> Credits into <skill> for you! You have <credits> Credits remaining.";
    }

    @ConfigSerializable
    protected static final class ExceptionMessages {
        @Comment("Shown to user when invalid arguments are used in a command. Placeholders: <sender>, <correct_syntax> (if syntax is wrong)")
        private String invalid_arguments = "<red>Invalid arguments! Correct syntax (if applicable): <gray><correct_syntax>";
        @Comment("Shown to user when they use an invalid number.")
        private String must_be_number = "<red>You need to specify a valid number.";
        @Comment("Shown to user when they do not have permission to execute a command! Placeholders: <sender>, <required_permission>")
        private String no_perms = "<hover:show_text:'<red>Required permission: <required_permission>'><red>You do not have permission to do this!";
        @Comment("Shown to user when they use the command system and populate it with a player that does not exist.")
        private String player_does_not_exist = "<red>This player does not exist in our database!";
        @Comment("Shown to user when there is a general error executing a command! Placeholder <sender>")
        private String command_error = "<red>There was an error executing this command!";
    }

    @ConfigSerializable
    protected static final class GeneralMessages {
        @Comment("Prefix for all plugin messages.")
        private String prefix = "<hover:show_text:'<green><player>: <credits> Credits'><gold><bold>CREDITS</bold> ";
        @Comment("Shown to user on login if send_login_message is set to true")
        private String login_message = "<hover:show_text:'<green>You have <credits> MCMMO Credits!'><yellow>Hover here to see how many MCMMO Credits you have!";
        @Comment("Message printed to console when a new user is added to the MCMMO Credits database")
        private String database_console_message = "<player> has been added to the database!";
    }

    @ConfigSerializable
    protected static final class GeneralSettings {
        @Comment("Perform offline player lookups with usercache. Only disable if you are facing unsolvable issues.")
        private boolean usercache_lookup = true;
        @Comment("Allow for unsafe, offline player lookups that do not utilize usercache. This is NOT recommended and may hang the server.")
        private boolean unsafe_lookup = false;
        @Comment("Toggles tab completion for Player based arguments. Useful if you have other plugins which hide staff.")
        private boolean player_tab_completion = true;
        @Comment("Toggles sending a login message to the user indicating how many MCMMO Credits they have.")
        private boolean send_login_message = true;
    }

    @ConfigSerializable
    protected static final class DatabaseSettings {
        @Comment("MySQL connection properties. All options will be ignored if SQLite is enabled. Use a separate MySQL user to manage this database.")
        @Setting("mysql-credentials")
        private SQLSettings sqlSettings = new SQLSettings();

        @Comment("Toggles console message when a user is added to the MCMMO Credits database")
        private boolean add_notification = true;
        @Comment("Options: sqlite, mysql")
        private String adapter = "sqlite";
    }

    @ConfigSerializable
    protected static final class SQLSettings {
        @Comment("This should be the host address of the MySQL database.")
        private String host = "127.0.0.1";
        @Comment("This should be the host address port of the MySQL database.")
        private int port = 3306;
        @Comment("This is the name of the relevant database.")
        private String name = "database";
        @Comment("This is the username to login to the database.")
        private String username = "root";
        @Comment("This is the password used to login to the database.")
        private String password = "passw0rd+";
    }
}
