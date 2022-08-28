package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.specifier.Range;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillTools;
import games.cultivate.mcmmocredits.config.GeneralConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.data.Database;
import games.cultivate.mcmmocredits.menu.MenuFactory;
import games.cultivate.mcmmocredits.placeholders.ResolverFactory;
import games.cultivate.mcmmocredits.text.Text;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

/**
 * This class is responsible for handling of the /credits command.
 */
@CommandMethod("credits|mcmmocredits")
public final class Credits {
    private final MenuConfig menus;
    private final GeneralConfig config;
    private final Database database;
    private final MenuFactory menuFactory;
    private final ResolverFactory resolverFactory;

    @Inject
    public Credits(final GeneralConfig config, final MenuConfig menus, final Database database, final MenuFactory factory, final ResolverFactory resolverFactory) {
        this.config = config;
        this.menus = menus;
        this.database = database;
        this.menuFactory = factory;
        this.resolverFactory = resolverFactory;
    }

    @CommandDescription("Check your own MCMMO Credit balance.")
    @CommandMethod("balance")
    @CommandPermission("mcmmocredits.balance.self")
    public void checkCredits(final Player player) {
        TagResolver resolver = this.resolverFactory.fromUsers(player);
        Text.fromString(player, this.config.string("selfBalance"), resolver).send();
    }

    @CommandDescription("Check someone else's MCMMO Credit balance.")
    @CommandMethod("balance <user>")
    @CommandPermission("mcmmocredits.balance.other")
    public void otherCredits(final CommandSender sender, final @Argument(suggestions = "user") @User String user) {
        this.database.getUUID(user).whenCompleteAsync((uuid, t) -> {
            if (this.database.doesPlayerExist(uuid)) {
                Text.fromString(sender, this.config.string("otherBalance"), this.resolverFactory.fromUsers(sender, this.database.getUsername(uuid))).send();
                return;
            }
            Text.fromString(sender, this.config.string("playerDoesNotExist"), this.resolverFactory.fromUsers(sender)).send();
        });
    }

    @CommandDescription("Modify your own MCMMO Credit balance.")
    @CommandMethod("<operation> <amount>")
    @CommandPermission("mcmmocredits.admin.modify.self")
    public void modifySelfCredits(final Player player, final @Argument(suggestions = "ops") String operation, final @Argument @Range(min = "1") int amount) {
        String op = operation.toLowerCase();
        if (this.performCreditTransaction(player, player.getUniqueId(), op, amount)) {
            Text.fromString(player, this.config.string(op + "Sender"), this.resolverFactory.fromTransaction(player, amount)).send();
        }
    }

    @CommandDescription("Modify MCMMO Credits of a user")
    @CommandMethod("<operation> <amount> <user>")
    @CommandPermission("mcmmocredits.admin.modify.other")
    public void modifyOtherCredits(final CommandSender sender, final @Argument(suggestions = "ops") String operation, final @Argument @Range(min = "0") int amount, final @Argument(suggestions = "user") @User String user, final @Flag("s") boolean silent) {
        this.database.getUUID(user).whenComplete((uuid, t) -> {
            if (!this.database.doesPlayerExist(uuid)) {
                Text.fromString(sender, this.config.string("playerDoesNotExist"), this.resolverFactory.fromUsers(sender, user)).send();
                return;
            }
            String op = operation.toLowerCase();
            if (this.performCreditTransaction(sender, uuid, op, amount)) {
                TagResolver r = this.resolverFactory.fromTransaction(sender, this.database.getUsername(uuid), amount);
                Text.fromString(sender, this.config.string(op + "Sender"), r).send();
                if (!silent) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && sender != player) {
                        Text.fromString(player, this.config.string(op + "Receiver"), r).send();
                    }
                }
            }
        });
    }

    @CommandDescription("Redeem your own MCMMO Credits into a specific skill.")
    @CommandMethod("redeem <amount> <skill>")
    @CommandPermission("mcmmocredits.redeem.self")
    public void selfRedeem(final Player player, final @Argument PrimarySkillType skill, final @Argument @Range(min = "1") int amount) {
        String content = this.performRedemption(player.getUniqueId(), skill, amount).orElse("selfRedeem");
        TagResolver resolver = this.resolverFactory.fromRedemption(player, skill, amount);
        Text.fromString(player, this.config.string(content), resolver).send();
    }

    @CommandDescription("Redeem MCMMO Credits into a specific skill for someone else")
    @CommandMethod("redeem <amount> <skill> <user>")
    @CommandPermission("mcmmocredits.redeem.other")
    public void adminRedeem(final CommandSender sender, final @Argument PrimarySkillType skill, final @Argument @Range(min = "1") int amount, final @Argument(suggestions = "user") @User String user, final @Flag("s") boolean silent) {
        if (user == null) {
            if (sender instanceof Player p) {
                this.selfRedeem(p, skill, amount);
                return;
            }
            Text.fromString(sender, this.config.string("commandExecution"), this.resolverFactory.fromUsers(sender)).send();
            return;
        }
        this.database.getUUID(user).whenComplete((uuid, throwable) -> {
            Optional<String> opt = this.performRedemption(uuid, skill, amount);
            if (opt.isPresent()) {
                Text.fromString(sender, this.config.string(opt.get()), this.resolverFactory.fromUsers(sender)).send();
                return;
            }
            Player player = Bukkit.getPlayer(uuid);
            TagResolver tr = this.resolverFactory.fromRedemption(sender, player.getName(), skill, amount);
            Text.fromString(sender, this.config.string("otherRedeemSender"), tr).send();
            if (!silent && sender != player) {
                Text.fromString(player, this.config.string("otherRedeemReceiver"), tr).send();
            }
        });
    }

    @CommandDescription("Reloads all configuration files provided by the plugin.")
    @CommandMethod("reload")
    @CommandPermission("mcmmocredits.admin.reload")
    public void reloadCredits(final CommandSender sender) {
        this.config.load();
        this.menus.load();
        Text.fromString(sender, this.config.string("reloadSuccessful"), this.resolverFactory.fromUsers(sender)).send();
    }

    @CommandDescription("Opens the Main Menu")
    @CommandMethod("menu main")
    @CommandPermission("mcmmocredits.menu.main")
    public void openMenu(final Player player) {
        this.menuFactory.createMainMenu(player).open();
    }

    @CommandDescription("Opens the Edit Configuration Menu")
    @CommandMethod("menu config")
    @CommandPermission("mcmmocredits.menu.config")
    public void openConfigMenu(final Player player) {
        this.menuFactory.createConfigMenu(player).open();
    }

    @CommandDescription("Opens the Credit Redemption Menu")
    @CommandMethod("menu redeem")
    @CommandPermission("mcmmocredits.menu.redeem")
    public void openRedeemMenu(final Player player) {
        this.menuFactory.createRedeemMenu(player).open();
    }

    /**
     * Performs a credit redemption.
     *
     * @param uuid   UUID of the target for the credit redemption.
     * @param skill  PrimarySkillType that is being modified.
     * @param amount amount of credits to take/levels to add to the target.
     * @return Failure reason via associated Config path, empty Optional if transaction was successful.
     */
    private Optional<String> performRedemption(final UUID uuid, final PrimarySkillType skill, final int amount) {
        if (SkillTools.isChildSkill(skill)) {
            return Optional.of("argumentParsing");
        }
        if (!this.database.doesPlayerExist(uuid)) {
            return Optional.of("playerDoesNotExist");
        }
        Player player = Bukkit.getPlayer(uuid);
        PlayerProfile profile = player != null ? UserManager.getPlayer(player).getProfile() : mcMMO.getDatabaseManager().loadPlayerProfile(uuid);
        if (profile.isLoaded()) {
            if (this.database.getCredits(uuid) < amount) {
                return Optional.of("notEnoughCredits");
            }
            int currentLevel = profile.getSkillLevel(skill);
            if (currentLevel + amount > mcMMO.p.getGeneralConfig().getLevelCap(skill)) {
                return Optional.of("skillCap");
            }
            profile.modifySkill(skill, currentLevel + amount);
            profile.save(true);
            this.database.takeCredits(uuid, amount);
            return Optional.empty();
        }
        return Optional.of("playerDoesNotExist");
    }

    private boolean performCreditTransaction(final CommandSender sender, final UUID uuid, final String op, final int amount) {
        boolean success = switch (op) {
            case "add" -> this.database.addCredits(uuid, amount);
            case "take" -> this.database.takeCredits(uuid, amount);
            case "set" -> this.database.setCredits(uuid, amount);
            default -> false;
        };
        if (!success) {
            //Transaction failed due to exception. Likely caused by the user failing SQL constraint of <0
            Text.fromString(sender, this.config.string("notEnoughCredits"), this.resolverFactory.fromUsers(sender)).send();
        }
        return success;
    }
}
