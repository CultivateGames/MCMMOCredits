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

import games.cultivate.mcmmocredits.converters.ConverterType;
import games.cultivate.mcmmocredits.database.DatabaseProperties;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.nio.file.Path;

/**
 * Configuration used to modify settings and messages.
 */
@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal, unused"})
public class MainConfig extends Config {
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
    private String commandPrefix = "credits";
    private String creditsAdd = "<green>You have given <amount> Credits to <target>.";
    private String creditsSet = "<yellow>You have set <target>'s Credits to <amount>.";
    private String creditsTake = "<red>You have taken <amount> Credits from <target>.";
    private String creditsAddUser = "<green><amount> Credits have been added to your balance by <sender>. You have <target_credits> Credits.";
    private String creditsSetUser = "<yellow>Your MCMMO Credit balance has been set to <amount> by <sender>.";
    private String creditsTakeUser = "<red>You had <amount> taken out of your Credit balance by <sender>. You now have <target_credits> Credits.";
    private String editConfig = "<green>You have changed <gray><setting> <green>to <gray><change>.";
    private String editConfigFail = "<red>There was an error while changing settings, operation aborted.";
    private String editConfigPrompt = "<red>Enter the new value for <gray><setting><red>, or type 'cancel' to abort.";
    private String invalidLeaderboard = "<#FF253C>This leaderboard page is invalid or not enabled!";
    private String invalidSender = "<red>Invalid command sender! You must be of type: <gray><correct_sender>";
    private String invalidSyntax = "<red>Invalid syntax! Correct syntax: <gray><correct_syntax>";
    private String leaderboardEntry = "<rank>. <green><target>: <white><target_credits>";
    private String leaderboardTitle = "<#FF253C>MCMMO Credits Leaderboard";
    private String loginMessage = "<hover:show_text:'<green>You have <sender_credits> MCMMO Credits!'><yellow>Hover to see your MCMMO Credit balance!";
    private String mcmmoProfileFail = "The mcMMO Profile for <target> is not loaded! Aborting operation...";
    private String mcmmoSkillCap = "<red>You cannot redeem this many MCMMO Credits into <skill>, due to the Level Cap (<cap>).";
    private String noPermission = "<hover:show_text:'<red>Required permission: <permission>'><red>You do not have permission to do this!";
    private String notEnoughCredits = "<red>You do not have enough MCMMO Credits to do this!";
    private String playerUnknown = "<red>This player does not exist in our database!";
    private String redeem = "<green>Redemption Successful! You have redeemed <amount> Credits into <skill>. You have <target_credits> Credits remaining.";
    private String redeemPrompt = "<gray>How many credits would you like to redeem into <green><skill>? <gray>You have <green><sender_credits> Credits available.";
    private String redeemSudo = "<green>Redemption Successful! You have redeemed <amount> Credits into <skill> for <target>. They have <target_credits> Credits remaining.";
    private String redeemSudoUser = "<green>Redemption Successful! <sender> has redeemed <amount> Credits into <skill> for you! You have <target_credits> Credits remaining.";
    private String reload = "<green>The configuration file has been reloaded.";

    private Settings settings = new Settings();
    private Conversion converter = new Conversion();

    /**
     * Constructs the configuration.
     */
    public MainConfig() {
        super(MainConfig.class, "config.yml");
    }

    public MainConfig(final Path path) {
        super(MainConfig.class, "config.yml", path);
    }

    /**
     * Settings used to control the plugin.
     */
    @ConfigSerializable
    static class Settings {
        private boolean addUserMessage = true;
        private boolean bstatsMetricsEnabled = true;
        private boolean debug = false;
        private boolean leaderboardEnabled = true;
        private int leaderboardPageSize = 10;
        private boolean sendLoginMessage = true;
        private boolean userTabComplete = true;
        private DatabaseProperties database = DatabaseProperties.defaults();
    }

    @ConfigSerializable
    static class Conversion {
        private boolean enabled = false;
        private ConverterType type = ConverterType.INTERNAL_SQLITE;
        private InternalConversion internal = new InternalConversion();
        private ExternalConversion external = new ExternalConversion();
    }

    @ConfigSerializable
    static class InternalConversion {
        private DatabaseProperties properties = DatabaseProperties.defaults();
    }

    @ConfigSerializable
    static class ExternalConversion {
        private long retryDelay = 60000L;
        private long attemptDelay = 100L;
    }
}
