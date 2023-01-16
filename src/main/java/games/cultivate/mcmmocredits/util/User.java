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
package games.cultivate.mcmmocredits.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Object representing a user of the plugin.
 *
 * @param uuid     {@link UUID} of the user.
 * @param username String representing the user's username.
 * @param credits  amount of credits the user currently has.
 * @param redeemed amount of credits redeemed historically.
 */
public record User(UUID uuid, String username, int credits, int redeemed) {
    public Map<String, String> placeholders(final String prefix) {
        Map<String, String> map = new HashMap<>();
        map.put(prefix, this.username);
        map.put(prefix + "_credits", this.credits + "");
        map.put(prefix + "_uuid", this.uuid.toString());
        map.put(prefix + "_redeemed", this.redeemed + "");
        return map;
    }
}
