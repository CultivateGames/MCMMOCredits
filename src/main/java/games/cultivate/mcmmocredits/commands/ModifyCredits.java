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
import java.util.function.BiConsumer;

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
    public void addCredits(CommandSender sender, @Argument @Range(min = "1") int amount, @Argument(suggestions = "user") String username, @Flag("s") boolean silent) {
        this.database.getUUID(username).whenCompleteAsync(this.modifyCredits(Operation.ADD, sender, username, amount, silent));
    }

    @CommandDescription("Set a user's MCMMO Credit balance to the specified amount.")
    @CommandMethod("set <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    public void setCredits(CommandSender sender, @Argument @Range(min = "0") int amount, @Argument(suggestions = "user") String username, @Flag("s") boolean silent) {
        this.database.getUUID(username).whenCompleteAsync(this.modifyCredits(Operation.SET, sender, username, amount, silent));
    }

    @CommandDescription("Take MCMMO Credits away from a user's balance")
    @CommandMethod("take <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    public void takeCredits(CommandSender sender, @Argument @Range(min = "1") int amount, @Argument(suggestions = "user") String username, @Flag("s") boolean silent) {
        this.database.getUUID(username).whenCompleteAsync(this.modifyCredits(Operation.TAKE, sender, username, amount, silent));
    }

    private BiConsumer<UUID,Throwable> modifyCredits(Operation op, CommandSender sender, String user, int amount, boolean silent) {
        return (i, t) -> {
            if (!this.database.doesPlayerExist(i)) {
                throw new CommandExecutionException(t);
            }
            switch (op) {
                case ADD -> this.database.addCredits(i, amount);
                case TAKE -> this.database.takeCredits(i, amount);
                case SET -> this.database.setCredits(i, amount);
            }
            TagResolver r = Resolver.builder().player(user, i).transaction(amount).sender(sender).build();
            String content = this.messages.string(op.name().toLowerCase() + "Sender");
            Text.fromString(sender, content, r).send();
            Player player = Bukkit.getPlayer(i);
            if (player != null && !silent && sender != player) {
                content = this.messages.string(op.name().toLowerCase() + "Receiver");
                Text.fromString(player, content, r).send();
            }
        };
    }

    private enum Operation {ADD, SET, TAKE}
}
