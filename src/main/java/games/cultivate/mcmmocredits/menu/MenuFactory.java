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
package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.GeneralConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.placeholders.ResolverFactory;
import games.cultivate.mcmmocredits.util.InputStorage;
import org.bukkit.entity.Player;

import javax.inject.Inject;

/**
 * Factory which is used to create {@link Menu} instances.
 */
public final class MenuFactory {
    private final MenuConfig menus;
    private final InputStorage storage;
    private final GeneralConfig config;
    private final ResolverFactory resolverFactory;
    private final MCMMOCredits plugin;

    @Inject
    public MenuFactory(final MenuConfig menus, final ResolverFactory resolverFactory, final GeneralConfig config, final InputStorage storage, final MCMMOCredits plugin) {
        this.menus = menus;
        this.resolverFactory = resolverFactory;
        this.config = config;
        this.storage = storage;
        this.plugin = plugin;
    }

    /**
     * Creates a {@link MainMenu} using the provided {@link Player}.
     *
     * @param player {@link Player} to link to the {@link Menu} instance.
     * @return the {@link Menu}
     */
    public Menu createMainMenu(final Player player) {
        Menu menu = new MainMenu(this.menus, this.resolverFactory, player, this.plugin);
        menu.load();
        return menu;
    }

    /**
     * Creates a {@link ConfigMenu} using the provided {@link Player}.
     *
     * @param player {@link Player} to link to the {@link Menu} instance.
     * @return the {@link Menu}
     */
    public Menu createConfigMenu(final Player player) {
        Menu configMenu = new ConfigMenu(this.menus, this.resolverFactory, player, this.plugin, this.config, this.storage);
        configMenu.load();
        return configMenu;
    }

    /**
     * Creates a {@link RedeemMenu} using the provided {@link Player}.
     *
     * @param player {@link Player} to link to the {@link Menu} instance.
     * @return the {@link Menu}
     */
    public Menu createRedeemMenu(final Player player) {
        Menu redeemMenu = new RedeemMenu(this.menus, this.resolverFactory, player, this.plugin, this.config, this.storage);
        redeemMenu.load();
        return redeemMenu;
    }
}
