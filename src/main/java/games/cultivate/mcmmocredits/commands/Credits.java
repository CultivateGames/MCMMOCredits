package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.CommandExecutionException;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.util.Menus;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        Util.sendMessage(player, Keys.CREDITS_BALANCE_SELF.get(), Util.basicBuilder(player).build());
    }

    @CommandDescription("Check someone else's MCMMO Credit balance.")
    @CommandMethod("<username>")
    @CommandPermission("mcmmocredits.check.other")
    private void checkCreditsOther(CommandSender sender, @Argument(value = "username", suggestions = "customPlayer") String username) {
        MCMMOCredits.getAdapter().getUUID(username).whenCompleteAsync((i, throwable) -> {
            if (MCMMOCredits.getAdapter().doesPlayerExist(i)) {
                Util.sendMessage(sender, Keys.CREDITS_BALANCE_OTHER.get(), Util.basicBuilder(Objects.requireNonNull(Bukkit.getPlayer(i))).build());
            } else {
                //TODO Async is swallowing the exception.
                sender.sendMessage(Util.exceptionMessage(sender, Keys.INVALID_ARGUMENTS.get()));
                throw new CommandExecutionException(throwable);
            }
        });
    }

    @CommandDescription("Reload all configuration files provided by the plugin.")
    @CommandMethod("reload")
    @CommandPermission("mcmmocredits.admin.reload")
    private void reloadCredits(CommandSender sender) {
        Config.MENU.load("menus.conf");
        Config.MESSAGES.load("messages.conf");
        Config.SETTINGS.load("settings.conf");
        Util.sendMessage(sender, Keys.CREDITS_RELOAD_SUCCESSFUL.get(), Util.quickResolver(sender));
    }

    @CommandDescription("Open a Menu that can be used to interface with this plugin.")
    @CommandMethod("menu")
    @CommandPermission("mcmmocredits.gui.basic")
    private void openMenu(Player player) {
        Menus.openMainMenu(player);
    }

    @CommandDescription("Open the Edit Messages Menu")
    @CommandMethod("menu messages")
    @CommandPermission("mcmmocredits.gui.admin")
    private void openMessagesMenu(Player player) {
        Menus.openMessagesMenu(player);
    }

    @CommandDescription("Open the Edit Settings Menu")
    @CommandMethod("menu settings")
    @CommandPermission("mcmmocredits.gui.admin")
    private void openSettingsMenu(Player player) {
        Menus.openSettingsMenu(player);
    }

    @CommandDescription("Open the Edit Messages Menu")
    @CommandMethod("menu redeem")
    @CommandPermission("mcmmocredits.gui.redeem")
    private void openRedeemMenu(Player player) {
        Menus.openRedeemMenu(player);
    }

    @Suggestions("settings")
    public List<String> settingSelection(CommandContext<CommandSender> sender, String input) {
        return Keys.CAN_CHANGE.stream().map(key -> key.path().get(key.path().size() - 1)).toList();
    }

    @Parser(suggestions = "settings")
    public String settingParser(CommandContext<CommandSender> sender, Queue<String> inputQueue) {
        final String input = inputQueue.peek();
        inputQueue.poll();
        return input;
    }
}
