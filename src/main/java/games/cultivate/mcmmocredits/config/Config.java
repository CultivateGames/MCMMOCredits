package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.menu.Button;
import games.cultivate.mcmmocredits.serializers.ItemStackSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
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

    Config(Class<? extends Config> type, String fileName) {
        this.type = type;
        this.fileName = fileName;
        this.nodeList = new ArrayList<>();
    }

    public CommentedConfigurationNode baseNode() {
        return root;
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
            if (node.path().toString().contains(path)) {
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
            if (node.path().toString().contains(path)) {
                try {
                    this.save(this.root.node(node).set(type, value));
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
    public ItemStack item(ItemType itemType, Player player) {
        return ItemStackSerializer.INSTANCE.deserializePlayer(this.root.node(itemType.path()), player);
    }

    public int itemSlot(ItemType itemType) {
        return this.root.node(NodePath.of(itemType.path()).withAppendedChild("slot").toString()).getInt(0);
    }

    public Button button(ItemType itemType, Player player) {
        CommentedConfigurationNode node = this.root.node(itemType.path());
        ItemStack item = ItemStackSerializer.INSTANCE.deserializePlayer(node, player);
        int slot = node.node("slot").getInt(0);
        return new Button(item, slot);
    }

    public HoconConfigurationLoader createLoader() {
        Path dir = JavaPlugin.getPlugin(MCMMOCredits.class).getDataFolder().toPath();
        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
                Bukkit.getLogger().log(Level.INFO, "[MCMMOCredits] Created " + fileName + " path!");
            }
            Files.createFile(dir.resolve(fileName));
        } catch (FileAlreadyExistsException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return HoconConfigurationLoader.builder()
                .defaultOptions(opts -> opts.serializers(build -> build.register(ItemStack.class, ItemStackSerializer.INSTANCE)))
                .path(dir.resolve(fileName)).prettyPrinting(true).build();
    }

    private void logWarning(String path) {
        Bukkit.getLogger().log(Level.WARNING, "[MCMMOCredits] Configuration was missing a value at {0}, Check your configuration!", path);
    }
}
