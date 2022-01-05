package games.cultivate.mcmmocredits.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.logging.Level;

/**
 * This class is responsible for any necessary Event Listeners that we may need.
 */
public class Listeners implements Listener {
    /**
     * This is responsible for adding users to the MCMMO Credits database.
     * <p>
     * We will only attempt to add users if they are able to login, and they do not exist already.
     * This is to filter any odd login activity.
     * TODO: See if this should be moved to {@link PlayerJoinEvent}.
     */
    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        if (!Database.doesPlayerExist(e.getPlayerProfile().getId()) && e.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            Database.addPlayer(e.getPlayerProfile().getId(), 0);
            if ((boolean) ConfigHandler.value("database-add-message")) {
                Bukkit.getLogger().log(Level.INFO, e.getPlayerProfile().getName() + " has been added to the MCMMO Credits Database!");
            }
        }
    }

    /**
     * This is responsible for sending users a message on login about the status of their
     * MCMMO Credits, if that is enabled.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if ((boolean) ConfigHandler.value("send-login-message")) {
            Player player = e.getPlayer();
            ConfigHandler.sendMessage(player, ConfigHandler.parse(player, ConfigHandler.message("login-message")));
        }
    }
}
