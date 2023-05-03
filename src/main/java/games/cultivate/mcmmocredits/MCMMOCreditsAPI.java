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

import games.cultivate.mcmmocredits.user.UserService;
import games.cultivate.mcmmocredits.util.CreditOperation;

import javax.inject.Inject;
import java.util.UUID;

@SuppressWarnings("unused")
public class MCMMOCreditsAPI {
    private final UserService service;

    @Inject
    public MCMMOCreditsAPI(final UserService service) {
        this.service = service;
    }

    /**
     * Retrieves the number of credits for the specified user.
     *
     * @param uuid the UUID of the user to retrieve credits for
     * @return the number of credits for the specified user, or 0 if the user does not exist
     */
    public int getCredits(final UUID uuid) {
        return this.service.getCredits(uuid);
    }

    /**
     * Adds specified amount of credits to a User.
     *
     * @param uuid   UUID of the user.
     * @param amount amount of credits to redeem.
     * @return if transaction was successful.
     */
    public boolean addCredits(final UUID uuid, final int amount) {
        return this.service.modifyCredits(uuid, CreditOperation.ADD, amount) != null;
    }

    /**
     * Set user's credit balance to specified amount.
     *
     * @param uuid   UUID of the user.
     * @param amount amount of credits to redeem.
     * @return if transaction was successful.
     */
    public boolean setCredits(final UUID uuid, final int amount) {
        return this.service.modifyCredits(uuid, CreditOperation.SET, amount) != null;
    }

    /**
     * Takes specified amount of credits from a user's balance.
     *
     * @param uuid   UUID of the user.
     * @param amount amount of credits to redeem.
     * @return if transaction was successful.
     */
    public boolean takeCredits(final UUID uuid, final int amount) {
        return this.service.modifyCredits(uuid, CreditOperation.TAKE, amount) != null;
    }
}
