package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.serializers.ItemSerializer;
import games.cultivate.mcmmocredits.util.FileUtil;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ObjectMapper;

import javax.inject.Inject;
import javax.inject.Named;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Object which represents all configuration files.
 */
public class BaseConfig implements Config {
    //transience added to avoid serialization by Configurate.
    private final transient String fileName;
    private final transient Class<? extends BaseConfig> type;
    private transient BaseConfig conf;
    private transient CommentedConfigurationNode root;
    private transient HoconConfigurationLoader loader;
    private transient List<CommentedConfigurationNode> nodeList;
    private transient @Inject @Named("dir") Path dir;

    BaseConfig(final Class<? extends BaseConfig> type, final String fileName) {
        this.type = type;
        this.fileName = fileName;
        this.nodeList = new ArrayList<>();
    }

    @Override
    public HoconConfigurationLoader createLoader() {
        FileUtil.createFile(this.dir, this.fileName);
        return HoconConfigurationLoader.builder()
                .defaultOptions(opts -> opts.serializers(build -> build.register(ItemStack.class, ItemSerializer.INSTANCE)))
                .path(this.dir.resolve(this.fileName)).prettyPrinting(true).build();
    }

    @Override
    public void load() {
        this.loader = this.createLoader();
        try {
            this.root = this.loader.load();
            this.conf = ObjectMapper.factory().get(this.type).load(this.root);
            this.save();
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(final CommentedConfigurationNode root) {
        try {
            this.loader.save(root);
            this.root = root;
            this.nodeList = this.nodesFromParent(this.root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public BaseConfig config() {
        return this.conf;
    }

    @Override
    public CommentedConfigurationNode rootNode() {
        return this.root;
    }

    @Override
    public List<CommentedConfigurationNode> nodes() {
        return this.nodeList;
    }
}
