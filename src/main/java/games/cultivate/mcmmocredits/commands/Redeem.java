package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Range;
import com.gmail.nossr50.api.DatabaseAPI;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.skills.SkillTools;
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.logging.Level;

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
    private void redeemCreditsSelf(CommandSender sender, @Argument("skill") PrimarySkillType skill, @Argument("amount") @Range(min = "1", max = "2147483647") int amount) {
        if (!(sender instanceof Player player)) {
            Bukkit.getLogger().log(Level.WARNING, "You must supply a username! /redeem <skill> <amount> <player>");
            return;
        }
        String skillName = skill.name();
        int cap = st.getLevelCap(skill);
        UUID uuid = player.getUniqueId();
        Keys result = this.conditionCheck(uuid, skill, amount);
        try {
            if (result == null) {
                Database.setCredits(uuid, Database.getCredits(uuid) - amount);
                ExperienceAPI.addLevel(player, skillName, amount);
                ConfigHandler.sendMessage(sender, Util.parse(Util.getOfflineUser(sender.getName()), Keys.REDEEM_SUCCESSFUL, skillName, cap, amount));
            } else {
                ConfigHandler.sendMessage(sender, Util.parse(Util.getOfflineUser(sender.getName()), result, skillName, cap, amount));
            }
        } catch (InvalidSkillException ignored) {
        }
    }

    @CommandDescription("Redeem MCMMO Credits into a specific skill for someone else")
    @CommandMethod("<skill> <amount> <player>")
    @CommandPermission("mcmmocredits.redeem.other")
    private void redeemCreditsOther(CommandSender sender, @Argument("skill") PrimarySkillType skill, @Argument("amount") @Range(min = "1", max = "2147483647") int amount, @Argument("player") String username) {
        if (sender instanceof Player) {
            String skillName = skill.name();
            int cap = st.getLevelCap(skill);
            OfflinePlayer offlinePlayer = Util.getOfflineUser(username);
            UUID uuid = offlinePlayer.getUniqueId();
            Keys result = this.conditionCheck(uuid, skill, amount);
            try {
                if (result == null) {
                    Database.setCredits(uuid, Database.getCredits(uuid) - amount);
                    if (offlinePlayer.isOnline()) {
                        ExperienceAPI.addLevel(offlinePlayer.getPlayer(), skillName, amount);
                    } else {
                        ExperienceAPI.addLevelOffline(uuid, skillName, amount);
                    }
                    ConfigHandler.sendMessage(sender, Util.parse(offlinePlayer, Keys.REDEEM_SUCCESSFUL_OTHER, skillName, cap, amount));
                    return;
                }
                //Specifically check for this key so that we are not parsing placeholders for a non-existent player.
                if (result.equals(Keys.PLAYER_DOES_NOT_EXIST)) {
                    ConfigHandler.sendMessage(sender, Keys.PLAYER_DOES_NOT_EXIST.getString());
                } else {
                    ConfigHandler.sendMessage(sender, Util.parse(offlinePlayer, result, skillName, cap, amount));
                }
            } catch (InvalidSkillException ignored) {
            }
        }
    }

    protected Keys conditionCheck(UUID uuid, PrimarySkillType skill, int amount) {
        if (!st.getNonChildSkills().contains(skill)) {
            return Keys.INVALID_ARGUMENTS;
        }
        if (!Database.doesPlayerExist(uuid) || !db.doesPlayerExistInDB(uuid)) {
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
}
