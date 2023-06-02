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
package games.cultivate.mcmmocredits.config.properties;

import games.cultivate.mcmmocredits.converters.ConverterType;
import games.cultivate.mcmmocredits.database.Database;
import org.bukkit.Bukkit;
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
public record ConverterProperties(ConverterType type, DatabaseProperties oldDatabase, long failureDelay, long requestDelay, boolean enabled) {
    /**
     * Constructs the object with sane defaults.
     *
     * @return The object.
     */
    public static ConverterProperties defaults() {
        return new ConverterProperties(ConverterType.INTERNAL, DatabaseProperties.defaults(), 60000L, 300L, false);
    }

    /**
     * Gets the correct path for external data conversions.
     *
     * @return The Path.
     */
    public Path getExternalPath() {
        return Bukkit.getPluginsFolder().toPath().resolve(this.type == ConverterType.GUI_REDEEM_MCMMO ? Path.of("GuiRedeemMCMMO", "playerdata") : Path.of("MorphRedeem", "PlayerData"));
    }

    /**
     * Gets the Database from stored database information and the provided path.
     *
     * @param path The data path of the database.
     * @return The database.
     */
    public Database getOldDatabase(final Path path) {
        return Database.getDatabase(this.oldDatabase, path);
    }
}
