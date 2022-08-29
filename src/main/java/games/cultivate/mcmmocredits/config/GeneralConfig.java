package games.cultivate.mcmmocredits.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

/**
 * Object that represents a specific configuration used to modify the plugin's settings and messages.
 */
@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal, unused"})
public final class GeneralConfig extends BaseConfig {
    private Settings settings = new Settings();
    private Messages messages = new Messages();

   GeneralConfig() {
        super(GeneralConfig.class, "config.conf");
    }

    public String databaseType() {
        return this.settings.databaseType;
    }

    /**
     * Returns string from configuration and applies prefix to the result.
     *
     * @param path       path where the string resides.
     * @param withPrefix whether we apply a prefix to the string.
     * @return String with or without prefix from the configuration.
     */
    public String string(final String path, final boolean withPrefix) {
        return withPrefix ? super.string("prefix") + super.string(path) : super.string(path);
    }

    @Override
    public String string(final String path) {
        return this.string(path, true);
    }

    @ConfigSerializable
    static class Messages {
        @Comment("Output for command execution.Includes command-specific errors.")
        private CommandMessages commands = new CommandMessages();
        @Comment("Sender output for command exceptions.")
        private ExceptionMessages exceptions = new ExceptionMessages();
        @Comment("Output for all other messages.")
        private GeneralMessages general = new GeneralMessages();
    }

    @ConfigSerializable
    static class Settings {
        @Comment("Options: sqlite, mysql, json.\n" + "NOTE: Changing DB type is not supported. JSON is included as a last resort.\n" + "JSON will slow as user size increases")
        @Setting("databaseType")
        private String databaseType = "sqlite";
        @Comment("Toggles tab completion for Player based arguments.\n" + "Useful if you have other plugins which hide staff.")
        @Setting("playerTabCompletion")
        private boolean playerTabCompletion = true;
        @Comment("Toggles sending a login message to the user.")
        @Setting("sendLoginMessage")
        private boolean sendLoginMessage = true;
        @Comment("Toggles console message when a user\n" + "is added to the MCMMO Credits database")
        @Setting("addPlayerNotification")
        private boolean addPlayerNotification = true;
        @Comment("Enables debug")
        private boolean debug = false;
        @Comment("Configurable MySQL connection settings")
        @Setting("mysql")
        private DatabaseSettings mysql = new DatabaseSettings();
    }

    @ConfigSerializable
    static class DatabaseSettings {
        @Comment("Host address of MySQL database.")
        private String host = "127.0.0.1";
        @Comment("Port for Host address of MySQL database.")
        private int port = 3306;
        @Comment("Name of Database to load.")
        private String database = "database";
        @Comment("MySQL Account Username.")
        private String username = "root";
        @Comment("MySQL Account Password.")
        private String password = "passw0rd+";
        @Comment("UseSSL connection property. Should the connection use SSL?")
        @Setting("useSSL")
        private boolean useSSL = true;
    }

    @ConfigSerializable
    static class ExceptionMessages {
        @Comment("Sender output for invalid command argument input.")
        @Setting("argumentParsing")
        private String argumentParsing = "<red>Argument Error: <gray><argument_error>";
        @Comment("Sender output for invalid command argument input.")
        @Setting("invalidSyntax")
        private String invalidSyntax = "<red>Invalid syntax! Correct syntax: <gray><correct_syntax>";
        @Comment("Sender output for invalid command argument input.")
        @Setting("invalidSender")
        private String invalidSender = "<red>Invalid command sender! You must be of type: <gray><correct_sender>";
        @Comment("Sender output for missing permission.")
        @Setting("noPermission")
        private String noPermission = "<hover:show_text:'<red>Required permission: <required_permission>'><red>You do not have permission to do this!";
        @Comment("Sender output when a player does not exist.")
        @Setting("playerDoesNotExist")
        private String playerDoesNotExist = "<red>This player does not exist in our database!";
        @Comment("Sender output for general command errors.")
        @Setting("commandExecution")
        private String commandExecution = "<red>There was an error executing this command!";
    }

    @ConfigSerializable
    static class GeneralMessages {
        @Comment("Prefix for all plugin messages.")
        private String prefix = "<hover:show_text:'<green><sender>: <sender_credits> Credits'><gold><bold>CREDITS</bold> ";
        @Comment("Output for player joining.\n" + "Requires send-login-message: true.")
        @Setting("loginMessage")
        private String loginMessage = "<hover:show_text:'<green>You have <target_credits> MCMMO Credits!'><yellow>Hover here to see how many MCMMO Credits you have!";
        @Comment("Output for adding new users to database.\n" + "Required database-add-message: true.")
        @Setting("addPlayerMessage")
        private String addPlayerMessage = "<target> has been added to the database!";
        @Comment("Sender output for cancelled prompt.")
        @Setting("cancelPrompt")
        private String cancelPrompt = "<red>You have cancelled the current modification.";
    }

    @ConfigSerializable
    static class CommandMessages {
        @Comment("All output for /credits")
        private CreditsMessages credits = new CreditsMessages();
        @Comment("All output for /modifycredits")
        @Setting("modifyCredits")
        private ModifyCreditsMessages modifyCredits = new ModifyCreditsMessages();
        @Comment("All output for /redeem")
        private RedeemMessages redeem = new RedeemMessages();
    }

    @ConfigSerializable
    static class CreditsMessages {
        @Comment("Sender output for /credits.")
        @Setting("selfBalance")
        private String selfBalance = "<green>You have <sender_credits> MCMMO Credits!";
        @Comment("Sender output for /credits <target>.")
        @Setting("otherBalance")
        private String otherBalance = "<green><target> has <target_credits> MCMMO Credits.";
        @Comment("Sender output for /credits reload.")
        @Setting("reloadSuccessful")
        private String reloadSuccessful = "<green>The configuration file has been reloaded.";
        @Comment("Sender output for /credits <setting> <change> success.")
        @Setting("settingChangeSuccessful")
        private String settingChangeSuccessful = "<green>You have changed <gray><setting> <green>to <gray><change>.";
        @Comment("Sender output for /credits <setting> <change> failure.")
        @Setting("settingChangeFailure")
        private String settingChangeFailure = "<red>There was an error while changing settings, operation aborted.";
        @Comment("Sender prompt for /credits menu redeem prompt.")
        @Setting("menuRedeemPrompt")
        private String menuRedeemPrompt = "<gray>How many credits would you like to redeem into <green><skill>? <gray>You have <green><target_credits> Credits available.";
        @Comment("Sender prompt for /credits menu settings/messages.")
        @Setting("menuEditingPrompt")
        private String menuEditingPrompt = "<red>Enter the new value for <gray><setting>, or type 'cancel' to abort.";
    }

    @ConfigSerializable
    static class ModifyCreditsMessages {
        @Comment("Sender output for /modifycredits add.")
        @Setting("addSender")
        private String addSender = "<green>You have given <amount> Credits to <target>.";
        @Comment("Sender output for /modifycredits modify.")
        @Setting("setSender")
        private String setSender = "<yellow>You have set <target>'s Credits to <amount>.";
        @Comment("Sender output for /modifycredits take.")
        @Setting("takeSender")
        private String takeSender = "<red>You have taken <amount> Credits from <target>.";
        @Comment("Receiver output for /modifycredits add.\n" + "Requires --s to be used.")
        @Setting("addReceiver")
        private String addReceiver = "<green><amount> Credits have been added to your balance by <sender>. You now have <target_credits> Credits.";
        @Comment("Receiver output for /modifycredits modify.\n" + "Requires --s to be used.")
        @Setting("setReceiver")
        private String setReceiver = "<yellow>Your MCMMO Credit balance has been set to <amount> by <sender>.";
        @Comment("Receiver output for /modifycredits take.\n" + "Requires --s to be used.")
        @Setting("takeReceiver")
        private String takeReceiver = "<red>You had <amount> taken out of your Credit balance by <sender>. You now have <target_credits> Credits.";
    }

    @ConfigSerializable
    static class RedeemMessages {
        @Comment("Sender output for /redeem failure.\n" + "Sent when skill cap would be exceeded.")
        @Setting("skillCap")
        private String skillCap = "<red>You cannot redeem this many MCMMO Credits into <skill>, due to the Level Cap (<cap>).";
        @Comment("Sender output for /redeem failure.\n" + "Sent when user does not have enough credits.")
        @Setting("notEnoughCredits")
        private String notEnoughCredits = "<red>You do not have enough MCMMO Credits to do this!";
        @Comment("Sender output for /redeem success.")
        @Setting("selfRedeem")
        private String selfRedeem = "<green>Redemption Successful! You have redeemed <amount> Credits into <skill>. You have <target_credits> Credits remaining.";
        @Comment("Sender output for /redeem success,\n" + "when used on another player.\n" + "Requires --s flag to be used.")
        @Setting("otherRedeemSender")
        private String otherRedeemSender = "<green>Redemption Successful! You have redeemed <amount> Credits into <skill> for <target>. They have <target_credits> Credits remaining.";
        @Comment("Receiver output for /redeem success,\n" + "when used on another player.\n" + "Requires --s flag to be used.")
        @Setting("otherRedeemReceiver")
        private String otherRedeemReceiver = "<green>Redemption Successful! <sender> has redeemed <amount> Credits into <skill> for you! You have <target_credits> Credits remaining.";
    }
}
