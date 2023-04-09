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

import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.user.User;
import org.incendo.interfaces.paper.type.ChestInterface;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Factory which is used to create {@link Menu} instances.
 */
public final class MenuFactory {
    private final MenuConfig menuConfig;
    private final ClickFactory clickFactory;
    private final MainConfig config;

    /**
     * Constructs the object.
     *
     * @param menuConfig   Instance of the MenuConfig.
     * @param config       Instance of the MainConfig.
     * @param clickFactory Instance of the ClickFactory.
     */
    @Inject
    public MenuFactory(final MenuConfig menuConfig, final MainConfig config, final ClickFactory clickFactory) {
        this.menuConfig = menuConfig;
        this.config = config;
        this.clickFactory = clickFactory;
    }

    /**
     * Builds the Menu.
     *
     * @param user User who requested the Menu.
     * @param path Node path where the menu is located.
     * @return A built ChestInterface
     * @see Menu
     */
    public ChestInterface buildMenu(final User user, final String path) {
        Menu menu = this.menuConfig.getMenu(path.toLowerCase());
        if (path.contains("config")) {
            this.addConfigItems(menu);
        }
        if (path.contains("main")) {
            this.addMainMenuItem(menu, user, "config");
            this.addMainMenuItem(menu, user, "redeem");
        }
        if (menu.fill()) {
            this.applyFill(menu, path);
        }
        return menu.createInterface(user, this.clickFactory);
    }

    /**
     * Adds items to Main Menu if the user has permission to access the menus.
     *
     * @param menu The Menu.
     * @param user The user viewing the menu.
     * @param type The type of menu to check against. (config, redeem etc.)
     */
    private void addMainMenuItem(final Menu menu, final User user, final String type) {
        if (user.player().hasPermission("mcmmocredits.menu." + type)) {
            Item item = this.menuConfig.getItem("main", "items", type);
            menu.items().add(item);
        }
    }

    /**
     * Applies all Fill items to the current Menu.
     *
     * @param menu The Menu.
     * @param path Node path of the menu.
     */
    private void applyFill(final Menu menu, final String path) {
        Set<Integer> allSlots = IntStream.range(0, menu.slots()).boxed().collect(Collectors.toSet());
        Set<Integer> itemSlots = menu.items().stream().map(Item::slot).collect(Collectors.toSet());
        allSlots.removeAll(itemSlots);
        Item fill = this.menuConfig.getItem(path, "items", "fill");
        allSlots.forEach(x -> menu.items().add(fill.withSlot(x)));
    }

    /**
     * Applies all required Configuration items to the current Menu.
     *
     * @param menu The current Menu.
     */
    private void addConfigItems(final Menu menu) {
        List<String> keys = this.config.filterKeys("mysql", "database");
        menu.items().removeIf(x -> x.slot() >= 0 && x.slot() <= keys.size());
        int x = 0;
        for (String key : keys) {
            String type = key.contains("settings") ? "settings" : "messages";
            menu.items().add(this.prepareConfigItem(type, key, x));
            x++;
        }
    }

    private Item prepareConfigItem(final String type, final String name, final int slot) {
        return this.menuConfig.getItem("config", "items", type).withName(name).withSlot(slot);
    }
}
