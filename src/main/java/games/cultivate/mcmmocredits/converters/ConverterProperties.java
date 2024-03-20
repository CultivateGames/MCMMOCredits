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
package games.cultivate.mcmmocredits.converters;

import games.cultivate.mcmmocredits.database.DatabaseProperties;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@SuppressWarnings({"FieldMayBeFinal", "unused", "FieldCanBeLocal"})
@ConfigSerializable
public final class ConverterProperties {
    private ConverterType type = ConverterType.CSV;
    private boolean enabled = false;
    private int requestsPerSecond = 15;
    private int retryDelay = 30;
    private int retries = 3;
    //TODO: db refactor... use constructor.
    private DatabaseProperties internal = DatabaseProperties.defaults();

    /**
     * Gets the type of the converter.
     *
     * @return type of the converter.
     */
    public ConverterType type() {
        return this.type;
    }

    /**
     * Gets if the converter is enabled.
     *
     * @return if the converter is enabled.
     */
    public boolean enabled() {
        return this.enabled;
    }

    /**
     * Gets amount of requests per second to Mojang. Default is suggested.
     *
     * @return amount of requests per second to Mojang. Default is suggested.
     */
    public int requestsPerSecond() {
        return this.requestsPerSecond;
    }

    /**
     * Delay between retries.
     *
     * @return delay between retries.
     */
    public int retryDelay() {
        return this.retryDelay;
    }

    /**
     * Amount of retries per entry.
     *
     * @return amount of retries per entry.
     */
    public int retries() {
        return this.retries;
    }

    /**
     * Properties of database used for internal transfers.
     *
     * @return Properties of database used for internal transfers.
     */
    public DatabaseProperties internal() {
        return this.internal;
    }
}
