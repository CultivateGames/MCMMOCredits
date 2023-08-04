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

import games.cultivate.mcmmocredits.menu.Menu;
import games.cultivate.mcmmocredits.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.util.NamingSchemes;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;

/**
 * A Configuration File.
 *
 * @param <D> Type of the data to parse for config.
 */
public final class Config<D extends Data> {
    private final YamlConfigurationLoader loader;
    private final ObjectMapper.Factory factory;
    private ConfigurationNode root;
    private List<String> paths;

    /**
     * Constructs the object.
     *
     * @param loader The YAMLConfigurationLoader used to load the config. Stored to load/save the configuration.
     */
    public Config(final YamlConfigurationLoader loader) {
        this.loader = loader;
        this.factory = ObjectMapper.factoryBuilder().defaultNamingScheme(NamingSchemes.LOWER_CASE_DASHED).build();
        this.paths = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    public void load(final Class<D> type) {
        try {
            this.root = this.loader.load();
            this.factory.get(type).load(this.root);
            this.save();
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void save() {
        try {
            this.loader.save(this.root);
            this.updatePaths();
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public <T> boolean set(@NotNull final T value, final Object... path) {
        try {
            this.root.node(path).set(value);
            this.save();
            return true;
        } catch (SerializationException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public <T> T get(final Class<T> type, final T def, final Object... path) {
        try {
            return this.root.node(path).get(type, def);
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        return def;
    }

    /**
     * Filters configuration node paths based on the provided Predicate.
     *
     * @param filter Predicate to filter list against.
     * @return A filtered list of configuration node paths.
     */
    public List<String> filterNodes(final Predicate<? super String> filter) {
        List<String> sorted = new ArrayList<>(this.paths);
        sorted.removeIf(filter);
        return sorted;
    }

    /**
     * Sets a value to the configuration at the provided NodePath.
     *
     * @param value The value to set.
     * @param path  The path to set the value.
     * @param <T>   The type of the value.
     * @return If it was successful.
     */
    public <T> boolean set(@NotNull final T value, final NodePath path) {
        return this.set(value, path.array());
    }

    /**
     * Gets a boolean from the configuration.
     *
     * @param path location where the value is found.
     * @return The value, or the public if the value is null.
     */
    public boolean getBoolean(final Object... path) {
        return this.get(boolean.class, false, path);
    }

    /**
     * Gets a String from the configuration, with the prefix prepended.
     *
     * @param path Node path where the value is found.
     * @return The value, or the public if the value is null.
     */
    public String getMessage(final Object... path) {
        return this.getString("prefix") + this.getString(path);
    }

    /**
     * Gets a String from the configuration.
     *
     * @param path Node path where the value is found.
     * @return The value, or the public if the value is null.
     */
    public String getString(final Object... path) {
        return this.get(String.class, "", path);
    }

    /**
     * Gets an int from the configuration.
     *
     * @param path Node path where the value is found.
     * @return The value, or the public if the value is null.
     */
    public int getInteger(final Object... path) {
        return this.get(int.class, 0, path);
    }

    /**
     * Gets a Menu from the configuration.
     *
     * @param path Node path where the value is found.
     * @return The value, or the public if the value is null.
     */
    public @Nullable Menu getMenu(final Object... path) {
        return this.get(Menu.class, null, path);
    }

    /**
     * Generates a list of possible paths from the configuration file.
     */
    private void updatePaths() {
        Queue<ConfigurationNode> queue = new ArrayDeque<>(this.root.childrenMap().values());
        List<String> sorted = new LinkedList<>();
        while (!queue.isEmpty()) {
            ConfigurationNode node = queue.poll();
            if (node.isMap()) {
                queue.addAll(node.childrenMap().values());
            } else {
                sorted.add(Util.joinString(".", node.path()));
            }
        }
        this.paths = sorted;
    }
}
