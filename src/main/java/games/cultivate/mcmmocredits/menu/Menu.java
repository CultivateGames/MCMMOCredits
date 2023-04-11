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
import org.bukkit.inventory.ItemStack;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.type.ChestInterface;

import java.util.*;

/**
 * Represents a menu in the game with a list of items, title, number of slots, and optional fill and navigation.
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

    public ChestInterface createConfigMenu(final User user, final MainConfig config, final ClickFactory factory) {
        this.addConfigItems(config);
        return this.createMenu(user, factory);
    }

    public ChestInterface createMainMenu(final User user, final ClickFactory factory) {
        this.checkMenuPermissions(user);
        return this.createMenu(user, factory);
    }

    public void createMenuContext(final User user, final ClickFactory factory) {
        Resolver resolver = Resolver.ofUser(user);
        List<TransformContext<ChestPane, PlayerViewer>> list = new ArrayList<>();
        for (var entry : this.items.entrySet()) {
            Item item = entry.getValue();
            list.add(TransformContext.of(0, (pane, view) -> {
                ItemStack menuItem = item.applyProperties(user.player(), resolver);
                return pane.element(ItemStackElement.of(menuItem, factory.getClick(item.clickType(), entry.getKey(), resolver)), item.slot() % 9, item.slot() / 9);
            }));
        }
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
