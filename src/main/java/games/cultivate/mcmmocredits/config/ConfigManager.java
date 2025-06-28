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

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.converters.ConverterProperties;
import games.cultivate.mcmmocredits.menu.MenuProperties;
import games.cultivate.mcmmocredits.inject.Dir;
import games.cultivate.mcmmocredits.storage.StorageProperties;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * Holds references to current configs and handles reloading.
 */
public final class ConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigManager.class);
    private final Path path;
    private Config<Captions> messages;
    private Config<Settings> settings;
    private Config<MenuProperties> menu;

    @Inject
    public ConfigManager(final @Dir Path path) {
        this.path = path;
        this.reloadConfigs();
    }

    public boolean reloadConfigs() {
        try {
            this.messages = this.create(Captions.class, "messages.yml");
            this.menu = this.create(MenuProperties.class, "menu.yml");
            this.settings = this.create(Settings.class, "settings.yml");
            return true;
        } catch (Exception e) {
            LOGGER.error("There was an issue reloading configurations", e);
            return false;
        }
    }

    public void saveConfigs() {
        this.messages.save();
        this.menu.save();
        this.settings.save();
    }

    public Captions messages() {
        if (this.messages == null) {
            this.messages = this.create(Captions.class, "messages.yml");
        }
        return this.messages.data();
    }

    public MenuProperties menu() {
        if (this.menu == null) {
            this.menu = this.create(MenuProperties.class, "menu.yml");
        }
        return this.menu.data();
    }

    public PluginProperties settings() {
        if (this.settings == null) {
            this.settings = this.create(Settings.class, "settings.yml");
        }
        return this.settings.data().plugin();
    }

    public StorageProperties database() {
        if (this.settings == null) {
            this.settings = this.create(Settings.class, "settings.yml");
        }
        return this.settings.data().database();
    }

    public ConverterProperties converter() {
        if (this.settings == null) {
            this.settings = this.create(Settings.class, "settings.yml");
        }
        return this.settings.data().converter();
    }

    /**
     * Creates a configuration using the provided type and file name. Creates the file for writing.
     *
     * @param type The data type to load data.
     * @param name Name of the file.
     * @param <D>  The data type.
     * @return The configuration.
     */
    private <D extends Data> Config<D> create(final Class<D> type, final String name) {
        Path file = this.path.resolve(name);
        MCMMOCredits.createFile(file);
        Config<D> config = new Config<>(type, file);
        config.load();
        return config;
    }
}
