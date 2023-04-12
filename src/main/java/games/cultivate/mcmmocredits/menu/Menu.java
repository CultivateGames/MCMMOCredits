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
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.user.User;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.paper.type.ChestInterface;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a menu with a list of items, title, number of slots, and optional fill and navigation.
 *
 * @param items      Map of items and their configuration node keys.
 * @param title      Unparsed title of the Menu.
 * @param slots      Size of the chest UI that holds the Menu.
 * @param fill       Whether bordering fill items should be enabled.
 * @param navigation Whether a navigation item should be enabled.
 */
public record Menu(Map<String, Item> items, String title, int slots, boolean fill, boolean navigation) {

    /**
     * Applies all required Configuration items to the current Menu.
     *
     * @param config The configuration.
     */
    private void addConfigItems(final MainConfig config) {
        List<String> keys = config.filterNodes(x -> x.contains("database"));
        Item messages = this.items.get("messages");
        Item settings = this.items.get("settings");
        this.items.remove("messages");
        this.items.remove("settings");
        int x = 0;
        for (String key : keys) {
            Item item = (key.contains("settings") ? settings : messages).withName(key).withSlot(x);
            this.items.put(key, item);
            x++;
        }
    }

    /**
     * Remove Main Menu shortcuts if the provided User does not have permission.
     *
     * @param user The user viewing the menu.
     */
    private void checkMenuPermissions(final User user) {
        Player player = user.player();
        if (!player.hasPermission("mcmmocredits.menu.config")) {
            this.items.remove("config");
        }
        if (!player.hasPermission("mcmmocredits.menu.redeem")) {
            this.items.remove("redeem");
        }
    }

    /**
     * Applies all Fill items to the current Menu.
     */
    private void createFill() {
        Item fill = this.items.get("fill");
        this.items.remove("fill");
        if (this.fill) {
            Set<Integer> slots = new HashSet<>();
            this.items.values().forEach(x -> slots.add(x.slot()));
            for (int i = 0; i < this.slots; i++) {
                if (!slots.contains(i)) {
                    this.items.put("fill" + i, fill.withSlot(i));
                }
            }
        }
    }

    /**
     * Creates the Config Editing menu.
     *
     * @param user    The viewer of the menu.
     * @param config  The config being edited.
     * @param factory The ClickFactory to generate click handlers.
     * @return The menu.
     */
    public ChestInterface createConfigMenu(final User user, final MainConfig config, final ClickFactory factory) {
        this.addConfigItems(config);
        return this.createMenu(user, factory);
    }

    /**
     * Creates the Main Menu.
     *
     * @param user    The viewer of the menu.
     * @param factory The ClickFactory to generate click handlers.
     * @return The menu.
     */
    public ChestInterface createMainMenu(final User user, final ClickFactory factory) {
        this.checkMenuPermissions(user);
        return this.createMenu(user, factory);
    }

    /**
     * Creates a new ChestInterface for the given user with the specified clickFactory.
     *
     * @param user         The user for which the ChestInterface will be created.
     * @param clickFactory The factory responsible for creating click actions.
     * @return A new ChestInterface instance.
     */
    public ChestInterface createMenu(final User user, final ClickFactory clickFactory) {
        if (!this.navigation) {
            this.items.remove("navigation");
        }
        this.createFill();
        var transforms = this.items.values().stream().map(x -> x.context(clickFactory, Resolver.ofUser(user))).toList();
        Component compTitle = Text.forOneUser(user, this.title).toComponent();
        return new ChestInterface(this.slots / 9, compTitle, transforms, List.of(), true, 10, ClickHandler.cancel());
    }
}
