package games.cultivate.mcmmocredits.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * This class is responsible for generating the default messages.conf file. All values here represent defaults.
 */
@ConfigSerializable
public class MessageConfig {
    @Comment("Prefix for all plugin messages.")
    private final String prefix = "<gold><bold>CREDITS</bold> ";

    @Comment("Shown to user when invalid arguments are used in a command.")
    private final String invalid_arguments = "<red>Invalid arguments!";

    @Comment("Shown to user when they use an invalid number.")
    private final String must_be_number = "<red>You need to specify a valid number.";

    @Comment("Shown to user when they do not have permission to execute a command!")
    private final String no_perms = "<red>You do not have permission to do this!";

    @Comment("Shown to user when they check their own MCMMO Credits amount with /credits.")
    private final String credits_check_self = "<green>You have %credits% MCMMO Credits!";

    @Comment("Shown to user when they check the MCMMO Credit balance of another user with /credits.")
    private final String credits_check_other = "<green>%player% has %credits% MCMMO Credits!";

    @Comment("Shown to user when they use the command system and populate it with a player that does not exist.")
    private final String player_does_not_exist = "<red>This player does not exist in our database!";

    @Comment("Shown to user when they successfully reload the Plugin.")
    private final String reload_successful = "<green>The relevant configuration files have been reloaded!";

    @Comment("Shown to user when they try to redeem MCMMO Credits to go over a skill's level cap.")
    private final String redeem_skill_cap = "<red>You cannot redeem this many MCMMO Credits into %skill%, due to the Level Cap (%cap%).";

    @Comment("Shown to user when they try to redeem more MCMMO Credits than they have available.")
    private final String redeem_not_enough_credits = "<red>You do not have enough MCMMO Credits to do this!";

    @Comment("Shown to user when they successfully redeem MCMMO Credits into a skill.")
    private final String redeem_successful = "<green>Redemption Successful! You have redeemed %amount% Credits into %skill%. You have %credits% Credits remaining.";

    @Comment("Shown to user when they successfully redeem MCMMO Credits into a skill.")
    private final String redeem_successful_other = "<green>Redemption Successful! You have redeemed %amount% Credits into %skill% for %player%. They have %credits% Credits remaining.";

    @Comment("Shown to user on login if send_login_message is set to true in settings.conf")
    private final String login_message = "<hover:show_text:'<green>You have %credits% MCMMO Credits!'><yellow>Hover here to see how many MCMMO Credits you have!";

    @Comment("Shown to user when a user adds MCMMO Credits to another user. %credits% placeholder will show previous balance since we are updating credit balances asynchronously.")
    private final String modify_credits_add = "<green>You have given %amount% Credits to %player%";

    @Comment("Shown to user when a user sets another user's balance to an amount. %credits% placeholder will show previous balance since we are updating credit balances asynchronously.")
    private final String modify_credits_set = "<yellow>You have set %player%'s Credits to %amount%!";

    @Comment("Shown to user when a user takes MCMMO Credits from another user. %credits% placeholder will show previous balance since we are updating credit balances asynchronously.")
    private final String modify_credits_take = "<red>You have taken %amount% Credits from %player%";

    @Comment("Message printed to console when a new user is added to the MCMMO Credits database")
    private final String database_console_message = "<white>%player% has been added to the database!";
}
