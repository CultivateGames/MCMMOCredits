package games.cultivate.mcmmocredits.util;

import games.cultivate.mcmmocredits.MCMMOCredits;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * This class is responsible for holding various methods which need to be accessible and have no sensible location elsewhere.
 */
public class Util {
    /**
     * This is responsible for checking if the plugin should process the user that is passed through.
     * <p>
     * If the player does not exist in our Database, we will skip any further processing.
     */
    public static boolean processPlayer(String username) {
        if (getOfflineUser(username) == null) {
            return false;
        }
        return Database.doesPlayerExist(getOfflineUser(username).getUniqueId());
    }

    /**
     * This will choose between possible ways to access an OfflinePlayer instance.
     * <p>
     * This selection is based on configuration options, and we are inside a Spigot or Paper environment.
     * <p>
     * If the "use_usercache_lookup" setting is "false" and the server is running Spigot, this could be problematic.
     * Looking up uncached Offline Players is risky. Users are advised to use Paper + the usercache.
     * TODO: Improve lookup to avoid using {@link Bukkit#getOfflinePlayer(String)}
     */
    @SuppressWarnings("deprecation")
    public static OfflinePlayer getOfflineUser(String username) {
        boolean usercacheLookup = (boolean) ConfigHandler.value("use-usercache-lookup");
        if(usercacheLookup && MCMMOCredits.isPaper() && Bukkit.getOfflinePlayerIfCached(username) != null) {
            return Bukkit.getOfflinePlayerIfCached(username);
        }
        return Bukkit.getOfflinePlayer(username);
    }
}
