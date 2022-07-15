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
import com.gmail.nossr50.util.skills.SkillTools;
import games.cultivate.mcmmocredits.config.MessagesConfig;
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
    private final MessagesConfig messages;
    private final Database database;

    @Inject
    public Redeem(MessagesConfig messages, Database database) {
        this.messages = messages;
        this.database = database;
    }

    @CommandDescription("Redeem your own MCMMO Credits into a specific skill.")
    @CommandMethod("<skill> <amount>")
    @CommandPermission("mcmmocredits.redeem.self")
    private void selfRedeem(Player player, @Argument("skill") PrimarySkillType skill, @Argument("amount") @Range(min = "1") int amount) {
        TagResolver resolver = Resolver.fromRedemption(player, player, skill, amount);
        Optional<String> opt = this.performTransaction(player.getUniqueId(), skill, amount);
        String content = opt.isEmpty() ? "selfRedeem" : opt.get();
        Text.fromString(player, this.messages.string(content), resolver).send();
    }

    @CommandDescription("Redeem MCMMO Credits into a specific skill for someone else")
    @CommandMethod("<skill> <amount> <player>")
    @CommandPermission("mcmmocredits.redeem.other")
    private void adminRedeem(CommandSender sender, @Argument("skill") PrimarySkillType skill, @Argument("amount") @Range(min = "1") int amount, @Argument(value = "player", suggestions = "players") String username, @Flag(value = "silent", permission = "mcmmocredits.redeem.other.silent") boolean silent) {
        database.getUUID(username).whenCompleteAsync((uuid, throwable) -> {
            Optional<String> opt = this.performTransaction(uuid, skill, amount);
            if (opt.isPresent()) {
                Text.fromString(sender, this.messages.string(opt.get())).send();
            } else {
                Player player = Bukkit.getPlayer(uuid);
                TagResolver tr = Resolver.fromRedemption(sender, player, skill, amount);
                Text.fromString(sender, "otherRedeemSender", tr).send();
                if (!silent) {
                    Text.fromString(player, "otherRedeemReceiver", tr).send();
                }
            }
        });
    }

    /**
     * Performs a credit redemption.
     *
     * @param uuid   UUID of the target for the credit redemption.
     * @param skill  PrimarySkillType that is being modified.
     * @param amount amount of credits to take/levels to add to the target.
     * @return Failure reason via associated StringKey, empty Optional if transaction was successful.
     */
    public Optional<String> performTransaction(UUID uuid, PrimarySkillType skill, int amount) {
        if (SkillTools.isChildSkill(skill)) {
            return Optional.of("invalidArguments");
        }
        if (!database.doesPlayerExist(uuid)) {
            return Optional.of("playerDoesNotExist");
        }
        PlayerProfile profile = mcMMO.getDatabaseManager().loadPlayerProfile(uuid);
        if (profile.isLoaded()) {
            if (database.getCredits(uuid) < amount) {
                return Optional.of("notEnoughCredits");
            }
            if (profile.getSkillLevel(skill) + amount > mcMMO.p.getGeneralConfig().getLevelCap(skill)) {
                return Optional.of("skillCap");
            }
            profile.addLevels(skill, amount);
            database.takeCredits(uuid, amount);
            return Optional.empty();
        }
        return Optional.of("playerDoesNotExist");
    }
}
