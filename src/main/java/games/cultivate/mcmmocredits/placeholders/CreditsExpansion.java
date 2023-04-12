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

import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserService;
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
    private final UserService service;

    @Inject
    public CreditsExpansion(final UserService service) {
        this.service = service;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getAuthor() {
        return "Cultivate Games";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getIdentifier() {
        return "mcmmocredits";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getVersion() {
        return "0.3.5-SNAPSHOT";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * Handles placeholder parsing by loading the User and exposing all properties of it.
     *
     * @param player Instance of the user.
     * @param id     suffix of the placeholder key. ex. "credits" in %mcmmocredits_credits%
     * @return The placeholder value
     */
    @Override
    public String onRequest(final OfflinePlayer player, final @NotNull String id) {
        Optional<User> optionalUser = this.service.getUser(player.getName());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return switch (id.toLowerCase()) {
                case "credits" -> String.valueOf(user.credits());
                case "redeemed" -> String.valueOf(user.redeemed());
                case "username" -> user.username();
                case "uuid" -> user.uuid().toString();
                case "cached" -> String.valueOf(this.service.isCached(user));
                default -> "Invalid User has been provided!";
            };
        }
        return "Invalid User has been provided!";
    }
}
