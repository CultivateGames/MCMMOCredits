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

import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

/**
 * Util for Mojang web requests.
 */
public final class MojangUtil {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private MojangUtil() {
        throw new AssertionError("Util cannot be instantiated!");
    }

    /**
     * Synchronously fetches username for a UUID from Mojang.
     *
     * @param uuid The UUID.
     * @return The username.
     */
    public static String fetchUsername(final UUID uuid) {
        return fetchUsername(uuid, 0, 0, false);
    }

    /**
     * Synchronously fetches username for a UUID from Mojang.
     *
     * @param uuid  The UUID.
     * @param delay delay between requests.
     * @param retry delay between failed requests.
     * @param sleep if delays are enabled.
     * @return The username.
     */
    public static String fetchUsername(final UUID uuid, final long delay, final long retry, final boolean sleep) {
        HttpRequest req = HttpRequest.newBuilder(URI.create("https://api.mojang.com/user/profile/" + uuid)).GET().build();
        String name = CLIENT.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> JsonParser.parseString(response.body()).getAsJsonObject().get("name").getAsString())
                .join();
        if (name != null) {
            if (sleep) {
                sleep(delay);
            }
            return name;
        }
        sleep(retry);
        return fetchUsername(uuid, delay, retry, true);
    }

    /**
     * Sleeps the current thread.
     *
     * @param millis Duration of sleep.
     */
    private static void sleep(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
