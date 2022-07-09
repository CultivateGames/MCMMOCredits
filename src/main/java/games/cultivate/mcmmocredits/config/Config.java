package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.keys.Key;
import games.cultivate.mcmmocredits.serializers.ItemStackSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public abstract class Config<T> {
    private final Class<T> type;
    private final List<Key<?>> keyCollection;
    private T configObject;
    private CommentedConfigurationNode root;
    private String fileName;
    private HoconConfigurationLoader loader;
    private Map<Object, CommentedConfigurationNode> configMap;

    protected Config(Class<T> type) {
        this.type = type;
        this.keyCollection = new ArrayList<>();
    }

    public abstract void setupKeys();

    public void load(String fileName) {
        this.fileName = fileName;
        try {
            if (this.loader == null) {
                this.loader = this.createLoader();
            }
            this.root = this.loader.load();
            this.configObject = this.root.get(this.type);
            this.loader.save(this.root);
            this.configMap = this.root.childrenMap();
            //Testing
            this.configMap.forEach((key, value) -> Bukkit.getLogger().info("Node Value: " + key.toString() + "Node Path: " + value.path()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.setupKeys();
    }

    public void save(CommentedConfigurationNode root) {
        try {
            this.loader.save(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.root = root;
    }

    public void save() {
        this.save(this.root);
    }

    public void addKey(Key<?> key) {
        this.keyCollection.add(key);
    }

    public List<Key<?>> keys() {
        return List.copyOf(this.keyCollection);
    }

    public CommentedConfigurationNode root() {
        return this.root;
    }

    public T config() {
        return this.configObject;
    }

    /**
     * Returns a boolean from the underlying configuration map.
     *
     * @param pathPart string that the path contains. Unique enough to not worry about duplication.
     * @param def      default value. Will log if the value is missing to let the user know to populate the value.
     * @return boolean from the root configuration nodes children map, or the provided default value.
     */
    public boolean bool(String pathPart, boolean def) {
            return valueFromMap(boolean.class, pathPart, def);
    }

    /**
     * Returns a String from the underlying configuration map.
     *
     * @param pathPart string that the path contains. Unique enough to not worry about duplication.
     * @param def      default value. Will log if the value is missing to let the user know to populate the value.
     * @return String from the root configuration nodes children map, or the provided default value.
     */
    public String string(String pathPart, String def) {
        return valueFromMap(String.class, pathPart, def);
    }

    /**
     * Returns an int from the underlying configuration map.
     *
     * @param pathPart string that the path contains. Unique enough to not worry about duplication.
     * @param def      default value. Will log if the value is missing to let the user know to populate the value.
     * @return String from the root configuration nodes children map, or the provided default value.
     */
    public int integer(String pathPart, int def) {
        return valueFromMap(int.class, pathPart, def);
    }

    private <C> C valueFromMap(Class<C> type, String pathPart, C def) {
        for (CommentedConfigurationNode node : this.configMap.values()) {
            if (node.path().toString().contains(pathPart)) {
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
     * Returns an ItemStack from the underlying configuration map.
     */
    public ItemStack item(ItemType itemType, Player player) {
        return ItemStackSerializer.INSTANCE.deserializePlayer(this.root.node(itemType.path()), player);
    }

    public HoconConfigurationLoader createLoader() throws IOException {
        Path p = JavaPlugin.getPlugin(MCMMOCredits.class).getDataFolder().toPath().resolve(this.fileName);
        Files.createFile(p);
        return HoconConfigurationLoader.builder()
                .defaultOptions(opts -> opts.serializers(build -> build.register(ItemStack.class, ItemStackSerializer.INSTANCE)))
                .path(p).prettyPrinting(true).build();
    }

    private void logWarning() {
        Bukkit.getLogger().log(Level.WARNING, "[MCMMOCredits] Configuration was missing a value, Check your configuration!");
    }
}
