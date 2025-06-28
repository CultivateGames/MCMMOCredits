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

import org.incendo.cloud.bukkit.BukkitCaptionKeys;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.StandardCaptionKeys;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"FieldMayBeFinal", "unused", "FieldCanBeLocal"})
@ConfigSerializable
public final class Captions implements Data {
    public static final Caption BALANCE_SELF = Caption.of("balance.self");
    public static final Caption BALANCE_OTHER = Caption.of("balance.other");
    public static final Caption ADD_SENDER = Caption.of("add.sender");
    public static final Caption ADD_TARGET = Caption.of("add.target");
    public static final Caption ADD_ALL = Caption.of("add.all");
    public static final Caption SET_SENDER = Caption.of("set.sender");
    public static final Caption SET_TARGET = Caption.of("set.target");
    public static final Caption SET_ALL = Caption.of("set.all");
    public static final Caption TAKE_SENDER = Caption.of("take.sender");
    public static final Caption TAKE_TARGET = Caption.of("take.target");
    public static final Caption TAKE_ALL = Caption.of("take.all");
    public static final Caption PAY_SENDER = Caption.of("pay.sender");
    public static final Caption PAY_TARGET = Caption.of("pay.target");
    public static final Caption PAY_ALL = Caption.of("pay.all");
    public static final Caption PAY_SAME_USER = Caption.of("pay.same.user");
    public static final Caption REDEEM_SENDER = Caption.of("redeem.sender");
    public static final Caption REDEEM_TARGET = Caption.of("redeem.target");
    public static final Caption REDEEM_ALL = Caption.of("redeem.all");
    public static final Caption REDEEM_PROMPT = Caption.of("redeem.prompt");
    public static final Caption REDEEM_SUDO = Caption.of("redeem.sudo");
    public static final Caption TOP_INVALID = Caption.of("top.invalid");
    public static final Caption TOP_ENTRY_FORMAT = Caption.of("top.entry.format");
    public static final Caption TOP_TITLE = Caption.of("top.title");
    public static final Caption MESSAGE_PREFIX = Caption.of("message.prefix");
    public static final Caption LOGIN_MESSAGE = Caption.of("login.message");
    public static final Caption CANCEL_TEXT_PROMPT = Caption.of("cancel.text.prompt");
    public static final Caption NOT_ENOUGH_CREDITS = Caption.of("not.enough.credits");
    public static final Caption MCMMO_PROFILE_ERROR = Caption.of("mcmmo.profile.error");
    public static final Caption MCMMO_SKILL_CAP_ERROR = Caption.of("mcmmo.skill.cap.error");
    public static final Caption RELOAD_SUCCESS = Caption.of("reload.success");
    public static final Caption RELOAD_FAILURE = Caption.of("reload.failure");
    public static final Caption USER_UPDATE_FAILURE = Caption.of("user.update.failure");
    private int version = 1;
    private Map<Caption, String> messages;

    public Captions() {
        this.messages = new HashMap<>();
        this.messages.put(Captions.BALANCE_SELF, "<green>You have <sender_credits> credits! You have redeemed <sender_redeemed> Credits.");
        this.messages.put(Captions.BALANCE_OTHER, "<green><target_name> has <target_credits> credits! They have redeemed <target_redeemed> Credits.");
        this.messages.put(Captions.ADD_SENDER, "<green>You have given <amount> Credits to <target_name>.");
        this.messages.put(Captions.ADD_TARGET, "<green><amount> Credits have been added to your balance by <sender_name>. You have <target_credits> Credits.");
        this.messages.put(Captions.ADD_ALL, "<green>You have given <amount> Credits to all online players!");
        this.messages.put(Captions.SET_SENDER, "<yellow>You have set <target_name>'s Credits to <amount>.");
        this.messages.put(Captions.SET_TARGET, "<yellow>Your MCMMO Credit balance has been set to <amount> by <sender_name>.");
        this.messages.put(Captions.SET_ALL, "<yellow>You have the credit balance of all online players to <amount>!");
        this.messages.put(Captions.TAKE_SENDER, "<red>You have taken <amount> Credits from <target_name>.");
        this.messages.put(Captions.TAKE_TARGET, "<red>You had <amount> removed from your Credit balance by <sender_name>. You now have <target_credits> Credits.");
        this.messages.put(Captions.TAKE_ALL, "<red>You have taken <amount> Credits from  all online players!");
        this.messages.put(Captions.PAY_SENDER, "<green>You have paid <amount> Credits to <target_name>. You now have <sender_credits> Credits.");
        this.messages.put(Captions.PAY_TARGET, "<green><sender_name> has paid you <amount> credits. You have <target_credits> credits.");
        this.messages.put(Captions.PAY_ALL, "<green>You have paid <amount> Credits to all online players!");
        this.messages.put(Captions.PAY_SAME_USER, "<red>Users cannot pay themselves! Cancelling transaction...");
        this.messages.put(Captions.REDEEM_SENDER, "<green>You have redeemed <amount> Credits into <skill>. You have <target_credits> Credits remaining.");
        this.messages.put(Captions.REDEEM_TARGET, "<green><sender_name> has redeemed <amount> Credits into <skill> for you! You have <target_credits> Credits remaining.");
        this.messages.put(Captions.REDEEM_ALL, "<green>You have redeemed <amount> Credits into <skill> for all online players!");
        this.messages.put(Captions.REDEEM_PROMPT, "<gray>How many credits would you like to redeem into <green><skill>? <gray>You have <green><sender_credits> <gray>Credits available.");
        this.messages.put(Captions.REDEEM_SUDO, "<green>You have redeemed <amount> Credits into <skill> for <target_name>. They have <target_credits> Credits remaining.");
        this.messages.put(Captions.TOP_INVALID, "<#FF253C>This leaderboard page is invalid or not enabled!");
        this.messages.put(Captions.TOP_ENTRY_FORMAT, "<rank>. <green><target_name>: <white><target_credits>");
        this.messages.put(Captions.TOP_TITLE, "<#FF253C>MCMMO Credits Leaderboard");
        this.messages.put(Captions.MESSAGE_PREFIX, "<hover:show_text:'<green><sender_name>: <sender_credits> Credits'><gold><bold>CREDITS</bold> ");
        this.messages.put(Captions.LOGIN_MESSAGE, "<hover:show_text:'<green>You have <sender_credits> credits!'><yellow>Hover to see your MCMMO Credit balance!");
        this.messages.put(Captions.CANCEL_TEXT_PROMPT, "<red>You have cancelled the current operation.");
        this.messages.put(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_BOOLEAN, "<red>Could not parse boolean from <input>");
        this.messages.put(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_AGGREGATE_MISSING_INPUT, "<red>No command input was provided. Please type a command.");
        this.messages.put(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_NUMBER, "<red><input> is not a valid number in the range <min> to <max>");
        this.messages.put(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_ENUM, "<red><input> is not one of the following: <acceptableValues>");
        this.messages.put(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_UNKNOWN_FLAG, "<red>Flag is unknown! Check the following: <flag>");
        this.messages.put(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_DUPLICATE_FLAG, "<red>A duplicate command flag has been found in input: <input>");
        this.messages.put(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_NO_FLAG_STARTED, "<red>No flag started. A command flag could not be parsed from: <input>");
        this.messages.put(StandardCaptionKeys.EXCEPTION_INVALID_ARGUMENT, "<red>Error: <error>");
        this.messages.put(StandardCaptionKeys.EXCEPTION_NO_SUCH_COMMAND, "<red>This command does not exist. Input: <error>");
        this.messages.put(StandardCaptionKeys.EXCEPTION_NO_PERMISSION, "<hover:show_text:'<red>Required permission: <permission>'><red>You do not have permission to do this!");
        this.messages.put(StandardCaptionKeys.EXCEPTION_INVALID_SYNTAX, "<red>Invalid syntax! Correct syntax: <gray><error>");
        this.messages.put(StandardCaptionKeys.EXCEPTION_INVALID_SENDER, "<red>Invalid command sender! You must be of type: <gray><error>");
        this.messages.put(BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER, "<red><input> does not exist as a user. Check command input!");
        this.messages.put(Captions.MCMMO_SKILL_CAP_ERROR, "<red>You cannot redeem these credits due to the skill level cap (<cap>).");
        this.messages.put(Captions.MCMMO_PROFILE_ERROR, "<red>The mcMMO Profile for <target_name> is not loaded! Aborting operation...");
        this.messages.put(Captions.NOT_ENOUGH_CREDITS, "<red>You do not have enough credits to do this!");
        this.messages.put(Captions.RELOAD_SUCCESS, "<green>Configurations have been successfully reloaded!");
        this.messages.put(Captions.RELOAD_FAILURE, "<red>There was an issue reloading configurations, check console!");
        this.messages.put(Captions.USER_UPDATE_FAILURE, "<red>There was an issue updating a user in the database, transaction aborted...");
    }

    public Map<Caption, String> messages() {
        return this.messages;
    }

    public int version() {
        return this.version;
    }

    public String get(final Caption caption) {
        return this.messages.get(caption);
    }
}
