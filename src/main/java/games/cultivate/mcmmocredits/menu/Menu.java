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

import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.serializers.MenuSerializer;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.user.User;
import net.kyori.adventure.text.Component;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.type.ChestInterface;

import java.util.List;

/**
 * Representation of a GUI created by the plugin.
 * <p>
 * When constructed, we only have a list of items serialized from a {@link Config}.
 * This object is passed to the {@link MenuFactory} to apply transformations to the serialized items.
 * It is then built using the createInterface method.
 *
 * @param properties Common menu properties.
 * @param items      List of Items obtained through serialization.
 * @see MenuSerializer
 * @see MenuFactory
 */
public record Menu(MenuProperties properties, List<Item> items) {
    /**
     * Transforms the Menu into a completed ChestInterface that is ready to view.
     *
     * @param user         User to parse the Menu's title against.
     * @param clickFactory Instance of the ClickFactory.
     * @return The built ChestInterface.
     * @see MenuFactory
     */
    public ChestInterface createInterface(final User user, final ClickFactory clickFactory) {
        Component title = Text.forOneUser(user, this.properties.title()).toComponent();
        List<TransformContext<ChestPane, PlayerViewer>> transforms = this.items().stream().map(x -> x.context(clickFactory, user.resolver())).toList();
        return new ChestInterface(this.properties.slots() / 9, title, transforms, List.of(), true, 10, ClickHandler.cancel());
    }
}
