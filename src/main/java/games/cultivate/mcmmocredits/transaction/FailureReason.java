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
package games.cultivate.mcmmocredits.transaction;

/**
 * Represents possible reasons for transaction failure.
 */
public enum FailureReason {
    NOT_ENOUGH_CREDITS("not-enough-credits"),
    MCMMO_PROFILE_FAIL("mcmmo-profile-fail"),
    MCMMO_SKILL_CAP("mcmmo-skill-cap"),
    SAME_USER("credits-pay-same-user");

    private final String key;

    /**
     * Constructs the object.
     *
     * @param key Represents a key to be used to get associated message from config.
     */
    FailureReason(final String key) {
        this.key = key;
    }

    /**
     * Gets the config key associated with the failure reason.
     *
     * @return The key.
     */
    public String getKey() {
        return this.key;
    }
}
