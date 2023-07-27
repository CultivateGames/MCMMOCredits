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
package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.config.properties.ConverterProperties;
import games.cultivate.mcmmocredits.config.properties.DatabaseProperties;
import games.cultivate.mcmmocredits.menu.Menu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.function.Predicate;

/**
 * Represents basic functionality of a file that holds configuration data.
 */
@ConfigSerializable
public interface Config<D extends Data> {
    /**
     * Loads the configuration.
     *
     * @param type Type of the data to load.
     */
    void load(Class<D> type);

    /**
     * Saves the configuration using the current root node.
     */
    void save();

    /**
     * Modifies the configuration.
     *
     * @param value The value to apply.
     * @param path  location where the value will be set.
     * @param <T>   Type of the value.
     * @return returns true if successful, false otherwise.
     */
    <T> boolean set(@NotNull T value, Object... path);

    /**
     * Gets a value from the configuration.
     *
     * @param type Value type.
     * @param def  Default value if missing.
     * @param path location where the value will be obtained.
     * @param <T>  Type of the value.
     * @return The value, or the default if the value is null.
     */
    <T> T get(Class<T> type, T def, Object... path);

    /**
     * Filters configuration node paths based on the provided Predicate.
     * @param filter Predicate to filter list against.
     * @return A filtered list of configuration node paths.
     */
    List<String> filterNodes(Predicate<? super String> filter);

    /**
     * Sets a value to the configuration at the provided NodePath.
     *
     * @param value The value to set.
     * @param path  The path to set the value.
     * @param <T>   The type of the value.
     * @return If it was successful.
     */
    default <T> boolean set(@NotNull final T value, final NodePath path) {
        return this.set(value, path.array());
    }

    /**
     * Gets a boolean from the configuration.
     *
     * @param path location where the value is found.
     * @return The value, or the default if the value is null.
     */
    default boolean getBoolean(final Object... path) {
        return this.get(boolean.class, false, path);
    }

    /**
     * Gets a String from the configuration, with the prefix prepended.
     *
     * @param path Node path where the value is found.
     * @return The value, or the default if the value is null.
     */
    default String getMessage(final Object... path) {
        return this.getString("prefix") + this.getString(path);
    }

    /**
     * Gets a String from the configuration.
     *
     * @param path Node path where the value is found.
     * @return The value, or the default if the value is null.
     */
    default String getString(final Object... path) {
        return this.get(String.class, "", path);
    }

    /**
     * Gets an int from the configuration.
     *
     * @param path Node path where the value is found.
     * @return The value, or the default if the value is null.
     */
    default int getInteger(final Object... path) {
        return this.get(int.class, 0, path);
    }

    /**
     * Gets a Menu from the configuration.
     *
     * @param path Node path where the value is found.
     * @return The value, or the default if the value is null.
     */
    default @Nullable Menu getMenu(final Object... path) {
        return this.get(Menu.class, null, path);
    }

    /**
     * Gets the DatabaseProperties object from the configuration.
     *
     * @param path Node path where the value is found.
     * @return The value, or the default if the value is null.
     */
    default DatabaseProperties getDatabaseProperties(final Object... path) {
        return this.get(DatabaseProperties.class, DatabaseProperties.defaults(), path);
    }

    /**
     * Gets the ConverterProperties object from the configuration.
     * A default is not returned to prevent execution of a conversion.
     *
     * @param path Node path where the value is found.
     * @return The value, or null.
     */
    default @Nullable ConverterProperties getConverterProperties(final Object... path) {
        return this.get(ConverterProperties.class, null, path);
    }
}
