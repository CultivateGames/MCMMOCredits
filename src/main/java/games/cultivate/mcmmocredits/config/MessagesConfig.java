package games.cultivate.mcmmocredits.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal, unused"})
public class MessagesConfig {
    @Comment("Sender output for command exceptions.")
    @Setting("exceptions")
    private ExceptionMessages exceptionMessages = new ExceptionMessages();
    @Comment("Output for command execution.\n" + "Includes command-specific errors.")
    @Setting("commands")
    private CommandMessages commandMessages = new CommandMessages();
    @Comment("Output for all other messages.")
    @Setting("general")
    private GeneralMessages generalMessages = new GeneralMessages();

    public ExceptionMessages getExceptionMessages() {
        return exceptionMessages;
    }

    public CommandMessages getCommandMessages() {
        return commandMessages;
    }

    public GeneralMessages getGeneralMessages() {
        return generalMessages;
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
        private String add_player_message = "<player> has been added to the database!";
        @Comment("Sender output for cancelled prompt.")
        private String cancel_prompt = "<red>You have cancelled the current modification.";
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
        @Comment("Sender prompt for /credits menu redeem prompt.\n" + "Placeholders: <player>, <credits>, <skill>, <cap>, PAPI")
        private String menu_redeem_prompt = "<gray>How many credits would you like to redeem into <green><skill>? You have <credits> Credits available.";
        @Comment("Sender prompt for /credits menu settings/messages.\n" + "Placeholders: <player>, <credits>, <option>, <current_value>, PAPI")
        private String menu_editing_prompt = "<red>Enter the new value for <gray><option>:";
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

}
