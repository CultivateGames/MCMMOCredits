package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.specifier.Range;
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.util.Util;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * This class is responsible for handling of the /modifycredits command.
 */
@CommandMethod("modifycredits")
public class ModifyCredits {
    @CommandDescription("Add MCMMO Credits to a user's balance.")
    @CommandMethod("add <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void addCredits(CommandSender sender, @Argument("amount") @Range(min = "1", max = "2147483647") int amount, @Argument(value = "username", suggestions = "customPlayer") String username, @Flag("silent") boolean silent) {
        Util.shouldProcessUUID(sender, username).ifPresent(uuid -> this.modifyCredits(sender, amount, uuid, Operation.ADD, silent));
    }

    @CommandDescription("Set a user's MCMMO Credit balance to the specified amount.")
    @CommandMethod("set <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void setCredits(CommandSender sender, @Argument("amount") @Range(min = "0", max = "2147483647") int amount, @Argument(value = "username", suggestions = "customPlayer") String username, @Flag("silent") boolean silent) {
        Util.shouldProcessUUID(sender, username).ifPresent(uuid -> this.modifyCredits(sender, amount, uuid, Operation.SET, silent));
    }

    @CommandDescription("Take MCMMO Credits away from a user's balance")
    @CommandMethod("take <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void takeCredits(CommandSender sender, @Argument("amount") @Range(min = "1", max = "2147483647") int amount, @Argument(value = "username", suggestions = "customPlayer") String username, @Flag("silent") boolean silent) {
        Util.shouldProcessUUID(sender, username).ifPresent(uuid -> this.modifyCredits(sender, amount, uuid, Operation.TAKE, silent));
    }

    private enum Operation { ADD,SET,TAKE }

    private void modifyCredits(CommandSender sender, int amount, UUID uuid, Operation op, boolean silent) {
        if (Database.doesPlayerExist(uuid)) {
            switch (op) {
                case ADD -> Database.addCredits(uuid, amount);
                case TAKE -> Database.takeCredits(uuid, amount);
                case SET -> Database.setCredits(uuid, amount);
            }
            Pair<CommandSender, Player> transactionPair = Pair.of(sender, Bukkit.getPlayer(uuid));
            ConfigHandler.sendMessage(sender, Keys.valueOf("MODIFY_CREDITS" + op.name() + "_SENDER"), Util.transactionBuilder(transactionPair, amount).build());
            if (sender != transactionPair.right() && !silent) {
                ConfigHandler.sendMessage(transactionPair.right(), Keys.valueOf("MODIFY_CREDITS" + op.name() + "_RECEIVER"), Util.transactionBuilder(transactionPair, amount).build());
            }
        }
    }
}
