package games.cultivate.mcmmocredits.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;
import java.util.logging.Level;

/**
 * <p>This class is responsible for creating {@link org.bukkit.event.Event} Listeners
 * used for processing data and sending messages to {@link org.bukkit.entity.Player} entities.</p>
 *
 * @see Listeners#onPlayerPreLogin(AsyncPlayerPreLoginEvent)
 * @see Listeners#onPlayerJoin(PlayerJoinEvent)
 */
public class Listeners implements Listener {
    /**
     * TODO: See if this should be moved to {@link PlayerJoinEvent}.
     * <p>This method is responsible for adding {@link org.bukkit.entity.Player} entities to the MCMMO Credits database.</p>
     *
     * <p>We will only attempt to add users if they are allowed to login, and if they do not exist in our Database.
     * This should be sufficient filtering for users who fail to login or are invalid in some other way.</p>
     *
     * <p>After that, we check our settings to see if we should print a log to console about this addition.</p>
     *
     * <p>We elected to use this event instead of a regular {@link PlayerJoinEvent} due
     * to the I/O operation required to add a user.</p>
     *
     * @param e This is the {@link AsyncPlayerPreLoginEvent} that is being fired by the server.
     * @see Database#addPlayer(UUID, int)
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
     * <p>This method is responsible for determining if we want to send the user a
     * message on login about the status of their MCMMO Credits.</p>
     *
     * <p>We check if this feature is enabled, and then if it is,
     * we send a parsed {@link Component} to the user.</p>
     *
     * @param e The {@link PlayerJoinEvent} that is being fired by the server.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if ((boolean) ConfigHandler.value("send-login-message")) {
            Player player = e.getPlayer();
            ConfigHandler.sendMessage(player, ConfigHandler.parse(player, ConfigHandler.message("login-message")));
        }
    }
}
