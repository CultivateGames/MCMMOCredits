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
package games.cultivate.mcmmocredits.command;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.config.Captions;
import games.cultivate.mcmmocredits.config.ConfigManager;
import games.cultivate.mcmmocredits.inject.CustomPerm;
import games.cultivate.mcmmocredits.menu.MenuService;
import games.cultivate.mcmmocredits.messages.Message;
import games.cultivate.mcmmocredits.messages.MessageService;
import games.cultivate.mcmmocredits.transaction.RedeemTransaction;
import games.cultivate.mcmmocredits.transaction.Transaction;
import games.cultivate.mcmmocredits.transaction.TransactionContext;
import games.cultivate.mcmmocredits.transaction.TransactionFactory;
import games.cultivate.mcmmocredits.transaction.TransactionType;
import games.cultivate.mcmmocredits.user.CommandSource;
import games.cultivate.mcmmocredits.user.CommandSourceData;
import games.cultivate.mcmmocredits.user.UserCommandSource;
import games.cultivate.mcmmocredits.user.UserService;
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Range;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.StandardCaptionKeys;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Handles all commands.
 */
@SuppressWarnings({"checkstyle:linelength", "unused"})
@Command("credits")
public final class Commands {
    private static final Logger LOGGER = LoggerFactory.getLogger(Commands.class);
    private final UserService userService;
    private final MenuService menuService;
    private final ConfigManager configs;
    private final MessageService messageService;
    private final TransactionFactory factory;

    @Inject
    public Commands(final UserService userService, final MenuService menuService, final ConfigManager configs, final MessageService messageService, final TransactionFactory factory) {
        this.userService = userService;
        this.menuService = menuService;
        this.configs = configs;
        this.messageService = messageService;
        this.factory = factory;
    }

    @Command("<type> <amount> [target] [skill]")
    @CustomPerm
    private void command(CommandSource source, final TransactionType type, @Range(min= "1") final int amount, final @Nullable CommandSourceData target, final @Nullable PrimarySkillType skill, @Flag("s") final boolean silent, @Flag("all") final boolean all) {
        // /credits all type amount [skill]
        // /credits



        //throw for no skill being available.
        //throw because pay cannot be from console.
        //throw because pay cannot be to same user.
        //all transaction takes priority. Ignore if target is also supplied.
        //If user, process transaction for user first to pass updated user to batch process.
        //throw for invalid command source. at this point we know that target is null and source is console.
        if (type == TransactionType.REDEEM && skill == null) {
            this.messageService.get(StandardCaptionKeys.EXCEPTION_INVALID_SYNTAX).addUser(source).send(source);
            return;
        }
        if (type == TransactionType.PAY && source.isConsole()) {
            this.messageService.get(StandardCaptionKeys.EXCEPTION_INVALID_SENDER).addUser(source).send(source);
            return;
        }
        if (type == TransactionType.PAY && source.data() == target) {
            this.messageService.get(Captions.PAY_SAME_USER).addUser(source).send(source);
            return;
        }
        boolean console = source.isConsole();
        TransactionContext template = TransactionContext.create(source, source.data(), amount, type);
        if (all) {
            if (!console) {
                this.factory.create(template).runForTarget(silent);
                CommandSourceData result = this.userService.getUser(source).orElseThrow();
                source = new UserCommandSource(source.stack(), result);
            }
            Set<CommandSourceData> users = this.userService.getOnlineUsers();
            users.remove(source.data());
            for (CommandSourceData u : users) {
                this.factory.create(template.replacePair(source, u)).runForTarget(silent);
            }
            this.messageService.get(template.allCaption()).addUser(source).send(source);
            return;
        }
        if (target != null) {
            template = template.withData(target);
            this.factory.create(template).runForTarget(silent);
            return;
        }
        if (!console) {
            this.factory.create(template).runForSender();
            return;
        }
        this.messageService.get(StandardCaptionKeys.EXCEPTION_INVALID_SENDER).addUser(source).send(source);
    }

    /**
     * Command to modify credits based on the command syntax.
     * Examples could be:
     * /credits add 10: Add 10 credits to self.
     * /credits add 10 username [--s]: Add 10 credits to username. If --s is present, target will not receive a message.
     * /credits add 10 --all: Add 10 credits to all online players if eligible.
     *
     * @param sender The sender
     * @param amount The amount
     */
    @Command("add <amount>")
    @Permission("mcmmocredits.add.self")
    @CommandDescription("Add credits to your own balance.")
    public void addCreditsSolo(final UserCommandSource sender, final @Range(min = "1") int amount) {
        TransactionContext ctx = TransactionContext.create(sender, amount, TransactionType.ADD);
        Transaction transaction = this.factory.create(ctx);
        transaction.runForSender();
    }

    @Command("add <amount> <target>")
    @Permission("mcmmocredits.add.other")
    @CommandDescription("Add credits to someone else's balance.")
    public void addCreditsPair(final CommandSource sender, final CommandSourceData target, final @Range(min = "1") int amount, final @Flag("s") boolean silent) {
        TransactionContext ctx = TransactionContext.create(sender, target, amount, TransactionType.ADD);
        Transaction transaction = this.factory.create(ctx);
        transaction.runForTarget(silent);
    }

    @Command("addall <amount>")
    @Permission("mcmmocredits.add.all")
    @CommandDescription("Add credits to all online players, if they are eligible.")
    public void addCreditsAll(final CommandSource sender, final @Range(min = "1") int amount, final boolean silent) {
        TransactionContext ctx = TransactionContext.create(sender, sender.data(), amount, TransactionType.ADD);
        this.applyAll(sender, ctx, Captions.ADD_ALL, silent);
    }

    @Command("set <amount>")
    @Permission("mcmmocredits.set.self")
    @CommandDescription("Set your own credit balance.")
    public void setCreditsSolo(final UserCommandSource sender, final @Range(min = "0") int amount) {
        TransactionContext ctx = TransactionContext.create(sender, amount, TransactionType.SET);
        Transaction transaction = this.factory.create(ctx);
        transaction.runForSender();
    }

    @Command("set <amount> <target>")
    @Permission("mcmmocredits.set.other")
    @CommandDescription("Set someone else's credit balance to a new amount.")
    public void setCreditsPair(final CommandSource sender, final CommandSourceData target, final @Range(min = "1") int amount, final @Flag("s") boolean silent) {
        TransactionContext ctx = TransactionContext.create(sender, target, amount, TransactionType.SET);
        Transaction transaction = this.factory.create(ctx);
        transaction.runForTarget(silent);
    }

    @Command("setall <amount>")
    @Permission("mcmmocredits.set.all")
    @CommandDescription("Set a specific credit balance for all online players, if they are eligible.")
    public void setCreditsAll(final CommandSource sender, final @Range(min = "0") int amount, final @Flag("s") boolean silent) {
        TransactionContext ctx = TransactionContext.create(sender, sender.data(), amount, TransactionType.SET);
        this.applyAll(sender, ctx, Captions.SET_ALL, silent);
    }

    @Command("take <amount>")
    @Permission("mcmmocredits.take.self")
    @CommandDescription("Take credits from your own balance.")
    public void takeCreditsSolo(final UserCommandSource sender, final @Range(min = "1") int amount) {
        TransactionContext ctx = TransactionContext.create(sender, amount, TransactionType.TAKE);
        Transaction transaction = this.factory.create(ctx);
        transaction.runForSender();
    }

    @Command("take <amount> <target>")
    @Permission("mcmmocredits.add.other")
    @CommandDescription("Add credits to someone else's balance.")
    public void takeCreditsPair(final CommandSource sender, final CommandSourceData target, final @Range(min = "1") int amount, final @Flag("s") boolean silent) {
        TransactionContext ctx = TransactionContext.create(sender, target, amount, TransactionType.TAKE);
        Transaction transaction = this.factory.create(ctx);
        transaction.runForTarget(silent);
    }

    @Command("takeall <amount>")
    @Permission("mcmmocredits.take.all")
    @CommandDescription("Take credits from all online players, if they are eligible.")
    public void takeCreditsAll(final CommandSource sender, final @Range(min = "1") int amount, final @Flag("s") boolean silent) {
        TransactionContext ctx = TransactionContext.create(sender, sender.data(), amount, TransactionType.TAKE);
        this.applyAll(sender, ctx, Captions.TAKE_ALL, silent);
    }

    @Command("redeem <amount> <skill>")
    @Permission("mcmmocredits.redeem.self")
    @CommandDescription("Redeem credits for MCMMO Skill levels.")
    public void redeemCreditsSolo(final UserCommandSource sender, final @Range(min = "1") int amount, final PrimarySkillType skill) {
        TransactionContext ctx = TransactionContext.create(sender, amount, TransactionType.REDEEM);
        RedeemTransaction transaction = this.factory.createRedeem(ctx);
        transaction.runForSender(skill);
    }

    @Command("redeem <amount> <skill> <target>")
    @Permission("mcmmocredits.redeem.other")
    @CommandDescription("Redeem credits for MCMMO Skill levels for another user.")
    public void redeemCreditsPair(final CommandSource sender, final @Range(min = "1") int amount, final PrimarySkillType skill, final CommandSourceData target, final @Flag("s") boolean silent) {
        TransactionContext ctx = TransactionContext.create(sender, target, amount, TransactionType.REDEEM);
        RedeemTransaction transaction = this.factory.createRedeem(ctx);
        transaction.runForTarget(skill, silent);
    }

    @Command("redeemall <amount> <skill>")
    @Permission("mcmmocredits.redeem.all")
    @CommandDescription("Redeem credits for MCMMO Skill levels for all online users.")
    public void redeemCreditsAll(final CommandSource sender, final @Range(min = "0") int amount, final PrimarySkillType skill, @Flag("s") boolean silent) {
        TransactionContext ctx = TransactionContext.create(sender, sender.data(), amount, TransactionType.REDEEM);
        if (!sender.isConsole()) {
            this.factory.createRedeem(ctx.withData(sender.data())).runForSender(skill);
            CommandSourceData result = this.userService.getUser(sender.data().uuid()).orElseThrow();
            UserCommandSource ucs = new UserCommandSource(sender).withData(result);
            this.userService.getOnlineUsers(ucs)
                    .forEach(u -> this.factory.createRedeem(ctx.replacePair(ucs, u)).runForTarget(skill, silent));
            this.messageService.get(Captions.REDEEM_ALL).addUser(ucs).send(ucs);
            return;
        }
        this.userService.getOnlineUsers()
                .forEach(user -> this.factory.createRedeem(ctx.withData(user)).runForTarget(skill, silent));
        this.messageService.get(Captions.REDEEM_ALL).addUser(sender).send(sender);
    }

    @Command("balance")
    @Permission("mcmmocredits.balance.self")
    @CommandDescription("Allows user to check credit statistics.")
    public void seeStats(final UserCommandSource sender) {
        this.messageService.get(Captions.BALANCE_SELF).addUser(sender).send(sender);
    }

    @Command("balance <target>")
    @Permission("mcmmocredits.balance.other")
    @CommandDescription("Allows user to check someone else's credit statistics.")
    public void seeStatsTargeted(final CommandSource sender, final CommandSourceData target) {
        this.messageService.get(Captions.BALANCE_OTHER).addPair(sender.data(), target).send(sender);
    }

    @Command("menu")
    @Permission("mcmmocredits.redeem.menu")
    @CommandDescription("Allows user to open the Redeem Menu.")
    public void openRedeemMenu(final UserCommandSource sender) {
        this.menuService.open((Player) sender.stack().getSender());
    }

    @Command("reload")
    @Permission("mcmmocredits.reload")
    @CommandDescription("Reloads configurations belonging to the plugin.")
    public void reload(final CommandSource sender) {
        Caption caption = this.configs.reloadConfigs() ? Captions.RELOAD_SUCCESS : Captions.RELOAD_FAILURE;
        this.messageService.get(caption).addUser(sender).send(sender);
    }

    @Command("top [page]")
    @Permission("mcmmocredits.top")
    @CommandDescription("Shows the specified page of the leaderboard.")
    public void top(final CommandSource sender, final @Range(min = "1") @Default(value = "1") int page) {
        Message invalid = this.messageService.get(Captions.TOP_INVALID).addPage(page).addUser(sender);
        if (!this.configs.settings().leaderboardEnabled()) {
            invalid.send(sender);
            return;
        }
        int limit = this.configs.settings().leaderboardPageSize();
        int offset = Math.max(0, (page - 1) * limit);
        List<CommandSourceData> users = this.userService.getUserGroup(limit, offset);
        if (users.isEmpty()) {
            invalid.send(sender);
            return;
        }
        this.messageService.get(Captions.TOP_TITLE).addPage(page).addUser(sender).send(sender);
        int bound = users.size();
        for (int i = 0; i <= bound; i++) {
            this.messageService.get(Captions.TOP_ENTRY_FORMAT)
                    .addPair(sender.data(), users.get(i))
                    .addPage(page)
                    .addRank(i + offset + 1)
                    .send(sender);
        }
    }

    private void applyAll(final CommandSource sender, final TransactionContext ctx, final Caption allCaption, final boolean silent) {
        //If user, process transaction for user first to pass to then pass updated user to batch process.
        if (!sender.isConsole()) {
            this.factory.create(ctx.withData(sender.data())).runForSender();
            UserCommandSource ucs = new UserCommandSource(sender.stack(), this.userService.getUser(sender.data().uuid())
                    .orElseThrow());
            this.userService.getOnlineUsers(ucs)
                    .forEach(u -> this.factory.create(ctx.replacePair(ucs, u)).runForTarget(silent));
            this.messageService.get(allCaption).addUser(ucs).send(ucs);
            return;
        }
        this.userService.getOnlineUsers().forEach(user -> this.factory.create(ctx.withData(user)).runForTarget(silent));
        this.messageService.get(allCaption).addUser(sender).send(sender);
    }

//    @Command("pay <amount> <user>")
//    @Permission("mcmmocredits.pay.other")
//    @CommandDescription("Pay credits to another user.")
//    @Cooldown(duration = 30, timeUnit = ChronoUnit.SECONDS, group = "mcmmocredits-pay")
//    public void payCredits(final UserCommandSource payer, final @Range(min = "1") int amount, final CommandSourceData payee) {
//        //TODO: this should be a transaction, and also if the user does not exist the API returns -1!!
//        if (payer.data().equals(payee)) {
//            this.messageService.sendToSender(context, Captions.PAY_SAME_USER);
//            return;
//        }
//        if (payer.data().credits() < amount || payee.credits() + amount < 0) {
//            this.messageService.sendToSender(context, Captions.NOT_ENOUGH_CREDITS);
//            return;
//        }
//        CommandSourceData updatedPayer = payer.takeCredits(amount);
//        CommandSourceData updatedPayee = payee.addCredits(amount);
//        if (this.userService.updateUser(updatedPayer) && this.userService.updateUser(updatedPayee)) {
//            this.messageService.sendToSender(context, Captions.PAY_SENDER);
//        }
//        if (!context.flags().isPresent("silent")) {
//            this.messageService.sendToTarget(context, Captions.PAY_TARGET);
//        }
//    }
//
//    @Command("payall <amount>")
//    @Permission("mcmmocredits.pay.all")
//    @CommandDescription("Pay credits to all online users.")
//    @Cooldown(duration = 30, timeUnit = ChronoUnit.SECONDS, group = "mcmmocredits-pay-all")
//    public void payCreditsAll(final CommandSourceData executor, final @Range(min = "1") int amount, final @Flag("s") boolean silent) {
//        CommandSourceData payer = (User) context.get(Keys.SENDER);
//        Set<CommandSourceData> users = this.userService.getOnlineUsers(payer);
//        users.removeIf(user -> user.credits() + amount < 0);
//        int payerAmount = amount * users.size();
//        if (payer.credits() < payerAmount) {
//            this.messageService.sendToSender(context, Captions.NOT_ENOUGH_CREDITS);
//            return;
//        }
//        CommandSourceData updatedPayer = payer.takeCredits(payerAmount);
//        boolean atLeastOne = false;
//        if (this.userService.updateUser(updatedPayer)) {
//            for (CommandSourceData data : users) {
//                CommandSourceData updatedPayee = user.addCredits(amount);
//                if (this.userService.updateUser(updatedPayee)) {
//                    atLeastOne = true;
//                    if (!silent) {
//                        this.messageService.sendToTarget(context, Captions.PAY_TARGET);
//                    }
//                }
//            }
//            if (atLeastOne) {
//                this.messageService.sendToSender(context, Captions.PAY_ALL);
//            }
//        }
//    }
//
//    @ExceptionHandler(ArgumentParseException.class)
//    public void handleError(final ExceptionContext<? extends CommandSource, ArgumentParseException> ex) {
//        this.messageService.sendToSender(ex.context(), StandardCaptionKeys.EXCEPTION_INVALID_ARGUMENT);
//    }
}
