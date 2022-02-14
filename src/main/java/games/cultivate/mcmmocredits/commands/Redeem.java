package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.specifier.Range;
import com.gmail.nossr50.api.DatabaseAPI;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.util.Util;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

/**
 * This class is responsible for handling of the /redeem command.
 */
@CommandMethod("redeem|rmc|redeemcredits")
public class Redeem {
    private static Database database;
    private static final DatabaseAPI DATABASE_API = new DatabaseAPI();

    public Redeem (Database database) {
        Redeem.database = database;
    }

    @CommandDescription("Redeem your own MCMMO Credits into a specific skill.")
    @CommandMethod("<skill> <amount>")
    @CommandPermission("mcmmocredits.redeem.self")
    private void redeemCreditsSelf(Player player, @Argument("skill") PrimarySkillType skill, @Argument("amount") @Range(min = "1", max = "2147483647") int amount) {
        if (creditRedemption(player, player.getUniqueId(), skill, amount)) {
            Util.sendMessage(player, Keys.REDEEM_SUCCESSFUL_SELF.get(), Util.fullRedeem(null, player, WordUtils.capitalizeFully(skill.name()), Util.SKILL_TOOLS.getLevelCap(skill), amount));
        }
    }

    @CommandDescription("Redeem MCMMO Credits into a specific skill for someone else")
    @CommandMethod("<skill> <amount> <player>")
    @CommandPermission("mcmmocredits.redeem.other")
    private void redeemCreditsOther(CommandSender sender, @Argument("skill") PrimarySkillType skill, @Argument("amount") @Range(min = "1", max = "2147483647") int amount, @Argument(value = "player", suggestions = "customPlayer") String username, @Flag(value = "silent", permission = "mcmmocredits.redeem.other.silent") boolean silent) {
       database.getUUID(username).whenCompleteAsync((i, throwable) -> {
            if (creditRedemption(sender, i, skill, amount)) {
                Player player = Bukkit.getPlayer(i);
                TagResolver tr = Util.fullRedeem(sender, player, WordUtils.capitalizeFully(skill.name()), Util.SKILL_TOOLS.getLevelCap(skill), amount);
                Util.sendMessage(sender, Keys.REDEEM_SUCCESSFUL_SENDER.get(), tr);
                if (!silent) {
                    Util.sendMessage(player, Keys.REDEEM_SUCCESSFUL_RECEIVER.get(), tr);
                }
            }
        });
    }

    public static boolean creditRedemption(CommandSender sender, UUID uuid, PrimarySkillType skill, int amount) {
        Keys key = null;
        if (!Util.SKILL_TOOLS.getNonChildSkills().contains(skill)) {
            key = Keys.INVALID_ARGUMENTS;
        }
        if (!database.doesPlayerExist(uuid) || !DATABASE_API.doesPlayerExistInDB(uuid)) {
            key = Keys.PLAYER_DOES_NOT_EXIST;
        }
        if (database.getCredits(uuid) < amount) {
            key = Keys.REDEEM_NOT_ENOUGH_CREDITS;
        }
        if (ExperienceAPI.getLevelOffline(uuid, skill.name()) + amount > Util.SKILL_TOOLS.getLevelCap(skill)) {
            key = Keys.REDEEM_SKILL_CAP;
        }
        Player player = Bukkit.getPlayer(uuid);
        if (key != null) {
            Util.sendMessage(sender, key.get(), key.equals(Keys.PLAYER_DOES_NOT_EXIST) ? Util.quick(sender) : Util.player(player));
            return false;
        } else {
            PlayerProfile profile = Objects.requireNonNull(player).isOnline() ? UserManager.getPlayer(player).getProfile() : mcMMO.getDatabaseManager().loadPlayerProfile(uuid);
            if (profile.isLoaded()) {
                profile.addLevels(skill, amount);
                database.takeCredits(uuid, amount);
                return true;
            } else {
                return false;
            }
        }
    }
}
