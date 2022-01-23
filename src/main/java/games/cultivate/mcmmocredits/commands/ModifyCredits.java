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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This class is responsible for handling of the /modifycredits command.
 */
@CommandMethod("modifycredits")
public class ModifyCredits {
    @CommandDescription("Add MCMMO Credits to a user's balance.")
    @CommandMethod("add <amount> <player>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void addCredits(CommandSender sender, @Argument("amount") @Range(min = "1", max = "2147483647") int amount, @Argument(value = "player", suggestions = "customPlayer") Player player, @Flag("silent") boolean silent) {
        if (Util.shouldProcess(sender, player)) {
            Database.addCredits(Util.getUser(player), amount);
            Pair<CommandSender, Player> transactionPair = Pair.of(sender, player);
            ConfigHandler.sendMessage(sender, Keys.MODIFY_CREDITS_ADD_SENDER, Util.transactionBuilder(transactionPair, amount).build());
            if (sender != player && !silent) {
                ConfigHandler.sendMessage(player, Keys.MODIFY_CREDITS_ADD_RECEIVER, Util.transactionBuilder(transactionPair, amount).build());
            }
        }
    }

    @CommandDescription("Set a user's MCMMO Credit balance to the specified amount.")
    @CommandMethod("set <amount> <player>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void setCredits(CommandSender sender, @Argument("amount") @Range(min = "0", max = "2147483647") int amount, @Argument(value = "player", suggestions = "customPlayer") Player player, @Flag(value = "silent", permission = "mcmmocredits.admin.modify.silent") boolean silent) {
        if (Util.shouldProcess(sender, player)) {
            Database.setCredits(Util.getUser(player), amount);
            Pair<CommandSender, Player> transactionPair = Pair.of(sender, player);
            ConfigHandler.sendMessage(sender, Keys.MODIFY_CREDITS_SET_SENDER, Util.transactionBuilder(transactionPair, amount).build());
            if (!silent) {
                ConfigHandler.sendMessage(player, Keys.MODIFY_CREDITS_SET_RECEIVER, Util.transactionBuilder(transactionPair, amount).build());
            }
        }
    }

    @CommandDescription("Take MCMMO Credits away from a user's balance")
    @CommandMethod("take <amount> <player>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void takeCredits(CommandSender sender, @Argument("amount") @Range(min = "1", max = "2147483647") int amount, @Argument(value = "player", suggestions = "customPlayer") Player player, @Flag(value = "silent", permission = "mcmmocredits.admin.modify.silent") boolean silent) {
        if (Util.shouldProcess(sender, player)) {
            Database.takeCredits(Util.getUser(player), amount);
            Pair<CommandSender, Player> transactionPair = Pair.of(sender, player);
            ConfigHandler.sendMessage(sender, Keys.MODIFY_CREDITS_TAKE_SENDER, Util.transactionBuilder(transactionPair, amount).build());
            if (!silent) {
                ConfigHandler.sendMessage(player, Keys.MODIFY_CREDITS_TAKE_RECEIVER, Util.transactionBuilder(transactionPair, amount).build());
            }
        }
    }
}
