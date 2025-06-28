//
// MIT License
//
// Copyright (c) 2024 Cultivate Games
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.util.NamingSchemes;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;

/**
 * A Configuration File.
 *
 * @param <D> Type of the data to parse for config.
 */
public final class Config<D extends Data> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    private final YamlConfigurationLoader loader;
    private final ObjectMapper.Factory factory;
    private final Class<D> type;
    private D data;
    private ConfigurationNode root;

    public Config(final Class<D> type, final YamlConfigurationLoader loader) {
        this.type = type;
        this.loader = loader;
        this.factory = ObjectMapper.factoryBuilder().defaultNamingScheme(NamingSchemes.LOWER_CASE_DASHED).build();
    }

    public Config(final Class<D> type, final Path path) {
        this(type, YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .path(path)
                .build());
    }

    /**
     * Loads the configuration.
     */
    public void load() {
        try {
            this.root = this.loader.load();
            this.data = this.factory.get(this.type).load(this.root);
            this.save();
        } catch (final ConfigurateException e) {
            LOGGER.error("There was an issue loading the configuration", e);
        }
    }

    /**
     * Saves the configuration using the current root node.
     */
    public void save() {
        try {
            this.loader.save(this.root);
        } catch (final ConfigurateException e) {
            LOGGER.error("There was an issue saving the configuration", e);
        }
    }

    public D data() {
        return this.data;
    }
}
