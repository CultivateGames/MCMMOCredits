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
package games.cultivate.mcmmocredits;

import com.google.inject.Guice;
import com.google.inject.Injector;
import games.cultivate.mcmmocredits.config.ConfigManager;
import games.cultivate.mcmmocredits.converters.Converter;
import games.cultivate.mcmmocredits.inject.PluginModule;
import games.cultivate.mcmmocredits.menu.MenuService;
import games.cultivate.mcmmocredits.messages.CreditsExpansion;
import games.cultivate.mcmmocredits.storage.StorageService;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the application. Handles startup and shutdown logic.
 */
public final class MCMMOCredits extends JavaPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(MCMMOCredits.class);
    private static MCMMOCreditsAPI api;
    private Injector injector;

    /**
     * Creates a file in the provided path if it doesn't exist.
     *
     * @param dir Path of the file to be created.
     */
    public static void createFile(final Path dir) {
        try {
            if (Files.notExists(dir)) {
                Files.createDirectories(dir.getParent());
                Files.createFile(dir);
            }
        } catch (IOException e) {
            LOGGER.error("There was an issue creating a file!", e);
        }
    }

    @SuppressWarnings("unused")
    public static MCMMOCreditsAPI getAPI() {
        return api;
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void onEnable() {
        this.getSLF4JLogger().info("Loading Dependencies...");
        if (this.usingPaper() && Bukkit.getPluginManager().isPluginEnabled("mcMMO")) {
            this.getSLF4JLogger().info("Paper and mcMMO found, continuing to load...");
            this.injector = Guice.createInjector(new PluginModule(this, Executors.newVirtualThreadPerTaskExecutor()));
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                this.injector.getInstance(CreditsExpansion.class).register();
            }
            ConfigManager configs = this.injector.getInstance(ConfigManager.class);
            if (configs.converter().enabled() && !this.injector.getInstance(Converter.class).run()) {
                this.setEnabled(false);
            }
            Bukkit.getPluginManager().registerEvents(this.injector.getInstance(Listeners.class), this);
            if (configs.settings().metricsEnabled()) {
                this.getSLF4JLogger().info("Enabling Bstats.. To disable metrics, set metrics-enabled to false in config.yml");
                new Metrics(this, 18254);
            } else {
                this.getSLF4JLogger().info("Bstats is disabled, skipping initialization...");
            }
            api = this.injector.getInstance(MCMMOCreditsAPI.class);
        } else {
            this.getSLF4JLogger().warn("Not using Paper or mcMMO, disabling plugin...");
            this.setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        this.injector.getInstance(StorageService.class).disable();
        this.injector.getInstance(MenuService.class).closeAll();
        this.injector.getInstance(ConfigManager.class).saveConfigs();
        this.injector.getInstance(CreditsExpansion.class).unregister();
        ExecutorService executorService = this.injector.getInstance(ExecutorService.class);
        executorService.shutdown();
        this.getSLF4JLogger().info("Shutting down Executor Service...");
        try {
            if (executorService.awaitTermination(20, TimeUnit.SECONDS)) {
                this.getSLF4JLogger().info("The Executor Service has shut down successfully!");
            }
        } catch (final InterruptedException e) {
            this.getSLF4JLogger().error("There was an issue shutting down the Executor Service!", e);
            Thread.currentThread().interrupt();
        }
    }

    private boolean usingPaper() {
        try {
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            return true;
        } catch (Exception e) {
            this.getSLF4JLogger().warn("Not using Paper, disabling plugin...");
            return false;
        }
    }
}
