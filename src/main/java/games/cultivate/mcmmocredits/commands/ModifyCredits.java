package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Range;
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.command.CommandSender;
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
            ConfigHandler.sendMessage(sender, Util.parse(Util.getOfflineUser(username), Keys.MODIFY_CREDITS_ADD, amount));
        }
    }

    @CommandDescription("Set a user's MCMMO Credit balance to the specified amount.")
    @CommandMethod("set <amount> <player>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void setCredits(CommandSender sender, @Argument("amount") @Range(min = "0", max = "2147483647") int amount, @Argument("player") String username) {
        if (shouldProcess(sender, username)) {
            UUID uuid = Util.getOfflineUser(username).getUniqueId();
            Database.setCredits(uuid, amount);
            ConfigHandler.sendMessage(sender, Util.parse(Util.getOfflineUser(username), Keys.MODIFY_CREDITS_SET, amount));
        }
    }

    @CommandDescription("Take MCMMO Credits away from a user's balance")
    @CommandMethod("take <amount> <player>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void takeCredits(CommandSender sender, @Argument("amount") @Range(min = "1", max = "2147483647") int amount, @Argument("player") String username) {
        if (shouldProcess(sender, username)) {
            UUID uuid = Util.getOfflineUser(username).getUniqueId();
            Database.setCredits(uuid, Database.getCredits(uuid) - amount);
            ConfigHandler.sendMessage(sender, Util.parse(Util.getOfflineUser(username), Keys.MODIFY_CREDITS_TAKE, amount));
        }
    }

    private boolean shouldProcess(CommandSender sender, String username) {
        if (!Util.processPlayer(username)) {
            ConfigHandler.sendMessage(sender, Keys.PLAYER_DOES_NOT_EXIST.getString());
            return false;
        }
        return true;
    }
}
