package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.specifier.Range;
import games.cultivate.mcmmocredits.config.GeneralConfig;
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
    private final GeneralConfig config;

    @Inject
    public ModifyCredits(final GeneralConfig config, final Database database) {
        this.config = config;
        this.database = database;
    }

    @CommandDescription("Add MCMMO Credits to a user's balance.")
    @CommandMethod("add <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    public void addCredits(final CommandSender sender, final @Argument @Range(min = "1") int amount, final @Argument(suggestions = "user") String username, final @Flag("s") boolean silent) {
        this.database.getUUID(username).whenCompleteAsync(this.modifyCredits(Operation.ADD, sender, username, amount, silent));
    }

    @CommandDescription("Set a user's MCMMO Credit balance to the specified amount.")
    @CommandMethod("set <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    public void setCredits(final CommandSender sender, final @Argument @Range(min = "0") int amount, final @Argument(suggestions = "user") String username, final @Flag("s") boolean silent) {
        this.database.getUUID(username).whenCompleteAsync(this.modifyCredits(Operation.SET, sender, username, amount, silent));
    }

    @CommandDescription("Take MCMMO Credits away from a user's balance")
    @CommandMethod("take <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    public void takeCredits(final CommandSender sender, final @Argument @Range(min = "1") int amount, final @Argument(suggestions = "user") String username, final @Flag("s") boolean silent) {
        this.database.getUUID(username).whenCompleteAsync(this.modifyCredits(Operation.TAKE, sender, username, amount, silent));
    }

    private BiConsumer<UUID, Throwable> modifyCredits(final Operation op, final CommandSender sender, final String user, final int amount, final boolean silent) {
        return (i, t) -> {
            if (!this.database.doesPlayerExist(i)) {
                Text.fromString(sender, this.config.string("playerDoesNotExist")).send();
                return;
            }
            try {
                switch (op) {
                    case ADD -> this.database.addCredits(i, amount);
                    case TAKE -> this.database.takeCredits(i, amount);
                    case SET -> this.database.setCredits(i, amount);
                    default -> {}
                }
            } catch (Exception e) {
                //Exception is from SQL constraint.
                Text.fromString(sender, this.config.string("notEnoughCredits")).send();
            }
            TagResolver r = Resolver.fromTransaction(sender, user, amount);
            String content = this.config.string(op.name().toLowerCase() + "Sender");
            Text.fromString(sender, content, r).send();
            Player player = Bukkit.getPlayer(i);
            if (player != null && !silent && sender != player) {
                content = this.config.string(op.name().toLowerCase() + "Receiver");
                Text.fromString(player, content, r).send();
            }
        };
    }

    private enum Operation {ADD, SET, TAKE}
}
