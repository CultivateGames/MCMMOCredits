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
import games.cultivate.mcmmocredits.config.MenuConfig.MenuProperties;
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
import java.util.NoSuchElementException;

/**
 * Representation of a GUI created by the plugin.
 * <p>
 * When constructed, we only have a list of items serialized from a {@link Config}.
 * This object is passed to the {@link MenuFactory} to apply transformations to the serialized items.
 * It is then built using {@link Menu#createInterface(List, User)}
 *
 * @param properties Common menu properties.
 * @param items      List of Items obtained through serialization.
 * @see MenuSerializer
 * @see MenuFactory
 * @see MenuTransform
 */
public record Menu(MenuProperties properties, List<Item> items) {
    /**
     * Gets an item based on its type, or throws a {@link NoSuchElementException}.
     *
     * @param type The ItemType to search for.
     * @return The first Item matching the provided type.
     */
    public Item findFirstOfType(final ItemType type) {
        return this.items.stream().filter(x -> x.type() == type).findFirst().orElseThrow();
    }

    /**
     * Transforms the Menu into a completed ChestInterface that is ready to view.
     *
     * @param context List of {@link TransformContext} created via {@link MenuTransform} instances.
     * @param user    User to parse the Menu's title against.
     * @return The built ChestInterface.
     * @see MenuFactory
     * @see MenuTransform
     */
    public ChestInterface createInterface(final List<TransformContext<ChestPane, PlayerViewer>> context, final User user) {
        Component title = Text.forOneUser(user, this.properties.title()).toComponent();
        return new ChestInterface(this.properties.slots() / 9, title, context, List.of(), true, 10, ClickHandler.cancel());
    }
}
