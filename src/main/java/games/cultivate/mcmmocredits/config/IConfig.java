package games.cultivate.mcmmocredits.config;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public interface IConfig {
    HoconConfigurationLoader createLoader();

    void load();

    void save(CommentedConfigurationNode root);

    default void save() {
        this.save(this.rootNode());
    }

    BaseConfig config();

    CommentedConfigurationNode rootNode();

    List<CommentedConfigurationNode> nodes();

    default String joinedPath(final CommentedConfigurationNode node) {
        return StringUtils.join(node.path().array(), ".");
    }

    default List<CommentedConfigurationNode> nodesFromParent(CommentedConfigurationNode parent) {
        List<CommentedConfigurationNode> nodes = new CopyOnWriteArrayList<>(parent.childrenMap().values());
        while (nodes.stream().anyMatch(ConfigurationNode::isMap)) {
            nodes.forEach(i -> {
                if (i.isMap()) {
                    nodes.addAll(i.childrenMap().values());
                    nodes.remove(i);
                }
            });
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

    default <V> V value(Class<V> type, String path, V def) {
        for (CommentedConfigurationNode node : this.nodes()) {
            if (this.joinedPath(node).contains(path)) {
                try {
                    return node.get(type);
                } catch (SerializationException e) {
                    e.printStackTrace();
                }
            }
        }
        Bukkit.getLogger().log(Level.WARNING, "[MCMMOCredits] Config is missing value: {0}, Check your files!", path);
        return def;
    }

    /**
     * Sets a value to config. Returns true if successful
     */
    default <V> boolean modify(Class<V> type, String path, V value) {
        if (value == null || value.toString().equalsIgnoreCase("cancel")) {
            return false;
        }
        for (CommentedConfigurationNode node : this.nodes()) {
            if (this.joinedPath(node).contains(path)) {
                try {
                    node.set(type, value);
                    this.save();
                    return true;
                } catch (SerializationException e) {
                    e.printStackTrace();
                }
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
}
