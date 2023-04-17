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
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.events.CreditRedemptionEvent;
import games.cultivate.mcmmocredits.events.CreditTransactionEvent;
import games.cultivate.mcmmocredits.menu.ClickFactory;
import games.cultivate.mcmmocredits.menu.Menu;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserService;
import games.cultivate.mcmmocredits.util.CreditOperation;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.incendo.interfaces.paper.PlayerViewer;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * Handles all commands. Prefix is customizable via config.
 * Default is /credits.
 */
@CommandMethod("${command.prefix}")
@SuppressWarnings("checkstyle:linelength")
public final class Credits {
    private final MenuConfig menuConfig;
    private final MainConfig config;
    private final UserService userService;
    private final ClickFactory clickFactory;
    private final MCMMOCredits plugin;

    /**
     * Constructs the command object via injection.
     *
     * @param config       Instance of MainConfig. Used for messages and reload command.
     * @param menuConfig   Instance of MenuConfig. Used for reload command.
     * @param userService  Instance of UserService. Used to obtain User info from command.
     * @param clickFactory Instance of ClickFactory. Used to construct menus.
     * @param plugin       Instance of the MCMMOCredits to run events synchronously.
     */
    @Inject
    public Credits(final MainConfig config, final MenuConfig menuConfig, final UserService userService, final ClickFactory clickFactory, final MCMMOCredits plugin) {
        this.config = config;
        this.menuConfig = menuConfig;
        this.userService = userService;
        this.clickFactory = clickFactory;
        this.plugin = plugin;
    }

    /**
     * /credits balance. Allows the user to check statistics (balance, redeemed).
     *
     * @param user Command executor. Must be an online player.
     */
    @CommandMethod("balance")
    @CommandPermission("mcmmocredits.balance.self")
    public void balance(final User user) {
        Text.forOneUser(user, this.config.getMessage("balance")).send();
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
        Optional<User> user = this.userService.getUser(username);
        if (user.isPresent()) {
            Text.fromString(executor, this.config.getMessage("balance-other"), Resolver.ofUsers(executor, user.get())).send();
            return;
        }
        this.playerUnknownError(executor, username);
    }

    /**
     * /credits top page. Sends leaderboard rankings to the user who triggers the command.
     *
     * @param executor the CommandExecutor to send the leaderboard message to
     * @param page     the page number of the leaderboard to retrieve
     */
    @CommandMethod("top <page>")
    @CommandPermission("mcmmocredits.leaderboard")
    public void top(final CommandExecutor executor, final @Argument @Range(min = "1") int page) {
        if (!this.config.getBoolean("settings", "leaderboard-enabled")) {
            Text.forOneUser(executor, this.config.getMessage("invalid-leaderboard")).send();
            return;
        }
        int limit = this.config.getInteger("settings", "leaderboard-page-size");
        int offset = Math.max(0, (page - 1) * limit);
        List<User> users = this.userService.getPageOfUsers(limit, offset);
        if (users.isEmpty()) {
            Text.forOneUser(executor, this.config.getMessage("invalid-leaderboard")).send();
            return;
        }
        Text.forOneUser(executor, this.config.getString("leaderboard-title")).send();
        Resolver resolver = Resolver.ofUser(executor);
        for (int i = 1; i <= limit; i++) {
            resolver.addUser(users.get(i - 1), "target");
            resolver.addIntTag("rank", i + offset);
            Text.fromString(executor, this.config.getString("leaderboard-entry"), resolver).send();
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
        this.runEvent(event);
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
        Optional<User> optionalUser = this.userService.getUser(username);
        if (optionalUser.isPresent()) {
            CreditTransactionEvent event = new CreditTransactionEvent(executor.sender(), optionalUser.get().uuid(), operation, amount, silent, false);
            this.runEvent(event);
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
        this.runEvent(event);
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
        Optional<User> optionalUser = this.userService.getUser(username);
        if (optionalUser.isPresent()) {
            CreditRedemptionEvent event = new CreditRedemptionEvent(executor.sender(), optionalUser.get().uuid(), skill, amount, silent, false);
            this.runEvent(event);
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
        Text.forOneUser(executor, this.config.getMessage("reload")).send();
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
        String menuType = type.toLowerCase();
        Menu menu = this.menuConfig.getMenu(menuType);
        PlayerViewer viewer = PlayerViewer.of(user.player());
        switch (menuType) {
            case "main" -> menu.createMainMenu(user, this.clickFactory).open(viewer);
            case "config" -> menu.createConfigMenu(user, this.config, this.clickFactory).open(viewer);
            case "redeem" -> menu.createMenu(user, this.clickFactory).open(viewer);
            default -> throw new IllegalArgumentException("Invalid menu type passed! Value: " + menuType);
        }
    }

    /**
     * Sends a message when a user is unknown.
     *
     * @param executor Command executor. Can be from Console.
     * @param username Username of unknown user.
     */
    private void playerUnknownError(final CommandExecutor executor, final String username) {
        Resolver resolver = Resolver.ofUser(executor);
        resolver.addUsername(username);
        Text.fromString(executor, this.config.getMessage("player-unknown"), resolver).send();
    }

    /**
     * Utility method to call events synchronously.
     *
     * @param event The event.
     */
    private void runEvent(final Event event) {
        Bukkit.getScheduler().getMainThreadExecutor(this.plugin).execute(() -> Bukkit.getPluginManager().callEvent(event));
    }
}
