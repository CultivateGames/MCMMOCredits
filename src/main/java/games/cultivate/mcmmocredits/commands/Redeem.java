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
import com.gmail.nossr50.util.skills.SkillTools;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.util.Util;
import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver;
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
    private static final SkillTools st = mcMMO.p.getSkillTools();
    private static final DatabaseAPI db = new DatabaseAPI();

    @CommandDescription("Redeem your own MCMMO Credits into a specific skill.")
    @CommandMethod("<skill> <amount>")
    @CommandPermission("mcmmocredits.redeem.self")
    private void redeemCreditsSelf(Player player, @Argument("skill") PrimarySkillType skill, @Argument("amount") @Range(min = "1", max = "2147483647") int amount) {
        if (creditRedemption(player, player.getUniqueId(), skill, amount)) {
            ConfigHandler.sendMessage(player, Keys.REDEEM_SUCCESSFUL_SELF, Util.redeemBuilder(Pair.of(null, player), WordUtils.capitalizeFully(skill.name()), st.getLevelCap(skill), amount).build());
        }
    }

    @CommandDescription("Redeem MCMMO Credits into a specific skill for someone else")
    @CommandMethod("<skill> <amount> <player>")
    @CommandPermission("mcmmocredits.redeem.other")
    private void redeemCreditsOther(CommandSender sender, @Argument("skill") PrimarySkillType skill, @Argument("amount") @Range(min = "1", max = "2147483647") int amount, @Argument(value = "player", suggestions = "customPlayer") String username, @Flag(value = "silent", permission = "mcmmocredits.redeem.other.silent") boolean silent) {
        MCMMOCredits.getAdapter().getUUID(username).whenCompleteAsync((i, throwable) -> {
            if (creditRedemption(sender, i, skill, amount)) {
                Player player = Objects.requireNonNull(Bukkit.getPlayer(i));
                PlaceholderResolver successfulResolver = Util.redeemBuilder(Pair.of(sender, player), WordUtils.capitalizeFully(skill.name()), st.getLevelCap(skill), amount).build();
                ConfigHandler.sendMessage(sender, Keys.REDEEM_SUCCESSFUL_SENDER, successfulResolver);
                if (!silent) {
                    ConfigHandler.sendMessage(player, Keys.REDEEM_SUCCESSFUL_RECEIVER, successfulResolver);
                }
            }
        });
    }

    //Combine into method to use within menu.
    public static boolean creditRedemption(CommandSender sender, UUID uuid, PrimarySkillType skill, int amount) {
        Keys key = null;
        if (!st.getNonChildSkills().contains(skill)) {
            key = Keys.INVALID_ARGUMENTS;
        }
        if (!MCMMOCredits.getAdapter().doesPlayerExist(uuid) || !db.doesPlayerExistInDB(uuid)) {
            key = Keys.PLAYER_DOES_NOT_EXIST;
        }
        if (MCMMOCredits.getAdapter().getCredits(uuid) < amount) {
            key = Keys.REDEEM_NOT_ENOUGH_CREDITS;
        }
        if (ExperienceAPI.getLevelOffline(uuid, skill.name()) + amount > st.getLevelCap(skill)) {
            key = Keys.REDEEM_SKILL_CAP;
        }
        Player player = Bukkit.getPlayer(uuid);
        if (key != null) {
            ConfigHandler.sendMessage(sender, key, key.equals(Keys.PLAYER_DOES_NOT_EXIST) ? null : Util.basicBuilder(Objects.requireNonNull(player)).build());
            return false;
        } else {
            PlayerProfile profile = Objects.requireNonNull(player).isOnline() ? UserManager.getPlayer(player).getProfile() : mcMMO.getDatabaseManager().loadPlayerProfile(uuid);
            if (profile.isLoaded()) {
                profile.addLevels(skill, amount);
                MCMMOCredits.getAdapter().takeCredits(uuid, amount);
                return true;
            } else {
                return false;
            }
        }
    }
}
