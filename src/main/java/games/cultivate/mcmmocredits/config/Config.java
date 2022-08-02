package games.cultivate.mcmmocredits.config;

import broccolai.corn.paper.item.PaperItemBuilder;
import games.cultivate.mcmmocredits.serializers.ItemSerializer;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.util.FileUtil;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;
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

    Config(final Class<? extends Config> type, final String fileName) {
        this.type = type;
        this.fileName = fileName;
        this.nodeList = new ArrayList<>();
    }

    public List<CommentedConfigurationNode> nodes() {
        return this.nodeList;
    }

    public String joinedPath(final CommentedConfigurationNode node) {
        return StringUtils.join(node.path().array(), ".");
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

    private List<CommentedConfigurationNode> nodesFromParent(final CommentedConfigurationNode parent) {
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

    public void save(final CommentedConfigurationNode root) {
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
    public boolean bool(final String path, final boolean def) {
        return this.valueFromMap(boolean.class, path, def);
    }

    /**
     * Returns a boolean from the underlying configuration map.
     *
     * @param path string that the path contains. Unique enough to not worry about duplication.
     * @return boolean from the root configuration nodes children map, or the provided default value.
     */
    public boolean bool(final String path) {
        return this.valueFromMap(boolean.class, path, false);
    }

    /**
     * Returns a String from the underlying configuration map.
     *
     * @param path string that the path contains. Unique enough to not worry about duplication.
     * @param def  default value. Will log if the value is missing to let the user know to populate the value.
     * @return String from the root configuration nodes children map, or the provided default value.
     */
    public String string(final String path, final String def) {
        return this.valueFromMap(String.class, path, def);
    }

    /**
     * Returns a String from the underlying configuration map.
     *
     * @param path string that the path contains. Unique enough to not worry about duplication.
     * @return String from the root configuration nodes children map, or an empty string.
     */
    public String string(final String path) {
        return this.valueFromMap(String.class, path, "");
    }

    /**
     * Returns an int from the underlying configuration map.
     *
     * @param path string that the path contains. Unique enough to not worry about duplication.
     * @param def  default value. Will log if the value is missing to let the user know to populate the value.
     * @return String from the root configuration nodes children map, or the provided default value.
     */
    public int integer(final String path, final int def) {
        return this.valueFromMap(int.class, path, def);
    }

    /**
     * Returns an int from the underlying configuration map.
     *
     * @param path string that the path contains. Unique enough to not worry about duplication.
     * @return String from the root configuration nodes children map, or the provided default value.
     */
    public int integer(final String path) {
        return this.valueFromMap(int.class, path, 0);
    }

    private <V> V valueFromMap(final Class<V> type, final String path, final V def) {
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
    public <V> boolean modify(final Class<V> type, final String path, final V value) {
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
    public boolean modify(final String path, final String value) {
        return this.modify(String.class, path, value);
    }

    /**
     * Returns an ItemStack from the underlying configuration map.
     */
    public ItemStack item(final String path, final Player player) {
        CommentedConfigurationNode node = this.root.node(NodePath.of(path.split("\\.")));
        try {
            return PaperItemBuilder.of(ItemSerializer.INSTANCE.deserialize(ItemStack.class, node))
                    .name(Text.parseComponent(Component.text(node.node("name").getString("")), player))
                    .loreModifier(i -> i.forEach(x -> Text.parseComponent(x, player))).build();
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        return new ItemStack(Material.AIR);
    }

    public int slot(final String path) {
        return this.integer(path + ".slot");
    }

    public HoconConfigurationLoader createLoader() {
        FileUtil.createFile(this.dir, this.fileName);
        return HoconConfigurationLoader.builder()
                .defaultOptions(opts -> opts.serializers(build -> build.register(ItemStack.class, ItemSerializer.INSTANCE)))
                .path(this.dir.resolve(this.fileName)).prettyPrinting(true).build();
    }

    private void logWarning(final String path) {
        Bukkit.getLogger().log(Level.WARNING, "[MCMMOCredits] Configuration was missing a value at {0}, Check your configuration!", path);
    }
}
