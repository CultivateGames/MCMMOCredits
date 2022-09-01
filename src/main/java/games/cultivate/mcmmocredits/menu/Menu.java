//
// MIT License
//
// Copyright (c) 2022 Cultivate Games
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

import org.bukkit.entity.Player;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.type.ChestInterface;

/**
 * Interface which represents all Menus.
 */
public interface Menu {
    /**
     * Opens a menu for the {@link PlayerViewer}
     */
    default void open() {
        this.chest().open(this.viewer());
    }

    /**
     * Closes a menu for the {@link PlayerViewer}
     */
    default void close() {
        this.viewer().close();
    }

    /**
     * Loads the menu.
     */
    void load();

    /**
     * Provides the backing {@link ChestInterface} of a menu.
     *
     * @return the {@link ChestInterface}
     */
    ChestInterface chest();

    /**
     * Provides the backing {@link PlayerViewer} of a menu. Typically derived from a {@link Player}
     *
     * @return the {@link PlayerViewer}
     */
    PlayerViewer viewer();
}
