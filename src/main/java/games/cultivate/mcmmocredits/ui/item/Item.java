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
package games.cultivate.mcmmocredits.ui.item;

import games.cultivate.mcmmocredits.ui.ContextFactory;
import games.cultivate.mcmmocredits.user.User;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.pane.ChestPane;

import java.util.List;

public interface Item {
    /**
     * Updates the name and lore based on the passed in Player and resolver.
     *
     * @param user The viewer of the item as a User.
     * @return A Bukkit ItemStack with updated properties.
     */
    ItemStack parseUser(final User user);

    /**
     * Executes an action if a click is performed on this Item within a Menu.
     *
     * @param user    The affected user.
     * @param factory The ContextFactory used to run click actions.
     * @param ctx     The context of the click.
     */
    void executeClick(final User user, final ContextFactory factory, final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> ctx);

    /**
     * Gets the raw item name.
     *
     * @return The Item's name.
     */
    String name();

    /**
     * Gets the raw item lore.
     *
     * @return The Item's lore.
     */
    List<String> lore();

    /**
     * Gets the Item's assigned slot in a Menu.
     *
     * @return The Item's slot
     */
    int slot();

    /**
     * Gets the base ItemStack.
     *
     * @return The ItemStack.
     */
    ItemStack stack();
}
