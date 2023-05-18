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
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.specifier.Range;
import cloud.commandframework.exceptions.NoPermissionException;
import cloud.commandframework.permission.Permission;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.events.CreditTransactionEvent;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.transaction.BasicTransaction;
import games.cultivate.mcmmocredits.transaction.BasicTransactionType;
import games.cultivate.mcmmocredits.transaction.RedeemTransaction;
import games.cultivate.mcmmocredits.transaction.Transaction;
import games.cultivate.mcmmocredits.ui.ContextFactory;
import games.cultivate.mcmmocredits.ui.menu.BaseMenu;
import games.cultivate.mcmmocredits.ui.menu.ConfigMenu;
import games.cultivate.mcmmocredits.ui.menu.MainMenu;
import games.cultivate.mcmmocredits.ui.menu.Menu;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserService;
import org.bukkit.Bukkit;
import org.incendo.interfaces.paper.PlayerViewer;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
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
    private final UserService service;
    private final ContextFactory factory;
    private final MCMMOCredits plugin;

    /**
     * Constructs the object.
     *
     * @param config     MainConfig to obtain messages and for usage in reload command.
     * @param menuConfig MenuConfig to obtain menus and for usage in reload command.
     * @param service    UserService to obtain Users from cache/database.
     * @param factory    ContextFactory to assist in menu creation.
     * @param plugin     Plugin instance to obtain main thread executor.
     */
    @Inject
    public Credits(final MainConfig config, final MenuConfig menuConfig, final UserService service, final ContextFactory factory, final MCMMOCredits plugin) {
        this.config = config;
        this.menuConfig = menuConfig;
        this.service = service;
        this.factory = factory;
        this.plugin = plugin;
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
        Text.forOneUser(user, this.config.getMessage("balance")).send();
    }

    /**
     * Processes the {@literal /credits balance <username>} command.
     *
     * @param executor CommandExecutor. Can be Console.
     * @param username Username of the user to check.
     */
    @CommandMethod("balance <username>")
    @CommandPermission("mcmmocredits.balance.other")
    @CommandDescription("Allows user to check someone else's credit statistics.")
    public void balanceOther(final CommandExecutor executor, final @Argument(suggestions = "user") String username) {
        Optional<User> user = this.service.getUser(username);
        if (user.isPresent()) {
            Text.fromString(executor, this.config.getMessage("balance-other"), Resolver.ofUsers(executor, user.get())).send();
            return;
        }
        this.playerUnknownError(executor, username);
    }

    /**
     * Processes the {@literal /credits <add|set|take> <amount>} command.
     *
     * @param user   CommandExecutor. Must be an online player.
     * @param type   Operation applied to credit balance (add, set, take).
     * @param amount Amount of credits to apply to balance.
     */
    @CommandMethod("<type> <amount>")
    @CommandPermission("mcmmocredits.modify.self")
    @CommandDescription("Allows user to modify their own credit balance.")
    public void modify(final User user, final @Argument BasicTransactionType type, final @Argument @Range(min = "0") int amount) {
        Transaction transaction = BasicTransaction.of(user, type, amount);
        this.execute(() -> Bukkit.getPluginManager().callEvent(new CreditTransactionEvent(transaction, true, false)));
    }

    /**
     * Processes the {@literal /credits <add|set|take> <username> [--s]} command.
     *
     * @param executor CommandExecutor. Can be Console.
     * @param type     Operation applied to credit balance (add, set, take).
     * @param amount   Amount of credits to apply to balance.
     * @param username Username of the user to modify.
     * @param silent   If the process should send feedback to the user based on presence.
     */
    @CommandMethod("<type> <amount> <username>")
    @CommandPermission("mcmmocredits.modify.other")
    @CommandDescription("Allows user to modify someone else's credit balance.")
    public void modifyOther(final CommandExecutor executor, final @Argument BasicTransactionType type, final @Argument @Range(min = "0") int amount, final @Argument(suggestions = "user") String username, final @Flag("s") boolean silent) {
        Optional<User> optionalUser = this.service.getUser(username);
        if (optionalUser.isPresent()) {
            Transaction transaction = BasicTransaction.of(executor, optionalUser.get(), type, amount);
            this.execute(() -> Bukkit.getPluginManager().callEvent(new CreditTransactionEvent(transaction, true, false)));
            return;
        }
        this.playerUnknownError(executor, username);
    }

    /**
     * Processes the {@literal /credits <redeem> <amount> <skill>} command.
     *
     * @param user   CommandExecutor. Must be an online player.
     * @param skill  The affected skill.
     * @param amount Amount of credits to apply to skill.
     */
    @CommandMethod("redeem <amount> <skill>")
    @CommandPermission("mcmmocredits.redeem.self")
    @CommandDescription("Allows user to redeem credits for MCMMO Skill levels.")
    public void redeem(final User user, final @Argument PrimarySkillType skill, final @Argument @Range(min = "1") int amount) {
        Transaction transaction = RedeemTransaction.of(user, skill, amount);
        this.execute(() -> Bukkit.getPluginManager().callEvent(new CreditTransactionEvent(transaction, true, false)));
    }

    /**
     * /credits redeem amount skill username [--s]. Allows a user to redeem credits for someone else.
     *
     * @param executor CommandExecutor. Can be Console.
     * @param skill    The affected skill.
     * @param amount   Amount of credits to apply to skill.
     * @param username Username of the user being modified.
     * @param silent   If the process should send feedback to the user based on presence.
     */
    @CommandMethod("redeem <amount> <skill> <username>")
    @CommandPermission("mcmmocredits.redeem.other")
    @CommandDescription("Allows user to modify their own credit balance.")
    public void redeemOther(final CommandExecutor executor, final @Argument PrimarySkillType skill, final @Argument @Range(min = "1") int amount, final @Argument(suggestions = "user") String username, final @Flag("s") boolean silent) {
        Optional<User> optionalUser = this.service.getUser(username);
        if (optionalUser.isPresent()) {
            Transaction transaction = RedeemTransaction.of(executor, optionalUser.get(), skill, amount);
            this.execute(() -> Bukkit.getPluginManager().callEvent(new CreditTransactionEvent(transaction, silent, false)));
            return;
        }
        this.playerUnknownError(executor, username);
    }

    /**
     * Processes the {@literal /credits top <page>} command.
     *
     * @param executor CommandExecutor. Can be Console.
     * @param page     Page number of the leaderboard.
     */
    @CommandMethod("top <page>")
    @CommandPermission("mcmmocredits.leaderboard")
    @CommandDescription("Shows the specified page of the leaderboard.")
    public void top(final CommandExecutor executor, final @Argument @Range(min = "1") int page) {
        Text invalid = Text.forOneUser(executor, this.config.getMessage("invalid-leaderboard"));
        if (!this.config.getBoolean("settings", "leaderboard-enabled")) {
            invalid.send();
            return;
        }
        int limit = this.config.getInteger("settings", "leaderboard-page-size");
        int offset = Math.max(0, (page - 1) * limit);
        List<User> users = this.service.getPageOfUsers(limit, offset);
        int size = users.size();
        if (size == 0) {
            invalid.send();
            return;
        }
        Text.forOneUser(executor, this.config.getString("leaderboard-title")).send();
        Resolver resolver = Resolver.ofUser(executor);
        for (int i = 1; i <= size; i++) {
            resolver.addUser(users.get(i - 1), "target");
            resolver.addTag("rank", i + offset);
            Text.fromString(executor, this.config.getString("leaderboard-entry"), resolver).send();
        }
    }

    /**
     * Processes the {@literal /credits menu [type]} command.
     * Users need "mcmmocredits.menu" and "mcmmocredits.menu.type" permissions to open.
     *
     * @param user CommandExecutor. Must be an online player.
     * @param type Menu type to be opened (main, config, redeem).
     */
    @CommandMethod("menu [type]")
    @CommandPermission("mcmmocredits.menu")
    @CommandDescription("Allows user to open a menu of a specific type.")
    public void openMenu(final User user, final @Argument(suggestions = "menus", defaultValue = "main") String type) {
        String menuType = type.toLowerCase();
        if (!user.player().hasPermission("mcmmocredits.menu." + menuType)) {
            this.execute(() -> {
                throw new NoPermissionException(Permission.of("mcmmocredits.menu." + menuType), user, List.of());
            });
        }
        Menu menu = Objects.requireNonNull(this.menuConfig.getMenu(menuType));
        PlayerViewer viewer = PlayerViewer.of(user.player());
        switch (menuType) {
            case "main" -> MainMenu.of(menu).build(user, this.factory).open(viewer);
            case "redeem" -> BaseMenu.of(menu).build(user, this.factory).open(viewer);
            case "config" -> ConfigMenu.of(this.config, menu).build(user, this.factory).open(viewer);
            default -> throw new IllegalArgumentException("Invalid menu type passed! Value: " + menuType);
        }
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
        this.config.load();
        this.menuConfig.load();
        Text.forOneUser(executor, this.config.getMessage("reload")).send();
    }

    /**
     * Handles message parsing when a User is unknown.
     *
     * @param executor CommandExecutor. Can be Console.
     * @param username Username of the unknown user.
     */
    private void playerUnknownError(final CommandExecutor executor, final String username) {
        Resolver resolver = Resolver.ofUser(executor);
        resolver.addUsername(username);
        Text.fromString(executor, this.config.getMessage("player-unknown"), resolver).send();
    }

    /**
     * Ensures runnable execution is on the main thread.
     *
     * @param command Runnable to execute on the main thread.
     */
    private void execute(final Runnable command) {
        Bukkit.getScheduler().getMainThreadExecutor(this.plugin).execute(command);
    }
}
