package games.cultivate.mcmmocredits.placeholders;

import games.cultivate.mcmmocredits.data.Database;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

/**
 * This is responsible for handling our registration with PlaceholderAPI.
 */
public class CreditsExpansion extends PlaceholderExpansion {
    private final Database database;

    @Inject
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
        return "0.0.4";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (identifier.equalsIgnoreCase("credits")) {
            return database.getCredits(player.getUniqueId()) + "";
        }
        return null;
    }
}
