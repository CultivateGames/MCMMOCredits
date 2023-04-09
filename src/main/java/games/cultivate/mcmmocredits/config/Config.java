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

import games.cultivate.mcmmocredits.data.DatabaseProperties;
import games.cultivate.mcmmocredits.menu.ClickTypes;
import games.cultivate.mcmmocredits.menu.Item;
import games.cultivate.mcmmocredits.menu.Menu;
import games.cultivate.mcmmocredits.serializers.ClickTypeSerializer;
import games.cultivate.mcmmocredits.serializers.ItemSerializer;
import games.cultivate.mcmmocredits.serializers.MenuSerializer;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.util.NamingSchemes;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.util.*;

/**
 * Represents a Configuration file.
 */
public class Config {
    private final transient Class<? extends Config> type;
    private final transient String fileName;
    private transient YamlConfigurationLoader loader;
    private transient CommentedConfigurationNode root;
    private transient List<String> paths;
    private static final String HEADER = """
            MCMMO Credits v0.3.5-SNAPSHOT Configuration
            Repository: https://github.com/CultivateGames/MCMMOCredits
            Wiki: https://github.com/CultivateGames/MCMMOCredits/wiki/
            """;

    /**
     * Constructs the object with properties of the file.
     *
     * @param type     Class of the config.
     * @param fileName Name of the config.
     */
    protected Config(final Class<? extends Config> type, final String fileName) {
        this.type = type;
        this.fileName = fileName;
        this.paths = new ArrayList<>();
    }

    /**
     * Builds the Configuration Loader. Creates the file if missing.
     *
     * @return The Loader.
     */
    private YamlConfigurationLoader createLoader() {
        Util.createFile(this.fileName);
        return YamlConfigurationLoader.builder()
                .defaultOptions(opts -> opts.header(HEADER).serializers(build -> {
                    build.register(Item.class, ItemSerializer.INSTANCE);
                    build.register(Menu.class, MenuSerializer.INSTANCE);
                    build.register(ClickTypes.class, ClickTypeSerializer.INSTANCE);
                })).path(Util.getPluginPath().resolve(this.fileName))
                .headerMode(HeaderMode.PRESET)
                .indent(2)
                .nodeStyle(NodeStyle.BLOCK).build();
    }

    /**
     * Loads the configuration and list of possible node paths from file. Supports re-loading.
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
     * Saves the configuration using the provided root node.
     *
     * @param root The root node.
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
     * Returns a ConfigurationNode linked to the provided path.
     *
     * @param path Node path of the new node.
     * @return A new node.
     */
    public CommentedConfigurationNode node(final Object... path) {
        return this.root.node(path);
    }

    /**
     * Modifies the configuration.
     *
     * @param value The value to apply.
     * @param path  Node path used to locate the value.
     * @param <T>   Type of the value.
     * @return If the operation was successful.
     */
    public <T> boolean modify(@NotNull final T value, final Object... path) {
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
     * Gets a value from the configuration at the provided path.
     *
     * @param type Class of the value.
     * @param def  Default value used if value is missing.
     * @param path Node path where the value is found.
     * @param <T>  Type of the value.
     * @return The value.
     */
    private <T> T value(final Class<T> type, final T def, final Object... path) {
        try {
            return this.root.node(path).get(type);
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        String log = "[MCMMOCredits] Config is missing value: " + this.translateNode(path);
        Bukkit.getLogger().warning(log);
        return def;
    }

    /**
     * Gets a boolean from the configuration.
     *
     * @param path Node path where the value is found.
     * @return The value.
     */
    public boolean bool(final Object... path) {
        return this.value(boolean.class, false, path);
    }

    /**
     * Gets a String from the configuration, with the prefix prepended.
     *
     * @param path Node path where the value is found.
     * @return The value.
     */
    public String string(final Object... path) {
        return this.value(String.class, "", "prefix") + this.value(String.class, "", path);
    }

    /**
     * Gets a String from the configuration.
     *
     * @param path Node path where the value is found.
     * @return The value.
     */
    public String rawString(final Object... path) {
        return this.value(String.class, "", path);
    }


    /**
     * Gets a Menu from the configuration.
     *
     * @param path Node path where the value is found.
     * @return The value.
     */
    public Menu getMenu(final Object... path) {
        return this.value(Menu.class, null, path);
    }

    /**
     * Gets an Item from the configuration.
     *
     * @param path Node path where the value is found.
     * @return The value.
     */
    public Item getItem(final Object... path) {
        return this.value(Item.class, null, path);
    }

    /**
     * Gets the DatabaseProperties object from the configuration.
     *
     * @return The value.
     */
    public DatabaseProperties getDatabaseProperties() {
        return this.value(DatabaseProperties.class, DatabaseProperties.defaults(), "settings", "database");
    }

    /**
     * Filters available node paths against provided String array.
     *
     * @param keys Strings to filter against.
     * @return Filtered node path list.
     */
    public List<String> filterKeys(final String... keys) {
        if (this.paths.isEmpty()) {
            this.updatePaths();
        }
        List<String> sorted = new ArrayList<>(this.paths);
        sorted.removeIf(x -> Arrays.stream(keys).anyMatch(x::contains));
        return sorted;
    }

    /**
     * Translates node paths into Strings. Example: "settings", "add-player-message" -> "settings.add-player-message".
     *
     * @param path The node path.
     * @return String representing the node path.
     */
    public String translateNode(final Object... path) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : path) {
            sb.append(obj).append(".");
        }
        sb.deleteCharAt(sb.lastIndexOf("."));
        return sb.toString();
    }

    private void updatePaths() {
        Queue<CommentedConfigurationNode> queue = new ArrayDeque<>(this.root.childrenMap().values());
        List<String> sorted = new LinkedList<>();
        while (!queue.isEmpty()) {
            CommentedConfigurationNode node = queue.poll();
            if (node.isMap()) {
                queue.addAll(node.childrenMap().values());
            } else {
                sorted.add(this.translateNode(node.path().array()));
            }
        }
        this.paths = sorted;
    }
}
