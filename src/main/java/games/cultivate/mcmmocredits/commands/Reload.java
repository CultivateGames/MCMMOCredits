package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import games.cultivate.mcmmocredits.util.ConfigHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This class is responsible for the handling of the /creditsreload command.
 */
@CommandMethod("creditsreload|creload")
public class Reload {
    @CommandDescription("Reload the settings.conf configuration file.")
    @CommandMethod("settings")
    @CommandPermission("mcmmocredits.admin.reload.settings")
    private void reloadSettings(CommandSender sender) {
        ConfigHandler.loadFile("settings");
        this.sendReloadMessage(sender);
    }

    @CommandDescription("Reload the messages.conf configuration file.")
    @CommandMethod("messages")
    @CommandPermission("mcmmocredits.admin.reload.messages")
    private void reloadMessages(CommandSender sender) {
        ConfigHandler.loadFile("messages");
        this.sendReloadMessage(sender);
    }

    @CommandDescription("Reload all configuration files provided by the plugin.")
    @CommandMethod("all")
    @CommandPermission("mcmmocredits.admin.reload")
    private void reloadAll(CommandSender sender) {
        ConfigHandler.loadFile("settings");
        ConfigHandler.loadFile("messages");
        this.sendReloadMessage(sender);
    }

    private void sendReloadMessage(CommandSender sender) {
        if (sender instanceof Player) {
            ConfigHandler.sendMessage(sender, ConfigHandler.parse((Player) sender, ConfigHandler.message("reload-successful")));
        } else {
            ConfigHandler.sendMessage(sender, ConfigHandler.message("reload-successful"));
        }
    }
}
