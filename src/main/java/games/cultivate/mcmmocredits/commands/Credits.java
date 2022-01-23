package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.specifier.Greedy;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        if (!(sender instanceof Player player)) {
            //TODO Cloud exception
            Bukkit.getLogger().log(Level.WARNING, "You must supply a username! /credits <player>");
            return;
        }
        ConfigHandler.sendMessage(player, Keys.CREDITS_BALANCE_SELF, Util.basicBuilder(player).build());
    }

    @CommandDescription("Check someone else's MCMMO Credit balance.")
    @CommandMethod("<player>")
    @CommandPermission("mcmmocredits.check.other")
    private void checkCreditsOther(CommandSender sender, @Argument(value = "player", suggestions = "customPlayer") Player player) {
        if (Util.shouldProcess(sender, player)) {
            ConfigHandler.sendMessage(sender, Keys.CREDITS_BALANCE_OTHER, Util.basicBuilder(player).build());
        }
    }

    @CommandDescription("Reload all configuration files provided by the plugin.")
    @CommandMethod("reload")
    @CommandPermission("mcmmocredits.admin.reload")
    private void reloadCredits(CommandSender sender) {
        ConfigHandler.instance().loadConfig();
        ConfigHandler.sendMessage(sender, Keys.CREDITS_RELOAD_SUCCESSFUL, Util.quickResolver(sender));
    }

    @CommandDescription("Change settings in game and have changes take effect immediately.")
    @CommandMethod("settings <settings> <change>")
    @CommandPermission("mcmmocredits.admin.settings")
    private void changeSettings(CommandSender sender, @Argument("settings") String setting, @Argument("change") @Greedy String change) {
        if (ConfigHandler.changeConfig(Util.getPathFromSuffix(setting), change)) {
            ConfigHandler.sendMessage(sender, Keys.CREDITS_SETTING_CHANGE_SUCCESSFUL, Util.settingsBuilder(sender, setting, change).build());
        } else {
            ConfigHandler.sendMessage(sender, Keys.CREDITS_SETTING_CHANGE_FAILURE, Util.quickResolver(sender));
        }
    }

    @Suggestions("settings")
    public List<String> settingSelection(CommandContext<CommandSender> sender, String input) {
        return Keys.all.stream().map(key -> key.path()[key.path().length - 1]).toList();
    }

    @Parser(suggestions = "settings")
    public String settingParser(CommandContext<CommandSender> sender, Queue<String> inputQueue) {
        final String input = inputQueue.peek();
        inputQueue.poll();
        return input;
    }
}
