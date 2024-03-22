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

import cloud.commandframework.annotations.injection.GuiceInjectionService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import games.cultivate.mcmmocredits.commands.CommandHandler;
import games.cultivate.mcmmocredits.config.ConfigService;
import games.cultivate.mcmmocredits.converters.Converter;
import games.cultivate.mcmmocredits.storage.AbstractStorage;
import games.cultivate.mcmmocredits.inject.PluginModule;
import games.cultivate.mcmmocredits.messages.CreditsExpansion;
import games.cultivate.mcmmocredits.util.Listeners;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

/**
 * Main class of the application. Handles startup and shutdown logic.
 */
public final class MCMMOCredits extends JavaPlugin {
    private static MCMMOCreditsAPI api;
    private Injector injector;
    private ConfigService configs;
    private Logger logger;

    /**
     * Gets an instance of the API. Allows basic user modification.
     *
     * @return the API.
     */
    @SuppressWarnings("unused")
    public static MCMMOCreditsAPI getAPI() {
        return api;
    }

    /**
     * Handles startup of the plugin. Duration is tracked if debug is enabled.
     */
    @Override
    public void onEnable() {
        long start = System.nanoTime();
        this.logger = this.getSLF4JLogger();
        this.injector = Guice.createInjector(new PluginModule(this));
        this.checkForDependencies();
        this.configs = this.injector.getInstance(ConfigService.class);
        this.configs.reloadConfigs();
        this.runConversionProcess();
        this.loadCommands();
        this.registerListeners();
        api = this.injector.getInstance(MCMMOCreditsAPI.class);
        this.enableMetrics();
        long end = System.nanoTime();
        this.logger.info("Plugin enabled! Startup took: {}s.", (double) (end - start) / 1000000000);
    }

    /**
     * Checks that all required software is present.
     * Registers PlaceholderAPI placeholders if it is present.
     */
    @SuppressWarnings("UnstableApiUsage")
    private void checkForDependencies() {
        this.logger.info("Checking Dependencies...");
        try {
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
        } catch (Exception e) {
            this.logger.warn("Not using Paper, disabling plugin...");
            this.setEnabled(false);
            return;
        }
        this.logger.info("Paper has been found! Continuing to load...");
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.getPlugin("mcMMO") == null) {
            this.logger.warn("Not using mcMMO, disabling plugin...");
            this.setEnabled(false);
            return;
        }
        this.logger.info("mcMMO has been found! Continuing to load...");
        if (pluginManager.getPlugin("PlaceholderAPI") != null) {
            this.injector.getInstance(CreditsExpansion.class).register();
        }
        this.logger.info("Dependencies loaded!");
    }

    /**
     * Parses and loads our commands.
     */
    private void loadCommands() {
        this.logger.info("Checking Commands...");
        this.injector.getInstance(CommandHandler.class).load(GuiceInjectionService.create(this.injector));
        this.logger.info("Commands loaded!");
    }

    /**
     * Registers all required Event Listeners.
     */
    private void registerListeners() {
        this.logger.info("Registering Listeners...");
        Bukkit.getPluginManager().registerEvents(this.injector.getInstance(Listeners.class), this);
        this.logger.info("Listeners registered!");
    }

    /**
     * Enables BStats if it is enabled in configuration.
     */
    private void enableMetrics() {
        if (this.configs.mainConfig().getBoolean("settings", "metrics-enabled")) {
            this.logger.info("Enabling Bstats.. To disable metrics, set metrics-enabled to false in config.yml");
            new Metrics(this, 18254);
            return;
        }
        this.logger.info("Bstats is disabled, skipping initialization...");
    }

    /**
     * Runs a Data Converter if it is enabled in configuration.
     */
    @SuppressWarnings("UnstableApiUsage")
    private void runConversionProcess() {
        boolean enabled = this.configs.mainConfig().getBoolean("converter", "enabled");
        if (enabled && !this.injector.getInstance(Converter.class).run()) {
            this.setEnabled(false);
        }
    }

    /**
     * Handles shutdown of the plugin.
     */
    @Override
    public void onDisable() {
        this.injector.getInstance(AbstractStorage.class).disable();
        this.configs.saveConfigs();
    }
}
