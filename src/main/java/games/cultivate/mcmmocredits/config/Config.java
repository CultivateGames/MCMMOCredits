package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.MCMMOCredits;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;

public class Config<T> {
    public static final Config<SettingsConfig> SETTINGS = new Config<>(SettingsConfig.class);
    public static final Config<MessagesConfig> MESSAGES = new Config<>(MessagesConfig.class);
    public static final Config<MenuConfig> MENU = new Config<>(MenuConfig.class);

    private final Class<T> TYPE;
    private T config;
    private CommentedConfigurationNode root;
    private String title;
    private HoconConfigurationLoader loader;

    public Config (Class<T> type) {
        this.TYPE = type;
    }

    public void load(String title) {
        try {
            this.title = title;
            this.root = this.configLoader().load();
            this.config = this.root.get(this.TYPE);
            this.configLoader().save(this.root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(CommentedConfigurationNode root) {
        try {
            this.configLoader().save(root);
            this.root = root;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CommentedConfigurationNode root() {
        return this.root;
    }

    public T config() {
        return this.config;
    }

    public <K> K get(Class<K> type, Iterable<?> path) {
        try {
            return root.node(path).get(type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <K> K key(Keys key) {
        try {
            return key.config().root().node(key.node()).get((Class<K>) key.type());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Class<?> type() {
        return TYPE;
    }

    private HoconConfigurationLoader createLoader() {
        File file = new File(MCMMOCredits.path + title);
        try {
            if (file.getParentFile().mkdirs() && file.createNewFile()) {
                Bukkit.getLogger().log(Level.INFO, "[MCMMOCredits] Created " + title + " file!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        HoconConfigurationLoader hcl = HoconConfigurationLoader.builder().defaultOptions(opts -> opts.serializers(build -> build.register(ItemStack.class, ItemStackSerializer.INSTANCE))).path(Paths.get(file.getPath())).prettyPrinting(true).build();
        this.loader = hcl;
        return hcl;
    }

    private HoconConfigurationLoader configLoader() {
        return loader != null ? loader : createLoader();
    }
}
