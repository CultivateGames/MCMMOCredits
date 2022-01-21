package games.cultivate.mcmmocredits.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

/**
 * This class is responsible for generating the default messages.conf file. All values here represent defaults.
 */
@ConfigSerializable
public class MessageConfig {
    @Comment("Messages sent at various times.")
    @Setting("general")
    private GeneralMessages generalMessages = new GeneralMessages();

    @Comment("Messages sent when there are general exceptions when running commands. Specific errors are found within the specific command section.")
    @Setting("exceptions")
    private ExceptionMessages exceptionMessages = new ExceptionMessages();

    @Comment("Messages sent during command execution.")
    @Setting("commands")
    private CommandMessages commandMessages = new CommandMessages();

    @ConfigSerializable
    protected static final class CommandMessages {
        @Comment("Messages sent during execution of /credits.")
        @Setting("credits")
        private CreditsCommandMessages credits = new CreditsCommandMessages();

        @Comment("Messages sent during execution of /modifycredits.")
        @Setting("modify-credits")
        private ModifyCreditsCommandMessages modify_credits = new ModifyCreditsCommandMessages();

        @Comment("Messages sent during execution of /redeem.")
        @Setting("redeem")
        private RedeemCommandMessages redeem = new RedeemCommandMessages();
    }

    @ConfigSerializable
    protected static final class CreditsCommandMessages {
        @Comment("Shown to user when they check their own MCMMO Credits amount with /credits.")
        private String balance_self = "<green>You have %credits% MCMMO Credits!";
        @Comment("Shown to user when they check the MCMMO Credit balance of another user with /credits <player>.")
        private String balance_other = "<green>%player% has %credits% MCMMO Credits!";
        @Comment("Shown to user when they successfully use /credits reload.")
        private String reload_successful = "<green>The relevant configuration files have been reloaded!";
    }

    @ConfigSerializable
    protected static final class ModifyCreditsCommandMessages {
        @Comment("Shown to user when they use the /modifycredits add command.")
        private String add_sender = "<green>You have given %amount% Credits to %player%";
        @Comment("Shown to user when they use the /modifycredits set command.")
        private String set_sender = "<yellow>You have set %player%'s Credits to %amount%!";
        @Comment("Shown to user when they use the /modifycredits take command.")
        private String take_sender = "<red>You have taken %amount% Credits from %player%";
        @Comment("Shown to user when they are the target of a /modifycredits add command, and command feedback is enabled.")
        private String add_receiver = "<green>You have given %amount% Credits to %player%";
        @Comment("Shown to user when they are the target of a /modifycredits set command, and command feedback is enabled.")
        private String set_receiver = "<yellow>You have set %player%'s Credits to %amount%!";
        @Comment("Shown to user when they are the target of a /modifycredits take command, and command feedback is enabled.")
        private String take_receiver = "<red>You have taken %amount% Credits from %player%";
    }

    @ConfigSerializable
    protected static final class RedeemCommandMessages {
        @Comment("Shown to user when they try to redeem MCMMO Credits to go over a skill's level cap.")
        private String skill_cap = "<red>You cannot redeem this many MCMMO Credits into %skill%, due to the Level Cap (%cap%).";
        @Comment("Shown to user when they try to redeem more MCMMO Credits than they have available.")
        private String not_enough_credits = "<red>You do not have enough MCMMO Credits to do this!";
        @Comment("Shown to user when they successfully redeem MCMMO Credits into a skill.")
        private String successful_self = "<green>Redemption Successful! You have redeemed %amount% Credits into %skill%. You have %credits% Credits remaining.";
        @Comment("Shown to user when they successfully redeem MCMMO Credits into a skill for another user.")
        private String successful_sender = "<green>Redemption Successful! You have redeemed %amount% Credits into %skill% for %player%. They have %credits% Credits remaining.";
        @Comment("Shown to user when they successfully have MCMMO Credits redeemed for them, and command feedback is not silent.")
        private String successful_receiver = "<green>Redemption Successful! You have redeemed %amount% Credits into %skill% for %player%. They have %credits% Credits remaining.";

    }

    @ConfigSerializable
    protected static final class ExceptionMessages {
        @Comment("Shown to user when invalid arguments are used in a command.")
        private String invalid_arguments = "<red>Invalid arguments!";
        @Comment("Shown to user when they use an invalid number.")
        private String must_be_number = "<red>You need to specify a valid number.";
        @Comment("Shown to user when they do not have permission to execute a command!")
        private String no_perms = "<red>You do not have permission to do this!";
        @Comment("Shown to user when they use the command system and populate it with a player that does not exist.")
        private String player_does_not_exist = "<red>This player does not exist in our database!";
    }

    @ConfigSerializable
    protected static final class GeneralMessages {
        @Comment("Prefix for all plugin messages.")
        private String prefix = "<gold><bold>CREDITS</bold> ";
        @Comment("Shown to user on login if send_login_message is set to true in settings.conf")
        private String login_message = "<hover:show_text:'<green>You have %credits% MCMMO Credits!'><yellow>Hover here to see how many MCMMO Credits you have!";
        @Comment("Message printed to console when a new user is added to the MCMMO Credits database")
        private String database_console_message = "<white>%player% has been added to the database!";
    }
}
