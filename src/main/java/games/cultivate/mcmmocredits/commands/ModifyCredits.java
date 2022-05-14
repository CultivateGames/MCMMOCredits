package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.specifier.Range;
import cloud.commandframework.exceptions.CommandExecutionException;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.util.Util;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * This class is responsible for handling of the /modifycredits command.
 */
@CommandMethod("modifycredits")
public class ModifyCredits {
    private Database database;
    
    public ModifyCredits(Database database) {
        this.database = database;
    }

    @CommandDescription("Add MCMMO Credits to a user's balance.")
    @CommandMethod("add <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void addCredits(CommandSender sender, @Argument("amount") @Range(min = "1") int amount, @Argument(value = "username", suggestions = "customPlayer") String username, @Flag("silent") boolean silent) {
        this.database.getUUID(username).whenCompleteAsync((i, throwable) -> this.modifyCredits(sender, amount, i, Operation.ADD, throwable, silent));
    }

    @CommandDescription("Set a user's MCMMO Credit balance to the specified amount.")
    @CommandMethod("set <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void setCredits(CommandSender sender, @Argument("amount") @Range(min = "0") int amount, @Argument(value = "username", suggestions = "customPlayer") String username, @Flag("silent") boolean silent) {
        this.database.getUUID(username).whenCompleteAsync((i, throwable) -> this.modifyCredits(sender, amount, i, Operation.SET, throwable, silent));
    }

    @CommandDescription("Take MCMMO Credits away from a user's balance")
    @CommandMethod("take <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void takeCredits(CommandSender sender, @Argument("amount") @Range(min = "1") int amount, @Argument(value = "username", suggestions = "customPlayer") String username, @Flag("silent") boolean silent) {
        this.database.getUUID(username).whenCompleteAsync((i, throwable) -> this.modifyCredits(sender, amount, i, Operation.TAKE, throwable, silent));
    }

    private void modifyCredits(CommandSender sender, int amount, UUID uuid, Operation op, Throwable throwable, boolean silent) {
        if (this.database.doesPlayerExist(uuid)) {
            switch (op) {
                case ADD -> this.database.addCredits(uuid, amount);
                case TAKE -> this.database.takeCredits(uuid, amount);
                case SET -> this.database.setCredits(uuid, amount);
            }
            Player player = Bukkit.getPlayer(uuid);
            TagResolver tr = Util.fullTransaction(sender, Bukkit.getPlayer(uuid), amount);
            Util.sendMessage(sender, Keys.valueOf("MODIFY_CREDITS_" + op.name() + "_SENDER").get(), tr);
            if (sender != player && !silent) {
                Util.sendMessage(player, Keys.valueOf("MODIFY_CREDITS_" + op.name() + "_RECEIVER").get(), tr);
            }
        } else {
            //TODO Async is swallowing the exception.
            sender.sendMessage(Util.exceptionMessage(sender, Keys.INVALID_ARGUMENTS.get()));
            throw new CommandExecutionException(throwable);
        }
    }

    private enum Operation {ADD, SET, TAKE}
}
