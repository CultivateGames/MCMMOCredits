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
import org.jetbrains.annotations.NotNull;
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

    public static TagResolver fromTransaction(CommandSender sender, String username, UUID uuid, int amount) {
        return new Builder().sender(sender).player(username, uuid).transaction(amount).build();
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
        private Player player;
        private PrimarySkillType skill;
        private int amount;
        private TagResolver.Builder resolverBuilder;

        public Builder() {
            this(new HashMap<>(), TagResolver.builder(), null, null, null, 0);
        }

        private Builder(Map<String, String> tags, TagResolver.Builder resolverBuilder, CommandSender sender, Player player, PrimarySkillType skill, int amount) {
            this.tags = tags;
            this.resolverBuilder = resolverBuilder;
            this.sender = sender;
            this.player = player;
            this.skill = skill;
            this.amount = amount;
        }

        public Builder tags(String key, String value) {
            this.tags.put(key, value);
            return this;
        }

        public TagResolver build() {
            if (this.tags.containsKey("player") && sender instanceof Player p) {
                this.tags.put("sender_credits", database.getCredits(p.getUniqueId()) + "");
            }
            this.tags.forEach((k, v) -> this.resolverBuilder = this.resolverBuilder.tag(k, Tag.preProcessParsed(v)));
            return this.resolverBuilder.build();
        }

        public Builder sender(@NotNull CommandSender sender) {
            this.tags.put("sender", sender.getName());
            return this;
        }

        public Builder player(@Nullable Player player) {
            if (player != null) {
                this.player = player;
                this.tags.put("player", this.player.getName());
                this.tags.put("credits", database.getCredits(this.player.getUniqueId()) + "");
            }
            return this;
        }

        public Builder player(String username, UUID uuid) {
            this.player = Bukkit.getPlayer(uuid);
            this.tags.put("player", username);
            this.tags.put("credits", database.getCredits(uuid) + "");
            return this;
        }

        public Builder skill(PrimarySkillType skill) {
            this.skill = skill;
            this.tags.put("skill", WordUtils.capitalizeFully(this.skill.name()));
            this.tags.put("cap", mcMMO.p.getGeneralConfig().getLevelCap(this.skill) + "");
            return this;
        }

        public Builder transaction(int amount) {
            this.amount = amount;
            this.tags.put("amount", this.amount + "");
            return this;
        }
    }
}
