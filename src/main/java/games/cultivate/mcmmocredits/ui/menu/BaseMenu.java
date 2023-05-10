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

import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.ui.ContextFactory;
import games.cultivate.mcmmocredits.ui.item.BaseItem;
import games.cultivate.mcmmocredits.ui.item.Item;
import games.cultivate.mcmmocredits.user.User;
import org.incendo.interfaces.paper.type.ChestInterface;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BaseMenu implements Menu {
    private final Map<String, Item> items;
    private final String title;
    private final int slots;
    private final boolean fill;
    private final boolean navigation;

    BaseMenu(final Map<String, Item> items, final String title, final int slots, final boolean fill, final boolean navigation) {
        this.items = items;
        this.title = title;
        this.slots = slots;
        this.fill = fill;
        this.navigation = navigation;
    }

    public static BaseMenu of(final Map<String, Item> items, final String title, final int slots, final boolean fill, final boolean navigation) {
        return new BaseMenu(items, title, slots, fill, navigation);
    }

    public static BaseMenu of(final Menu menu) {
        return new BaseMenu(menu.items(), menu.title(), menu.slots(), menu.fill(), menu.navigation());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addExtraItems() {
        if (!this.navigation) {
            this.items.remove("navigation");
        }
        Item filler = this.items.remove("fill");
        if (!this.fill) {
            return;
        }
        Set<Integer> itemSlots = new HashSet<>(this.items.values().stream().map(Item::slot).toList());
        for (int i = 0; i < this.slots; i++) {
            if (!itemSlots.contains(i)) {
                Item item = BaseItem.of(filler.stack(), filler.name(), filler.lore(), i);
                this.items.put("fill" + i, item);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChestInterface build(final User user, final ContextFactory factory) {
        this.addExtraItems();
        ChestInterface.Builder builder = ChestInterface.builder().rows(this.slots / 9).title(Text.forOneUser(user, this.title).toComponent());
        for (Map.Entry<String, Item> entry : this.items.entrySet()) {
            builder = builder.addTransform(0, factory.createContext(user, entry.getValue()));
        }
        return builder.build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Item> items() {
        return this.items;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String title() {
        return this.title;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int slots() {
        return this.slots;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean fill() {
        return this.fill;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean navigation() {
        return this.navigation;
    }
}
