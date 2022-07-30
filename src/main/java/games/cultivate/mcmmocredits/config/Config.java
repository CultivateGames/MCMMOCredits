package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.menu.Button;
import games.cultivate.mcmmocredits.serializers.ItemSerializer;
import games.cultivate.mcmmocredits.util.FileUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.SerializationException;

import javax.inject.Inject;
import javax.inject.Named;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class Config {
    private final transient String fileName;
    private final transient Class<? extends Config> type;
    private transient Config conf;
    private transient CommentedConfigurationNode root;
    private transient HoconConfigurationLoader loader;
    private transient List<CommentedConfigurationNode> nodeList;
    private transient @Inject @Named("dir") Path dir;

    Config(Class<? extends Config> type, String fileName) {
        this.type = type;
        this.fileName = fileName;
        this.nodeList = new ArrayList<>();
    }

    public List<CommentedConfigurationNode> nodes() {
        return nodeList;
    }

    public String joinedPath(CommentedConfigurationNode node) {
        return StringUtils.join(node.path().array(), ".");
    }

    public String joinedPath(Iterable<String> path) {
        return StringUtils.join(path.iterator(), ".");
    }

    public void load() {
        this.loader = this.createLoader();
        try {
            this.root = this.loader.load();
            this.conf = ObjectMapper.factory().get(this.type).load(this.root);
            this.save(this.root);
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

    private List<CommentedConfigurationNode> nodesFromParent(CommentedConfigurationNode parent) {
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

    public void save(CommentedConfigurationNode root) {
        try {
            this.loader.save(root);
            this.root = root;
            this.nodeList = this.nodesFromParent(this.root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        this.save(this.root);
    }

    public Config config() {
        return this.conf;
    }

    public String name() {
        return this.conf.getClass().getSimpleName();
    }

    /**
     * Returns a boolean from the underlying configuration map.
     *
     * @param path string that the path contains. Unique enough to not worry about duplication.
     * @param def  default value. Will log if the value is missing to let the user know to populate the value.
     * @return boolean from the root configuration nodes children map, or the provided default value.
     */
    public boolean bool(String path, boolean def) {
        return valueFromMap(boolean.class, path, def);
    }

    /**
     * Returns a boolean from the underlying configuration map.
     *
     * @param path string that the path contains. Unique enough to not worry about duplication.
     * @return boolean from the root configuration nodes children map, or the provided default value.
     */
    public boolean bool(String path) {
        return valueFromMap(boolean.class, path, false);
    }

    /**
     * Returns a String from the underlying configuration map.
     *
     * @param path string that the path contains. Unique enough to not worry about duplication.
     * @param def  default value. Will log if the value is missing to let the user know to populate the value.
     * @return String from the root configuration nodes children map, or the provided default value.
     */
    public String string(String path, String def) {
        return valueFromMap(String.class, path, def);
    }

    /**
     * Returns a String from the underlying configuration map.
     *
     * @param path string that the path contains. Unique enough to not worry about duplication.
     * @return String from the root configuration nodes children map, or an empty string.
     */
    public String string(String path) {
        return valueFromMap(String.class, path, "");
    }

    /**
     * Returns an int from the underlying configuration map.
     *
     * @param path string that the path contains. Unique enough to not worry about duplication.
     * @param def  default value. Will log if the value is missing to let the user know to populate the value.
     * @return String from the root configuration nodes children map, or the provided default value.
     */
    public int integer(String path, int def) {
        return valueFromMap(int.class, path, def);
    }

    /**
     * Returns an int from the underlying configuration map.
     *
     * @param path string that the path contains. Unique enough to not worry about duplication.
     * @return String from the root configuration nodes children map, or the provided default value.
     */
    public int integer(String path) {
        return valueFromMap(int.class, path, 0);
    }

    private <V> V valueFromMap(Class<V> type, String path, V def) {
        for (CommentedConfigurationNode node : this.nodeList) {
            if (this.joinedPath(node).contains(path)) {
                try {
                    return node.get(type);
                } catch (SerializationException e) {
                    e.printStackTrace();
                }
            }
        }
        this.logWarning(path);
        return def;
    }

    /**
     * Sets a value to config. Returns true if successful
     */
    public <V> boolean modify(Class<V> type, String path, V value) {
        if (value == null || value.toString().equalsIgnoreCase("cancel")) {
            return false;
        }
        for (CommentedConfigurationNode node : this.nodeList) {
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
    public boolean modify(String path, String value) {
        return this.modify(String.class, path, value);
    }

    /**
     * Returns an ItemStack from the underlying configuration map.
     */
    public ItemStack item(ItemType itemType, Player player)  {
        try {
            return ItemSerializer.INSTANCE.deserializePlayer(this.root.node(itemType.path()), player);
        } catch (SerializationException e) {
            e.printStackTrace();
            return new ItemStack(Material.AIR);
        }
    }

    public int itemSlot(ItemType itemType) {
        List<String> list = new ArrayList<>(itemType.path());
        list.add("slot");
        return this.integer(this.joinedPath(list));
    }

    public Button button(ItemType itemType, Player player) {
        return new Button(this.item(itemType, player), this.itemSlot(itemType));
    }

    public Button button(ItemType itemType, Player player, String command, int slot) {
       return new Button(this.item(itemType, player), slot, command);
    }

    public HoconConfigurationLoader createLoader() {
        Path path = this.dir.resolve(this.fileName);
        FileUtil.createFile(path);
        return HoconConfigurationLoader.builder()
                .defaultOptions(opts -> opts.serializers(build -> build.register(ItemStack.class, ItemSerializer.INSTANCE)))
                .path(path).prettyPrinting(true).build();
    }

    private void logWarning(String path) {
        Bukkit.getLogger().log(Level.WARNING, "[MCMMOCredits] Configuration was missing a value at {0}, Check your configuration!", path);
    }
}
