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

import games.cultivate.mcmmocredits.util.Dir;
import jakarta.inject.Inject;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Service which handles creation and storage of configuration files.
 */
public final class ConfigService {
    private final YamlConfigurationLoader.Builder builder;
    private final Path path;
    private Config<MainData> config;
    private Config<MenuData> menuConfig;

    @Inject
    public ConfigService(final YamlConfigurationLoader.Builder builder, final @Dir Path path) {
        this.path = path;
        this.builder = builder;
    }

    /**
     * Creates a file and path's directories if they do not exist.
     *
     * @param dir      Path of the file to be created.
     * @param fileName Name of the file to be created.
     * @return the path of the created file.
     */
    public static Path createFile(final Path dir, final String fileName) {
        try {
            if (Files.notExists(dir)) {
                Files.createDirectories(dir);
            }
            return Files.createFile(dir.resolve(fileName));
        } catch (FileAlreadyExistsException ignored) { //Ignore if file already exists.
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dir.resolve(fileName);
    }

    public <T extends Data> Config<T> loadConfig(final Class<T> type) {
        Config<T> conf = new BaseConfig<>(this.builder.build());
        conf.load(type);
        return conf;
    }

    public <T extends Data> Config<T> loadConfig(final Class<T> type, final String name) {
        Path filePath = ConfigService.createFile(this.path, name);
        Config<T> conf = new BaseConfig<>(this.builder.path(filePath).build());
        conf.load(type);
        return conf;
    }

    public void reloadConfigs() {
        this.config = this.loadConfig(MainData.class, "config.yml");
        this.menuConfig = this.loadConfig(MenuData.class, "menus.yml");
    }

    public Config<MainData> mainConfig() {
        if (this.config == null) {
            this.config = this.loadConfig(MainData.class, "config.yml");
        }
        return this.config;
    }

    public Config<MenuData> menuConfig() {
        if (this.menuConfig == null) {
            this.menuConfig = this.loadConfig(MenuData.class, "menus.yml");
        }
        return this.menuConfig;
    }
}
