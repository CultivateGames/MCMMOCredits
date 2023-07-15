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

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.nio.file.Path;

/**
 * Represents basic functionality of a configuration.
 */
@ConfigSerializable
public interface Config {
    /**
     * Loads the configuration. Supports reloading.
     *
     * @param path     The path of the configuration.
     * @param fileName The name of the configuration file.
     */
    void load(Path path, String fileName);

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
}
