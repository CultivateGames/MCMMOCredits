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
package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.specifier.Range;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.data.UserDAO;
import games.cultivate.mcmmocredits.events.CreditRedemptionEvent;
import games.cultivate.mcmmocredits.events.CreditTransactionEvent;
import games.cultivate.mcmmocredits.menu.Menu;
import games.cultivate.mcmmocredits.menu.MenuFactory;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.util.CreditOperation;
import org.bukkit.Bukkit;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.type.ChestInterface;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * Handles all /credits commands. Alias: /mcmmocredits.
 * We use duplicate code in some places to enforce unique permissions.
 */
@CommandMethod("credits|mcmmocredits")
public final class Credits {
    private final MenuConfig menuConfig;
    private final MainConfig config;
    private final UserDAO dao;
    private final MenuFactory factory;

    /**
     * Constructs the command object via injection.
     *
     * @param config     Instance of MainConfig. Used for messages and reload command.
     * @param menuConfig Instance of MenuConfig. Used for reload command.
     * @param dao        Instance of UserDAO. Used to obtain User info from command.
     * @param factory    Instance of MenuFactory. Used to construct menus.
     */
    @Inject
    public Credits(final MainConfig config, final MenuConfig menuConfig, final UserDAO dao, final MenuFactory factory) {
        this.config = config;
        this.menuConfig = menuConfig;
        this.dao = dao;
        this.factory = factory;
    }

    /**
     * /credits balance. Allows the user to check statistics (balance, redeemed).
     *
     * @param user Command executor. Must be an online player.
     */
    @CommandMethod("balance")
    @CommandPermission("mcmmocredits.balance.self")
    public void balance(final User user) {
        Text.forOneUser(user, this.config.string("balance")).send();
    }

    /**
     * /credits balance username. Allows the user to check the statistics of someone else.
     *
     * @param executor Command executor. Can be from Console.
     * @param username Username of the {@link User} being actioned.
     */
    @CommandMethod("balance <username>")
    @CommandPermission("mcmmocredits.balance.other")
    public void balanceOther(final CommandExecutor executor, final @Argument(suggestions = "user") String username) {
        Optional<User> optionalUser = this.dao.getUser(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Text.fromString(executor, this.config.string("balance-other"), executor.resolver().with(user)).send();
            return;
        }
        this.playerUnknownError(executor, username);
    }

    @CommandMethod("top <page>")
    @CommandPermission("mcmmocredits.leaderboard")
    public void top(final CommandExecutor executor, final @Argument @Range(min = "1") int page) {
        int limit = this.config.node("leaderboard-page-size").getInt(10);
        int offset = Math.max(0, (page - 1) * limit);
        List<User> users = this.dao.getPageOfUsers(limit, offset);
        if (users.isEmpty()) {
            Text.forOneUser(executor, this.config.string("invalid-leaderboard")).send();
            return;
        }
        Text.forOneUser(executor, this.config.rawString("leaderboard-title")).send();
        for (int i = 1; i <= 10; i++) {
            int rank = i + offset;
            Resolver resolver = Resolver.builder().users(executor, users.get(i - 1)).tag("place", rank + "").build();
            Text.fromString(executor, this.config.rawString("leaderboard-entry"), resolver).send();
        }
    }

    /**
     * /credits add|set|take number. Allows a user to modify their own credit balance.
     *
     * @param user      Command executor. Must be an online player.
     * @param operation Operation that is performed on the balance.
     * @param amount    Amount of credits affected by transaction.
     */
    @CommandMethod("<operation> <amount>")
    @CommandPermission("mcmmocredits.modify.self")
    public void modify(final User user, final @Argument CreditOperation operation, final @Argument @Range(min = "0") int amount) {
        CreditTransactionEvent event = new CreditTransactionEvent(user.player(), user.uuid(), operation, amount, true, false);
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * /credits add|set|take number username [--s]. Allows a user to modify someone else's balance.
     *
     * @param executor  Command executor. Can be from Console.
     * @param operation Operation that is performed on the balance.
     * @param amount    Amount of credits affected by transaction.
     * @param username  Username of the {@link User} being actioned.
     * @param silent    If command should be "silent". User will not get feedback upon completion if true.
     */
    @CommandMethod("<operation> <amount> <username>")
    @CommandPermission("mcmmocredits.modify.other")
    public void modifyOther(final CommandExecutor executor, final @Argument CreditOperation operation, final @Argument @Range(min = "0") int amount, final @Argument(suggestions = "user") String username, final @Flag("s") boolean silent) {
        Optional<User> optionalUser = this.dao.getUser(username);
        if (optionalUser.isPresent()) {
            CreditTransactionEvent event = new CreditTransactionEvent(executor.sender(), optionalUser.get().uuid(), operation, amount, silent, false);
            Bukkit.getPluginManager().callEvent(event);
            return;
        }
        this.playerUnknownError(executor, username);
    }

    /**
     * /credits redeem amount skill. Allows a user to redeem credits for MCMMO Skill levels.
     *
     * @param user   Command executor. Must be an online player.
     * @param skill  The skill being actioned.
     * @param amount Amount of credits affected by transaction.
     */
    @CommandMethod("redeem <amount> <skill>")
    @CommandPermission("mcmmocredits.redeem.self")
    public void redeem(final User user, final @Argument PrimarySkillType skill, final @Argument @Range(min = "1") int amount) {
        CreditRedemptionEvent event = new CreditRedemptionEvent(user.player(), user.uuid(), skill, amount, true, false);
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * /credits redeem amount skill username [--s]. Allows a user to redeem credits for someone else.
     *
     * @param executor Command executor. Can be from Console.
     * @param skill    The skill being actioned.
     * @param amount   Amount of credits affected by transaction.
     * @param username Username of the {@link User} being actioned.
     * @param silent   If command should be "silent". User will not get feedback upon completion if true.
     */
    @CommandMethod("redeem <amount> <skill> <username>")
    @CommandPermission("mcmmocredits.redeem.other")
    public void redeemOther(final CommandExecutor executor, final @Argument PrimarySkillType skill, final @Argument @Range(min = "1") int amount, final @Argument(suggestions = "user") String username, final @Flag("s") boolean silent) {
        Optional<User> optionalUser = this.dao.getUser(username);
        if (optionalUser.isPresent()) {
            CreditRedemptionEvent event = new CreditRedemptionEvent(executor.sender(), optionalUser.get().uuid(), skill, amount, silent, false);
            Bukkit.getPluginManager().callEvent(event);
            return;
        }
        this.playerUnknownError(executor, username);
    }

    /**
     * /credits reload. Reloads the configuration files with any changes applied.
     *
     * @param executor Command executor. Can be from Console.
     */
    @CommandMethod("reload")
    @CommandPermission("mcmmocredits.admin")
    public void reload(final CommandExecutor executor) {
        this.config.load();
        this.menuConfig.load();
        Text.forOneUser(executor, this.config.string("reload")).send();
    }

    /**
     * /credits menu main. Allows user to open the Main Menu.
     *
     * @param user Command executor. Must be an online player.
     */
    @CommandMethod("menu main")
    @CommandPermission("mcmmocredits.menu.main")
    public void mainMenu(final User user) {
        this.handleMenu(user, "main");
    }

    /**
     * /credits menu config. Allows user to open the Configuration Menu.
     *
     * @param user Command executor. Must be an online player.
     */
    @CommandMethod("menu config")
    @CommandPermission("mcmmocredits.menu.config")
    public void configMenu(final User user) {
        this.handleMenu(user, "config");
    }

    /**
     * /credits menu redeem. Allows user to open the Redeem Menu.
     *
     * @param user Command executor. Must be an online player.
     */
    @CommandMethod("menu redeem")
    @CommandPermission("mcmmocredits.menu.redeem")
    public void redeemMenu(final User user) {
        this.handleMenu(user, "redeem");
    }

    /**
     * Displays a {@link Menu} to a User.
     *
     * @param user The executor of the command.
     * @param type Menu Type from command input.
     */
    private void handleMenu(final User user, final String type) {
        ChestInterface chest = this.factory.buildMenu(user, type);
        chest.open(PlayerViewer.of(user.player()));
    }

    /**
     * Sends a message when a user is unknown.
     *
     * @param executor Command executor. Can be from Console.
     * @param username Username of unknown user.
     */
    private void playerUnknownError(final CommandExecutor executor, final String username) {
        Resolver resolver = executor.resolver().toBuilder().username(username).build();
        Text.fromString(executor, this.config.string("player-unknown"), resolver).send();
    }
}
