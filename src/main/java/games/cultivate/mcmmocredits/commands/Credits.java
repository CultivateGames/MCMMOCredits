package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.specifier.Greedy;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.CommandExecutionException;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.util.Menus;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Queue;

/**
 * This class is responsible for handling of the /credits command.
 */
@CommandMethod("credits")
public class Credits {

    @CommandDescription("Check your own MCMMO Credit balance.")
    @CommandMethod("")
    @CommandPermission("mcmmocredits.check.self")
    private void checkCredits(Player player) {
        ConfigHandler.sendMessage(player, Keys.CREDITS_BALANCE_SELF, Util.basicBuilder(player).build());
    }

    @CommandDescription("Check someone else's MCMMO Credit balance.")
    @CommandMethod("<username>")
    @CommandPermission("mcmmocredits.check.other")
    private void checkCreditsOther(CommandSender sender, @Argument(value = "username", suggestions = "customPlayer") String username) {
        MCMMOCredits.getAdapter().getUUID(username).whenCompleteAsync((i, throwable) -> {
            if (MCMMOCredits.getAdapter().doesPlayerExist(i)) {
                ConfigHandler.sendMessage(sender, Keys.CREDITS_BALANCE_OTHER, Util.basicBuilder(Objects.requireNonNull(Bukkit.getPlayer(i))).build());
            } else {
                //TODO Async is swallowing the exception.
                sender.sendMessage(ConfigHandler.exceptionMessage(sender, Keys.INVALID_ARGUMENTS));
                throw new CommandExecutionException(throwable);
            }
        });
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
        if (ConfigHandler.changeConfigInGame(Util.getPathFromSuffix(setting), change)) {
            ConfigHandler.sendMessage(sender, Keys.CREDITS_SETTING_CHANGE_SUCCESSFUL, Util.settingsBuilder(sender, setting, change).build());
        } else {
            ConfigHandler.sendMessage(sender, Keys.CREDITS_SETTING_CHANGE_FAILURE, Util.quickResolver(sender));
        }
    }

    @CommandDescription("Open a Menu that can be used to interface with this plugin.")
    @CommandMethod("gui")
    @CommandPermission("mcmmocredits.gui.basic")
    private void openGUI(Player player, @Flag(value = "location", permission = "mcmmocredits.gui.admin") @Nullable String string) {
        if (string == null) {
            Menus.openMainMenu(player);
            return;
        }
        if (string.equalsIgnoreCase("messages")) {
            Menus.openMessagesMenu(player);
            return;
        }
        if (string.equalsIgnoreCase("settings")) {
            Menus.openSettingsMenu(player);
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
