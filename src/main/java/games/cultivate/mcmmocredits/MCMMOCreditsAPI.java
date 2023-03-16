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
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.User;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

//TODO: replace with better design.
@SuppressWarnings("unused")
public final class MCMMOCreditsAPI {

    private final UserDAO dao;

    @Inject
    MCMMOCreditsAPI(final UserDAO dao) {
        this.dao = dao;
    }

    /**
     * Returns a Player's MCMMO Credit amount if they exist, otherwise -1.
     *
     * @param uuid UUID of the player.
     * @return Credit balance or -1.
     */
    public int getCredits(final UUID uuid) {
        Optional<User> optionalUser = this.dao.getUser(uuid);
        return optionalUser.map(CommandExecutor::credits).orElse(-1);
    }

    /**
     * Returns a Player's MCMMO Credit amount if they exist, otherwise -1.
     *
     * @param username String username of the player.
     * @return Credit balance or -1.
     */
    public int getCredits(final String username) {
        Optional<User> optionalUser = this.dao.getUser(username);
        return optionalUser.map(CommandExecutor::credits).orElse(-1);
    }

    public boolean addCredits(final UUID uuid, final int amount) {
        return this.dao.addCredits(uuid, amount);
    }

    public boolean addCredits(final String username, final int amount) {
        Optional<User> optionalUser = this.dao.getUser(username);
        if (optionalUser.isPresent()) {
            return this.dao.addCredits(optionalUser.get().uuid(), amount);
        }
        return false;
    }

    public boolean setCredits(final UUID uuid, final int amount) {
        return this.dao.setCredits(uuid, amount);
    }

    public boolean setCredits(final String username, final int amount) {
        Optional<User> optionalUser = this.dao.getUser(username);
        if (optionalUser.isPresent()) {
            return this.dao.setCredits(optionalUser.get().uuid(), amount);
        }
        return false;
    }

    public boolean takeCredits(final UUID uuid, final int amount) {
        return this.dao.takeCredits(uuid, amount);
    }

    public boolean takeCredits(final String username, final int amount) {
        Optional<User> optionalUser = this.dao.getUser(username);
        if (optionalUser.isPresent()) {
            return this.dao.takeCredits(optionalUser.get().uuid(), amount);
        }
        return false;
    }
}
