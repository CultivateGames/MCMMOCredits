package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.specifier.Range;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import com.gmail.nossr50.api.DatabaseAPI;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.skills.SkillTools;
import games.cultivate.mcmmocredits.util.ConfigHandler;
import games.cultivate.mcmmocredits.util.Database;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
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
        String result = this.conditionCheck(uuid, skill, amount);
        try {
            if (result == null) {
                Database.setCredits(uuid, Database.getCredits(uuid) - amount);
                ExperienceAPI.addLevel(player, skillName, amount);
                ConfigHandler.sendMessage(sender, ConfigHandler.parse(Util.getOfflineUser(sender.getName()), ConfigHandler.message("redeem-successful"), skillName, cap, amount));
            } else {
                ConfigHandler.sendMessage(sender, ConfigHandler.parse(Util.getOfflineUser(sender.getName()), result, skillName, cap, amount));
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
            String result = this.conditionCheck(uuid, skill, amount);
            try {
                if (result == null) {
                    Database.setCredits(uuid, Database.getCredits(uuid) - amount);
                    ExperienceAPI.addLevelOffline(uuid, skillName, amount);
                    ConfigHandler.sendMessage(sender, ConfigHandler.parse(offlinePlayer, ConfigHandler.message("redeem-successful-other"), skillName, cap, amount));
                    return;
                }
                //Specifically check for this key so that we are not parsing placeholders for a non-existent player.
                if (result.equals(ConfigHandler.message("player-does-not-exist"))) {
                    ConfigHandler.sendMessage(sender, ConfigHandler.parse(offlinePlayer, result));
                } else {
                    ConfigHandler.sendMessage(sender, ConfigHandler.parse(offlinePlayer, result, skillName, cap, amount));
                }
            } catch (InvalidSkillException ignored) {
            }
        }
    }

    protected String conditionCheck(UUID uuid, PrimarySkillType skill, int amount) {
        if (!st.getNonChildSkills().contains(skill)) {
            return ConfigHandler.message("invalid-args");
        }
        if (!Database.doesPlayerExist(uuid) || !db.doesPlayerExistInDB(uuid)) {
            return ConfigHandler.message("player-does-not-exist");
        }
        if (Database.getCredits(uuid) > amount) {
            return ConfigHandler.message("redeem-not-enough-credits");
        }
        if (ExperienceAPI.getLevelOffline(uuid, skill.name()) + amount > st.getLevelCap(skill)) {
            return ConfigHandler.message("redeem-skill-cap");
        }
        return null;
    }

    /**
     * This is responsible for creating a Suggestions provider for these commands.
     * <p>
     * TODO: Figure out if this needs to be duplicated per class.
     */
    @Suggestions("player")
    public List<String> playerSuggestions(CommandContext<CommandSender> context, String input) {
        List<String> list = new ArrayList<>();
        if ((boolean) ConfigHandler.value("player-tab-completion")) {
            Bukkit.getOnlinePlayers().forEach(p -> list.add(p.getName()));
            return list;
        }
        return list;
    }

    /**
     * This is responsible for creating an Argument Parser for these commands.
     * <p>
     * TODO: Figure out if this needs to be duplicated per class.
     */
    @Parser(suggestions = "player")
    public String playerParser(CommandContext<CommandSender> sender, Queue<String> inputQueue) {
        final String input = inputQueue.peek();
        inputQueue.poll();
        return input;
    }
}
