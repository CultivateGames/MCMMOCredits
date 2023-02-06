//
// MIT License
//
// Copyright (c) 2023 Cultivate Games
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.data.DAOProvider;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * Configuration used to modify settings and messages.
 */
@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal, unused"})
public class MainConfig extends Config {
    private static final String HEADER = """
            Repository: https://github.com/CultivateGames/MCMMOCredits
                        
            Argument Parse Failure Messages:
            Messages prepended with "argument-parse" are strings used in the <argument_error> tag.
            The message sent when parsing fails is "argument-parsing". It should include the <argument_error> tag.
            You can format the <argument_error> tag in "argument-parsing" to apply formatting to all argument keys!
                        
            Messages:
            All messages support MiniMessage and Placeholder API placeholders.
            prefix: prefix for all plugin messages.
            add-user: Output for adding user to database.
            argument-parse-failure-no-input-provided: Output when no input was provided to command.
            argument-parse-failure-boolean: Output when invalid boolean was provided to command.
            argument-parse-failure-enum: Output when value is not inside of an enum for command.
            argument-parse-failure-flag-unknown-flag: Output when a command flag is unknown.
            argument-parse-failure-flag-duplicate-flag: Output when a duplicate flag is used in a command.
            argument-parse-failure-flag-no-flag-started: Output when a flag is not started properly.
            argument-parse-failure-flag-missing-argument: Output when a flag is used but a command argument is missing.
            argument-parse-failure-number: Output when an invalid number is provided to command.
            argument-parse-failure-player: Output when a Player cannot be parsed in a command.
            argument-parse-failure-string: Output when an invalid string is provided to command.
            argument-parsing: Outputs the actual argument parsing message. All other keys are templates.
            balance: Output for /credits balance
            balance-other: Output for /credits balance <user>
            cancel-prompt: Output when cancelling chat input.
            command-execution: Output for general command errors.
            credits-add: Output for /credits add <amount> <player>.
            credits-add-user: Receiver output for /credits add <amount> <player>. Disable with --s flag.
            credits-set: Output for /credits set <amount> <player>.
            credits-set-user: Receiver output for /credits set <amount> <player>. Disable with --s flag.
            credits-take: Output for /credits take <amount> <player>.
            credits-take-user: Receiver output for /credits take <amount> <player>. Disable with --s flag.
            edit-config: Output for successfully editing config via GUI.
            edit-config-fail: Output for failing to edit config via GUI due to error.
            edit-config-prompt: Prompt for editing config via GUI.
            invalid-sender: Output for command error where sender is invalid.
            invalid-syntax: Output for command error where command is typed incorrectly.
            login-message: Output when a player joins the server.
            mcmmo-profile-fail: Output when a redemption fails because a player's mcMMO profile is not loaded.
            mcmmo-skill-cap: Output when a redemption is cancelled from a skill's cap level being exceeded.
            no-permission: Output when a player has no permission to execute a command.
            not-enough-credits: Output when a player does not have enough credits for an operation.
            player-unknown: Output when a player does not exist in our data.
            redeem: Output for /credits redeem <amount> <skill>
            redeem-prompt: Prompt for redeeming credits via GUI.
            redeem-sudo: Sender output for /credits redeem <amount> <skill> <user>
            redeem-sudo-user: Receiver output for /credits redeem <amount> <skill> <user>. Disable with --s flag.
            reload: Output when reloading configuration files.
                       
            Settings:
            add-user-message: boolean that toggles console message sent when user is added to database.
            database-type: Database type to use. Supported: SQLITE, MYSQL. Data is not transferred between types.
            debug: boolean that toggles debug messages to be sent during plugin operations.
            send-login-message: boolean that toggles login message for users.
            user-tab-complete: boolean that toggles tab completion for player-based command arguments. Requires restart to change.
            mysql.*: Connection settings for a MySQL database.
            """;
    private String prefix = "<hover:show_text:'<green><sender>: <sender_credits> Credits'><gold><bold>CREDITS</bold> ";
    private String addUser = "<target> has been added to the database!";
    private String argumentParseFailureNoInputWasProvided = "No input was provided";
    private String argumentParseFailureBoolean = "Could not parse boolean from <input>";
    private String argumentParseFailureEnum = "<input> is not one of the following: <acceptableValues>";
    private String argumentParseFailureFlagUnknownFlag = "Unknown flag <flag>";
    private String argumentParseFailureFlagDuplicateFlag = "Duplicate flag <flag>";
    private String argumentParseFailureFlagNoFlagStarted = "No flag started. Don't know what to do with <input>";
    private String argumentParseFailureFlagMissingArgument = "Missing argument for <flag>";
    private String argumentParseFailureNumber = "<input> is not a valid number in the range <min> to <max>";
    private String argumentParseFailurePlayer = "No player found for input <input>";
    private String argumentParseFailureString = "<input> is not a valid string of type <stringMode>";
    private String argumentParsing = "<red>Argument Error: <argument_error>";
    private String balance = "<green>You have <sender_credits> MCMMO Credits! You have redeemed <sender_redeemed> Credits.";
    private String balanceOther = "<green><target> has <target_credits> MCMMO Credits! They have redeemed <target_redeemed> Credits.";
    private String cancelPrompt = "<red>You have cancelled the current operation.";
    private String commandExecution = "<red>There was an error executing this command!";
    private String creditsAdd = "<green>You have given <amount> Credits to <target>.";
    private String creditsSet = "<yellow>You have set <target>'s Credits to <amount>.";
    private String creditsTake = "<red>You have taken <amount> Credits from <target>.";
    private String creditsAddUser = "<green><amount> Credits have been added to your balance by <sender>. You have <target_credits> Credits.";
    private String creditsSetUser = "<yellow>Your MCMMO Credit balance has been set to <amount> by <sender>.";
    private String creditsTakeUser = "<red>You had <amount> taken out of your Credit balance by <sender>. You now have <target_credits> Credits.";
    private String editConfig = "<green>You have changed <gray><setting> <green>to <gray><change>.";
    private String editConfigFail = "<red>There was an error while changing settings, operation aborted.";
    private String editConfigPrompt = "<red>Enter the new value for <gray><setting><red>, or type 'cancel' to abort.";
    private String invalidSender = "<red>Invalid command sender! You must be of type: <gray><correct_sender>";
    private String invalidSyntax = "<red>Invalid syntax! Correct syntax: <gray><correct_syntax>";
    private String loginMessage = "<hover:show_text:'<green>You have <sender_credits> MCMMO Credits!'><yellow>Hover to see your MCMMO Credit balance!";
    private String mcmmoProfileFail = "The mcMMO Profile for <target> is not loaded! Aborting operation...";
    private String mcmmoSkillCap = "<red>You cannot redeem this many MCMMO Credits into <skill>, due to the Level Cap (<cap>).";
    private String noPermission = "<hover:show_text:'<red>Required permission: <required_permission>'><red>You do not have permission to do this!";
    private String notEnoughCredits = "<red>You do not have enough MCMMO Credits to do this!";
    private String playerUnknown = "<red>This player does not exist in our database!";
    private String redeem = "<green>Redemption Successful! You have redeemed <amount> Credits into <skill>. You have <target_credits> Credits remaining.";
    private String redeemPrompt = "<gray>How many credits would you like to redeem into <green><skill>? <gray>You have <green><sender_credits> Credits available.";
    private String redeemSudo = "<green>Redemption Successful! You have redeemed <amount> Credits into <skill> for <target>. They have <target_credits> Credits remaining.";
    private String redeemSudoUser = "<green>Redemption Successful! <sender> has redeemed <amount> Credits into <skill> for you! You have <target_credits> Credits remaining.";
    private String reload = "<green>The configuration file has been reloaded.";

    private Settings settings = new Settings();

    /**
     * Constructs the configuration.
     */
    MainConfig() {
        super(MainConfig.class, "config.yml", HEADER);
    }

    /**
     * Types of possible Database implementations.
     *
     * @see DAOProvider
     */
    public enum DatabaseType {
        MYSQL, SQLITE
    }

    /**
     * Settings used to control the plugin.
     */
    @ConfigSerializable
    static class Settings {
        private boolean addUserMessage = true;
        private DatabaseType databaseType = DatabaseType.SQLITE;
        private boolean debug = false;
        private boolean sendLoginMessage = true;
        private boolean userTabComplete = true;
        private DatabaseProperties mysql = new DatabaseProperties("127.0.0.1", "database", "root", "passw0rd+", 3306, true);
    }

    /**
     * Properties used in creation of the Database.
     *
     * @param host     Host of the Database. Typically, an IP address.
     * @param name     Name of the Database.
     * @param user     Name of the Database user.
     * @param password Password for the Database user.
     * @param port     Port where the Database instance is located.
     * @param ssl      If useSSL is used in the connection URL.
     * @see DAOProvider
     */
    @ConfigSerializable
    public record DatabaseProperties(String host, String name, String user, String password, int port, boolean ssl) {
    }
}
