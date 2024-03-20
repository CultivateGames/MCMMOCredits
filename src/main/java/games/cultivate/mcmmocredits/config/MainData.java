//
// MIT License
//
// Copyright (c) 2024 Cultivate Games
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

import games.cultivate.mcmmocredits.converters.ConverterProperties;
import games.cultivate.mcmmocredits.database.DatabaseProperties;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * Configuration used to modify settings and messages.
 */
@SuppressWarnings({"FieldMayBeFinal, unused"})
@ConfigSerializable
public final class MainData implements Data {
    private String prefix = "<hover:show_text:'<green><viewer>: <viewer_credits> Credits'><gold><bold>CREDITS</bold> ";
    private String addUser = "<target> has been added to the database!";
    private String argumentParseFailureNoInputWasProvided = "No input was provided";
    private String argumentParseFailureBoolean = "Could not parse boolean from <input>";
    private String argumentParseFailureEnum = "<input> is not one of the following: <acceptableValues>";
    private String argumentParseFailureFlagUnknownFlag = "Unknown flag <flag>";
    private String argumentParseFailureFlagDuplicateFlag = "Duplicate flag <flag>";
    private String argumentParseFailureFlagNoFlagStarted = "No flag started. Don't know what to do with <input>";
    private String argumentParseFailureNumber = "<input> is not a valid number in the range <min> to <max>";
    private String argumentParseFailurePlayer = "<red><input> does not exist in our database!";
    private String argumentParseFailureString = "<input> is not a valid string of type <stringMode>";
    private String argumentParsing = "<red>Error: <argument_error>";
    private String balance = "<green>You have <sender_credits> credits! You have redeemed <sender_redeemed> Credits.";
    private String balanceOther = "<green><target> has <target_credits> credits! They have redeemed <target_redeemed> Credits.";
    private String cancelPrompt = "<red>You have cancelled the current operation.";
    private String commandExecution = "<red>There was an error executing this command!";
    private String commandPrefix = "credits";
    private String creditsAdd = "<green>You have given <amount> Credits to <target>.";
    private String creditsAddAll = "<green>You have given <amount> Credits to all online players!";
    private String creditsAddUser = "<green><amount> Credits have been added to your balance by <sender>. You have <target_credits> Credits.";
    private String creditsPay = "<green>You have paid <amount> Credits to <target>. You now have <sender_credits> Credits.";
    private String creditsPaySameUser = "<red>Users cannot pay themselves! Cancelling transaction...";
    private String creditsPayUser = "<green><sender> has paid you <amount> credits. You have <target_credits> credits.";
    private String creditsRedeem = "<green>You have redeemed <amount> Credits into <skill>. You have <target_credits> Credits remaining.";
    private String creditsRedeemAll = "<green>You have redeemed <amount> Credits into <skill> for all online players!";
    private String creditsRedeemPrompt = "<gray>How many credits would you like to redeem into <green><skill>? <gray>You have <green><sender_credits> <gray>Credits available.";
    private String creditsRedeemSudo = "<green>You have redeemed <amount> Credits into <skill> for <target>. They have <target_credits> Credits remaining.";
    private String creditsRedeemUser = "<green><sender> has redeemed <amount> Credits into <skill> for you! You have <target_credits> Credits remaining.";
    private String creditsSet = "<yellow>You have set <target>'s Credits to <amount>.";
    private String creditsSetAll = "<yellow>You have the credit balance of all online players to <amount>!";
    private String creditsSetUser = "<yellow>Your MCMMO Credit balance has been set to <amount> by <sender>.";
    private String creditsTake = "<red>You have taken <amount> Credits from <target>.";
    private String creditsTakeAll = "<red>You have taken <amount> Credits from  all online players!";
    private String creditsTakeUser = "<red>You had <amount> removed from your Credit balance by <sender>. You now have <target_credits> Credits.";
    private String invalidLeaderboard = "<#FF253C>This leaderboard page is invalid or not enabled!";
    private String invalidSender = "<red>Invalid command sender! You must be of type: <gray><correct_sender>";
    private String invalidSyntax = "<red>Invalid syntax! Correct syntax: <gray><correct_syntax>";
    private String leaderboardEntry = "<rank>. <green><target>: <white><target_credits>";
    private String leaderboardTitle = "<#FF253C>MCMMO Credits Leaderboard";
    private String loginMessage = "<hover:show_text:'<green>You have <sender_credits> credits!'><yellow>Hover to see your MCMMO Credit balance!";
    private String mcmmoProfileFail = "The mcMMO Profile for <target> is not loaded! Aborting operation...";
    private String mcmmoSkillCap = "<red>You cannot redeem this many credits into <skill>, due to the Level Cap (<cap>).";
    private String noPermission = "<hover:show_text:'<red>Required permission: <permission>'><red>You do not have permission to do this!";
    private String notEnoughCredits = "<red>You do not have enough credits to do this!";
    private String notEnoughCreditsOther = "<red><target> does not have enough credits for: <transaction>!";
    private String reload = "<green>The configuration file has been reloaded.";
    private Settings settings = new Settings();
    private ConverterProperties converter = new ConverterProperties();

    @ConfigSerializable
    static class Settings {
        private boolean addUserMessage = true;
        private boolean metricsEnabled = true;
        private boolean leaderboardEnabled = false;
        private int leaderboardPageSize = 10;
        private boolean sendLoginMessage = true;
        private boolean userTabComplete = true;
        private DatabaseProperties database = DatabaseProperties.defaults();
    }
}
