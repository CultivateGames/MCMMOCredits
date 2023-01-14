//
// MIT License
//
// Copyright (c) 2022 Cultivate Games
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package games.cultivate.mcmmocredits.placeholders;

import games.cultivate.mcmmocredits.data.UserDAO;
import games.cultivate.mcmmocredits.events.CreditRedemptionEvent;
import games.cultivate.mcmmocredits.events.CreditTransactionEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

/**
 * Factory used to create {@link TagResolver}s.
 */
public final class ResolverFactory {
    private final UserDAO dao;

    @Inject
    public ResolverFactory(final UserDAO dao) {
        this.dao = dao;
    }

    /**
     * Creates a {@link TagResolver} from a {@link CommandSender}.
     *
     * @param sender The {@link CommandSender}
     * @return The resulting {@link TagResolver}
     */
    public TagResolver fromUsers(final CommandSender sender) {
        return this.builder().users(sender).build();
    }

    /**
     * Creates a {@link TagResolver} from multiple users. Typically used during a transaction.
     *
     * @param sender The {@link CommandSender}
     * @param target Another user. Typically, a {@link Player}'s username.
     * @return The resulting {@link TagResolver}
     */
    public TagResolver fromUsers(final CommandSender sender, final String target) {
        return this.builder().users(sender, target).build();
    }

    /**
     * Creates a {@link TagResolver} from a credit transaction.
     *
     * @param e Instance of the {@link CreditTransactionEvent} that is used to populate the resolver.
     * @return The resulting {@link TagResolver}
     */
    public TagResolver fromTransaction(final CreditTransactionEvent e) {
        return this.builder().users(e.sender(), e.user().username()).transaction(e.amount()).build();
    }

    /**
     * Creates a {@link TagResolver} from a credit redemption involving multiple users.
     *
     * @param e Instance of the {@link CreditRedemptionEvent} that is used to populate the resolver.
     * @return The resulting {@link TagResolver}
     */
    public TagResolver fromRedemption(final CreditRedemptionEvent e) {
        return this.builder().users(e.sender(), e.user().username()).transaction(e.amount()).skill(e.skill()).build();
    }

    /**
     * Generates a new {@link Resolver.Builder} using the injected {@link UserDAO}
     *
     * @return The {@link Resolver.Builder}
     */
    public Resolver.Builder builder() {
        return Resolver.builder(this.dao);
    }
}
