package games.cultivate.mcmmocredits.util;

import games.cultivate.mcmmocredits.database.Database;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ResolverBuilder {
    private TagResolver.Builder builder;
    private Database database;

    public ResolverBuilder(Database database) {
        this.builder = TagResolver.builder();
        this.database = database;
    }

    public ResolverBuilder tags(String... s) {
        for (int i = 0; i < s.length - 1; i += 2) {
            this.builder = this.builder.tag(s[i], Tag.preProcessParsed(s[i+1]));
        }
        return this;
    }

    //TODO send regular player placeholders if sender is player, and they are the only relevant user within context.
    public ResolverBuilder sender(CommandSender sender) {
        if(sender == null) {
            return this;
        }
        List<String> senderList = Arrays.asList("sender", sender.getName());
        if (sender instanceof Player player) {
            senderList.add("sender_credits");
            senderList.add(database.getCredits(player.getUniqueId()) + "");
        }
        return this.tags(senderList.toArray(new String[0]));
    }

    public ResolverBuilder player(Player player) {
        if(player == null) {
            return this;
        }
        return this.tags("player", player.getName(), "credits", database.getCredits(player.getUniqueId()) + "");
    }

    public ResolverBuilder redeem(String skill, int cap) {
        return this.tags("skill", skill, "cap", cap + "");
    }

    public ResolverBuilder transaction(int amount) {
        return this.tags("amount", amount + "");
    }

    public TagResolver build() {
        return this.builder.build();
    }
}
