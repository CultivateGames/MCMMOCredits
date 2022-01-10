package games.cultivate.mcmmocredits.util;

import com.google.inject.Inject;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.database.Database;
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
    private MCMMOCredits plugin;
    @Inject private Database database;

    @Inject
    public Listeners(MCMMOCredits plugin) {
        this.plugin = plugin;
    }

    /**
     * This is responsible for adding users to the MCMMO Credits database.
     * <p>
     * We will only attempt to add users if they are able to login, and they do not exist already.
     * This is to filter any odd login activity.
     * TODO: See if this should be moved to {@link PlayerJoinEvent}.
     */
    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        if (!database.doesPlayerExist(e.getPlayerProfile().getId()) && e.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            database.addPlayer(e.getPlayerProfile().getId(), 0);
            if (Keys.DATABASE_ADD_MESSAGE.getBoolean()) {
                Bukkit.getLogger().log(Level.INFO, Util.parse(Util.getOfflineUser(e.getPlayerProfile().getName()), Keys.DATABASE_CONSOLE_MESSAGE));
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
            Player player = e.getPlayer();
            ConfigHandler.sendMessage(player, Util.parse(player, Keys.LOGIN_MESSAGE));
        }
    }
}
