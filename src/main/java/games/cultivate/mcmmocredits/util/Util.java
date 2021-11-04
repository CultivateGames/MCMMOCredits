package games.cultivate.mcmmocredits.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * <p>This is a Utility class which stores various methods which need to be widely accessed and
 * don't have a sensible location elsewhere.</p>
 *
 * @see Util#processPlayer(String)
 * @see Util#isPaper()
 * @see Util#getOfflineUser(String)
 */
public class Util {
    /**
     * <p>This method checks if the plugin should do processing by seeing if the user exists in our Database.
     * If the user does not exist in our Database, we should NOT do any processing of the user.</p>
     *
     * @param username String representing a {@link OfflinePlayer}'s username.
     * @return if the user exists in the MCMMO Credits Database based on their UUID.
     */
    public static boolean processPlayer(String username) {
        if (getOfflineUser(username) == null) {
           return false;
        }
        return Database.doesPlayerExist(getOfflineUser(username).getUniqueId());
    }

    /**
     * <p>This method checks if the server is running Paper by checking for a Paper-specific class.
     * This saves us from uselessly packaging PaperLib. This Library is not needed for this type of Plugin.</p>
     *
     * @return if the Minecraft server is running Paper or a fork of Paper.
     * @see <a href="https://papermc.io/" target="_top">Seriously, use Paper.</a>
     */
    public static boolean isPaper() {
        boolean isPaper = false;
        try {
            Class.forName("com.destroystokyo.paper.MaterialSetTag");
            isPaper = true;
        } catch (Exception ignored) {
        }
        return isPaper;
    }

    /**
     * TODO: Improve lookup to avoid using {@link Bukkit#getOfflinePlayer(String)}
     *
     * <p>This method chooses between ways to access a {@link OfflinePlayer} instance, based on configuration options and
     * if the server is running Spigot or Paper.</p>
     *
     * <p>If the use_usercache_lookup setting is "false" and the server is running Spigot this could be problematic.
     * Looking up uncached Offline Players is risky. Users should REALLY use Paper + the usercache.</p>
     *
     * @param username String representing a {@link OfflinePlayer}'s username.
     * @return OfflinePlayer obtained by respecting plugin settings and server type.
     * @see <a href="https://papermc.io/" target="_top">Seriously, use Paper.</a>
     */
    @SuppressWarnings("deprecation")
    public static OfflinePlayer getOfflineUser(String username) {
        if (Bukkit.getOfflinePlayerIfCached(username) == null) {
            return Bukkit.getOfflinePlayer(username);
        }
        return (boolean) ConfigHandler.value("use-usercache-lookup") && isPaper() ? Bukkit.getOfflinePlayerIfCached(username) : Bukkit.getOfflinePlayer(username);
    }
}