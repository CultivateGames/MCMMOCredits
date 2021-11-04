package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import games.cultivate.mcmmocredits.util.ConfigHandler;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;

/**
 * <p>This class is responsible for letting users execute commands which allow them to see their own
 * or another user's MCMMO Credit Balance.</p>
 *
 * <p>There are two commands created here:</p>
 * 1. /credits: Shows your own MCMMO Credit Balance
 * <br>
 * 2. /credits <username>: Shows the MCMMO Credit Balance of a specified user.
 *
 * @see CheckCredits#checkCredits(CommandSender)
 * @see CheckCredits#checkCreditsOther(CommandSender, String)
 */
public class CheckCredits {
    /**
     * <p>Command that is used for player to check their own MCMMO Credit balance.
     * If the sender is not a Player, we send a warning and stop processing.</p>
     *
     * <p>If we know they are a player, checks are not necessary since they will be checking their own balance.
     * We can safely process them since they are in the database as long as the Event Listeners work properly</p>
     *
     * @param sender The {@link CommandSender} that executed the command.
     */
    @CommandDescription("Check your own MCMMO Credits!")
    @CommandMethod("credits")
    @CommandPermission("mcmmocredits.check.self")
    private void checkCredits(CommandSender sender) {
        if(!(sender instanceof Player)) {
            Bukkit.getLogger().log(Level.WARNING, "You must supply a username! /credits <player>");
            return;
        }
        ConfigHandler.sendMessage(sender, ConfigHandler.parse((Player) sender, ConfigHandler.message("credits-check-self")));
    }

    /**
     * <p>This command processes MCMMO Credit balance check in the same way,
     * but passes another user instead of self-checking. The only difference is error handling in case this command
     * is selected by accident by Cloud. This should be rare/non-existent.</p>
     *
     * <p>If the player should not be processed, we are just going to parse external placeholders for the message viewer
     * If the message viewer is not a Player, skip parsing placeholders as we have no one to parse for.</p>
     *
     * @param sender   The {@link CommandSender} that executed the command.
     * @param username String which represents an {@link OfflinePlayer}'s username.
     * @see CheckCredits#checkCredits(CommandSender)
     */
    @CommandDescription("Check the MCMMO Credits of another user!")
    @CommandMethod("credits <player>")
    @CommandPermission("mcmmocredits.check.other")
    private void checkCreditsOther(CommandSender sender, @Argument("player") String username) {
        if (Util.processPlayer(username)) {
            ConfigHandler.sendMessage(sender, ConfigHandler.parse(Util.getOfflineUser(username), ConfigHandler.message("credits-check-other")));
            return;
        }
        if(sender instanceof Player) {
            ConfigHandler.sendMessage(sender, ConfigHandler.parse((Player) sender, ConfigHandler.message("player-does-not-exist")));
        } else {
            ConfigHandler.sendMessage(sender, ConfigHandler.message("player-does-not-exist"));
        }
    }

    /**
     * <p>This method is used to create a Suggestions Provider for this set of commands.</p>
     *
     * @return {@link List<String>} of {@link Player} usernames.
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
     * @return user input when parsing command arguments?
     * @see cloud.commandframework.context.CommandContext
     * @see cloud.commandframework.annotations.parsers.Parser
     */
    @Parser(suggestions = "player")
    public String playerParser(CommandContext<CommandSender> sender, Queue<String> inputQueue) {
        final String input = inputQueue.peek();
        inputQueue.poll();
        return input;
    }
}