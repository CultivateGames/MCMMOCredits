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

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.placeholders.ResolverFactory;
import games.cultivate.mcmmocredits.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.core.util.Vector2;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.type.ChestInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Object that represents all {@link Menu} types.
 */
public abstract class BaseMenu implements Menu {
    protected final List<TransformContext<ChestPane, PlayerViewer>> transformations;
    protected final MenuConfig menu;
    protected final Player player;
    protected final ResolverFactory resolverFactory;
    protected final MCMMOCredits plugin;
    private final Component title;
    private final int rows;
    private ChestInterface chest;

    BaseMenu(final MenuConfig menu, final ResolverFactory resolverFactory, final Player player, final MCMMOCredits plugin, final String path) {
        this.transformations = new ArrayList<>();
        this.menu = menu;
        this.resolverFactory = resolverFactory;
        this.player = player;
        this.plugin = plugin;
        this.title = Text.fromString(player, menu.string(path + ".info.title"), resolverFactory.fromUsers(player)).toComponent();
        this.rows = menu.integer(path + ".info.size") / 9;
    }

    /**
     * Applies all extraneous items to the menu via {@link TransformContext}. These items typically perform an action.
     */
    public abstract void applySpecialItems();

    /**
     * {@inheritDoc}
     */
    @Override
    public ChestInterface chest() {
        if (this.chest == null) {
            this.load();
        }
        return this.chest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlayerViewer viewer() {
        return PlayerViewer.of(this.player);
    }

    /**
     * Runs a command synchronously for the linked {@link Player}. WARNING: This method is Paper only!
     *
     * @param command the command to execute.
     */
    public void runSyncCommand(final String command) {
        Bukkit.getScheduler().getMainThreadExecutor(this.plugin).execute(() -> Bukkit.dispatchCommand(this.player, command));
    }

    /**
     * If enabled, applies the Navigation item to the menu via {@link TransformContext}. This item returns to the {@link MainMenu} when left-clicked.
     *
     * @see games.cultivate.mcmmocredits.config.MenuConfig
     * @see MainMenu
     */
    public void applyNavigationItem() {
        if (this.menu.bool("all.navigation")) {
            String path = "main.items.navigation";
            ItemStack item = this.menu.item(path, this.player, this.resolverFactory);
            this.transformations.add(TransformContext.of(2, (pane, view) -> {
                pane = pane.element(ItemStackElement.of(item, click -> {
                    if (click.cause().isLeftClick()) {
                        this.runSyncCommand("credits menu main");
                    }
                }), this.menu.slot(path) % 9, this.menu.slot(path) / 9);
                return pane;
            }));
        }
    }

    /**
     * If enabled, applies filler items to the menu via {@link TransformContext}. The items do nothing when clicked.
     *
     * @see games.cultivate.mcmmocredits.config.MenuConfig
     */
    public void applyFillItems() {
        if (this.menu.bool("all.fill")) {
            ItemStack item = this.menu.item("main.items.fill", this.player, this.resolverFactory);
            this.transformations.add(TransformContext.of(1, (pane, view) -> {
                for (var ele : pane.chestElements().entrySet()) {
                    if (ele.getValue().equals(ItemStackElement.empty())) {
                        Vector2 vector = ele.getKey();
                        pane = pane.element(ItemStackElement.of(item), vector.x(), vector.y());
                    }
                }
                return pane;
            }));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load() {
        this.applySpecialItems();
        this.applyNavigationItem();
        this.applyFillItems();
        this.chest = new ChestInterface(this.rows, this.title, this.transformations, List.of(), true, 10, ClickHandler.cancel());
    }
}
