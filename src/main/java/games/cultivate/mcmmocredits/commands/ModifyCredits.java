package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.specifier.Range;
import cloud.commandframework.exceptions.CommandExecutionException;
import games.cultivate.mcmmocredits.data.Database;
import games.cultivate.mcmmocredits.keys.StringKey;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
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
    private static final Database database = Database.getDatabase();

    @CommandDescription("Add MCMMO Credits to a user's balance.")
    @CommandMethod("add <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void addCredits(CommandSender sender, @Argument("amount") @Range(min = "1") int amount, @Argument(value = "username", suggestions = "players") String username, @Flag("silent") boolean silent) {
        database.getUUID(username).whenCompleteAsync((i, throwable) -> this.modifyCredits(sender, amount, i, Operation.ADD, throwable, silent));
    }

    @CommandDescription("Set a user's MCMMO Credit balance to the specified amount.")
    @CommandMethod("set <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void setCredits(CommandSender sender, @Argument("amount") @Range(min = "0") int amount, @Argument(value = "username", suggestions = "players") String username, @Flag("silent") boolean silent) {
        database.getUUID(username).whenCompleteAsync((i, throwable) -> this.modifyCredits(sender, amount, i, Operation.SET, throwable, silent));
    }

    @CommandDescription("Take MCMMO Credits away from a user's balance")
    @CommandMethod("take <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void takeCredits(CommandSender sender, @Argument("amount") @Range(min = "1") int amount, @Argument(value = "username", suggestions = "players") String username, @Flag("silent") boolean silent) {
        database.getUUID(username).whenCompleteAsync((i, throwable) -> this.modifyCredits(sender, amount, i, Operation.TAKE, throwable, silent));
    }

    private void modifyCredits(CommandSender sender, int amount, UUID uuid, Operation op, Throwable throwable, boolean silent) {
        if (!database.doesPlayerExist(uuid)) {
            throw new CommandExecutionException(throwable);
        }
        switch (op) {
            case ADD -> database.addCredits(uuid, amount);
            case TAKE -> database.takeCredits(uuid, amount);
            case SET -> database.setCredits(uuid, amount);
        }
        Player player = Bukkit.getPlayer(uuid);
        TagResolver resolver = Resolver.fromTransaction(sender, player, amount);
        String keyOp = "MODIFY_CREDITS" + op.name();
        Text text = Text.fromKey(sender, StringKey.valueOf(keyOp + "_SENDER"), resolver);
        text.send();
        if (sender != player && !silent) {
            text.toBuilder().audience(player).key(StringKey.valueOf(keyOp + "_RECEIVER")).build().send();
        }
    }

    private enum Operation {ADD, SET, TAKE}
}
