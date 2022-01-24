package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.specifier.Range;
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.util.Util;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This class is responsible for handling of the /modifycredits command.
 */
@CommandMethod("modifycredits")
public class ModifyCredits {
    @CommandDescription("Add MCMMO Credits to a user's balance.")
    @CommandMethod("add <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void addCredits(CommandSender sender, @Argument("amount") @Range(min = "1", max = "2147483647") int amount, @Argument(value = "username", suggestions = "customPlayer") String username, @Flag("silent") boolean silent) {
        Util.shouldProcessUUID(sender, username).ifPresent(uuid -> {
            if (Database.doesPlayerExist(uuid)) {
                Database.addCredits(uuid, amount);
                Pair<CommandSender, Player> transactionPair = Pair.of(sender, Bukkit.getPlayer(uuid));
                ConfigHandler.sendMessage(sender, Keys.MODIFY_CREDITS_ADD_SENDER, Util.transactionBuilder(transactionPair, amount).build());
                if (sender != transactionPair.right() && !silent) {
                    ConfigHandler.sendMessage(transactionPair.right(), Keys.MODIFY_CREDITS_ADD_RECEIVER, Util.transactionBuilder(transactionPair, amount).build());
                }
            }
        });
    }

    @CommandDescription("Set a user's MCMMO Credit balance to the specified amount.")
    @CommandMethod("set <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void setCredits(CommandSender sender, @Argument("amount") @Range(min = "0", max = "2147483647") int amount, @Argument(value = "username", suggestions = "customPlayer") String username, @Flag(value = "silent", permission = "mcmmocredits.admin.modify.silent") boolean silent) {
        Util.shouldProcessUUID(sender, username).ifPresent(uuid -> {
            if (Database.doesPlayerExist(uuid)) {
                Database.setCredits(uuid, amount);
                Player player = Bukkit.getPlayer(uuid);
                Pair<CommandSender, Player> transactionPair = Pair.of(sender, Bukkit.getPlayer(uuid));
                ConfigHandler.sendMessage(sender, Keys.MODIFY_CREDITS_SET_SENDER, Util.transactionBuilder(transactionPair, amount).build());
                if (sender != player && !silent) {
                    ConfigHandler.sendMessage(player, Keys.MODIFY_CREDITS_SET_RECEIVER, Util.transactionBuilder(transactionPair, amount).build());
                }
            }
        });
    }

    @CommandDescription("Take MCMMO Credits away from a user's balance")
    @CommandMethod("take <amount> <username>")
    @CommandPermission("mcmmocredits.admin.modify")
    private void takeCredits(CommandSender sender, @Argument("amount") @Range(min = "1", max = "2147483647") int amount, @Argument(value = "username", suggestions = "customPlayer") String username, @Flag(value = "silent", permission = "mcmmocredits.admin.modify.silent") boolean silent) {
        Util.shouldProcessUUID(sender, username).ifPresent(uuid -> {
            if (Database.doesPlayerExist(uuid)) {
                Database.takeCredits(uuid, amount);
                Player player = Bukkit.getPlayer(uuid);
                Pair<CommandSender, Player> transactionPair = Pair.of(sender, Bukkit.getPlayer(uuid));
                ConfigHandler.sendMessage(sender, Keys.MODIFY_CREDITS_TAKE_SENDER, Util.transactionBuilder(transactionPair, amount).build());
                if (sender != player && !silent) {
                    ConfigHandler.sendMessage(player, Keys.MODIFY_CREDITS_TAKE_RECEIVER, Util.transactionBuilder(transactionPair, amount).build());
                }
            }
        });
    }
}
