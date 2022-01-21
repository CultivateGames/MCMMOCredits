package games.cultivate.mcmmocredits.util;

import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * This class is responsible for any necessary Event Listeners that we may need.
 */
public class Listeners implements Listener {

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
            if (Keys.DATABASE_ADD_MESSAGE.getBoolean()) {
                ConfigHandler.sendMessage(Bukkit.getConsoleSender(), Keys.DATABASE_CONSOLE_MESSAGE.getString(), null);
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
            ConfigHandler.sendMessage(e.getPlayer(), Keys.LOGIN_MESSAGE.getString(), Util.quickResolver(e.getPlayer()));
        }
    }
}
