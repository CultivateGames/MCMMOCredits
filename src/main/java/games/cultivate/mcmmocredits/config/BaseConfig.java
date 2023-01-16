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
import java.util.HashMap;
import java.util.Map;

/**
 * Object represents all {@link Config} types.
 */
public class BaseConfig implements Config {
    //transience added to avoid serialization by Configurate.
    private final transient String fileName;
    private final transient Class<? extends BaseConfig> type;
    private transient BaseConfig conf;
    private transient CommentedConfigurationNode root;
    private transient HoconConfigurationLoader loader;
    private transient Map<String, CommentedConfigurationNode> nodeMap;
    private transient @Inject @Named("dir") Path dir;

    BaseConfig(final Class<? extends BaseConfig> type, final String fileName) {
        this.type = type;
        this.fileName = fileName;
        this.nodeMap = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HoconConfigurationLoader createLoader() {
        FileUtil.createFile(this.dir, this.fileName);
        return HoconConfigurationLoader.builder()
                .defaultOptions(opts -> opts.serializers(build -> build.register(ItemStack.class, ItemSerializer.INSTANCE)))
                .path(this.dir.resolve(this.fileName)).prettyPrinting(true).build();
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(final CommentedConfigurationNode root) {
        try {
            this.loader.save(root);
            this.root = root;
            this.nodeMap = this.nodesFromParent(this.root);
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BaseConfig config() {
        return this.conf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentedConfigurationNode rootNode() {
        return this.root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, CommentedConfigurationNode> nodes() {
        return this.nodeMap;
    }
}
