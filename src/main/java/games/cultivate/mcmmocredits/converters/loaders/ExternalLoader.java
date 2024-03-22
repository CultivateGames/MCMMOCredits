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
package games.cultivate.mcmmocredits.converters.loaders;

import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.JsonParser;
import games.cultivate.mcmmocredits.config.Settings.ConverterProperties;
import games.cultivate.mcmmocredits.user.User;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("UnstableApiUsage")
public final class ExternalLoader implements UserLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalLoader.class);
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private final ExecutorService service;
    private final Path path;
    private final RateLimiter limiter;
    private final int delay;
    private final int retries;

    public ExternalLoader(final Path path, final ExecutorService service, final ConverterProperties properties) {
        this.path = path;
        this.service = service;
        this.delay = properties.retryDelay();
        this.limiter = RateLimiter.create(properties.requestsPerSecond());
        this.retries = properties.retries();
    }

    @Override
    public List<User> getUsers() {
        File[] files = this.path.toFile().listFiles((f, n) -> n.length() == 40 && n.contains(".yml"));
        if (files == null) {
            LOGGER.warn("There was an issue running the conversion for plugin files, aborting...");
            return List.of();
        }
        ConcurrentLinkedQueue<User> users = new ConcurrentLinkedQueue<>();
        CountDownLatch latch = new CountDownLatch(files.length);
        for (File file : files) {
            this.service.execute(() -> {
                users.add(this.fromFile(file));
                latch.countDown();
                LOGGER.info("Users remaining: {}", latch.getCount());
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            LOGGER.error("PluginUserLoader was interrupted while waiting for tasks to complete", e);
            Thread.currentThread().interrupt();
        }
        return new ArrayList<>(users);
    }

    private @Nullable User fromFile(final File file) {
        UUID uuid = UUID.fromString(file.getName().replace(".yml", ""));
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        for (int attempt = 1; attempt <= this.retries; attempt++) {
            this.limiter.acquire();
            try {
                Optional<String> username = this.getUsername(uuid);
                if (username.isEmpty()) {
                    TimeUnit.SECONDS.sleep(this.delay);
                    continue;
                }
                return username.map(name -> new User(uuid, name, configuration.getInt("Credits", 0), configuration.getInt("Credits_Spent", 0))).orElseThrow();
            } catch (InterruptedException | IOException e) {
                LOGGER.error("Exception during fetching username for UUID: {}", uuid, e);
                Thread.currentThread().interrupt();
            }
        }
        return null;
    }

    private Optional<String> getUsername(final UUID uuid) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid)).build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return Optional.of(JsonParser.parseString(response.body()).getAsJsonObject().get("name").getAsString());
        }
        return Optional.empty();
    }
}
