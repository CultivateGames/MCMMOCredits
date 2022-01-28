package games.cultivate.mcmmocredits.util;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.Keys;
import it.unimi.dsi.fastutil.Pair;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.placeholder.Placeholder;
import net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class is responsible for holding various methods which need to be accessible and have no sensible location elsewhere.
 */
public class Util {

    public static Component parse(Component comp, Player player) {
        Pattern pattern = PlaceholderAPI.getPlaceholderPattern();
        comp = comp.replaceText(i -> i.match(pattern).replacement((matchResult, builder) -> Component.empty().decoration(TextDecoration.ITALIC, false).append(Component.text(PlaceholderAPI.setPlaceholders(player, matchResult.group())))));
        return MiniMessage.miniMessage().deserialize(MiniMessage.miniMessage().serialize(comp), Util.quickResolver(player));
    }

    private static final String[] EMPTY_ARRAY = new String[0];

    public static String[] getPathFromSuffix(String suffix) {
        for (Keys key : Keys.all) {
            if (key.path()[key.path().length - 1].endsWith(suffix)) {
                return key.path();
            }
        }
        return EMPTY_ARRAY;
    }

    /**
     * PlaceholderResolver for Config messages.
     * player = Player's username
     * credits = Player's credits
     */
    public static PlaceholderResolver.Builder basicBuilder(Player player) {
        return PlaceholderResolver.builder().placeholders(createPlaceholders("player", player.getName(), "credits", MCMMOCredits.getAdapter().getCredits(player.getUniqueId()) + ""));
    }

    public static PlaceholderResolver.Builder settingsBuilder(CommandSender sender, String setting, String change) {
        List<Placeholder<?>> phList = createPlaceholders("setting", setting, "change", change);
        return sender instanceof Player player ? basicBuilder(player).placeholders(phList) : PlaceholderResolver.builder().placeholders(phList);
    }

    /**
     * amount = Amount of Credits in transaction
     * sender = CommandSender's name (must be player)
     * sender_credits = CommandSender's MCMMO Credits (must be player)
     * + basicBuilder
     */
    public static PlaceholderResolver.Builder transactionBuilder(Pair<@Nullable CommandSender, @NotNull Player> pair, int amount) {
        PlaceholderResolver.Builder right = basicBuilder(pair.right()).placeholder(createPlaceholder("amount", amount + ""));
        if (pair.left() != null && pair.left() instanceof Player leftPlayer) {
            return right.placeholders(createPlaceholders("sender", leftPlayer.getName(), "sender_credits", MCMMOCredits.getAdapter().getCredits(leftPlayer.getUniqueId()) + ""));
        }
        return right;
    }

    /**
     * skill = skill redeemed into
     * cap = skill cap of relevant skill
     * + transactionBuilder
     */
    public static PlaceholderResolver.Builder redeemBuilder(Pair<@Nullable CommandSender, @NotNull Player> pair, String skill, int cap, int amount) {
        return transactionBuilder(pair, amount).placeholders(createPlaceholders("skill", skill, "cap", cap + ""));
    }

    public static @NotNull PlaceholderResolver quickResolver(@NotNull CommandSender sender) {
        return sender instanceof Player senderPlayer ? basicBuilder(senderPlayer).build() : PlaceholderResolver.placeholders(createPlaceholder("sender", sender.getName()));
    }

    public static List<Placeholder<?>> createPlaceholders(String... s) {
        List<Placeholder<?>> phList = new ArrayList<>();
        for (int i = 0; i < s.length - 1; i += 2) {
            phList.add(Placeholder.miniMessage(s[i], s[i + 1]));
        }
        return phList;
    }

    public static Placeholder<?> createPlaceholder(String... s) {
        return Placeholder.miniMessage(s[0], s[1]);
    }
}
