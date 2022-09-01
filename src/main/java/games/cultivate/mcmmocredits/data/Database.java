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
package games.cultivate.mcmmocredits.data;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Interface which represents all available database operations. All I/O should run asynchronously unless noted.
 */
public sealed interface Database permits SQLDatabase, JSONDatabase {
    /**
     * Called when the server shuts down properly.
     */
    void disable();

    /**
     * Adds a player to the database if they are not in the database.
     *
     * @param uuid     UUID of the player.
     * @param username Username of the player.
     * @param credits  Amount of credits to add to the player when their database entry is created (typically 0).
     */
    void addPlayer(UUID uuid, String username, int credits);

    /**
     * Gets UUID of a player through username. Avoids OfflinePlayer call.
     *
     * @param username username of Player that holds relevant UUID.
     * @return UUID of player with specified username.
     */
    CompletableFuture<UUID> getUUID(String username);

    /**
     * Finds if the player exists in our database.
     *
     * @param uuid UUID of player we are searching for.
     * @return If the player we are searching for is in our database.
     */
    boolean doesPlayerExist(UUID uuid);

    /**
     * Sets username of an existing user in our database. Finds user via UUID.
     *
     * @param uuid     UUID to search for player with.
     * @param username username to overwrite with in database.
     */
    void setUsername(UUID uuid, String username);

    /**
     * Gets username of player in our database. Finds user via UUID.
     *
     * @param uuid UUID to search for player with.
     * @return the username if it exists.
     */
    String getUsername(UUID uuid);

    /**
     * Sets credit amount of player in our database. Finds user via UUID. This method runs synchronously.
     *
     * @param uuid    UUID to search for player with.
     * @param credits amount of credits to set the player's balance to.
     * @return if the operation was successful.
     */
    boolean setCredits(UUID uuid, int credits);

    /**
     * Gets credit balance for player in our database. Finds user via UUID. This method runs synchronously.
     *
     * @param uuid UUID to search for player with.
     * @return amount of credits the player currently has.
     */
    int getCredits(UUID uuid);

    /**
     * Adds credit amount to existing credit balance of user in our database. Finds user via UUID. This method runs synchronously.
     *
     * @param uuid    UUID to search for player with.
     * @param credits amount of credits to add to player.
     * @return if the operation was successful.
     */
    boolean addCredits(UUID uuid, int credits);

    /**
     * Takes credit amount from existing credit balance of user in our database. Finds user via UUID. This method runs synchronously.
     *
     * @param uuid    UUID to search for player with.
     * @param credits amount of credits to add to player.
     * @return if the operation was successful.
     */
    boolean takeCredits(UUID uuid, int credits);
}
