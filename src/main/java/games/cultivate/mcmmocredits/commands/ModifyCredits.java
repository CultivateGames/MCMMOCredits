package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.specifier.Range;
import cloud.commandframework.exceptions.CommandExecutionException;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Keys;
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
    private void addCredits(CommandSender sender, @Argument("amount") @Range(min = "1") int amount, @Argument(value = "username", suggestions = "customPlayer") String username, @Flag("silent") boolean silent) {
        MCMMOCredits.getAdapter().getUUID(username).whenCompleteAsync((i, throwable) -> this.modifyCredits(sender, amount, i, Operation.ADD, throwable, silent));
    }

    @CommandDescription("Set a user's MCMMO Credit balance to the specified amount.")
    @CommandMethod("set <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void setCredits(CommandSender sender, @Argument("amount") @Range(min = "0") int amount, @Argument(value = "username", suggestions = "customPlayer") String username, @Flag("silent") boolean silent) {
        MCMMOCredits.getAdapter().getUUID(username).whenCompleteAsync((i, throwable) -> this.modifyCredits(sender, amount, i, Operation.SET, throwable, silent));
    }

    @CommandDescription("Take MCMMO Credits away from a user's balance")
    @CommandMethod("take <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void takeCredits(CommandSender sender, @Argument("amount") @Range(min = "1") int amount, @Argument(value = "username", suggestions = "customPlayer") String username, @Flag("silent") boolean silent) {
        MCMMOCredits.getAdapter().getUUID(username).whenCompleteAsync((i, throwable) -> this.modifyCredits(sender, amount, i, Operation.TAKE, throwable, silent));
    }

    private void modifyCredits(CommandSender sender, int amount, UUID uuid, Operation op, Throwable throwable, boolean silent) {
        if (MCMMOCredits.getAdapter().doesPlayerExist(uuid)) {
            switch (op) {
                case ADD -> MCMMOCredits.getAdapter().addCredits(uuid, amount);
                case TAKE -> MCMMOCredits.getAdapter().takeCredits(uuid, amount);
                case SET -> MCMMOCredits.getAdapter().setCredits(uuid, amount);
            }
            Pair<CommandSender, Player> transactionPair = Pair.of(sender, Bukkit.getPlayer(uuid));
            Keys senderKey = Keys.valueOf("MODIFY_CREDITS_" + op.name() + "_SENDER");
            Keys receiverKey = Keys.valueOf("MODIFY_CREDITS_" + op.name() + "_RECEIVER");
            ConfigHandler.sendMessage(sender, senderKey, Util.transactionBuilder(transactionPair, amount).build());
            if (sender != transactionPair.right() && !silent) {
                ConfigHandler.sendMessage(transactionPair.right(), receiverKey, Util.transactionBuilder(transactionPair, amount).build());
            }
        } else {
            //TODO Async is swallowing the exception.
            sender.sendMessage(ConfigHandler.exceptionMessage(sender, Keys.INVALID_ARGUMENTS));
            throw new CommandExecutionException(throwable);
        }
    }

    private enum Operation {ADD, SET, TAKE}
}
