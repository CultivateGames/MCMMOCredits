package games.cultivate.mcmmocredits.placeholders;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.data.Database;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;

public final class ResolverFactory {

    private final Database database;

    @Inject
    public ResolverFactory(final Database database) {
        this.database = database;
    }

    public TagResolver fromUsers(final CommandSender sender) {
        return Resolver.builder(this.database).users(sender).build();
    }

    public TagResolver fromUsers(final CommandSender sender, final String target) {
        return Resolver.builder(this.database).users(sender, target).build();
    }

    public TagResolver fromTransaction(final CommandSender sender, final int amount) {
        return Resolver.builder(this.database).users(sender).transaction(amount).build();
    }

    public TagResolver fromTransaction(final CommandSender sender, final String target, final int amount) {
        return Resolver.builder(this.database).users(sender, target).transaction(amount).build();
    }

    public TagResolver fromRedemption(final CommandSender sender, final PrimarySkillType skill, final int amount) {
        return Resolver.builder(this.database).users(sender).transaction(amount).skill(skill).build();
    }

    public TagResolver fromRedemption(final CommandSender sender, final String target, final PrimarySkillType skill, final int amount) {
        return Resolver.builder(this.database).users(sender, target).transaction(amount).skill(skill).build();
    }

    public Resolver.Builder builder() {
        return Resolver.builder(this.database);
    }
}
