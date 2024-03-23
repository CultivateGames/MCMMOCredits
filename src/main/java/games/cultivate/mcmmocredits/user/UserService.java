//
// MIT License
//
// Copyright (c) 2024 Cultivate Games
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
package games.cultivate.mcmmocredits.user;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public interface UserService {
    /**
     * Adds a user to the service.
     *
     * @param user The user.
     */
    void addUser(User user);

    /**
     * Creates a user and adds them to the backing service.
     *
     * @param player The player to wrap around.
     * @return The created user.
     */
    User createUser(Player player);

    /**
     * Gets a user based on UUID if they exist.
     *
     * @param uuid The uuid of the user.
     * @return The user, or empty optional if they do not exist.
     */
    Optional<User> getUser(UUID uuid);

    /**
     * Gets a user based on String username if they exist.
     *
     * @param username The username of the user.
     * @return The user, or empty optional if they do not exist.
     */
    Optional<User> getUser(String username);

    /**
     * Gets a user based on the provided player, or creates the user if they do not exist.
     *
     * @param player The player to wrap around.
     * @return The user.
     */
    User getOrCreate(Player player);

    /**
     * Defines any required actions to be taken when a user leaves.
     *
     * @param player The player who is leaving.
     */
    void onLogout(Player player);

    /**
     * Update the user located at the provided UUID with the provided User.
     *
     * @param user The user to update with.
     */
    boolean updateUser(User user);

    List<User> getUserGroup(int limit, int offset);

    /**
     * Creates a CommandExecutor from a CommandSender.
     *
     * @param sender The CommandSender.
     * @return The CommandExecutor.
     */
    CommandExecutor fromBukkit(CommandSender sender);

    /**
     * Gets all online users by converting all online players.
     *
     * @return The online users.
     */
    default Set<User> getOnlineUsers() {
        return Bukkit.getOnlinePlayers().stream().map(this::getOrCreate).collect(Collectors.toSet());
    }

    /**
     * Gets all online users by converting all online players, and removes the executor.
     *
     * @param executor The executor.
     * @return The online users, with the executor removed.
     */
    default Set<User> getOnlineUsers(final CommandExecutor executor) {
        Set<User> users = this.getOnlineUsers();
        if (executor instanceof User user) {
            users.remove(user);
        }
        return users;
    }
}
