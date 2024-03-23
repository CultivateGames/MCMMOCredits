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
import com.google.inject.Module;
import games.cultivate.mcmmocredits.config.Settings;
import games.cultivate.mcmmocredits.converters.Converter;
import games.cultivate.mcmmocredits.converters.ConverterModule;
import games.cultivate.mcmmocredits.inject.PluginModule;
import games.cultivate.mcmmocredits.menu.MenuModule;
import games.cultivate.mcmmocredits.menu.MenuService;
import games.cultivate.mcmmocredits.messages.CreditsExpansion;
import games.cultivate.mcmmocredits.storage.StorageModule;
import games.cultivate.mcmmocredits.storage.StorageService;
import games.cultivate.mcmmocredits.user.UserModule;
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
    //TODO: may move skills set to main class.. investigate.
    //public static final Set<String> SKILLS = Set.of("acrobatics", "alchemy", "archery", "axes", "excavation", "fishing", "herbalism", "mining", "repair", "swords", "taming", "unarmed", "woodcutting");
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
        LOGGER.info("Loading Dependencies...");
        if (!this.usingPaper() || !Bukkit.getPluginManager().isPluginEnabled("mcMMO")) {
            LOGGER.warn("Not using Paper or mcMMO, disabling plugin...");
            this.setEnabled(false);
            return;
        }
        LOGGER.info("Paper and mcMMO found, continuing to load...");
        Module[] modules = new Module[]{new ConverterModule(), new MenuModule(), new StorageModule(), new UserModule(), new PluginModule(this, Executors.newVirtualThreadPerTaskExecutor())};
        this.injector = Guice.createInjector(modules);
        api = this.injector.getInstance(MCMMOCreditsAPI.class);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.injector.getInstance(CreditsExpansion.class).register();
        }
        Settings settings = this.injector.getInstance(Settings.class);
        if (settings.converter().enabled() && !this.injector.getInstance(Converter.class).run()) {
            this.setEnabled(false);
        }
        Bukkit.getPluginManager().registerEvents(this.injector.getInstance(Listeners.class), this);
        if (settings.metricsEnabled()) {
            LOGGER.info("Enabling Bstats.. To disable metrics, set metrics-enabled to false in config.yml");
            new Metrics(this, 18254);
        } else {
            LOGGER.info("Bstats is disabled, skipping initialization...");
        }
    }

    @Override
    public void onDisable() {
        this.injector.getInstance(StorageService.class).disable();
        this.injector.getInstance(MenuService.class).closeAll();
        ExecutorService executorService = this.injector.getInstance(ExecutorService.class);
        executorService.shutdown();
        LOGGER.info("Shutting down Executor Service...");
        try {
            if (executorService.awaitTermination(20, TimeUnit.SECONDS)) {
                LOGGER.info("The Executor Service has shut down successfully!");
            }
        } catch (final InterruptedException e) {
            LOGGER.error("There was an issue shutting down the Executor Service!", e);
            Thread.currentThread().interrupt();
        }
    }

    private boolean usingPaper() {
        try {
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            return true;
        } catch (Exception e) {
            LOGGER.warn("Not using Paper, disabling plugin...");
            return false;
        }
    }
}
