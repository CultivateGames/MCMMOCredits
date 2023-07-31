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
package games.cultivate.mcmmocredits.converters;

import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.database.DatabaseProperties;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.nio.file.Path;

/**
 * Represents properties of a Data Converter.
 *
 * @param type         Type of the Converter.
 * @param oldDatabase  Properties of the old Database.
 * @param failureDelay Delay used when a Mojang request fails.
 * @param requestDelay Delay used between Mojang requests.
 * @param enabled      If conversion is enabled.
 */
@ConfigSerializable
public record ConverterProperties(DataLoadingStrategy type, DatabaseProperties oldDatabase, long failureDelay, long requestDelay, boolean enabled) {
    /**
     * Constructs the object with sane defaults.
     *
     * @return The object.
     */
    public static ConverterProperties defaults() {
        return new ConverterProperties(DataLoadingStrategy.INTERNAL, DatabaseProperties.defaults(), 60000L, 300L, false);
    }

    /**
     * Creates a converter using the provided database and path.
     *
     * @param database The current database.
     * @param path     The provided path.
     * @return A new Converter.
     */
    public Converter create(final Database database, final Path path) {
        return new Converter(this, database, path);
    }
}
