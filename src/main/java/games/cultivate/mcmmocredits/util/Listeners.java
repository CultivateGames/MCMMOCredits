package games.cultivate.mcmmocredits.util;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.database.Database;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.placeholder.Placeholder;
import net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This class is responsible for any necessary Event Listeners that we may need.
 */
public class Listeners implements Listener {
    public static List<Player> chatInputPlayers = new ArrayList<>();

    /**
     * This is responsible for adding users to the MCMMO Credits database.
     * <p>
     * We will only attempt to add users if they are able to login, and they do not exist already.
     * This is to filter any odd login activity.
     */
    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        if (!Database.doesPlayerExist(e.getPlayerProfile().getId()) && e.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            Database.addPlayer(e.getPlayerProfile().getId(), 0);
            if (Keys.DATABASE_ADD_NOTIFICATION.getBoolean()) {
                ConfigHandler.sendMessage(Bukkit.getConsoleSender(), Keys.DATABASE_CONSOLE_MESSAGE, PlaceholderResolver.placeholders(Placeholder.miniMessage("player", e.getName())));
            }
        }
    }

    /**
     * This is responsible for sending users a message on login about the status of their
     * MCMMO Credits, if that is enabled.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (Keys.SEND_LOGIN_MESSAGE.getBoolean()) {
            ConfigHandler.sendMessage(e.getPlayer(), Keys.LOGIN_MESSAGE, Util.basicBuilder(e.getPlayer()).build());
        }
    }

    /**
     * Capture chat input if the user is in our list of Players.
     **/
    @EventHandler
    public void onPlayerChat(AsyncChatEvent e) {
        Player player = e.getPlayer();
        if (chatInputPlayers.contains(player)) {
            UtilGUI.chatInputMessage.complete(MiniMessage.miniMessage().serialize(e.message()));
            e.setCancelled(true);
            chatInputPlayers.remove(player);
            Bukkit.getScheduler().runTaskLater(MCMMOCredits.getInstance(), () -> UtilGUI.chatInputMessage = new CompletableFuture<>(), 20L);
        }
    }
}
