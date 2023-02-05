package games.cultivate.mcmmocredits.user;

import games.cultivate.mcmmocredits.placeholders.Resolver;
import net.kyori.adventure.text.minimessage.tag.PreProcess;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class CommandExecutor {
    private final UUID uuid;
    private final String username;
    private final int credits;
    private final int redeemed;

    CommandExecutor(final UUID uuid, final String username, final int credits, final int redeemed) {
        this.uuid = uuid;
        this.username = username;
        this.credits = credits;
        this.redeemed = redeemed;
    }

    public Map<String, PreProcess> placeholders(final String prefix) {
        Map<String, String> map = new HashMap<>();
        map.put(prefix, this.username);
        map.put(prefix + "_credits", this.credits + "");
        map.put(prefix + "_uuid", this.uuid.toString());
        map.put(prefix + "_redeemed", this.redeemed + "");
        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, x -> Tag.preProcessParsed(x.getValue())));
    }

    public Resolver resolver() {
        return Resolver.builder().user(this, "sender").build();
    }

    public UUID uuid() {
        return this.uuid;
    }

    public String username() {
        return this.username;
    }

    public int credits() {
        return this.credits;
    }

    public int redeemed() {
        return this.redeemed;
    }

    public abstract boolean isPlayer();

    public abstract boolean isConsole();

    public abstract CommandSender sender();

    public abstract Player player();
}
