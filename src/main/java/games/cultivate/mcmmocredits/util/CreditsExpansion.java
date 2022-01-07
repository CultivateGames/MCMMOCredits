package games.cultivate.mcmmocredits.util;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.database.Database;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * This is responsible for handling our registration with PlaceholderAPI.
 * TODO: Fix compiler warning
 */
public class CreditsExpansion extends PlaceholderExpansion {
    private final MCMMOCredits instance;

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
