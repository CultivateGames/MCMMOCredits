package games.cultivate.mcmmocredits.util;

import games.cultivate.mcmmocredits.MCMMOCredits;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * <p>This is a class used to provide compatability with PlaceholderAPI.
 * There is currently only one placeholder:</p>
 * <br>
 * 1. %mcmmocredits_credits%: Returns the amount of MCMMO Credits a user has.
 */
public class CreditsExpansion extends PlaceholderExpansion {
    /**
     * <p>Instance of the plugin needed for PlaceholderAPI</p>
     */
    private final MCMMOCredits instance;

    /**
     * This is part of the PlaceholderAPI integration
     * @param instance Instance of the plugin providing Placeholder data.
     * @see <a href="https://github.com/PlaceholderAPI/PlaceholderAPI" target="_top">PlaceholderAPI documentation.</a>
     */
    public CreditsExpansion(MCMMOCredits instance) {
        this.instance = (MCMMOCredits) MCMMOCredits.getInstance();
    }

    @Override
    public @NotNull String getAuthor() {
        return "Cultivate Games";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "mcmmocredits";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.0.1";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (identifier.equalsIgnoreCase("credits")) {
            return Database.getCredits(player.getUniqueId()) + "";
        }

        return null;
    }
}
