package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.specifier.Completions;
import cloud.commandframework.annotations.specifier.Range;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import games.cultivate.mcmmocredits.util.ConfigHandler;
import games.cultivate.mcmmocredits.util.Database;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

/**
 * <p>This class is responsible for letting users execute commands which allow them to modify the MCMMO Credit balance of
 * any eligible user on the server.</p>
 *
 * <p>There are three commands created here:</p>
 * <br>
 * 1. /modifycredits add [number] [username]: Adds the specified amount of Credits to a user's MCMMO Credit balance.
 * <br>
 * 2. /modifycredits take [number] [username]: Takes the specified amount of Credits to a user's MCMMO Credit balance.
 * <br>
 * 3. /modifycredits set [number] [username]: Sets a user's MCMMO Credit balance to the provided balance.
 * <br>
 *
 * @see ModifyCredits#addCredits(CommandSender, String, int, String)
 * @see ModifyCredits#setCredits(CommandSender, String, int, String)
 * @see ModifyCredits#takeCredits(CommandSender, String, int, String)
 */
public class ModifyCredits {
    /**
     * <p>Command that is used to modify MCMMO Credit balance of any user on the server.
     * First, we check if the player should be processed, then perform actions based on the result. If we believe they
     * should not be processed, then send a message and end execution.</p>
     *
     * <p>Next, we perform actual modification of Credit balance, based on the "modify" parameter, and then send a message
     * with confirmation of the operation. If we get an invalid value here we will throw an {@link IllegalStateException}.</p>
     *
     * <p>Completions for this command are handled largely by the {@link Completions} annotation since they are static.</p>
     *
     * <p>We also do not allow negative integers through the {@link Range} annotation,
     * so users will not be able to invert the usage of take/add modifiers.</p>
     *
     * @param sender   The {@link CommandSender} that executed the command.
     * @param add      String which represents how to modify MCMMO Credit balance.
     * @param amount   Amount by which to modify MCMMO Credit Balance
     * @param username String which represents an {@link OfflinePlayer}'s username.
     * @see Database#update(UUID, int)
     */
    @CommandDescription("MCMMO Credits Modification - Add")
    @CommandMethod("modifycredits add <amount> <player>")
    @CommandPermission("mcmmocredits.modify.add")
    private void addCredits(CommandSender sender, @Completions("add, set, take") String add, @Argument("amount") @Range(min = "0", max = "2147483647") int amount, @Argument("player") String username) {
        if (!Util.processPlayer(username)) {
            ConfigHandler.sendMessage(sender, ConfigHandler.parse(Util.getOfflineUser(username), ConfigHandler.message("player-does-not-exist")));
            return;
        }
        UUID uuid = Util.getOfflineUser(username).getUniqueId();
        Database.update(uuid, Database.getCredits(uuid) + amount);
        ConfigHandler.sendMessage(sender, ConfigHandler.parse(Util.getOfflineUser(username), ConfigHandler.message("modify-credits-add"), amount));
    }

    /**
     * <p>Procedure is the same as {@link ModifyCredits#addCredits(CommandSender, String, int, String)},
     * except we are setting the balance instead of adding to it.</p>
     *
     * @param sender   The {@link CommandSender} that executed the command.
     * @param set      String which represents how to modify MCMMO Credit balance.
     * @param amount   Amount by which to modify MCMMO Credit Balance
     * @param username String which represents an {@link OfflinePlayer}'s username.
     * @see Database#update(UUID, int)
     * @see ModifyCredits#addCredits(CommandSender, String, int, String)
     */
    @CommandDescription("MCMMO Credits Modification - Set")
    @CommandMethod("modifycredits set <amount> <player>")
    @CommandPermission("mcmmocredits.modify.set")
    private void setCredits(CommandSender sender, @Completions("add, set, take") String set, @Argument("amount") @Range(min = "0", max = "2147483647") int amount, @Argument("player") String username) {
        if (!Util.processPlayer(username)) {
            ConfigHandler.sendMessage(sender, ConfigHandler.parse(Util.getOfflineUser(username), ConfigHandler.message("player-does-not-exist")));
            return;
        }
        UUID uuid = Util.getOfflineUser(username).getUniqueId();
        Database.update(uuid, amount);
        ConfigHandler.sendMessage(sender, ConfigHandler.parse(Util.getOfflineUser(username), ConfigHandler.message("modify-credits-set"), amount));
    }

    /**
     * <p>Procedure is the same as {@link ModifyCredits#addCredits(CommandSender, String, int, String)},
     * except we are subtracting from the balance instead of adding to it.</p>
     *
     * @param sender   The {@link CommandSender} that executed the command.
     * @param take     String which represents how to modify MCMMO Credit balance.
     * @param amount   Amount by which to modify MCMMO Credit Balance
     * @param username String which represents an {@link OfflinePlayer}'s username.
     * @see Database#update(UUID, int)
     * @see ModifyCredits#addCredits(CommandSender, String, int, String)
     */
    @CommandDescription("MCMMO Credits Modification - Add")
    @CommandMethod("modifycredits take <amount> <player>")
    @CommandPermission("mcmmocredits.modify.take")
    private void takeCredits(CommandSender sender, @Completions("add, set, take") String take, @Argument("amount") @Range(min = "0", max = "2147483647") int amount, @Argument("player") String username) {
        if (!Util.processPlayer(username)) {
            ConfigHandler.sendMessage(sender, ConfigHandler.parse(Util.getOfflineUser(username), ConfigHandler.message("player-does-not-exist")));
            return;
        }
        UUID uuid = Util.getOfflineUser(username).getUniqueId();
        Database.update(uuid, Database.getCredits(uuid) - amount);
        ConfigHandler.sendMessage(sender, ConfigHandler.parse(Util.getOfflineUser(username), ConfigHandler.message("modify-credits-take"), amount));
    }

    /**
     * <p>This method is used to create a Suggestions Provider for this set of commands.</p>
     *
     * @return {@link List<String>} of {@link Player} usernames.
     * @see cloud.commandframework.context.CommandContext
     * @see cloud.commandframework.annotations.suggestions.Suggestions
     * @see cloud.commandframework.arguments.CommandArgument.Builder
     */
    @Suggestions("player")
    public List<String> playerSuggestions(CommandContext<CommandSender> context, String input) {
        List<String> list = new ArrayList<>();
        if ((boolean) ConfigHandler.value("player-tab-completion")) {
            Bukkit.getOnlinePlayers().forEach(p -> list.add(p.getName()));
            return list;
        }
        return list;
    }

    /**
     * <p>This method is used to create an {@link ArgumentParser} for this set of commands.</p>
     *
     * @return user input when parsing command arguments?
     * @see cloud.commandframework.context.CommandContext
     * @see cloud.commandframework.annotations.parsers.Parser
     */
    @Parser(suggestions = "player")
    public String playerParser(CommandContext<CommandSender> sender, Queue<String> inputQueue) {
        final String input = inputQueue.peek();
        inputQueue.poll();
        return input;
    }
}
