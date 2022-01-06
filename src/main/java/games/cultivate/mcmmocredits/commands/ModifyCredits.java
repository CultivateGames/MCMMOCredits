package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.specifier.Range;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Messages;
import games.cultivate.mcmmocredits.config.Settings;
import games.cultivate.mcmmocredits.util.Database;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

/**
 * This class is responsible for handling of the /modifycredits command.
 */
@CommandMethod("modifycredits")
public class ModifyCredits {
    @CommandDescription("Add MCMMO Credits to a user's balance.")
    @CommandMethod("add <amount> <player>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void addCredits(CommandSender sender, @Argument("amount") @Range(min = "1", max = "2147483647") int amount, @Argument("player") String username) {
        if (shouldProcess(sender, username)) {
            UUID uuid = Util.getOfflineUser(username).getUniqueId();
            Database.setCredits(uuid, Database.getCredits(uuid) + amount);
            ConfigHandler.sendMessage(sender, Util.parse(Util.getOfflineUser(username), Messages.MODIFY_CREDITS_ADD, amount));
        }
    }

    @CommandDescription("Set a user's MCMMO Credit balance to the specified amount.")
    @CommandMethod("set <amount> <player>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void setCredits(CommandSender sender, @Argument("amount") @Range(min = "0", max = "2147483647") int amount, @Argument("player") String username) {
        if (shouldProcess(sender, username)) {
            UUID uuid = Util.getOfflineUser(username).getUniqueId();
            Database.setCredits(uuid, amount);
            ConfigHandler.sendMessage(sender, Util.parse(Util.getOfflineUser(username), Messages.MODIFY_CREDITS_SET, amount));
        }
    }

    @CommandDescription("Take MCMMO Credits away from a user's balance")
    @CommandMethod("take <amount> <player>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void takeCredits(CommandSender sender, @Argument("amount") @Range(min = "1", max = "2147483647") int amount, @Argument("player") String username) {
        if (shouldProcess(sender, username)) {
            UUID uuid = Util.getOfflineUser(username).getUniqueId();
            Database.setCredits(uuid, Database.getCredits(uuid) - amount);
            ConfigHandler.sendMessage(sender, Util.parse(Util.getOfflineUser(username), Messages.MODIFY_CREDITS_TAKE, amount));
        }
    }

    private boolean shouldProcess(CommandSender sender, String username) {
        if (!Util.processPlayer(username)) {
            ConfigHandler.sendMessage(sender, Util.parse(Util.getOfflineUser(username), Messages.PLAYER_DOES_NOT_EXIST));
            return false;
        }
        return true;
    }

    /**
     * This is responsible for creating a Suggestions provider for these commands.
     *
     * TODO: Figure out if this needs to be duplicated per class.
     */
    @Suggestions("player")
    public List<String> playerSuggestions(CommandContext<CommandSender> context, String input) {
        List<String> list = new ArrayList<>();
        if ((boolean) Settings.PLAYER_TAB_COMPLETION.value()) {
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
