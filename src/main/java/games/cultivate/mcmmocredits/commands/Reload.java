package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import games.cultivate.mcmmocredits.util.ConfigHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * <p>This class houses one command method, which is for reloading plugin files.</p>
 * <p>Due to the way Configurate works, all we have to do is call the same methods used on plugin startup.</p>
 *
 * @see Reload#reloadAll(CommandSender)
 * @see <a href="https://github.com/SpongePowered/Configurate" target="_top">Thank you to Configurate!</a>
 */
@CommandMethod("creditsreload|creload")
public class Reload {
    /**
     * <p>Command used to reload the settings.conf file.</p>
     *
     * @param sender The {@link CommandSender} that executed the Command.
     */
    @CommandDescription("MCMMO Credits Reload - Settings")
    @CommandMethod("settings")
    @CommandPermission("mcmmocredits.admin.reload.settings")
    private void reloadSettings(CommandSender sender) {
        ConfigHandler.loadFile("settings");
        this.sendReloadMessage(sender);
    }
    /**
     * <p>Command used to reload the messages.conf file.</p>
     *
     * @param sender The {@link CommandSender} that executed the Command.
     */
    @CommandDescription("MCMMO Credits Reload - Settings")
    @CommandMethod("messages")
    @CommandPermission("mcmmocredits.admin.reload.messages")
    private void reloadMessages(CommandSender sender) {
        ConfigHandler.loadFile("messages");
        this.sendReloadMessage(sender);
    }
    /**
     * <p>Command used to reload all configuration files.</p>
     *
     * @param sender The {@link CommandSender} that executed the Command.
     */
    @CommandDescription("MCMMO Credits Reload - All Files")
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
