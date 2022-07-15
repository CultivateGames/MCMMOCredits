package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.specifier.Range;
import cloud.commandframework.exceptions.CommandExecutionException;
import games.cultivate.mcmmocredits.config.MessagesConfig;
import games.cultivate.mcmmocredits.data.Database;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.UUID;

/**
 * This class is responsible for handling of the /modifycredits command.
 */
@CommandMethod("modifycredits")
public final class ModifyCredits {
    private final Database database;
    private final MessagesConfig messages;

    @Inject
    public ModifyCredits(MessagesConfig messages, Database database) {
        this.messages = messages;
        this.database = database;
    }

    @CommandDescription("Add MCMMO Credits to a user's balance.")
    @CommandMethod("add <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    public void addCredits(CommandSender sender, @Argument("amount") @Range(min = "1") int amount, @Argument(value = "username", suggestions = "players") String username, @Flag("silent") boolean silent) {
        this.database.getUUID(username).whenCompleteAsync((i, t) -> this.modifyCredits(sender, amount, i, Operation.ADD, t, silent));
    }

    @CommandDescription("Set a user's MCMMO Credit balance to the specified amount.")
    @CommandMethod("set <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    public void setCredits(CommandSender sender, @Argument("amount") @Range(min = "0") int amount, @Argument(value = "username", suggestions = "players") String username, @Flag("silent") boolean silent) {
        this.database.getUUID(username).whenCompleteAsync((i, t) -> this.modifyCredits(sender, amount, i, Operation.SET, t, silent));
    }

    @CommandDescription("Take MCMMO Credits away from a user's balance")
    @CommandMethod("take <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    public void takeCredits(CommandSender sender, @Argument("amount") @Range(min = "1") int amount, @Argument(value = "username", suggestions = "players") String username, @Flag("silent") boolean silent) {
        this.database.getUUID(username).whenCompleteAsync((i, t) -> this.modifyCredits(sender, amount, i, Operation.TAKE, t, silent));
    }

    private void modifyCredits(CommandSender sender, int amount, UUID uuid, Operation op, Throwable throwable, boolean silent) {
        if (!this.database.doesPlayerExist(uuid)) {
            throw new CommandExecutionException(throwable);
        }
        switch (op) {
            case ADD -> this.database.addCredits(uuid, amount);
            case TAKE -> this.database.takeCredits(uuid, amount);
            case SET -> this.database.setCredits(uuid, amount);
        }
        Player player = Bukkit.getPlayer(uuid);
        TagResolver resolver = Resolver.fromTransaction(sender, player, amount);
        Text.fromString(sender, this.messages.string(op.name().toLowerCase() + "Sender"), resolver).send();
        if (sender != player && !silent) {
            Text.fromString(player, this.messages.string(op.name().toLowerCase() + "Receiver"), resolver).send();
        }
    }

    enum Operation {ADD, SET, TAKE}
}
