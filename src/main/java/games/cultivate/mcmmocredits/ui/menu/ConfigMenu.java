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
package games.cultivate.mcmmocredits.ui.menu;

import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.ui.item.BaseItem;
import games.cultivate.mcmmocredits.ui.item.ConfigItem;
import games.cultivate.mcmmocredits.ui.item.Item;
import org.spongepowered.configurate.NodePath;

import java.util.List;
import java.util.Map;

/**
 * Represents the menu provided by executing /credits menu config.
 */
public final class ConfigMenu extends BaseMenu {
    /**
     * Constructs the object.
     *
     * @param items      The items and their keys in a map.
     * @param title      Unparsed title of the Inventory.
     * @param slots      Size of the Inventory.
     * @param fill       Whether the inventory will have fill border items.
     * @param navigation Whether the inventory will have a navigation item.
     */
    private ConfigMenu(final Map<String, Item> items, final String title, final int slots, final boolean fill, final boolean navigation) {
        super(items, title, slots, fill, navigation);
    }

    /**
     * Constructs the object from an existing Menu.
     *
     * @param config MainConfig to derive menu items.
     * @param menu   The existing Menu.
     * @return The menu.
     */
    public static ConfigMenu of(final MainConfig config, final Menu menu) {
        ConfigMenu cmenu = new ConfigMenu(menu.items(), menu.title(), menu.slots(), menu.fill(), menu.navigation());
        cmenu.addConfigKeys(config.filterNodes(x -> x.contains("database") || x.contains("converter")));
        return cmenu;
    }

    /**
     * Calculates menu items based on the provided config keys.
     *
     * @param keys Config node paths as strings.
     */
    private void addConfigKeys(final List<String> keys) {
        ConfigItem messages = (ConfigItem) this.items().remove("messages");
        ConfigItem settings = (ConfigItem) this.items().remove("settings");
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            ConfigItem item = key.contains("settings") ? settings : messages;
            this.items().put(key, ConfigItem.of(NodePath.of(key.split("\\.")), BaseItem.of(item.stack(), key, item.lore(), i)));
        }
    }
}
