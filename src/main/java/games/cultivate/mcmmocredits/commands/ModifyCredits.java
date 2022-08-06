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

    @CommandDescription("Modify MCMMO Credits of a user")
    @CommandMethod("add|set|take <username> <amount>")
    @CommandPermission("mcmmocredits.admin.modify")
    public void modifyCredits(final CommandSender sender, final @Argument String op, final @Argument @Range(min = "1") int amount, final @Argument(suggestions = "user") String username, final @Flag("s") boolean silent) {
        this.database.getUUID(username).whenComplete((i, t) -> {
            if (!this.database.doesPlayerExist(i)) {
                Text.fromString(sender, this.config.string("playerDoesNotExist")).send();
                return;
            }
            String operation = op.toLowerCase();
            try {
                switch (operation) {
                    case "add" -> this.database.addCredits(i, amount);
                    case "take" -> this.database.takeCredits(i, amount);
                    case "set" -> this.database.setCredits(i, amount);
                    default -> { //do nothing by default.
                    }
                }
            } catch (Exception e) {
                //Exception is from SQL constraint.
                Text.fromString(sender, this.config.string("notEnoughCredits")).send();
            }
            TagResolver r = Resolver.fromTransaction(sender, username, amount);
            Text.fromString(sender, this.config.string(operation + "Sender"), r).send();
            Player player = Bukkit.getPlayer(i);
            if (player != null && !silent && sender != player) {
                Text.fromString(player, this.config.string(operation + "Receiver"), r).send();
            }
        });
    }
}
