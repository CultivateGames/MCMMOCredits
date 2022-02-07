package games.cultivate.mcmmocredits.util;

import games.cultivate.mcmmocredits.database.Database;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * This is responsible for handling our registration with PlaceholderAPI.
 */
public class CreditsExpansion extends PlaceholderExpansion {
    private Database database;

    public CreditsExpansion(Database database) {
        this.database = database;
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
        return "0.0.3";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (identifier.equalsIgnoreCase("credits")) {
            return this.database.getCredits(player.getUniqueId()) + "";
        }
        return null;
    }
}
