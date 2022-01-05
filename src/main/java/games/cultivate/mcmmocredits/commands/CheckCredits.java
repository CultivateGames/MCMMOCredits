package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import games.cultivate.mcmmocredits.util.ConfigHandler;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;

/**
 * This class is responsible for handling of the /credits command.
 */
@CommandMethod("credits")
public class CheckCredits {
    @CommandDescription("Check your own MCMMO Credit balance.")
    @CommandMethod("")
    @CommandPermission("mcmmocredits.check.self")
    private void checkCredits(CommandSender sender) {
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().log(Level.WARNING, "You must supply a username! /credits <player>");
            return;
        }
        ConfigHandler.sendMessage(sender, ConfigHandler.parse((Player) sender, ConfigHandler.message("credits-check-self")));
    }

    @CommandDescription("Check someone else's MCMMO Credit balance.")
    @CommandMethod("<player>")
    @CommandPermission("mcmmocredits.check.other")
    private void checkCreditsOther(CommandSender sender, @Argument("player") String username) {
        if (Util.processPlayer(username)) {
            ConfigHandler.sendMessage(sender, ConfigHandler.parse(Util.getOfflineUser(username), ConfigHandler.message("credits-check-other")));
            return;
        }
        if (sender instanceof Player) {
            ConfigHandler.sendMessage(sender, ConfigHandler.parse((Player) sender, ConfigHandler.message("player-does-not-exist")));
        } else {
            ConfigHandler.sendMessage(sender, ConfigHandler.message("player-does-not-exist"));
        }
    }

    /**
     * This is responsible for creating a Suggestions provider for these commands.
     *
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
     *
     * TODO: Figure out if this needs to be duplicated per class.
     */
    @Parser(suggestions = "player")
    public String playerParser(CommandContext<CommandSender> sender, Queue<String> inputQueue) {
        final String input = inputQueue.peek();
        inputQueue.poll();
        return input;
    }
}
