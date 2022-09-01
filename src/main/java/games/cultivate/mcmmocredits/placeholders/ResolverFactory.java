package games.cultivate.mcmmocredits.placeholders;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.data.Database;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

/**
 * Factory used to create {@link TagResolver}s.
 */
public final class ResolverFactory {
    private final Database database;

    @Inject
    public ResolverFactory(final Database database) {
        this.database = database;
    }

    /**
     * Creates a {@link TagResolver} from a {@link CommandSender}.
     *
     * @param sender The {@link CommandSender}
     * @return The resulting {@link TagResolver}
     */
    public TagResolver fromUsers(final CommandSender sender) {
        return Resolver.builder(this.database).users(sender).build();
    }

    /**
     * Creates a {@link TagResolver} from multiple users. Typically used during a transaction.
     *
     * @param sender The {@link CommandSender}
     * @param target Another user. Typically, a {@link Player}'s username.
     * @return The resulting {@link TagResolver}
     */
    public TagResolver fromUsers(final CommandSender sender, final String target) {
        return Resolver.builder(this.database).users(sender, target).build();
    }

    /**
     * Creates a {@link TagResolver} from a transaction impacting only one user.
     *
     * @param sender The {@link CommandSender}
     * @param amount The amount of credits in the relevant transaction.
     * @return The resulting {@link TagResolver}
     */
    public TagResolver fromTransaction(final CommandSender sender, final int amount) {
        return Resolver.builder(this.database).users(sender).transaction(amount).build();
    }

    /**
     * Creates a {@link TagResolver} from a transaction impacting multiple users.
     *
     * @param sender The {@link CommandSender}
     * @param target Another user. Typically, a {@link Player}'s username.
     * @param amount The amount of credits used in the transaction.
     * @return The resulting {@link TagResolver}
     */
    public TagResolver fromTransaction(final CommandSender sender, final String target, final int amount) {
        return Resolver.builder(this.database).users(sender, target).transaction(amount).build();
    }

    /**
     * Creates a {@link TagResolver} from a credit redemption involving one user.
     *
     * @param sender The {@link CommandSender}. This is almost always a {@link Player}.
     * @param skill  The affected {@link PrimarySkillType}.
     * @param amount The amount of credits used in the transaction.
     * @return The resulting {@link TagResolver}
     */
    public TagResolver fromRedemption(final CommandSender sender, final PrimarySkillType skill, final int amount) {
        return Resolver.builder(this.database).users(sender).transaction(amount).skill(skill).build();
    }

    /**
     * Creates a {@link TagResolver} from a credit redemption involving multiple users.
     *
     * @param sender The {@link CommandSender}.
     * @param target Another user. Typically, a {@link Player}'s username.
     * @param skill  The affected {@link PrimarySkillType}.
     * @param amount The amount of credits used in the transaction.
     * @return The resulting {@link TagResolver}
     */
    public TagResolver fromRedemption(final CommandSender sender, final String target, final PrimarySkillType skill, final int amount) {
        return Resolver.builder(this.database).users(sender, target).transaction(amount).skill(skill).build();
    }

    /**
     * Generates a new {@link Resolver.Builder} using the injected {@link Database}
     *
     * @return The {@link Resolver.Builder}
     */
    public Resolver.Builder builder() {
        return Resolver.builder(this.database);
    }
}
