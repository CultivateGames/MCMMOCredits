package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Messages;
import games.cultivate.mcmmocredits.config.Settings;
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
public class Credits {
    @CommandDescription("Check your own MCMMO Credit balance.")
    @CommandMethod("")
    @CommandPermission("mcmmocredits.check.self")
    private void checkCredits(CommandSender sender) {
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().log(Level.WARNING, "You must supply a username! /credits <player>");
            return;
        }
        ConfigHandler.sendMessage(sender, Util.parse((Player) sender, Messages.CREDITS_CHECK_SELF));
    }

    @CommandDescription("Check someone else's MCMMO Credit balance.")
    @CommandMethod("<player>")
    @CommandPermission("mcmmocredits.check.other")
    private void checkCreditsOther(CommandSender sender, @Argument("player") String username) {
        if (Util.processPlayer(username)) {
            ConfigHandler.sendMessage(sender, Util.parse(Util.getOfflineUser(username), Messages.CREDITS_CHECK_OTHER));
            return;
        }
        if (sender instanceof Player) {
            ConfigHandler.sendMessage(sender, Util.parse((Player) sender, Messages.PLAYER_DOES_NOT_EXIST));
        } else {
            ConfigHandler.sendMessage(sender, Messages.PLAYER_DOES_NOT_EXIST.message());
        }
    }

    @CommandDescription("Reload the settings.conf configuration file.")
    @CommandMethod("reload settings")
    @CommandPermission("mcmmocredits.admin.reload")
    private void reloadSettings(CommandSender sender) {
        ConfigHandler.loadFile("settings");
        this.sendReloadMessage(sender);
    }

    @CommandDescription("Reload the messages.conf configuration file.")
    @CommandMethod("reload messages")
    @CommandPermission("mcmmocredits.admin.reload")
    private void reloadMessages(CommandSender sender) {
        ConfigHandler.loadFile("messages");
        this.sendReloadMessage(sender);
    }

    @CommandDescription("Reload all configuration files provided by the plugin.")
    @CommandMethod("reload all")
    @CommandPermission("mcmmocredits.admin.reload")
    private void reloadAll(CommandSender sender) {
        ConfigHandler.loadFile("settings");
        ConfigHandler.loadFile("messages");
        this.sendReloadMessage(sender);
    }

    private void sendReloadMessage(CommandSender sender) {
        if (sender instanceof Player) {
            ConfigHandler.sendMessage(sender, Util.parse((Player) sender, Messages.RELOAD_SUCCESSFUL));
        } else {
            ConfigHandler.sendMessage(sender, Messages.RELOAD_SUCCESSFUL.message());
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
