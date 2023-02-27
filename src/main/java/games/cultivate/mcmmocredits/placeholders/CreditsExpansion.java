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
package games.cultivate.mcmmocredits.placeholders;

import games.cultivate.mcmmocredits.data.UserDAO;
import games.cultivate.mcmmocredits.user.User;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Handles registration with {@link PlaceholderAPI}
 */
public final class CreditsExpansion extends PlaceholderExpansion {
    private final UserDAO dao;

    @Inject
    public CreditsExpansion(final UserDAO dao) {
        this.dao = dao;
    }

    @Override
    public @NotNull String getAuthor() {
        return "Cultivate Games";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "mcmmocredits";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.3.4-SNAPSHOT";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(final OfflinePlayer player, final @NotNull String identifier) {
        Optional<User> optionalUser = this.dao.getUser(player.getName());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (identifier.equalsIgnoreCase("credits")) {
                return user.credits() + "";
            }
            if (identifier.equalsIgnoreCase("redeemed")) {
                return user.redeemed() + "";
            }
            if (identifier.equalsIgnoreCase("username")) {
                return user.username();
            }
            if (identifier.equalsIgnoreCase("uuid")) {
                return user.uuid().toString();
            }
        }
        return 0 + "";
    }
}
