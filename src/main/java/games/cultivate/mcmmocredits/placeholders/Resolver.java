package games.cultivate.mcmmocredits.placeholders;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import games.cultivate.mcmmocredits.data.Database;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Resolver {

    private Resolver() {
    }

    public static TagResolver fromSender(CommandSender sender) {
        return new Builder().sender(sender).build();
    }

    public static TagResolver fromPlayer(Player player) {
        return new Builder().player(player).build();
    }

    public static TagResolver fromTransaction(CommandSender sender, Player player, int amount) {
        return new Builder().sender(sender).player(player).transaction(amount).build();
    }

    public static TagResolver fromRedemption(CommandSender sender, Player player, PrimarySkillType skill, int amount) {
        return new Builder().sender(sender).player(player).skill(skill).transaction(amount).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        @Inject //This is not ideal.
        private static Database database;
        private final Map<String, String> tags;
        private final CommandSender sender;
        private final Player player;
        private final PrimarySkillType skill;
        private final int transactionAmount;
        private TagResolver.Builder resolverBuilder;

        public Builder() {
            this(new HashMap<>(), TagResolver.builder(), null, null, null, 0);
        }

        private Builder(Map<String, String> tags, TagResolver.Builder resolverBuilder, CommandSender sender, Player player, PrimarySkillType skill, int transactionAmount) {
            this.tags = tags;
            this.resolverBuilder = resolverBuilder;
            this.sender = sender;
            this.player = player;
            this.skill = skill;
            this.transactionAmount = transactionAmount;
        }

        private Builder(Map<String, String> tags, CommandSender sender, Player player, PrimarySkillType skill, int transactionAmount) {
            this.tags = tags;
            this.sender = sender;
            this.player = player;
            this.skill = skill;
            this.transactionAmount = transactionAmount;
        }

        public Builder tags(String key, String value) {
            this.tags.put(key, value);
            return new Builder(this.tags, this.sender, this.player, this.skill, this.transactionAmount);
        }

        public TagResolver build() {
            if (this.tags.containsKey("player") && sender instanceof Player p) {
                this.tags.put("sender_credits", database.getCredits(p.getUniqueId()) + "");
            }
            this.tags.forEach((k, v) -> this.resolverBuilder = this.resolverBuilder.tag(k, Tag.preProcessParsed(v)));
            return this.resolverBuilder.build();
        }

        public Builder sender(@Nullable CommandSender sender) {
            if (sender != null) {
                this.tags.put("sender", sender.getName());
            }
            return new Builder(this.tags, sender, this.player, this.skill, this.transactionAmount);
        }

        public Builder player(@Nullable Player player) {
            if (player != null) {
                this.tags.put("player", player.getName());
                this.tags.put("credits", database.getCredits(player.getUniqueId()) + "");
            }
            return new Builder(this.tags, this.sender, player, this.skill, this.transactionAmount);
        }

        public Builder player(String username, UUID uuid) {
            this.tags.put("player", username);
            this.tags.put("credits", database.getCredits(uuid) + "");
            return new Builder(this.tags, this.sender, Bukkit.getPlayer(uuid), this.skill, this.transactionAmount);
        }

        public Builder skill(PrimarySkillType skill) {
            this.tags.put("skill", WordUtils.capitalizeFully(skill.name()));
            this.tags.put("cap", mcMMO.p.getGeneralConfig().getLevelCap(skill) + "");
            return new Builder(this.tags, this.sender, this.player, skill, this.transactionAmount);
        }

        public Builder transaction(int amount) {
            this.tags.put("amount", amount + "");
            return new Builder(this.tags, this.sender, this.player, this.skill, amount);
        }
    }
}
