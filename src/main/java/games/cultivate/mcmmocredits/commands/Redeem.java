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
import games.cultivate.mcmmocredits.data.Database;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

/**
 * This class is responsible for handling of the /redeem command.
 */
@CommandMethod("redeem|rmc|redeemcredits")
public final class Redeem {
    private final GeneralConfig config;
    private final Database database;

    @Inject
    public Redeem(final GeneralConfig config, final Database database) {
        this.config = config;
        this.database = database;
    }

    @CommandDescription("Redeem your own MCMMO Credits into a specific skill.")
    @CommandMethod("<skill> <amount>")
    @CommandPermission("mcmmocredits.redeem.self")
    public void selfRedeem(final Player player, final @Argument PrimarySkillType skill, final @Argument @Range(min = "1") int amount) {
        TagResolver resolver = Resolver.fromRedemption(player, player, skill, amount);
        Optional<String> opt = this.performTransaction(player.getUniqueId(), skill, amount);
        String content = opt.isEmpty() ? "selfRedeem" : opt.get();
        Text.fromString(player, this.config.string(content), resolver).send();
    }

    @CommandDescription("Redeem MCMMO Credits into a specific skill for someone else")
    @CommandMethod("<skill> <amount> [username]")
    @CommandPermission("mcmmocredits.redeem.other")
    public void adminRedeem(final CommandSender sender, final @Argument PrimarySkillType skill, final @Argument @Range(min = "1") int amount, final @Argument(suggestions = "user") String username, final @Flag("s") boolean s) {
        String userArg;
        if (username != null) {
            userArg = username;
        } else if (sender instanceof Player p) {
            userArg = p.getName();
        } else {
            Text.fromString(sender, this.config.string("playerDoesNotExist")).send();
            return;
        }
        this.database.getUUID(userArg).whenCompleteAsync((uuid, throwable) -> {
            Optional<String> opt = this.performTransaction(uuid, skill, amount);
            if (opt.isPresent()) {
                Text.fromString(sender, this.config.string(opt.get())).send();
                return;
            }
            Player player = Bukkit.getPlayer(uuid);
            TagResolver tr = Resolver.fromRedemption(sender, player, skill, amount);
            Text.fromString(sender, this.config.string("otherRedeemSender"), tr).send();
            if (!s) {
                Text.fromString(player, this.config.string("otherRedeemReceiver"), tr).send();
            }
        });
    }

    /**
     * Performs a credit redemption.
     *
     * @param uuid   UUID of the target for the credit redemption.
     * @param skill  PrimarySkillType that is being modified.
     * @param amount amount of credits to take/levels to add to the target.
     * @return Failure reason via associated Config path, empty Optional if transaction was successful.
     */
    public Optional<String> performTransaction(final UUID uuid, final PrimarySkillType skill, final int amount) {
        if (SkillTools.isChildSkill(skill)) {
            return Optional.of("invalidArguments");
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
}
