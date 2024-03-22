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
package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.converters.ConverterType;
import games.cultivate.mcmmocredits.storage.StorageType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * Configuration used to modify settings.
 */
@SuppressWarnings({"FieldMayBeFinal", "unused", "FieldCanBeLocal"})
@ConfigSerializable
public final class Settings implements Data {
    private boolean leaderboardEnabled = true;
    private boolean sendLoginMessage = true;
    private boolean metricsEnabled = true;
    private int leaderboardPageSize = 10;
    private boolean userTabComplete = true;
    private StorageProperties storage = new StorageProperties();
    private ConverterProperties converter = new ConverterProperties();

    public boolean leaderboardEnabled() {
        return this.leaderboardEnabled;
    }

    public boolean sendLoginMessage() {
        return this.sendLoginMessage;
    }

    public boolean metricsEnabled() {
        return this.metricsEnabled;
    }

    public int leaderboardPageSize() {
        return this.leaderboardPageSize;
    }

    public boolean userTabComplete() {
        return this.userTabComplete;
    }

    public StorageProperties database() {
        return this.storage;
    }

    public ConverterProperties converter() {
        return this.converter;
    }

    @ConfigSerializable
    public static final class ConverterProperties {
        private ConverterType type = ConverterType.CSV;
        private boolean enabled = false;
        private int requestsPerSecond = 15;
        private int retryDelay = 30;
        private int retries = 3;
        private StorageProperties internal = new StorageProperties();

        public ConverterType type() {
            return this.type;
        }

        public boolean enabled() {
            return this.enabled;
        }

        public int requestsPerSecond() {
            return this.requestsPerSecond;
        }

        public int retryDelay() {
            return this.retryDelay;
        }

        public int retries() {
            return this.retries;
        }

        public StorageProperties internal() {
            return this.internal;
        }
    }

    @ConfigSerializable
    public static final class StorageProperties {
        private StorageType type = StorageType.H2;
        private String url = "jdbc:mysql://127.0.0.1:3306/DATABASE_NAME";
        private String username = "rootroot";
        private String password = "passw0rd+";

        public StorageType type() {
            return this.type;
        }

        public String url() {
            return this.url;
        }

        public String username() {
            return this.username;
        }

        public String password() {
            return this.password;
        }
    }
}
