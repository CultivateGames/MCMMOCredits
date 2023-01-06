//
// MIT License
//
// Copyright (c) 2022 Cultivate Games
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

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;

/**
 * Interface which represents a configuration file.
 */
public interface Config {
    /**
     * Creates the HoconConfigurationLoader used by the configuration.
     *
     * @return the Loader to be used.
     */
    HoconConfigurationLoader createLoader();

    /**
     * Loads the configuration.
     */
    void load();

    /**
     * Saves the configuration using the provided root.
     *
     * @param root The configuration root node to apply to our configuration.
     */
    void save(CommentedConfigurationNode root);

    /**
     * Saves the configuration using the existing root node.
     */
    default void save() {
        this.save(this.rootNode());
    }

    /**
     * Gets the current configuration object.
     *
     * @return the current configuration object.
     */
    BaseConfig config();

    /**
     * Gets the current root node of the configuration.
     *
     * @return the root node of the configuration.
     */
    CommentedConfigurationNode rootNode();

    /**
     * Gets the current map of all nodes represented by this configuration object.
     *
     * @return map of all nodes represented by this configuration object.
     * @see Config#nodesFromParent(CommentedConfigurationNode)
     */
    Map<String, CommentedConfigurationNode> nodes();

    /**
     * Grabs all CommentedConfigurationNodes and their paths using the root node generated in the loading process.
     * <p>
     * Path is stored with a "." delimiter. For example, "messages.commands.credits.selfBalance"
     *
     * @param parent The configurations root node.
     * @return map of all configuration nodes and their paths represented in the config.
     */
    default Map<String, CommentedConfigurationNode> nodesFromParent(CommentedConfigurationNode parent) {
        Queue<CommentedConfigurationNode> queue = new LinkedList<>(parent.childrenMap().values());
        Map<String, CommentedConfigurationNode> nodes = new HashMap<>();
        while (!queue.isEmpty()) {
            CommentedConfigurationNode node = queue.poll();
            if (node.isMap()) {
                queue.addAll(node.childrenMap().values());
                continue;
            }
            nodes.put(StringUtils.join(node.path().array(), "."), node);
        }
        return nodes;
    }

    /**
     * Returns a boolean from the underlying configuration map.
     *
     * @param path string that the path contains. Unique enough to not worry about duplication.
     * @param def  default value. Will log if the value is missing to let the user know to populate the value.
     * @return boolean from the root configuration nodes children map, or the provided default value.
     */
    default boolean bool(String path, boolean def) {
        return this.value(boolean.class, path, def);
    }

    /**
     * Returns a boolean from the underlying configuration map.
     *
     * @param path string that the path contains. Unique enough to not worry about duplication.
     * @return boolean from the root configuration nodes children map, or the provided default value.
     */
    default boolean bool(String path) {
        return this.value(boolean.class, path, false);
    }

    /**
     * Returns a String from the underlying configuration map.
     *
     * @param path string that the path contains. Unique enough to not worry about duplication.
     * @param def  default value. Will log if the value is missing to let the user know to populate the value.
     * @return String from the root configuration nodes children map, or the provided default value.
     */
    default String string(String path, String def) {
        return this.value(String.class, path, def);
    }

    /**
     * Returns a String from the underlying configuration map.
     *
     * @param path string that the path contains. Unique enough to not worry about duplication.
     * @return String from the root configuration nodes children map, or an empty string.
     */
    default String string(String path) {
        return this.value(String.class, path, "");
    }

    /**
     * Returns an int from the underlying configuration map.
     *
     * @param path string that the path contains. Unique enough to not worry about duplication.
     * @param def  default value. Will log if the value is missing to let the user know to populate the value.
     * @return String from the root configuration nodes children map, or the provided default value.
     */
    default int integer(String path, int def) {
        return this.value(int.class, path, def);
    }

    /**
     * Returns an int from the underlying configuration map.
     *
     * @param path string that the path contains. Unique enough to not worry about duplication.
     * @return String from the root configuration nodes children map, or the provided default value.
     */
    default int integer(String path) {
        return this.value(int.class, path, 0);
    }

    /**
     * Accesses a configuration's node map to return a value from the node that best matches the provided path.
     *
     * @param type type of value to return.
     * @param path configuration path where the value is retrieved from.
     * @param def  default value to provide if value cannot be found.
     * @param <V>  value type used to correctly retrieve value.
     * @return value found at provided path.
     */
    default <V> V value(Class<V> type, String path, V def) {
        CommentedConfigurationNode node = this.findNode(path);
        if (node != null) {
            try {
                return node.get(type);
            } catch (SerializationException e) {
                e.printStackTrace();
            }
        }
        Bukkit.getLogger().log(Level.WARNING, "[MCMMOCredits] Config is missing value: {0}, Check your files!", path);
        return def;
    }

    /**
     * Modifies an existing value in the configuration. If path does not exist, does nothing.
     *
     * @param type  Type of the value we are setting in configuration.
     * @param path  path of the node we are trying to modify.
     * @param value value we are setting to the node.
     * @param <V>   type of the value we are setting to configuration.
     * @return if the modification was successful.
     */
    default <V> boolean modify(Class<V> type, String path, V value) {
        if (value == null || value.toString().equalsIgnoreCase("cancel")) {
            return false;
        }
        CommentedConfigurationNode node = this.findNode(path);
        if (node != null) {
            try {
                node.set(type, value);
                this.save();
                return true;
            } catch (SerializationException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Sets a value to config. Returns true if successful.
     *
     * @param path  config path where we want to change value.
     * @param value value used for modification of config.
     * @return if the change was successful.
     */
    default boolean modify(String path, String value) {
        return this.modify(String.class, path, value);
    }

    /**
     * Finds the CommentedConfigurationNode which has a path that contains the string provided.
     *
     * @param string the string to check against.
     * @return the associated node
     */
    private @Nullable CommentedConfigurationNode findNode(String string) {
        return this.nodes().keySet().stream().filter(x -> x.contains(string)).findAny().map(this.nodes()::get).orElse(null);
    }
}
