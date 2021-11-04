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
 * @see Reload#reloadFiles(CommandSender)
 * @see <a href="https://github.com/SpongePowered/Configurate" target="_top">Thank you to Configurate!</a>
 */
public class Reload {
    /**
     * <p>This is a command that is used to reload both settings.conf and messages.conf if a change was made.
     * This should be reserved for use by administrators. If the command is executed by Console, we skip placeholder parsing.</p>
     *
     * @param sender The {@link CommandSender} that executed the Command.
     */
    @CommandDescription("MCMMO Credits Reload")
    @CommandMethod("creditsreload")
    @CommandPermission("mcmmocredits.admin")
    private void reloadFiles(CommandSender sender) {
        ConfigHandler.loadFile("settings");
        ConfigHandler.loadFile("messages");
        if (sender instanceof Player) {
            ConfigHandler.sendMessage(sender, ConfigHandler.parse((Player) sender, ConfigHandler.message("reload-successful")));
        } else {
            ConfigHandler.sendMessage(sender, ConfigHandler.message("reload-successful"));
        }
    }
}