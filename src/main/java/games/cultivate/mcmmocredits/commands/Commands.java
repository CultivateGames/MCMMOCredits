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
package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.specifier.Range;
import cloud.commandframework.context.CommandContext;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.ConfigService;
import games.cultivate.mcmmocredits.events.CreditTransactionEvent;
import games.cultivate.mcmmocredits.messages.Resolver;
import games.cultivate.mcmmocredits.transaction.Transaction;
import games.cultivate.mcmmocredits.transaction.TransactionType;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserService;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;

/**
 * Handles all commands. Prefix is customizable via config.
 * Default is /credits.
 */
@SuppressWarnings("checkstyle:linelength")
@CommandMethod("${command.prefix}")
public final class Commands {
    private final ConfigService configs;
    private final MCMMOCredits plugin;
    private final UserService service;

    /**
     * Constructs the object.
     *
     * @param configs ConfigService to obtain configs.
     * @param plugin  Plugin instance to obtain main thread executor.
     * @param service The UserService to obtain users.
     */
    @Inject
    public Commands(final ConfigService configs, final MCMMOCredits plugin, final UserService service) {
        this.configs = configs;
        this.plugin = plugin;
        this.service = service;
    }

    /**
     * Processes the {@literal /credits balance} command.
     *
     * @param user CommandExecutor. Must be an online player.
     */
    @CommandMethod("balance")
    @CommandPermission("mcmmocredits.balance.self")
    @CommandDescription("Allows user to check credit statistics.")
    public void balance(final User user) {
        user.sendText(this.configs.getMessage("balance"));
    }

    /**
     * Processes the {@literal /credits balance <user>} command.
     *
     * @param executor CommandExecutor. Can be Console.
     * @param user     The user to check. Must be a player, but can be offline/online.
     */
    @CommandMethod("balance <user>")
    @CommandPermission("mcmmocredits.balance.other")
    @CommandDescription("Allows user to check someone else's credit statistics.")
    public void balanceOther(final CommandExecutor executor, final @Argument User user) {
        executor.sendText(this.configs.getMessage("balance-other"), r -> r.addUser(user, "target"));
    }

    /**
     * Processes the {@literal /credits <add|set|take> <amount>} command.
     *
     * @param executor CommandExecutor. Must be an online player.
     * @param type     Operation applied to credit balance (add, set, take).
     * @param amount   Amount of credits to apply to balance.
     */
    @CommandMethod("<type> <amount>")
    @CommandPermission("mcmmocredits.modify.self")
    @CommandDescription("Allows user to modify their own credit balance.")
    public void modify(final User executor, final @Argument TransactionType type, final @Argument @Range(min = "0") int amount) {
        Bukkit.getPluginManager().callEvent(new CreditTransactionEvent(Transaction.of(executor, type, amount), true, false));
    }

    /**
     * Processes the {@literal /credits <add|set|take> <amount> <user> [--s]} command.
     *
     * @param executor CommandExecutor. Can be Console.
     * @param type     Operation applied to credit balance (add, set, take).
     * @param amount   Amount of credits to apply to balance.
     * @param user     The user to modify.
     * @param silent   If the process should send feedback to the user based on presence.
     */
    @CommandMethod("<type> <amount> <user>")
    @CommandPermission("mcmmocredits.modify.other")
    @CommandDescription("Allows user to modify someone else's credit balance.")
    public void modifyOther(final CommandExecutor executor, final @Argument TransactionType type, final @Argument @Range(min = "0") int amount, final @Argument User user, final @Flag("s") boolean silent) {
        Transaction tr = Transaction.builder(executor, type, amount).targets(user).build();
        Bukkit.getPluginManager().callEvent(new CreditTransactionEvent(tr, silent, false));
    }

    /**
     * Processes the {@literal /credits <addall|setall|takeall> <amount> [--s]} command.
     *
     * @param executor CommandExecutor. Must be an online player.
     * @param args     Command args to derive the transaction type.
     * @param amount   Amount of credits to apply to balance.
     * @param silent   If the process should send feedback to the user based on presence.
     */
    @CommandMethod("addall|setall|takeall <amount>")
    @CommandPermission("mcmmocredits.modify.all")
    @CommandDescription("Allows user to modify balance of all online players.")
    public void modifyAll(final CommandExecutor executor, final CommandContext<CommandExecutor> args, final @Argument @Range(min = "0") int amount, final @Flag("s") boolean silent) {
        this.service.getOnlineUsers()
                .thenApply(x -> Transaction.builder(executor, TransactionType.fromArgs(args, 1), amount).targets(x).build())
                .thenAccept(t -> Bukkit.getPluginManager().callEvent(new CreditTransactionEvent(t, silent, false)));
    }

    /**
     * Processes the {@literal /credits redeem <amount> <skill>} command.
     *
     * @param executor CommandExecutor. Must be an online player.
     * @param skill    The affected skill.
     * @param amount   Amount of credits to apply to skill.
     */
    @CommandMethod("redeem <amount> <skill>")
    @CommandPermission("mcmmocredits.redeem.self")
    @CommandDescription("Allows user to redeem credits for MCMMO Skill levels.")
    public void redeem(final User executor, final @Argument PrimarySkillType skill, final @Argument @Range(min = "1") int amount) {
        Transaction tr = Transaction.builder(executor, TransactionType.REDEEM, amount).skill(skill).build();
        Bukkit.getPluginManager().callEvent(new CreditTransactionEvent(tr, true, false));
    }

    /**
     * Processes the {@literal /credits redeem <amount> <skill> <user> [--s]} command.
     *
     * @param executor CommandExecutor. Can be Console.
     * @param skill    The affected skill.
     * @param amount   Amount of credits to apply to skill.
     * @param user     The user being modified.
     * @param silent   If the process should send feedback to the user based on presence.
     */
    @CommandMethod("redeem <amount> <skill> <user>")
    @CommandPermission("mcmmocredits.redeem.other")
    @CommandDescription("Allows a user to redeem credits for someone else.")
    public void redeemOther(final CommandExecutor executor, final @Argument PrimarySkillType skill, final @Argument @Range(min = "1") int amount, final @Argument User user, final @Flag("s") boolean silent) {
        Transaction tr = Transaction.builder(executor, TransactionType.REDEEM, amount).targets(user).skill(skill).build();
        Bukkit.getPluginManager().callEvent(new CreditTransactionEvent(tr, silent, false));
    }

    /**
     * Processes the {@literal /credits <redeemall> <amount> <skill> [--s]} command.
     *
     * @param executor CommandExecutor. Must be an online player.
     * @param amount   Amount of credits to apply to balance.
     * @param skill    The affected skill.
     * @param silent   If the process should send feedback to the user based on presence.
     */
    @CommandMethod("redeemall <amount> <skill>")
    @CommandPermission("mcmmocredits.redeem.all")
    @CommandDescription("Allows user to redeem credits for all online players.")
    public void redeemAll(final CommandExecutor executor, final @Argument @Range(min = "1") int amount, final @Argument PrimarySkillType skill, final @Flag("s") boolean silent) {
        this.service.getOnlineUsers()
                .thenApply(x -> Transaction.builder(executor, TransactionType.REDEEMALL, amount).targets(x).skill(skill).build())
                .thenAccept(t -> Bukkit.getPluginManager().callEvent(new CreditTransactionEvent(t, silent, false)));
    }

    /**
     * Processes the {@literal /credits pay <amount> <user>} command.
     *
     * @param executor CommandExecutor. Must be an online player.
     * @param amount   Amount of credits to pay.
     * @param user     The user being paid.
     */
    @CommandMethod("pay <amount> <user>")
    @CommandPermission("mcmmocredits.pay")
    @CommandDescription("Allows user to give their credits to another user.")
    public void pay(final User executor, final @Argument @Range(min = "1") int amount, final @Argument User user) {
        Transaction tr = Transaction.builder(executor, TransactionType.PAY, amount).targets(user).build();
        Bukkit.getPluginManager().callEvent(new CreditTransactionEvent(tr, false, false));
    }

    /**
     * Processes the {@literal /credits top <page>} command.
     *
     * @param executor CommandExecutor. Can be Console.
     * @param page     Page number of the leaderboard.
     */
    @CommandMethod("top [page]")
    @CommandPermission("mcmmocredits.leaderboard")
    @CommandDescription("Shows the specified page of the leaderboard.")
    public void top(final CommandExecutor executor, @Argument @Range(min = "1") Integer page) {
        if (!this.configs.mainConfig().getBoolean("settings", "leaderboard-enabled")) {
            executor.sendText(this.configs.getMessage("invalid-leaderboard"));
            return;
        }
        int limit = this.configs.mainConfig().getInteger("settings", "leaderboard-page-size");
        int offset = Math.max(0, (page == null ? 0 : page - 1) * limit);
        this.service.rangeOfUsers(limit, offset).thenAccept(users -> {
            if (users.isEmpty()) {
                executor.sendText(this.configs.getMessage("invalid-leaderboard"));
                return;
            }
            executor.sendText(this.configs.mainConfig().getString("leaderboard-title"));
            Resolver resolver = Resolver.ofUser(executor);
            String entry = this.configs.mainConfig().getString("leaderboard-entry");
            for (int i = 1; i <= users.size(); i++) {
                executor.sendText(entry, resolver.addUser(users.get(i - 1), "target").addTag("rank", i + offset));
            }
        });
    }

    /**
     * Processes the {@literal /credits menu} command.
     *
     * @param executor CommandExecutor. Must be an online player.
     */
    @CommandMethod("menu")
    @CommandPermission("mcmmocredits.redeem.menu")
    @CommandDescription("Allows user to open the Redeem Menu.")
    public void openRedeemMenu(final User executor) {
        this.configs.menuConfig().getMenu().openInventory(this.plugin, executor);
    }

    /**
     * Processes the {@literal /credits reload} command.
     *
     * @param executor CommandExecutor. Can be Console.
     */
    @CommandMethod("reload")
    @CommandPermission("mcmmocredits.admin")
    @CommandDescription("Reloads the configuration files with most changes applied.")
    public void reload(final CommandExecutor executor) {
        this.configs.reloadConfigs();
        executor.sendText(this.configs.getMessage("reload"));
    }
}
