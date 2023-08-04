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

import games.cultivate.mcmmocredits.user.User;
import org.spongepowered.configurate.NodePath;

import java.util.List;
import java.util.Map;

/**
 * Represents a Menu in which users can edit the config.
 *
 * @param menu The existing menu.
 * @param keys List of node keys to show as editable.
 */
public record ConfigMenu(Menu menu, List<String> keys) implements Menu {
    /**
     * {@inheritDoc}
     */
    @Override
    public void addExtraItems(final User user) {
        Item messages = this.items().remove("messages");
        Item settings = this.items().remove("settings");
        for (int i = 0; i < this.keys.size(); i++) {
            String key = this.keys.get(i);
            Item item = key.contains("settings") ? settings : messages;
            this.items().put(key, new Item(item.stack(), key, item.lore(), i, Action.editConfig(NodePath.of(key.split("\\.")))));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Item> items() {
        return this.menu.items();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String title() {
        return this.menu.title();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int slots() {
        return this.menu.slots();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean fill() {
        return this.menu.fill();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean navigation() {
        return this.menu.navigation();
    }
}
