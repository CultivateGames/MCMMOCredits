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

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.placeholders.ResolverFactory;
import org.bukkit.entity.Player;
import org.incendo.interfaces.core.transform.Transform;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;

/**
 * {@link Menu} instance that represents the main menu users will interface with. Links out to other {@link Menu} instances.
 */
public final class MainMenu extends BaseMenu {

    MainMenu(final MenuConfig menu, final ResolverFactory resolverFactory, final Player player, final MCMMOCredits plugin) {
        super(menu, resolverFactory, player, plugin, "main");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applySpecialItems() {
        this.transformations.add(this.mainTransformContext("config"));
        this.transformations.add(this.mainTransformContext("redeem"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyNavigationItem() {
        //skip adding navigation item in main menu.
    }

    /**
     * Creates {@link TransformContext} for each menu type we are linking out to. Does not show the button if the user does not have permission.
     *
     * @param type String representing the {@link Menu} type.
     * @return The newly created {@link TransformContext} to be added to the Menu's {@link Transform} list.
     */
    private TransformContext<ChestPane, PlayerViewer> mainTransformContext(final String type) {
        return TransformContext.of(3, (pane, view) -> {
            if (this.player.hasPermission("mcmmocredits.menu." + type)) {
                String menuPath = "main.items." + type;
                int slot = this.menu.slot(menuPath);
                pane = pane.element(ItemStackElement.of(this.menu.item(menuPath, this.player, this.resolverFactory), click -> {
                    if (click.cause().isLeftClick()) {
                        this.runSyncCommand("credits menu " + type);
                    }
                }), slot % 9, slot / 9);
            }
            return pane;
        });
    }
}
