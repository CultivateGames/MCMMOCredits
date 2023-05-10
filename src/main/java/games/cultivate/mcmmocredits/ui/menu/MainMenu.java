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
import org.bukkit.entity.Player;
import org.incendo.interfaces.paper.type.ChestInterface;

import java.util.Map;

public final class MainMenu extends BaseMenu {
    private MainMenu(final Map<String, Item> items, final String title, final int slots, final boolean fill, final boolean navigation) {
        super(items, title, slots, fill, navigation);
    }

    public static MainMenu of(final Menu menu) {
        return new MainMenu(menu.items(), menu.title(), menu.slots(), menu.fill(), menu.navigation());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChestInterface build(final User user, final ContextFactory factory) {
        this.hideItems(user);
        return super.build(user, factory);
    }

    private void hideItems(final User user) {
        Player player = user.player();
        if (!player.hasPermission("mcmmocredits.menu.config")) {
            this.items().remove("config");
        }
        if (!player.hasPermission("mcmmocredits.menu.redeem")) {
            this.items().remove("redeem");
        }
    }
}
