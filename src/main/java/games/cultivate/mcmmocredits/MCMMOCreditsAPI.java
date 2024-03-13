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
package games.cultivate.mcmmocredits;

import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserService;
import jakarta.inject.Inject;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Handles basic user modification for 3rd party applications.
 * Method calls in this class will not trigger the CreditTransactionEvent.
 */
@SuppressWarnings("unused")
public class MCMMOCreditsAPI {
    private final UserService service;

    /**
     * Constructs the object.
     *
     * @param service UserService, used to apply actions within the API.
     */
    @Inject
    public MCMMOCreditsAPI(final UserService service) {
        this.service = service;
    }

    /**
     * Gets the credit balance of a user with the specified UUID.
     *
     * @param uuid The UUID of the user.
     * @return The credit balance of the user.
     */
    public int getCredits(final UUID uuid) {
        return this.service.getCredits(uuid).join();
    }

    /**
     * Adds credits to the credit balance of a user with the specified UUID.
     *
     * @param uuid   The UUID of the user.
     * @param amount The amount of credits to add.
     * @return True if the transaction was successful, otherwise false.
     */
    public boolean addCredits(final UUID uuid, final int amount) {
        int result = this.getCredits(uuid) + amount;
        return result >= 0 && this.service.setCredits(uuid, result).join();
    }

    /**
     * Sets the credit balance of a user with the specified UUID.
     *
     * @param uuid   The UUID of the user.
     * @param amount The new amount of credits.
     * @return True if the transaction was successful, otherwise false.
     */
    public boolean setCredits(final UUID uuid, final int amount) {
        return amount >= 0 && this.service.setCredits(uuid, amount).join();
    }

    /**
     * Removes credits from the credit balance of a user with the specified UUID.
     *
     * @param uuid   The UUID of the user.
     * @param amount The amount of credits to remove.
     * @return True if the transaction was successful, otherwise false.
     */
    public boolean takeCredits(final UUID uuid, final int amount) {
        int result = this.getCredits(uuid) - amount;
        return result >= 0 && this.service.setCredits(uuid, result).join();
    }

    /**
     * Gets a user from the UserService.
     * Can be used in conjunction with the Transaction system to call an event.
     *
     * @param uuid UUID of the user.
     * @return The user.
     */
    public Optional<User> getUser(final UUID uuid) {
        return this.service.getUser(uuid).join();
    }

    /**
     * Gets a user from the UserService.
     * Can be used in conjunction with the Transaction system to call an event.
     *
     * @param username Username of the user.
     * @return The user.
     */
    public Optional<User> getUser(final String username) {
        return this.service.getUser(username).join();
    }

    /**
     * Gets the credit balance of a user with the specified UUID.
     *
     * @param uuid The UUID of the user.
     * @return The credit balance of the user.
     */
    public CompletableFuture<Integer> getCreditsAsync(final UUID uuid) {
        return this.service.getCredits(uuid);
    }

    /**
     * Adds credits to the credit balance of a user with the specified UUID.
     *
     * @param uuid   The UUID of the user.
     * @param amount The amount of credits to add.
     * @return True if the transaction was successful, otherwise false.
     */
    public CompletableFuture<Boolean> addCreditsAsync(final UUID uuid, final int amount) {
        return this.getCreditsAsync(uuid).thenCompose(c -> this.setCreditsAsync(uuid, c + amount));
    }

    /**
     * Sets the credit balance of a user with the specified UUID.
     *
     * @param uuid   The UUID of the user.
     * @param amount The new amount of credits.
     * @return True if the transaction was successful, otherwise false.
     */
    public CompletableFuture<Boolean> setCreditsAsync(final UUID uuid, final int amount) {
        return amount >= 0 ? this.service.setCredits(uuid, amount) : CompletableFuture.completedFuture(false);
    }

    /**
     * Removes credits from the credit balance of a user with the specified UUID.
     *
     * @param uuid   The UUID of the user.
     * @param amount The amount of credits to remove.
     * @return True if the transaction was successful, otherwise false.
     */
    public CompletableFuture<Boolean> takeCreditsAsync(final UUID uuid, final int amount) {
        return this.getCreditsAsync(uuid).thenCompose(c -> this.setCreditsAsync(uuid, c - amount));
    }

    /**
     * Gets a user from the UserService.
     * Can be used in conjunction with the Transaction system to call an event.
     *
     * @param uuid UUID of the user.
     * @return The user.
     */
    public CompletableFuture<Optional<User>> getUserAsync(final UUID uuid) {
        return this.service.getUser(uuid);
    }

    /**
     * Gets a user from the UserService.
     * Can be used in conjunction with the Transaction system to call an event.
     *
     * @param username Username of the user.
     * @return The user.
     */
    public CompletableFuture<Optional<User>> getUserAsync(final String username) {
        return this.service.getUser(username);
    }
}
