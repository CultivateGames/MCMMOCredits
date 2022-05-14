package games.cultivate.mcmmocredits.util;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.skills.SkillTools;
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.database.Database;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.List;
import java.util.regex.Pattern;

/**
 * This class is responsible for holding various methods which need to be accessible and have no sensible location elsewhere.
 */
public class Util {
    public static final Style DEFAULT_STYLE = Style.style().decoration(TextDecoration.ITALIC, false).build();
    public static final SkillTools SKILL_TOOLS = mcMMO.p.getSkillTools();
    private static Database database;

    //Definitely a mistake
    public static void setDatabase(Database db) {
        database = db;
    }

    public static ResolverBuilder resolverBuilder() {
        return new ResolverBuilder(database);
    }

    public static TagResolver quick(CommandSender sender) {
        return new ResolverBuilder(database).sender(sender).build();
    }

    public static TagResolver player(Player player) {
        return new ResolverBuilder(database).player(player).build();
    }

    public static TagResolver fullTransaction(CommandSender sender, Player player, int amount) {
        return new ResolverBuilder(database).sender(sender).player(player).transaction(amount).build();
    }

    public static TagResolver settings(CommandSender sender, String setting, String change) {
        return new ResolverBuilder(database).sender(sender).tags("setting", setting, "change", change).build();
    }

    public static TagResolver fullRedeem(CommandSender sender, Player player, String skill, int cap, int amount) {
        return new ResolverBuilder(database).sender(sender).player(player).transaction(amount).redeem(skill, cap).build();
    }

    /**
     * This is responsible for actually sending the message to a user. All messages sent out are prepended with a prefix.
     */
    public static void sendMessage(Audience audience, String string, @Nullable TagResolver resolver) {
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

    public static Component exceptionMessage(CommandSender sender, String string, String... tags) {
        TagResolver pr;
        ResolverBuilder resolverBuilder = Util.resolverBuilder();
        if (tags.length != 0 && sender instanceof Player player) {
            pr = resolverBuilder.player(player).build();
        } else {
            pr = resolverBuilder.sender(sender).build();
        }
        return MiniMessage.miniMessage().deserialize(Keys.PREFIX.get() + string, pr);
    }

    public static Component parse(Component comp, Player player) {
        Pattern pattern = PlaceholderAPI.getPlaceholderPattern();
        comp = comp.replaceText(i -> i.match(pattern).replacement((matchResult, builder) -> Component.text(PlaceholderAPI.setPlaceholders(player, matchResult.group()))));
        return Component.empty().style(DEFAULT_STYLE).append(MiniMessage.miniMessage().deserialize(MiniMessage.miniMessage().serialize(comp), Util.player(player)));
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
