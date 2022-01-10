package games.cultivate.mcmmocredits.util;

import com.google.inject.Inject;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.database.Database;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for holding various methods which need to be accessible and have no sensible location elsewhere.
 */
public class Util {
    private static final Pattern pattern = Pattern.compile("%(.*?)%");
    @Inject private final Database database;

    public Util(Database database) {
        this.database = database;
    }

    /**
     * This is responsible for checking if the plugin should process the user that is passed through.
     * <p>
     * If the player does not exist in our Database, we will skip any further processing.
     */
    public boolean processPlayer(String username) {
        if (getOfflineUser(username) == null) {
            return false;
        }
        return database.doesPlayerExist(getOfflineUser(username).getUniqueId());
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
        if(Keys.USERCACHE_LOOKUP.getBoolean() && MCMMOCredits.isPaper() && Bukkit.getOfflinePlayerIfCached(username) != null) {
            return Bukkit.getOfflinePlayerIfCached(username);
        }
        return Bukkit.getOfflinePlayer(username);
    }

    /**
     * This is responsible for parsing out placeholders within our messages.
     * <p>
     * This is the basic placeholder parser, which will only parse information we will always have.
     */
    public static String parse(OfflinePlayer offlinePlayer, Keys key) {
        Matcher matcher = pattern.matcher(key.getString());
        StringBuilder sb = new StringBuilder();
        String replacement = null;
        UUID uuid = offlinePlayer.getUniqueId();
        while (matcher.find()) {
            switch (matcher.group(0)) {
                case "%credits%" -> replacement = Database.getCredits(uuid) + "";
                case "%player%" -> replacement = offlinePlayer.getName();
            }
            if (replacement == null) {
                continue;
            }
            matcher.appendReplacement(sb, "");
            sb.append(replacement);
        }
        matcher.appendTail(sb);
        return PlaceholderAPI.setPlaceholders(offlinePlayer, sb.toString());
    }

    /**
     * This is responsible for parsing out placeholders within our messages.
     * <p>
     * This is a contextual placeholder parser, which will only work if we are
     * within a transaction which modifies Credit balance.
     */
    public static String parse(OfflinePlayer offlinePlayer, Keys key, int amount) {
        Matcher matcher = pattern.matcher(key.getString());
        StringBuilder sb = new StringBuilder();
        String replacement = null;
        while (matcher.find()) {
            switch (matcher.group(0)) {
                case "%credits%" -> replacement = Database.getCredits(offlinePlayer.getUniqueId()) + "";
                case "%player%" -> replacement = offlinePlayer.getName();
                case "%amount%" -> replacement = amount + "";
            }
            if (replacement == null) {
                continue;
            }
            matcher.appendReplacement(sb, "");
            sb.append(replacement);
        }
        matcher.appendTail(sb);
        return PlaceholderAPI.setPlaceholders(offlinePlayer, sb.toString());
    }

    /**
     * This is responsible for parsing out placeholders within our messages.
     * <p>
     * This is a contextual placeholder parser, which will only work if we are
     * within a transaction which is part of redeeming MCMMO Credits into a skill.
     */
    public static String parse(OfflinePlayer offlinePlayer, Keys key, String skill, int cap, int amount) {
        Matcher matcher = pattern.matcher(key.getString());
        StringBuilder sb = new StringBuilder();
        String replacement = null;
        while (matcher.find()) {
            switch (matcher.group(0)) {
                case "%credits%" -> replacement = Database.getCredits(offlinePlayer.getUniqueId()) + "";
                case "%player%" -> replacement = offlinePlayer.getName();
                case "%amount%" -> replacement = amount + "";
                case "%skill%" -> replacement = skill;
                case "%cap%" -> replacement = cap + "";
            }
            if (replacement == null) {
                continue;
            }
            matcher.appendReplacement(sb, "");
            sb.append(replacement);
        }
        matcher.appendTail(sb);
        return PlaceholderAPI.setPlaceholders(offlinePlayer, sb.toString());
    }
}
