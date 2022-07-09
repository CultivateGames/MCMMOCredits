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
import games.cultivate.mcmmocredits.data.Database;
import games.cultivate.mcmmocredits.keys.StringKey;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

import static games.cultivate.mcmmocredits.keys.StringKey.*;

/**
 * This class is responsible for handling of the /redeem command.
 */
@CommandMethod("redeem|rmc|redeemcredits")
public class Redeem {
    @CommandDescription("Redeem your own MCMMO Credits into a specific skill.")
    @CommandMethod("<skill> <amount>")
    @CommandPermission("mcmmocredits.redeem.self")
    private void selfRedeem(Player player, @Argument("skill") PrimarySkillType skill, @Argument("amount") @Range(min = "1") int amount) {
        TagResolver resolver = Resolver.fromRedemption(null, player, skill, amount);
        Optional<StringKey> opt = this.performTransaction(player.getUniqueId(), skill, amount);
        StringKey key = opt.isEmpty() ? REDEEM_SUCCESSFUL_SELF : opt.get();
        Text.fromKey(player, key, resolver).send();
    }

    @CommandDescription("Redeem MCMMO Credits into a specific skill for someone else")
    @CommandMethod("<skill> <amount> <player>")
    @CommandPermission("mcmmocredits.redeem.other")
    private void adminRedeem(CommandSender sender, @Argument("skill") PrimarySkillType skill, @Argument("amount") @Range(min = "1") int amount, @Argument(value = "player", suggestions = "players") String username, @Flag(value = "silent", permission = "mcmmocredits.redeem.other.silent") boolean silent) {
        Database database = Database.getDatabase();
        database.getUUID(username).whenCompleteAsync((uuid, throwable) -> {
            Optional<StringKey> opt = this.performTransaction(uuid, skill, amount);
            opt.ifPresentOrElse(k -> Text.fromKey(sender, k).send(), () -> {
                Player player = Bukkit.getPlayer(uuid);
                TagResolver tr = Resolver.fromRedemption(sender, player, skill, amount);
                Text.fromKey(sender, REDEEM_SUCCESSFUL_SENDER, tr).send();
                if (!silent) {
                    Text.fromKey(player, REDEEM_SUCCESSFUL_RECEIVER, tr).send();
                }
            });
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
    public Optional<StringKey> performTransaction(UUID uuid, PrimarySkillType skill, int amount) {
        Database database = Database.getDatabase();
        if (SkillTools.isChildSkill(skill)) {
            return Optional.of(INVALID_ARGUMENTS);
        }
        if (!database.doesPlayerExist(uuid)) {
            return Optional.of(PLAYER_DOES_NOT_EXIST);
        }
        PlayerProfile profile = mcMMO.getDatabaseManager().loadPlayerProfile(uuid);
        if (profile.isLoaded()) {
            if (database.getCredits(uuid) < amount) {
                return Optional.of(REDEEM_NOT_ENOUGH_CREDITS);
            }
            if (profile.getSkillLevel(skill) + amount > mcMMO.p.getGeneralConfig().getLevelCap(skill)) {
                return Optional.of(REDEEM_SKILL_CAP);
            }
            profile.addLevels(skill, amount);
            database.takeCredits(uuid, amount);
            return Optional.empty();
        }
        return Optional.of(PLAYER_DOES_NOT_EXIST);
    }
}
