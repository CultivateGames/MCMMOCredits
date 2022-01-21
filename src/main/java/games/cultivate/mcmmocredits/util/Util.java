package games.cultivate.mcmmocredits.util;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.database.Database;
import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.minimessage.placeholder.Placeholder;
import net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * This class is responsible for holding various methods which need to be accessible and have no sensible location elsewhere.
 */
public class Util {

    public static @Nullable UUID getUser(Player player) {
        if (player.isOnline()) {
            return player.getUniqueId();
        }
        if (Keys.USERCACHE_LOOKUP.getBoolean() && MCMMOCredits.isPaper() && Bukkit.getOfflinePlayerIfCached(player.getName()) != null) {
            return Objects.requireNonNull(Bukkit.getOfflinePlayerIfCached(player.getName())).getUniqueId();
        }
        return null;
    }

   public static boolean shouldProcess(CommandSender sender, Player player) {
        if(Util.getUser(player) != null && Database.doesPlayerExist(Util.getUser(player))) {
            return true;
        } else {
            //Methods should only do one thing! (oh well)
            ConfigHandler.sendMessage(sender, Keys.PLAYER_DOES_NOT_EXIST.getString(), Util.quickResolver(sender));
            return false;
        }
    }

    /**
     * PlaceholderResolver for Config messages.
     */
    public static PlaceholderResolver.Builder basicBuilder(Player player) {
        return PlaceholderResolver.builder().placeholders(createPlaceholders("player", player.getName(), "credits", Database.getCredits(player.getUniqueId()) + ""));
    }

    public static PlaceholderResolver.Builder transactionBuilder(Pair<@Nullable CommandSender, @NotNull Player> pair, int amount) {
        PlaceholderResolver.Builder right = basicBuilder(pair.right()).placeholder(createPlaceholder("amount",amount + ""));
        if (pair.left() != null && pair.left() instanceof Player leftPlayer) {
            return right.placeholders(createPlaceholders("sender", leftPlayer.getName(), "sender_credits", Database.getCredits(leftPlayer.getUniqueId()) + ""));
        }
        return right;
    }

    public static PlaceholderResolver.Builder redeemBuilder(Pair<@Nullable CommandSender, @NotNull Player> pair, String skill, int cap, int amount) {
        return transactionBuilder(pair, amount).placeholders(createPlaceholders("skill", skill, "cap", cap + ""));
    }

    public static @Nullable PlaceholderResolver quickResolver(@NotNull CommandSender sender) {
        return sender instanceof Player senderPlayer ? Util.basicBuilder(senderPlayer).build() : null;
    }

    public static List<Placeholder<?>> createPlaceholders(String... s) {
        List<Placeholder<?>> phList = new ArrayList<>();
        for (int i = 0; i < s.length - 1; i+=2) {
            phList.add(Placeholder.miniMessage(s[i], s[i+1]));
        }
        return phList;
    }

    public static Placeholder<?> createPlaceholder(String... s) {
        return Placeholder.miniMessage(s[0], s[1]);
    }
}
