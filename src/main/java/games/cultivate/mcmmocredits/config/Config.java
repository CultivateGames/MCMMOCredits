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

import games.cultivate.mcmmocredits.converters.ConverterType;
import games.cultivate.mcmmocredits.database.DatabaseProperties;
import games.cultivate.mcmmocredits.serializers.ItemSerializer;
import games.cultivate.mcmmocredits.serializers.MenuSerializer;
import games.cultivate.mcmmocredits.ui.item.Item;
import games.cultivate.mcmmocredits.ui.menu.Menu;
import games.cultivate.mcmmocredits.util.Util;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.util.NamingSchemes;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;

/**
 * A Configuration file.
 */
public class Config {
    private static final String HEADER = """
            MCMMO Credits v0.3.8-SNAPSHOT Configuration
            Repository: https://github.com/CultivateGames/MCMMOCredits
            Wiki: https://github.com/CultivateGames/MCMMOCredits/wiki/
            """;
    private final transient Class<? extends Config> type;
    private final transient String fileName;
    private final transient Path path;
    private transient YamlConfigurationLoader loader;
    private transient CommentedConfigurationNode root;
    private transient List<String> paths;

    /**
     * Constructs the object.
     *
     * @param type     Config type.
     * @param fileName Config file name.
     * @param path     Config file path.
     */
    protected Config(final Class<? extends Config> type, final String fileName, final Path path) {
        this.type = type;
        this.fileName = fileName;
        this.path = path;
        this.paths = new ArrayList<>();
    }

    /**
     * Constructs the object with Bukkit plugin path.
     *
     * @param type     Config type.
     * @param fileName Config file name.
     */
    protected Config(final Class<? extends Config> type, final String fileName) {
        this(type, fileName, Util.getPluginPath());
    }

    /**
     * Creates Configuration loader and physical file.
     *
     * @return The Configuration Loader.
     */
    private YamlConfigurationLoader createLoader() {
        Util.createFile(this.path, this.fileName);
        return YamlConfigurationLoader.builder()
                .defaultOptions(opts -> opts.header(HEADER).serializers(build -> build
                        .register(Item.class, ItemSerializer.INSTANCE)
                        .register(Menu.class, MenuSerializer.INSTANCE)))
                .path(this.path.resolve(this.fileName))
                .headerMode(HeaderMode.PRESET)
                .indent(2)
                .nodeStyle(NodeStyle.BLOCK).build();
    }

    /**
     * Loads the configuration. Supports reloading.
     */
    public void load() {
        this.loader = this.createLoader();
        try {
            this.root = this.loader.load();
            ObjectMapper.Factory factory = ObjectMapper.factoryBuilder().defaultNamingScheme(NamingSchemes.LOWER_CASE_DASHED).build();
            factory.get(this.type).load(this.root);
            this.save();
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the configuration using the current root node.
     */
    public void save() {
        this.save(this.root);
    }

    /**
     * Saves the configuration using a provided root node.
     *
     * @param root A root node.
     */
    private void save(final CommentedConfigurationNode root) {
        try {
            this.loader.save(root);
            this.root = root;
            this.updatePaths();
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

    /**
     * Modifies the configuration.
     *
     * @param value The value to apply.
     * @param path  location where the value will be set.
     * @param <T>   Type of the value.
     * @return returns true if successful, false otherwise.
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
     * Gets a value from the configuration.
     *
     * @param type Value type.
     * @param def  Default value if missing.
     * @param path location where the value will be obtained.
     * @param <T>  Type of the value.
     * @return The value, or the default if the value is null.
     */
    private <T> T get(final Class<T> type, final T def, final Object... path) {
        try {
            T value = this.root.node(path).get(type);
            return value != null ? value : def;
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        return def;
    }

    /**
     * Gets a boolean from the configuration.
     *
     * @param path location where the value is found.
     * @return The value, or the default if the value is null.
     */
    public boolean getBoolean(final Object... path) {
        return this.get(boolean.class, false, path);
    }

    /**
     * Gets a String from the configuration, with the prefix prepended.
     *
     * @param path Node path where the value is found.
     * @return The value, or the default if the value is null.
     */
    public String getMessage(final Object... path) {
        return this.get(String.class, "", "prefix") + this.get(String.class, "", path);
    }

    /**
     * Gets a String from the configuration.
     *
     * @param path Node path where the value is found.
     * @return The value, or the default if the value is null.
     */
    public String getString(final Object... path) {
        return this.get(String.class, "", path);
    }

    /**
     * Gets an int from the configuration.
     *
     * @param path Node path where the value is found.
     * @return The value, or the default if the value is null.
     */
    public int getInteger(final Object... path) {
        return this.get(int.class, 0, path);
    }

    /**
     * Gets a Menu from the configuration.
     *
     * @param path Node path where the value is found.
     * @return The value, or the default if the value is null.
     */
    public @Nullable Menu getMenu(final Object... path) {
        return this.get(Menu.class, null, path);
    }

    /**
     * Gets an Item from the configuration.
     *
     * @param path Node path where the value is found.
     * @return The value, or the default if the value is null.
     */
    public @Nullable Item getItem(final Object... path) {
        return this.get(Item.class, null, path);
    }

    /**
     * Gets the DatabaseProperties object from the configuration.
     *
     * @param path Node path where the value is found.
     * @return The value, or the default if the value is null.
     */
    public DatabaseProperties getDatabaseProperties(final Object... path) {
        return this.get(DatabaseProperties.class, DatabaseProperties.defaults(), path);
    }

    /**
     * Gets the ConverterType object from the configuration.
     *
     * @param path Node path where the value is found.
     * @return The value, or the default if the value is null.
     */
    public @Nullable ConverterType getConverterType(final Object... path) {
        return this.get(ConverterType.class, null, path);
    }

    /**
     * Gets an int from the configuration.
     *
     * @param path Node path where the value is found.
     * @return The value, or the default if the value is null.
     */
    public long getLong(final Object... path) {
        return this.get(long.class, 0L, path);
    }

    /**
     * Generates a list of possible paths from the configuration file.
     */
    private void updatePaths() {
        Queue<CommentedConfigurationNode> queue = new ArrayDeque<>(this.root.childrenMap().values());
        List<String> sorted = new LinkedList<>();
        while (!queue.isEmpty()) {
            CommentedConfigurationNode node = queue.poll();
            if (node.isMap()) {
                queue.addAll(node.childrenMap().values());
            } else {
                sorted.add(Util.joinString(".", node.path()));
            }
        }
        this.paths = sorted;
    }

    /**
     * Gets stored list of node paths, or creates it if the list is empty.
     *
     * @return The list of node paths.
     */
    public List<String> getPaths() {
        if (this.paths.isEmpty()) {
            this.updatePaths();
        }
        return this.paths;
    }

    /**
     * Filters configuration node paths based on the provided Predicate.
     *
     * @param filter Predicate to filter list against.
     * @return A filtered list of configuration node paths.
     */
    public List<String> filterNodes(final Predicate<? super String> filter) {
        if (this.paths.isEmpty()) {
            this.updatePaths();
        }
        if (filter == null) {
            return this.paths;
        }
        List<String> sorted = new ArrayList<>(this.paths);
        sorted.removeIf(filter);
        return sorted;
    }
}
