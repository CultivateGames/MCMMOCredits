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

import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.user.User;
import net.kyori.adventure.text.Component;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.type.ChestInterface;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents an Inventory and it's properties.
 */
public interface Menu {
    /**
     * Adds extra items to the menu if needed.
     *
     * @param user The user viewing the Menu.
     */
    void addExtraItems(User user);

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

    /**
     * Creates a new ChestInterface for the specified user.
     *
     * @param user The user to build the interface for.
     * @return The ChestInterface.
     */
    default ChestInterface build(User user) {
        this.addExtraItems(user);
        if (!this.navigation()) {
            this.items().remove("navigation");
        }
        Item filler = this.items().remove("fill");
        if (this.fill()) {
            Set<Integer> itemSlots = new HashSet<>(this.items().values().stream().map(Item::slot).toList());
            for (int i = 0; i < this.slots(); i++) {
                if (!itemSlots.contains(i)) {
                    //TODO: replace.
                    Item item = new Item(filler.stack(), filler.name(), filler.lore(), filler.slot(), x -> {});
                    this.items().put("fill" + i, item);
                }
            }
        }
        Component parsedTitle = Text.forOneUser(user, this.title()).toComponent();
        List<TransformContext<ChestPane, PlayerViewer>> context = this.items().values().stream().map(item -> TransformContext.of(0, item.transform(user))).toList();
        return new ChestInterface(this.slots() / 9, parsedTitle, context, List.of(), true, 20, ClickHandler.dummy());
    }
}
