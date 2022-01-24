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
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.database.Database;
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
        UUID uuid = player.getUniqueId();
        Keys result = this.conditionCheck(uuid, skill, amount);
        if (result != null) {
            ConfigHandler.sendMessage(player, result, Util.quickResolver(player));
        } else if (this.processRedemption(uuid, skill, amount)) {
            ConfigHandler.sendMessage(player, Keys.REDEEM_SUCCESSFUL_SELF, Util.redeemBuilder(Pair.of(null, player), WordUtils.capitalizeFully(skill.name()), st.getLevelCap(skill), amount).build());
        }
    }

    @CommandDescription("Redeem MCMMO Credits into a specific skill for someone else")
    @CommandMethod("<skill> <amount> <player>")
    @CommandPermission("mcmmocredits.redeem.other")
    private void redeemCreditsOther(CommandSender sender, @Argument("skill") PrimarySkillType skill, @Argument("amount") @Range(min = "1", max = "2147483647") int amount, @Argument(value = "player", suggestions = "customPlayer") String username, @Flag(value = "silent", permission = "mcmmocredits.redeem.other.silent") boolean silent) {
        Util.shouldProcessUUID(sender, username).ifPresent(uuid -> {
            if (Database.doesPlayerExist(uuid)) {
                Player player = Bukkit.getPlayer(uuid);
                Keys result = this.conditionCheck(uuid, skill, amount);
                if (result != null) {
                    ConfigHandler.sendMessage(sender, result, result.equals(Keys.PLAYER_DOES_NOT_EXIST) ? null : Util.basicBuilder(Objects.requireNonNull(player)).build());
                } else if (this.processRedemption(uuid, skill, amount)) {
                    PlaceholderResolver successfulResolver = Util.redeemBuilder(Pair.of(sender, Objects.requireNonNull(player)), WordUtils.capitalizeFully(skill.name()), st.getLevelCap(skill), amount).build();
                    ConfigHandler.sendMessage(sender, Keys.REDEEM_SUCCESSFUL_SENDER, successfulResolver);
                    if (!silent) {
                        ConfigHandler.sendMessage(player, Keys.REDEEM_SUCCESSFUL_RECEIVER, successfulResolver);
                    }
                }
            }
        });
    }

    //TODO Cloud exception
    private Keys conditionCheck(UUID uuid, PrimarySkillType skill, int amount) {
        if (!st.getNonChildSkills().contains(skill)) {
            return Keys.INVALID_ARGUMENTS;
        }
        if (!db.doesPlayerExistInDB(uuid)) {
            return Keys.PLAYER_DOES_NOT_EXIST;
        }
        if (Database.getCredits(uuid) < amount) {
            return Keys.REDEEM_NOT_ENOUGH_CREDITS;
        }
        if (ExperienceAPI.getLevelOffline(uuid, skill.name()) + amount > st.getLevelCap(skill)) {
            return Keys.REDEEM_SKILL_CAP;
        }
        return null;
    }

    //MCMMO API may actually be the death of me.
    private boolean processRedemption(UUID uuid, PrimarySkillType skill, int amount) {
        Player player = Bukkit.getPlayer(uuid);
        PlayerProfile profile = Objects.requireNonNull(player).isOnline() ? UserManager.getPlayer(player).getProfile() : mcMMO.getDatabaseManager().loadPlayerProfile(uuid);
        if(profile.isLoaded()) {
            profile.addLevels(skill, amount);
            Database.takeCredits(uuid, amount);
            return true;
        }
        return false;
    }
}
