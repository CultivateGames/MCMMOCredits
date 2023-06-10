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
import games.cultivate.mcmmocredits.serializers.ItemSerializer;
import games.cultivate.mcmmocredits.serializers.MenuSerializer;
import games.cultivate.mcmmocredits.ui.item.Item;
import games.cultivate.mcmmocredits.ui.menu.Menu;
import games.cultivate.mcmmocredits.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.util.NamingSchemes;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

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
public class BaseConfig implements Config {
    private static final String HEADER = """
            MCMMO Credits v0.4.0 Configuration
            Repository: https://github.com/CultivateGames/MCMMOCredits
            Wiki: https://github.com/CultivateGames/MCMMOCredits/wiki/
            """;
    private final transient ObjectMapper.Factory factory;
    private transient YamlConfigurationLoader loader;
    private transient CommentedConfigurationNode root;
    private transient List<String> paths;

    /**
     * Constructs the object.
     */
    BaseConfig() {
        this.factory = ObjectMapper.factoryBuilder().defaultNamingScheme(NamingSchemes.LOWER_CASE_DASHED).build();
        this.paths = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(final Path path, final String fileName) {
        Class<? extends BaseConfig> type = this.getClass();
        if (this.loader == null) {
            this.loader = this.createLoader(path, fileName);
        }
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
    @Override
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
    @Override
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
    @Override
    public <T> T get(final Class<T> type, final T def, final Object... path) {
        try {
            T value = this.root.node(path).get(type);
            return value != null ? value : def;
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        return def;
    }

    /**
     * Creates Configuration loader and physical file.
     *
     * @param path     The path of the configuration.
     * @param fileName The name of the configuration file.
     * @return The Configuration Loader.
     */
    private YamlConfigurationLoader createLoader(final Path path, final String fileName) {
        return YamlConfigurationLoader.builder()
                .defaultOptions(opts -> opts.header(HEADER).serializers(build -> build
                        .register(Item.class, ItemSerializer.INSTANCE)
                        .register(Menu.class, MenuSerializer.INSTANCE)))
                .path(Util.createFile(path, fileName))
                .headerMode(HeaderMode.PRESET)
                .indent(2)
                .nodeStyle(NodeStyle.BLOCK)
                .build();
    }

    /**
     * Sets the loader of the configuration.
     *
     * @param loader The loader.
     */
    public void setLoader(final YamlConfigurationLoader loader) {
        this.loader = loader;
    }

    /**
     * Gets a boolean from the configuration.
     *
     * @param path location where the value is found.
     * @return The value, or the default if the value is null.
     */
    public boolean getBoolean(final Object... path) {
        return this.root.node(path).getBoolean(false);
    }

    /**
     * Gets a String from the configuration, with the prefix prepended.
     *
     * @param path Node path where the value is found.
     * @return The value, or the default if the value is null.
     */
    public String getMessage(final Object... path) {
        return this.getString("prefix") + this.getString(path);
    }

    /**
     * Gets a String from the configuration.
     *
     * @param path Node path where the value is found.
     * @return The value, or the default if the value is null.
     */
    public String getString(final Object... path) {
        return this.root.node(path).getString("");
    }

    /**
     * Gets an int from the configuration.
     *
     * @param path Node path where the value is found.
     * @return The value, or the default if the value is null.
     */
    public int getInteger(final Object... path) {
        return this.root.node(path).getInt(0);
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
     * Gets the DatabaseProperties object from the configuration.
     *
     * @param path Node path where the value is found.
     * @return The value, or the default if the value is null.
     */
    public DatabaseProperties getDatabaseProperties(final Object... path) {
        return this.get(DatabaseProperties.class, DatabaseProperties.defaults(), path);
    }

    /**
     * Gets the ConverterProperties object from the configuration.
     * A default is not returned to prevent execution of a conversion.
     *
     * @param path Node path where the value is found.
     * @return The value, or null.
     */
    public @Nullable ConverterProperties getConverterProperties(final Object... path) {
        return this.get(ConverterProperties.class, null, path);
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
}
