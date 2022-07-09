package games.cultivate.mcmmocredits.util;

import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.ConfigUtil;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

/**
 * This class is responsible for holding various methods which need to be accessible and have no sensible location elsewhere.
 */
public class Util {

    private Util() {}



    public static Component parse(Component comp, Player player) {
        Pattern p = PlaceholderAPI.getPlaceholderPattern();
        comp = comp.replaceText(i -> i.match(p).replacement((m, b) -> b.content(PlaceholderAPI.setPlaceholders(player, m.group()))));
        return Component.empty().style(Text.DEFAULT_STYLE).append(MiniMessage.miniMessage().deserialize(MiniMessage.miniMessage().serialize(comp), Resolver.fromPlayer(player)));
    }

    public static Component placeholder(Component comp, Player player) {
        //BiFunction<MatchResult, TextComponent.Builder, ComponentLike> replace = (m, b) -> b.content(PlaceholderAPI.setPlaceholders(player, m.group()));
        //TextReplacementConfig config = TextReplacementConfig.builder().match(PlaceholderAPI.getPlaceholderPattern()).replacement(replace).build();
        //comp = comp.replaceText(config);
        String msg = PlaceholderAPI.setPlaceholders(player, PlainTextComponentSerializer.plainText().serialize(comp));
        TagResolver resolver = Resolver.fromPlayer(player);
        return Component.empty().style(comp.style()).append(MiniMessage.miniMessage().deserialize(msg, resolver));
    }

    public static boolean modifyConfig(String path, String change) {
        if (change == null || change.equalsIgnoreCase("cancel")) {
            return false;
        }
        Config<?> config = ConfigUtil.fromPath(path);
        try {
            if (config.keys().stream().anyMatch(k -> k.path().equals(path))) {
                //We set to String so that the value can be read when next accessed, without egregious reflection.
                config.save(config.root().node(path).set(String.class, change));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
