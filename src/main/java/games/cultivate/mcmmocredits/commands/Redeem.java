package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.specifier.Range;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.arguments.parser.ArgumentParser;
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
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.logging.Level;

/**
 * <p>This class is responsible for core functionality of the plugin, which is to redeem MCMMO Credits into MCMMO Skills in
 * order to increase their level via simple command usage.</p>
 *
 * <p>This creates two commands:</p>
 * 1. /redeem [skill] [amount]: Allows you to redeem a specified amount of MCMMO Credits into a MCMMO Skill to increase it's level.
 * <br>
 * Example: /redeem herbalism 1500 would add 1500 additional levels to the Herbalism MCMMO Skill.
 * <br>
 * 2. /redeem [skill] [amount] [username]: Allows an administrator to redeem MCMMO Credits for another user, even if they are offline.
 *
 * @see Redeem#redeemCreditsSelf(CommandSender, PrimarySkillType, int)
 * @see Redeem#redeemCreditsOther(CommandSender, PrimarySkillType, int, String)
 */
public class Redeem {
    /**
     * <p>This command is used when a user wants to redeem their own MCMMO Credits into a MCMMO Skill.</p>
     *
     * <p>First, we check if the command is chosen by mistake. If it's executed from console, send feedback.</p>
     *
     * <p>Next, we are setting our variables. This could possibly be cleaned up, but many of these are accessed frequently.</p>
     *
     * <p>Then, we do some validation to make sure this transaction is valid. We check the skill type and make sure it is "non-child",
     * we check that the user has enough MCMMO Credits, and we check that the transaction will not go over the level cap
     * for a MCMMO Skill.</p>
     *
     * <p>Finally, we execute the transaction by taking the Credits and applying the levels to the player.</p>
     *
     * @param sender The {@link CommandSender} that executed the command.
     * @param skill  The {@link PrimarySkillType} to apply MCMMO Credits to.
     * @param amount The amount of MCMMO Credits to redeem. This is also the amount by which a skill's level will increase.
     */
    @CommandDescription("Redeem MCMMO Credits to increase the level of a Skill")
    @CommandMethod("redeem|rmc|redeemcredits <skill> <amount>")
    @CommandPermission("mcmmocredits.redeem.self")
    private void redeemCreditsSelf(CommandSender sender, @Argument("skill") PrimarySkillType skill, @Argument("amount") @Range(min = "1", max = "2147483647") int amount) {
        if (sender instanceof ConsoleCommandSender) {
            Bukkit.getLogger().log(Level.WARNING, "You must supply a username! /redeem <skill> <amount> <player>");
            return;
        }
        SkillTools st = mcMMO.p.getSkillTools();
        Player player = (Player) sender;
        String username = player.getName();
        OfflinePlayer offlinePlayer = Util.getOfflineUser(username);
        UUID uuid = offlinePlayer.getUniqueId();
        int credits = Database.getCredits(uuid);
        String skillName = skill.name();
        int cap = st.getLevelCap(skill);
        try {
            if (!st.getNonChildSkills().contains(skill)) {
                ConfigHandler.sendMessage(sender, ConfigHandler.parse(offlinePlayer, ConfigHandler.message("invalid-args"), skillName, cap, amount));
                return;
            }
            if (credits < amount) {
                ConfigHandler.sendMessage(sender, ConfigHandler.parse(offlinePlayer, ConfigHandler.message("redeem-not-enough-credits"), skillName, cap, amount));
                return;
            }
            if (ExperienceAPI.getLevel(player, skill) + amount > cap) {
                ConfigHandler.sendMessage(sender, ConfigHandler.parse(offlinePlayer, ConfigHandler.message("redeem-skill-cap"), skillName, cap, amount));
                return;
            }
            int newAmount = credits - amount;
            Database.setCredits(player.getUniqueId(), newAmount);
            ExperienceAPI.addLevel(player, skill.name(), amount);
            ConfigHandler.sendMessage(sender, ConfigHandler.parse(offlinePlayer, ConfigHandler.message("redeem-successful-other"), skillName, cap, amount));
        } catch (InvalidSkillException ignored) {
        }

    }

    /**
     * <p>This command allows an administrator to redeem MCMMO Credits on behalf of another user.
     * The user can be either online or offline. The only difference in processing is the passing of another user
     * and checking if that user exists in MCMMO's database.</p>
     *
     * @param sender   The {@link CommandSender} that executed the command.
     * @param skill    The {@link PrimarySkillType} to apply MCMMO Credits to.
     * @param amount   The amount of MCMMO Credits to redeem. This is also the amount by which a skill's level will increase.
     * @param username A string which represents the username of an {@link OfflinePlayer}.
     * @see Redeem#redeemCreditsSelf(CommandSender, PrimarySkillType, int)
     */
    @CommandDescription("Redeem MCMMO Credits to increase the level of a Skill of someone else.")
    @CommandMethod("redeem|rmc|redeemcredits <skill> <amount> <player>")
    @CommandPermission("mcmmocredits.redeem.other")
    private void redeemCreditsOther(CommandSender sender, @Argument("skill") PrimarySkillType skill, @Argument("amount") @Range(min = "1", max = "2147483647") int amount, @Argument("player") String username) {
        if (sender instanceof ConsoleCommandSender) {
            Bukkit.getLogger().log(Level.WARNING, "You must supply a username! /redeem <skill> <amount> <player>");
            return;
        }
        SkillTools st = mcMMO.p.getSkillTools();
        OfflinePlayer offlinePlayer = Util.getOfflineUser(username);
        UUID uuid = offlinePlayer.getUniqueId();
        int credits = Database.getCredits(uuid);
        String skillName = skill.name();
        int cap = st.getLevelCap(skill);
        DatabaseAPI db = new DatabaseAPI();
        if (!db.doesPlayerExistInDB(uuid)) {
            if (sender instanceof Player) {
                ConfigHandler.sendMessage(sender, ConfigHandler.parse((Player) sender, ConfigHandler.message("player-does-not-exist")));
            } else {
                ConfigHandler.sendMessage(sender, ConfigHandler.message("player-does-not-exist"));
            }
            return;
        }
        try {
            if (!st.getNonChildSkills().contains(skill)) {
                ConfigHandler.sendMessage(sender, ConfigHandler.parse(offlinePlayer, ConfigHandler.message("invalid-args"), skillName, cap, amount));
                return;
            }
            if (credits < amount) {
                ConfigHandler.sendMessage(sender, ConfigHandler.parse(offlinePlayer, ConfigHandler.message("redeem-not-enough-credits"), skillName, cap, amount));
                return;
            }
            if (ExperienceAPI.getLevelOffline(uuid, skillName) + amount > cap) {
                ConfigHandler.sendMessage(sender, ConfigHandler.parse(offlinePlayer, ConfigHandler.message("redeem-skill-cap"), skillName, cap, amount));
                return;
            }
            int newAmount = credits - amount;
            Database.setCredits(uuid, newAmount);
            ExperienceAPI.addLevelOffline(uuid, skillName, amount);
            ConfigHandler.sendMessage(sender, ConfigHandler.parse(offlinePlayer, ConfigHandler.message("redeem-successful-other"), skillName, cap, amount));
        } catch (InvalidSkillException ignored) {
        }

    }

    /**
     * <p>This method is used to create a Suggestions Provider for this set of commands.</p>
     *
     * @param context Context of the command sender.
     * @param input Command input from this command.
     * @return {@link List} of {@link Player} usernames.
     * @see cloud.commandframework.context.CommandContext
     * @see cloud.commandframework.annotations.suggestions.Suggestions
     * @see cloud.commandframework.arguments.CommandArgument.Builder
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
     * <p>This method is used to create an {@link ArgumentParser} for this set of commands.</p>
     *
     * @param sender The command sender for this command.
     * @param inputQueue Command input from the command sender.
     * @return user input when parsing command arguments?
     * @see cloud.commandframework.annotations.parsers.Parser
     */
    @Parser(suggestions = "player")
    public String playerParser(CommandContext<CommandSender> sender, Queue<String> inputQueue) {
        final String input = inputQueue.peek();
        inputQueue.poll();
        return input;
    }
}
