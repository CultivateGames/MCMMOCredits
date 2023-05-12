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

import games.cultivate.mcmmocredits.ui.ContextFactory;
import games.cultivate.mcmmocredits.ui.item.Item;
import games.cultivate.mcmmocredits.user.User;
import org.incendo.interfaces.paper.type.ChestInterface;

import java.util.Map;

/**
 * Represents an Inventory and it's properties.
 */
public interface Menu {
    /**
     * Adds extra items if needed.
     */
    void addExtraItems();

    /**
     * Creates a new ChestInterface for the given user with the specified contextFactory.
     */
    ChestInterface build(User user, ContextFactory factory);

    /**
     * Gets a Map of items in the Menu.
     *
     * @return The map.
     */
    Map<String, Item> items();

    /**
     * Gets the unparsed title of the inventory.
     *
     * @return The title.
     */
    String title();

    /**
     * Gets the size of the backing chest inventory.
     *
     * @return The size.
     */
    int slots();

    /**
     * Gets if the menu should be filled with border items.
     *
     * @return If the Menu has border fill items.
     */
    boolean fill();

    /**
     * Gets if the menu should be filled with border items.
     *
     * @return If the Menu has a navigation item.
     */
    boolean navigation();
}
