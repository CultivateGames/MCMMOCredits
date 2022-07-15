package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.menu.Button;
import games.cultivate.mcmmocredits.serializers.ItemStackSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Level;

public abstract class Config<T> {
    private final String fileName;
    private final Class<T> type;
    private T conf;
    private CommentedConfigurationNode root;
    private HoconConfigurationLoader loader;
    private Map<Object, CommentedConfigurationNode> configMap;

    Config(Class<T> type, String fileName) {
        this.type = type;
        this.fileName = fileName;
    }

    public CommentedConfigurationNode baseNode() {
        return root;
    }

    public void load() {
        try {
            this.loader = this.createLoader();
            this.root = this.loader.load();
            this.conf = this.root.get(this.type);
            this.loader.save(this.root);
            this.configMap = this.root.childrenMap();
            //Testing
            this.configMap.forEach((key, value) -> Bukkit.getLogger().info("Node Value: " + key.toString() + "Node Path: " + value.path()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(CommentedConfigurationNode root) {
        try {
            this.loader.save(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.root = root;
        this.configMap = root.childrenMap();
    }

    public void save() {
        this.save(this.root);
    }

    public T config() {
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

    private <C> C valueFromMap(Class<C> type, String path, C def) {
        for (CommentedConfigurationNode node : this.configMap.values()) {
            if (node.path().toString().contains(path)) {
                try {
                    return node.get(type);
                } catch (SerializationException e) {
                    e.printStackTrace();
                }
            }
        }
        this.logWarning();
        return def;
    }

    /**
     * Sets a value to config. Returns true if successful
     */
    public <V> boolean modify(Class<V> type, String path, V value) {
        if (value == null || value.toString().equalsIgnoreCase("cancel")) {
            return false;
        }
        for (CommentedConfigurationNode node : this.configMap.values()) {
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
     * @param path path of value
     * @param value value to modify path to.
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
        int slot = node.node("inventory-slot").getInt(0);
        return new Button(item, slot, "");
    }

    public HoconConfigurationLoader createLoader() throws IOException {
        Path p = JavaPlugin.getPlugin(MCMMOCredits.class).getDataFolder().toPath().resolve(this.fileName);
        Files.createFile(p);
        return HoconConfigurationLoader.builder()
                .defaultOptions(opts -> opts.serializers(build -> build.register(ItemStack.class, ItemStackSerializer.INSTANCE)))
                .path(p).prettyPrinting(true).build();
    }

    private void logWarning() {
        Bukkit.getLogger().log(Level.WARNING, "Configuration was missing a value, Check your configuration!");
    }
}
