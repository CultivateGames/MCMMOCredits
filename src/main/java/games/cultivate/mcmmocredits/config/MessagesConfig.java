package games.cultivate.mcmmocredits.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal, unused"})
public class MessagesConfig extends Config<MessagesConfig> {
    @Comment("Sender output for command exceptions.")
    @Setting("exceptions")
    private ExceptionMessages exceptionMessages = new ExceptionMessages();
    @Comment("Output for command execution.Includes command-specific errors.")
    @Setting("commands")
    private CommandMessages commandMessages = new CommandMessages();
    @Comment("Output for all other messages.")
    @Setting("general")
    private GeneralMessages generalMessages = new GeneralMessages();

    MessagesConfig() {
        super(MessagesConfig.class, "messages.conf");
    }

    public String string(String path, boolean prefix) {
        return prefix ? this.string("prefix") + this.string(path) : this.string(path);
    }

    @Override
    public String string(String path) {
        return this.string(path, true);
    }

    @ConfigSerializable
    protected static final class ExceptionMessages {
        //Placeholders: <sender>, <correct_syntax> (if syntax is wrong)
        @Comment("Sender output for invalid command argument input.")
        private String invalidArguments = "<red>Invalid arguments! Correct syntax (if applicable): <gray><correct_syntax>";
        @Comment("Sender output for invalid number input.")
        private String invalidNumber = "<red>You need to specify a valid number.";
        //Placeholders: <sender>, <required_permission>
        @Comment("Sender output for missing permission.")
        private String noPermission = "<hover:show_text:'<red>Required permission: <required_permission>'><red>You do not have permission to do this!";
        @Comment("Sender output when a player does not exist.")
        private String playerDoesNotExist = "<red>This player does not exist in our database!";
        //Placeholders: <sender>
        @Comment("Sender output for general command errors.")
        private String commandError = "<red>There was an error executing this command!";
    }

    @ConfigSerializable
    protected static final class GeneralMessages {
        @Comment("Prefix for all plugin messages.")
        private String prefix = "<hover:show_text:'<green><player>: <credits> Credits'><gold><bold>CREDITS</bold> ";
        @Comment("Output for player joining.\n" + "Requires send-login-message: true.")
        private String loginMessage = "<hover:show_text:'<green>You have <credits> MCMMO Credits!'><yellow>Hover here to see how many MCMMO Credits you have!";
        @Comment("Output for adding new users to database.\n" + "Required database-add-message: true.")
        private String addPlayerMessage = "<player> has been added to the database!";
        @Comment("Sender output for cancelled prompt.")
        private String cancelPrompt = "<red>You have cancelled the current modification.";
    }

    @ConfigSerializable
    protected static final class CommandMessages {
        //Placeholders: <player>, <credits>, PAPI
        @Comment("All output for /credits")
        @Setting("credits")
        private CreditsMessages credits = new CreditsMessages();
        //Placeholders: <player>, <credits>, <amount>, <sender>, <sender_credits>, PAPI
        @Comment("All output for /modifycredits")
        @Setting("modify-credits")
        private ModifyCreditsMessages modifyCredits = new ModifyCreditsMessages();
        //Placeholders: <player>, <credits>, <amount>, <sender>, <sender_credits>, <skill>, <cap>, PAPI
        @Comment("All output for /redeem")
        @Setting("redeem")
        private RedeemMessages redeem = new RedeemMessages();
    }

    @ConfigSerializable
    protected static final class CreditsMessages {
        @Comment("Sender output for /credits.")
        private String selfBalance = "<green>You have <credits> MCMMO Credits!";
        @Comment("Sender output for /credits <player>.")
        private String otherBalance = "<green><player> has <credits> MCMMO Credits.";
        @Comment("Sender output for /credits reload.")
        private String reloadSuccessful = "<green>The configuration file has been reloaded.";
        @Comment("Sender output for /credits <setting> <change> success.")
        private String settingChangeSuccessful = "<green>You have changed <gray><setting> <green>to <gray><change>.";
        @Comment("Sender output for /credits <setting> <change> failure.")
        private String settingChangeFailure = "<red>There was an error while changing settings, operation aborted.";
        //Placeholders: <player>, <credits>, <skill>, <cap>, PAPI
        @Comment("Sender prompt for /credits menu redeem prompt.")
        private String menuRedeemPrompt = "<gray>How many credits would you like to redeem into <green><skill>? You have <credits> Credits available.";
        //Placeholders: <player>, <credits>, <option>, <current_value>, PAPI
        @Comment("Sender prompt for /credits menu settings/messages.")
        private String menuEditingPrompt = "<red>Enter the new value for <gray><option>:";
    }

    @ConfigSerializable
    protected static final class ModifyCreditsMessages {
        @Comment("Sender output for /modifycredits add.")
        private String addSender = "<green>You have given <amount> Credits to <player>.";
        @Comment("Sender output for /modifycredits modify.")
        private String setSender = "<yellow>You have modify <player>'s Credits to <amount>.";
        @Comment("Sender output for /modifycredits take.")
        private String takeSender = "<red>You have taken <amount> Credits from <player>.";
        @Comment("Receiver output for /modifycredits add.\n" + "Requires --silent flag to be used.")
        private String addReceiver = "<green><amount> Credits have been added to your balance by <sender>. You now have <credits> Credits.";
        @Comment("Receiver output for /modifycredits modify.\n" + "Requires --silent flag to be used.")
        private String setReceiver = "<yellow>Your MCMMO Credit balance has been modify to <amount> by <sender>.";
        @Comment("Receiver output for /modifycredits take.\n" + "Requires --silent flag to be used.")
        private String takeReceiver = "<red>You had <amount> taken out of your Credit balance by <sender>. You now have <credits> Credits.";
    }

    @ConfigSerializable
    protected static final class RedeemMessages {
        @Comment("Sender output for /redeem failure.\n" + "Sent when skill cap would be exceeded.")
        private String skillCap = "<red>You cannot redeem this many MCMMO Credits into <skill>, due to the Level Cap (<cap>).";
        @Comment("Sender output for /redeem failure.\n" + "Sent when user does not have enough credits.")
        private String notEnoughCredits = "<red>You do not have enough MCMMO Credits to do this!";
        @Comment("Sender output for /redeem success.")
        private String selfRedeem = "<green>Redemption Successful! You have redeemed <amount> Credits into <skill>. You have <credits> Credits remaining.";
        @Comment("Sender output for /redeem success,\n" + "when used on another player.\n" + "Requires --silent flag to be used.")
        private String otherRedeemSender = "<green>Redemption Successful! You have redeemed <amount> Credits into <skill> for <player>. They have <credits> Credits remaining.";
        @Comment("Receiver output for /redeem success,\n" + "when used on another player.\n" + "Requires --silent flag to be used.")
        private String otherRedeemReceiver = "<green>Redemption Successful! <sender> has redeemed <amount> Credits into <skill> for you! You have <credits> Credits remaining.";
    }
}
