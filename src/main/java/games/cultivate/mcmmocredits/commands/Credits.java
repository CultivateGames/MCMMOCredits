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
import games.cultivate.mcmmocredits.transaction.BasicTransaction;
import games.cultivate.mcmmocredits.transaction.BasicTransactionType;
import games.cultivate.mcmmocredits.transaction.PayTransaction;
import games.cultivate.mcmmocredits.transaction.RedeemTransaction;
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

import jakarta.inject.Inject;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Handles all commands. Prefix is customizable via config.
 * Default is /credits.
 */
@CommandMethod("${command.prefix}")
@SuppressWarnings("checkstyle:linelength")
public final class Credits {
    private final MainConfig config;
    private final MenuConfig menuConfig;
    private final MCMMOCredits plugin;

    /**
     * Constructs the object.
     *
     * @param config     MainConfig to obtain messages and for usage in reload command.
     * @param menuConfig MenuConfig to obtain menus and for usage in reload command.
     * @param plugin     Plugin instance to obtain main thread executor.
     */
    @Inject
    public Credits(final MainConfig config, final MenuConfig menuConfig, final MCMMOCredits plugin) {
        this.config = config;
        this.menuConfig = menuConfig;
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
        user.sendText(this.config.getMessage("balance"));
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
        executor.sendText(this.config.getMessage("balance-other"), Resolver.ofUsers(executor, user));
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
    public void modify(final User executor, final @Argument BasicTransactionType type, final @Argument @Range(min = "0") int amount) {
        Bukkit.getPluginManager().callEvent(new CreditTransactionEvent(BasicTransaction.of(executor, type, amount), true, false));
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
    public void modifyOther(final CommandExecutor executor, final @Argument BasicTransactionType type, final @Argument @Range(min = "0") int amount, final @Argument User user, final @Flag("s") boolean silent) {
        Bukkit.getPluginManager().callEvent(new CreditTransactionEvent(BasicTransaction.of(executor, user, type, amount), silent, false));
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
        Bukkit.getPluginManager().callEvent(new CreditTransactionEvent(RedeemTransaction.of(executor, skill, amount), true, false));
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
        Bukkit.getPluginManager().callEvent(new CreditTransactionEvent(RedeemTransaction.of(executor, user, skill, amount), silent, false));
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
        Bukkit.getPluginManager().callEvent(new CreditTransactionEvent(PayTransaction.of(executor, user, amount), true, false));
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
    public void top(final CommandExecutor executor, final UserService service, final @Argument @Range(min = "1") int page) {
        if (!this.config.getBoolean("settings", "leaderboard-enabled")) {
            executor.sendText(this.config.getMessage("invalid-leaderboard"));
            return;
        }
        int limit = this.config.getInteger("settings", "leaderboard-page-size");
        int offset = Math.max(0, (page - 1) * limit);
        List<User> users = service.getPageOfUsers(limit, offset);
        if (users.isEmpty()) {
            executor.sendText(this.config.getMessage("invalid-leaderboard"));
            return;
        }
        executor.sendText(this.config.getString("leaderboard-title"));
        Resolver resolver = Resolver.ofUser(executor);
        String entry = this.config.getString("leaderboard-entry");
        for (int i = 1; i <= users.size(); i++) {
            executor.sendText(entry, resolver.addUser(users.get(i - 1), "target").addTag("rank", i + offset));
        }
    }

    /**
     * Processes the {@literal /credits menu [type]} command.
     * Users need "mcmmocredits.menu" and "mcmmocredits.menu.type" permissions to open.
     *
     * @param executor CommandExecutor. Must be an online player.
     * @param type     Menu type to be opened (main, config, redeem).
     */
    @CommandMethod("menu [type]")
    @CommandPermission("mcmmocredits.menu")
    @CommandDescription("Allows user to open a menu of a specific type.")
    public void openMenu(final User executor, final ContextFactory factory, @Argument(suggestions = "menus", defaultValue = "main") String type) {
        String menuType = type.toLowerCase();
        if (!executor.player().hasPermission("mcmmocredits.menu." + menuType)) {
            this.plugin.execute(() -> {
                throw new NoPermissionException(Permission.of("mcmmocredits.menu." + menuType), executor, List.of());
            });
        }
        Menu menu = Objects.requireNonNull(this.menuConfig.getMenu(menuType));
        PlayerViewer viewer = PlayerViewer.of(executor.player());
        switch (menuType) {
            case "main" -> MainMenu.of(menu).build(executor, factory).open(viewer);
            case "redeem" -> BaseMenu.of(menu).build(executor, factory).open(viewer);
            case "config" -> ConfigMenu.of(this.config, menu).build(executor, factory).open(viewer);
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
        Path path = this.plugin.getDataFolder().toPath();
        this.config.load(path, "config.yml");
        this.menuConfig.load(path, "menus.yml");
        executor.sendText(this.config.getMessage("reload"));
    }
}
