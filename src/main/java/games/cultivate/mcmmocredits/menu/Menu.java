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

import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.user.User;
import net.kyori.adventure.text.Component;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.paper.type.ChestInterface;

import java.util.List;

/**
 * Represents a menu in the game with a list of items, title, number of slots, and optional fill and navigation.
 */
public record Menu(List<Item> items, String title, int slots, boolean fill, boolean navigation) {

    /**
     * Creates a new ChestInterface for the given user with the specified clickFactory.
     *
     * @param user         The user for which the ChestInterface will be created.
     * @param clickFactory The factory responsible for creating click actions.
     * @return A new ChestInterface instance.
     */
    public ChestInterface createInterface(final User user, final ClickFactory clickFactory) {
        Component compTitle = Text.forOneUser(user, this.title).toComponent();
        Resolver resolver = Resolver.ofUser(user);
        var transforms = this.items().stream().map(x -> x.context(clickFactory, resolver)).toList();
        return new ChestInterface(this.slots / 9, compTitle, transforms, List.of(), true, 10, ClickHandler.cancel());
    }
}
