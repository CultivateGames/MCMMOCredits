//
// MIT License
//
// Copyright (c) 2023 Cultivate Games
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
package games.cultivate.mcmmocredits;

import games.cultivate.mcmmocredits.data.UserDAO;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.util.CreditOperation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.UUID;

@SuppressWarnings("unused")
public final class MCMMOCreditsAPI {

    private final UserDAO dao;

    @Inject
    MCMMOCreditsAPI(final UserDAO dao) {
        this.dao = dao;
    }

    /**
     * Returns MCMMO Credit balance of a {@link Player} if they exist, otherwise -1.
     *
     * @param uuid UUID of the player.
     * @return Credit balance or -1.
     */
    public int getCredits(final UUID uuid) {
        User user = this.getUser(uuid);
        return user == null ? -1 : user.credits();
    }

    /**
     * Returns MCMMO Credit balance of a {@link Player} if they exist, otherwise -1.
     *
     * @param username String username of the player.
     * @return Credit balance or -1.
     */
    public int getCredits(final String username) {
        User user = this.getUser(username);
        return user == null ? -1 : user.credits();
    }

    /**
     * Modifies MCMMO Credit balance of a {@link Player} if they exist. Returns false if the operation fails.
     *
     * @param uuid      UUID of the player.
     * @param operation Operation performed on the credit balance.
     * @param amount    Amount of credits to modify the balance with.
     * @return If the transaction was successful.
     */
    public boolean modifyCredits(final UUID uuid, final CreditOperation operation, final int amount) {
        return this.modifyUserCredits(this.getUser(uuid), operation, amount);
    }

    /**
     * Modifies MCMMO Credit balance of a {@link Player} if they exist. Returns false if the operation fails.
     *
     * @param username  String username of the player.
     * @param operation Operation performed on the credit balance.
     * @param amount    Amount of credits to modify the balance with.
     * @return If the transaction was successful.
     */
    public boolean modifyCredits(final String username, final CreditOperation operation, final int amount) {
        return this.modifyUserCredits(this.getUser(username), operation, amount);
    }

    @Nullable
    private <T> User getUser(final T id) {
        if (id instanceof String string) {
            return this.dao.getUser(string).orElse(null);
        }
        if (id instanceof UUID uuid) {
            return this.dao.getUser(uuid).orElse(null);
        }
        return null;
    }

    private boolean modifyUserCredits(@Nullable final User user, final CreditOperation operation, final int amount) {
        if (user == null) {
            return false;
        }
        UUID uuid = user.uuid();
        return switch (operation) {
            case ADD -> this.dao.addCredits(uuid, amount);
            case SET -> this.dao.setCredits(uuid, amount);
            case TAKE -> this.dao.takeCredits(uuid, amount);
        };
    }
}
