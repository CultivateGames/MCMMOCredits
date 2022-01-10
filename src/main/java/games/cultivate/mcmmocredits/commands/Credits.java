package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.google.inject.Inject;
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

/**
 * This class is responsible for handling of the /credits command.
 */
@CommandMethod("credits")
public class Credits {
    @Inject private final Database database;

    public Credits(Database database) {
        this.database = database;
    }

    @CommandDescription("Check your own MCMMO Credit balance.")
    @CommandMethod("")
    @CommandPermission("mcmmocredits.check.self")
    private void checkCredits(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            Bukkit.getLogger().log(Level.WARNING, "You must supply a username! /credits <player>");
            return;
        }
        ConfigHandler.sendMessage(player, Util.parse(player, Keys.CREDITS_CHECK_SELF));
    }

    @CommandDescription("Check someone else's MCMMO Credit balance.")
    @CommandMethod("<player>")
    @CommandPermission("mcmmocredits.check.other")
    private void checkCreditsOther(CommandSender sender, @Argument("player") String username) {
        if (Util.getOfflineUser(username) != null && database.doesPlayerExist(Util.getOfflineUser(username).getUniqueId())) {
            ConfigHandler.sendMessage(sender, Util.parse(Util.getOfflineUser(username), Keys.CREDITS_CHECK_OTHER));
            return;
        }
        if (sender instanceof Player player) {
            ConfigHandler.sendMessage(player, Util.parse(player, Keys.PLAYER_DOES_NOT_EXIST));
        } else {
            ConfigHandler.sendMessage(sender, Keys.PLAYER_DOES_NOT_EXIST.getString());
        }
    }

    @CommandDescription("Reload the settings.conf configuration file.")
    @CommandMethod("reload settings")
    @CommandPermission("mcmmocredits.admin.reload")
    private void reloadSettings(CommandSender sender) {
        ConfigHandler.loadConfig(Config.SETTINGS);
        this.sendReloadMessage(sender);
    }

    @CommandDescription("Reload the messages.conf configuration file.")
    @CommandMethod("reload messages")
    @CommandPermission("mcmmocredits.admin.reload")
    private void reloadMessages(CommandSender sender) {
        ConfigHandler.loadConfig(Config.MESSAGES);
        this.sendReloadMessage(sender);
    }

    @CommandDescription("Reload all configuration files provided by the plugin.")
    @CommandMethod("reload all")
    @CommandPermission("mcmmocredits.admin.reload")
    private void reloadAll(CommandSender sender) {
        ConfigHandler.loadAllConfigs();
        this.sendReloadMessage(sender);
    }

    @CommandDescription("Change settings in game and have changes take effect immediately.")
    @CommandMethod("setting <setting> <result>")
    @CommandPermission("mcmmocredits.admin.settings")
    private void changeSettings(CommandSender sender, @Argument("setting") String setting, @Argument("result") String result) {
        //TODO
        this.sendReloadMessage(sender);
    }

    @CommandDescription("Change settings in game and have changes take effect immediately.")
    @CommandMethod("message <message> <result>")
    @CommandPermission("mcmmocredits.admin.messages")
    private void changeMessages(CommandSender sender, @Argument("message") String message, @Argument("result") String result) {
        //TODO
        this.sendReloadMessage(sender);
    }

    private void sendReloadMessage(CommandSender sender) {
        if (sender instanceof Player) {
            ConfigHandler.sendMessage(sender, Util.parse((Player) sender, Keys.RELOAD_SUCCESSFUL));
        } else {
            ConfigHandler.sendMessage(sender, Keys.RELOAD_SUCCESSFUL.getString());
        }
    }



}
