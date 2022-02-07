package games.cultivate.mcmmocredits.util;

import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.database.Database;
import it.unimi.dsi.fastutil.Pair;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.placeholder.Placeholder;
import net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class is responsible for holding various methods which need to be accessible and have no sensible location elsewhere.
 */
public class Util {
    public static final Style DEFAULT_STYLE = Style.style().decoration(TextDecoration.ITALIC, false).build();
    private static Database database;

    //Definitely a mistake
    public static void setDatabase(Database db) {
        database = db;
    }

    /**
     * This is responsible for actually sending the message to a user. All messages sent out are prepended with a prefix.
     */
    public static void sendMessage(Audience audience, String string, @Nullable PlaceholderResolver resolver) {
        String send = Keys.PREFIX.get() + string;
        if (resolver == null) {
            audience.sendMessage(MiniMessage.miniMessage().deserialize(send));
            return;
        }
        if (audience instanceof Player player) {
            audience.sendMessage(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, send), resolver));
        } else {
            audience.sendMessage(MiniMessage.miniMessage().deserialize(send, resolver));
        }
    }

    public static Component exceptionMessage(CommandSender sender, String string, Placeholder<?>... placeholderList) {
        PlaceholderResolver pr = placeholderList.length != 0 && sender instanceof Player player ? Util.basicBuilder(player).placeholders(placeholderList).build() : PlaceholderResolver.placeholders(Util.createPlaceholder("sender", sender.getName()));
        return MiniMessage.miniMessage().deserialize(Keys.PREFIX.get() + string, pr);
    }

    public static Component parse(Component comp, Player player) {
        Pattern pattern = PlaceholderAPI.getPlaceholderPattern();
        comp = comp.replaceText(i -> i.match(pattern).replacement((matchResult, builder) -> Component.text(PlaceholderAPI.setPlaceholders(player, matchResult.group()))));
        return Component.empty().style(DEFAULT_STYLE).append(MiniMessage.miniMessage().deserialize(MiniMessage.miniMessage().serialize(comp), Util.basicBuilder(player).build()));
    }

    /**
     * PlaceholderResolver for Config messages.
     * player = Player's username
     * credits = Player's credits
     */
    public static PlaceholderResolver.Builder basicBuilder(Player player) {
        return PlaceholderResolver.builder().placeholders(createPlaceholders("player", player.getName(), "credits", database.getCredits(player.getUniqueId()) + ""));
    }

    public static PlaceholderResolver.Builder settingsBuilder(CommandSender sender, String setting, String change) {
        List<Placeholder<?>> phList = createPlaceholders("setting", setting, "change", change);
        return sender instanceof Player player ? basicBuilder(player).placeholders(phList) : PlaceholderResolver.builder().placeholders(phList);
    }

    public static PlaceholderResolver redeemPromptResolver(Player player, String skill, int cap) {
        return basicBuilder(player).placeholders(createPlaceholders("skill", skill, "cap", cap + "")).build();
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
            return right.placeholders(createPlaceholders("sender", leftPlayer.getName(), "sender_credits", database.getCredits(leftPlayer.getUniqueId()) + ""));
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

    public static boolean changeConfigInGame(Config<?> config, List<String> path, String change) {
        boolean canProceed = false;
        Keys compare = null;
        if (change == null || change.equalsIgnoreCase("cancel")) {
            return false;
        }
        for (Keys key : Keys.CAN_CHANGE) {
            if (key.path().equals(path)) {
                canProceed = true;
                compare = key;
                break;
            }
        }
        if (!canProceed)  return false;
        CommentedConfigurationNode root = config.root();
        try {
            root.node(compare.path()).set(String.class, change);
            config.save(root);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
