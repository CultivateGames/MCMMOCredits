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
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.skills.SkillTools;
import games.cultivate.mcmmocredits.config.GeneralConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.data.UserDAO;
import games.cultivate.mcmmocredits.events.CreditRedemptionEvent;
import games.cultivate.mcmmocredits.events.CreditTransactionEvent;
import games.cultivate.mcmmocredits.menu.MenuFactory;
import games.cultivate.mcmmocredits.placeholders.ResolverFactory;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.util.CreditOperation;
import games.cultivate.mcmmocredits.util.User;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Optional;

/**
 * This class is responsible for handling of all commands.
 */
@CommandMethod("credits|mcmmocredits")
public final class Credits {
    private final MenuConfig menus;
    private final GeneralConfig config;
    private final UserDAO dao;
    private final MenuFactory menuFactory;
    private final ResolverFactory resolverFactory;

    @Inject
    public Credits(final GeneralConfig config, final MenuConfig menus, final UserDAO dao, final MenuFactory factory, final ResolverFactory resolverFactory) {
        this.config = config;
        this.menus = menus;
        this.dao = dao;
        this.menuFactory = factory;
        this.resolverFactory = resolverFactory;
    }

    /**
     * Command which allows the player to check their own credits. Cannot be executed by Console.
     * <p>
     * Usage: /credits balance
     *
     * @param player The {@link Player} executing the command.
     */
    @CommandDescription("Check your own MCMMO Credit balance.")
    @CommandMethod("balance")
    @CommandPermission("mcmmocredits.balance.self")
    public void checkCredits(final Player player) {
        TagResolver resolver = this.resolverFactory.fromUsers(player);
        Text.fromString(player, this.config.string("selfBalance"), resolver).send();
    }

    /**
     * Command which allows a {@link CommandSender} to check credit balance of any user.
     * <p>
     * Usage: /credits balance Notch
     *
     * @param sender   The {@link CommandSender} executing the command.
     * @param username The username of the {@link Player} being checked.
     */
    @CommandDescription("Check someone else's MCMMO Credit balance.")
    @CommandMethod("balance <username>")
    @CommandPermission("mcmmocredits.balance.other")
    public void otherCredits(final CommandSender sender, final @Argument(suggestions = "user") String username) {
        this.dao.getUser(username).ifPresentOrElse(user -> {
            TagResolver resolver = this.resolverFactory.fromUsers(sender, user.username());
            Text.fromString(sender, this.config.string("otherBalance"), resolver).send();
        }, () -> Text.fromString(sender, this.config.string("playerDoesNotExist"), this.resolverFactory.fromUsers(sender)).send());
    }

    /**
     * Command which allows a player to modify their own credit balance.
     * <p>
     * Usage: /credits add 100, /credits set 100, /credits take 100
     *
     * @param player    The {@link Player} being affected by credit balance modification.
     * @param operation How to modify the balance. Add or take credits from the credit balance, or set the balance to a specific amount.
     * @param amount    Amount of credits to modify the credit balance with.
     */
    @CommandDescription("Modify your own MCMMO Credit balance.")
    @CommandMethod("<operation> <amount>")
    @CommandPermission("mcmmocredits.modify.self")
    public void modifySelfCredits(final Player player, final @Argument CreditOperation operation, final @Argument @Range(min = "1") int amount) {
        CreditTransactionEvent event = new CreditTransactionEvent(player, this.dao.fromPlayer(player).orElseThrow(), operation, amount, true);
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * Command that allows a {@link CommandSender} to modify the credit balance of a {@link Player}.
     * <p>
     * Usage: /credits add 100 Notch, /credits set 100 Notch, /credits take 100 Notch
     * <p>
     * Silent Flag Usage: /credits add 100 Notch --s, /credits set 100 Notch --s, /credits take 100 Notch --s
     *
     * @param sender    The {@link CommandSender} who executed the command.
     * @param operation How to modify the balance. Add or take credits from the credit balance, or set the balance to a specific amount.
     * @param amount    Amount of credits to modify the credit balance with.
     * @param username  The username of the {@link Player} being modified.
     * @param silent    A "flag" indicating whether command feedback is enabled for the user. --s added to the end of the command means that the user will not see a message when their credit balance is modified.
     */
    @CommandDescription("Modify MCMMO Credits of a user")
    @CommandMethod("<operation> <amount> <username>")
    @CommandPermission("mcmmocredits.modify.other")
    public void modifyOtherCredits(final CommandSender sender, final @Argument CreditOperation operation, final @Argument @Range(min = "0") int amount, final @Argument(suggestions = "user") String username, final @Flag("s") boolean silent) {
        this.dao.getUser(username).ifPresentOrElse(user -> {
            CreditTransactionEvent event = new CreditTransactionEvent(sender, user, operation, amount, silent);
            Bukkit.getPluginManager().callEvent(event);
        }, () -> Text.fromString(sender, this.config.string("playerDoesNotExist"), this.resolverFactory.fromUsers(sender, username)).send());
    }

    /**
     * Command which allows a {@link Player} to exchange their credits for +1 level in a {@link PrimarySkillType}.
     * <p>
     * Usage: /credits redeem 100 acrobatics. This will exchange 100 credits for +100 levels in Acrobatics.
     *
     * @param player The {@link Player} being affected by credit balance redemption.
     * @param skill  The {@link PrimarySkillType} being upgraded.
     * @param amount Amount of credits to exchange.
     */
    @CommandDescription("Redeem your own MCMMO Credits into a specific skill.")
    @CommandMethod("redeem <amount> <skill>")
    @CommandPermission("mcmmocredits.redeem.self")
    public void selfRedeem(final Player player, final @Argument PrimarySkillType skill, final @Argument @Range(min = "1") int amount) {
        CreditRedemptionEvent event = new CreditRedemptionEvent(player, this.dao.fromPlayer(player).orElseThrow(), skill, amount, true);
        if (this.isChildSkill(skill)) {
            TagResolver resolver = this.resolverFactory.fromRedemption(event);
            Text.fromString(player, this.config.string("argumentParsing"), resolver).send();
            return;
        }
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * Command that allows a {@link CommandSender} to sudo a {@link Player} for a credit exchange into a {@link PrimarySkillType}.
     * <p>
     * Usage: /credits redeem 100 acrobatics Notch. Command exchanges 100 credits for +100 levels in Acrobatics for Notch.
     * <p>
     * Silent Flag Usage: /credits redeem 100 acrobatics Notch --s
     *
     * @param sender   The {@link CommandSender} who executed the command.
     * @param skill    The {@link PrimarySkillType} being upgraded.
     * @param amount   Amount of credits to exchange.
     * @param username The username of the {@link Player} being modified.
     * @param silent   A "flag" indicating whether command feedback is enabled for the user. --s added to the end of the command means that the user will not see a message when their credit balance is changed. Depending on MCMMO config settings, a user may see a broadcast in chat about leveling up a skill in MCMMO.
     * @see #selfRedeem(Player, PrimarySkillType, int)
     */
    @CommandDescription("Redeem MCMMO Credits into a specific skill for someone else")
    @CommandMethod("redeem <amount> <skill> <username>")
    @CommandPermission("mcmmocredits.redeem.other")
    public void adminRedeem(final CommandSender sender, final @Argument PrimarySkillType skill, final @Argument @Range(min = "1") int amount, final @Argument(suggestions = "user") String username, final @Flag("s") boolean silent) {
        if (sender instanceof Player p && username == null) {
            this.selfRedeem(p, skill, amount);
            return;
        }
        Optional<User> user = this.dao.getUser(username);
        if (user.isPresent()) {
            CreditRedemptionEvent event = new CreditRedemptionEvent(sender, user.get(), skill, amount, silent);
            if (this.isChildSkill(skill)) {
                //Argument parsing error because a child skill was used.
                TagResolver resolver = this.resolverFactory.fromRedemption(event);
                Text.fromString(sender, this.config.string("argumentParsing"), resolver).send();
                return;
            }
            Bukkit.getPluginManager().callEvent(event);
            return;
        }
        //At this point, the user likely does not exist.
        Text.fromString(sender, this.config.string("playerDoesNotExist"), this.resolverFactory.fromUsers(sender)).send();
    }

    /**
     * Command which will reload configuration files. Does not support changes to Database.
     *
     * @param sender The {@link CommandSender} who executed the command.
     */
    @CommandDescription("Reloads all configuration files provided by the plugin.")
    @CommandMethod("reload")
    @CommandPermission("mcmmocredits.admin.reload")
    public void reloadCredits(final CommandSender sender) {
        this.config.load();
        this.menus.load();
        Text.fromString(sender, this.config.string("reloadSuccessful"), this.resolverFactory.fromUsers(sender)).send();
    }

    /**
     * Command which allows a {@link Player} to open the Main Menu.
     *
     * @param player The {@link Player} who executed the command and will see the menu.
     */
    @CommandDescription("Opens the Main Menu")
    @CommandMethod("menu main")
    @CommandPermission("mcmmocredits.menu.main")
    public void openMenu(final Player player) {
        this.menuFactory.createMainMenu(player).open();
    }

    /**
     * Command which allows a {@link Player} to open the Edit Configuration Menu.
     *
     * @param player The {@link Player} who executed the command and will see the menu.
     */
    @CommandDescription("Opens the Edit Configuration Menu")
    @CommandMethod("menu config")
    @CommandPermission("mcmmocredits.menu.config")
    public void openConfigMenu(final Player player) {
        this.menuFactory.createConfigMenu(player).open();
    }

    /**
     * Command which allows a {@link Player} to open the Credit Redemption Menu.
     *
     * @param player The {@link Player} who executed the command and will see the menu.
     */
    @CommandDescription("Opens the Credit Redemption Menu")
    @CommandMethod("menu redeem")
    @CommandPermission("mcmmocredits.menu.redeem")
    public void openRedeemMenu(final Player player) {
        this.menuFactory.createRedeemMenu(player).open();
    }

    //This method is wrapped in case the methodology of checking child skills changes.
    private boolean isChildSkill(final PrimarySkillType skill) {
        return SkillTools.isChildSkill(skill);
    }
}
