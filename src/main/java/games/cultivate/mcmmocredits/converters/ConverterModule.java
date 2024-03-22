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
package games.cultivate.mcmmocredits.converters;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import games.cultivate.mcmmocredits.config.Settings;
import games.cultivate.mcmmocredits.config.Settings.ConverterProperties;
import games.cultivate.mcmmocredits.converters.loaders.CSVLoader;
import games.cultivate.mcmmocredits.converters.loaders.ExternalLoader;
import games.cultivate.mcmmocredits.converters.loaders.StorageLoader;
import games.cultivate.mcmmocredits.converters.loaders.UserLoader;
import jakarta.inject.Singleton;
import org.bukkit.Bukkit;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

public final class ConverterModule extends AbstractModule {
    private static final Path MR_PATH = Path.of("MorphRedeem", "PlayerData");
    private static final Path GRM_PATH = Path.of("GuiRedeemMCMMO", "playerdata");

    @Override
    protected void configure() {
        this.bind(Converter.class).to(DefaultConverter.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public UserLoader getUserLoader(final Settings settings, final ExecutorService executorService, final Injector injector) {
        ConverterProperties properties = settings.converter();
        ConverterType type = properties.type();
        Path pluginPath = Bukkit.getPluginsFolder().toPath();
        return switch (type) {
            case CSV -> injector.getInstance(CSVLoader.class);
            case INTERNAL -> injector.getInstance(StorageLoader.class);
            case PLUGIN_MR -> new ExternalLoader(pluginPath.resolve(MR_PATH), executorService, properties);
            case PLUGIN_GRM -> new ExternalLoader(pluginPath.resolve(GRM_PATH), executorService, properties);
        };
    }
}
