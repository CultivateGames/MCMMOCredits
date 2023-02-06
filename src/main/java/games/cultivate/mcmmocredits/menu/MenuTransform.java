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
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.util.ChatQueue;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.core.transform.Transform;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.type.ChestInterface;

/**
 * Represents a {@link TransformContext} and an accompanying {@link Item}.
 * A Transform is an action that is applied to the Menu and its viewers when an update is executed,
 * or when the item is clicked.
 */
public final class MenuTransform {
    private final Item item;
    private final TransformContext<ChestPane, PlayerViewer> transform;

    /**
     * Constructs the object.
     *
     * @param item      The Item.
     * @param transform The TransformContext.
     * @see TransformContext
     */
    private MenuTransform(final Item item, final TransformContext<ChestPane, PlayerViewer> transform) {
        this.item = item;
        this.transform = transform;
    }

    /**
     * Generates a new instance of the Builder.
     *
     * @return A new Builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Gets the Item.
     *
     * @return The item.
     */
    public Item item() {
        return this.item;
    }

    /**
     * Gets the TransformContext.
     *
     * @return The TransformContext.
     */
    public TransformContext<ChestPane, PlayerViewer> context() {
        return this.transform;
    }

    /**
     * Builder for MenuTransform objects.
     */
    public static final class Builder {
        private Item item = Item.of(Material.STONE);
        private Resolver resolver = Resolver.builder().build();
        private int priority = 0;
        private ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer, ClickContext<ChestPane, InventoryClickEvent, PlayerViewer>> handler = ClickHandler.dummy();

        /**
         * Sets the Item.
         *
         * @param item The new item.
         * @return The updated Builder.
         */
        public Builder item(final Item item) {
            this.item = item;
            return this;
        }

        /**
         * Sets the Resolver.
         *
         * @param resolver The new resolver.
         * @return The updated Builder.
         */
        public Builder resolver(final Resolver resolver) {
            this.resolver = resolver;
            return this;
        }

        /**
         * Sets the priority of the transformation.
         *
         * @param priority The priority.
         * @return The updated Builder.
         */
        public Builder priority(final int priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Builds a Redemption Menu Item click. Used to redeem credits via GUI.
         *
         * @param storage Instance of ChatQueue to transport chat messages.
         * @param content Redemption chat prompt.
         * @param plugin  Instance of the plugin to execute the redeem command.
         * @return The updated Builder.
         */
        public Builder redeemClick(final ChatQueue storage, final String content, final MCMMOCredits plugin) {
            String skill = this.item.data();
            this.handler = this.closeInventory().andThen(click -> {
                Player player = click.viewer().player();
                this.resolver.addResolver("skill", WordUtils.capitalizeFully(skill));
                Text.fromString(player, content, this.resolver).send();
                storage.act(player.getUniqueId(), i -> {
                    String command = "credits redeem " + Integer.parseInt(i) + " " + skill.toLowerCase();
                    plugin.executeCommand(player, command);
                });
            });
            return this;
        }

        /**
         * Builds a Config Menu item click. Used to edit config via GUI.
         *
         * @param config  The config to modify.
         * @param storage Instance of ChatQueue to transport chat messages.
         * @param path    The node path being modified.
         * @return The updated Builder.
         */
        public Builder configClick(final Config config, final ChatQueue storage, final Object... path) {
            this.handler = this.closeInventory().andThen(click -> config.modifyInGame(storage, click.viewer().player(), this.resolver, path));
            return this;
        }

        /**
         * Builds a Command Menu item click. Used to execute commands from clicks of an item.
         *
         * @param command The command.
         * @param plugin  The plugin to execute the command.
         * @return The updated Builder.
         */
        public Builder commandClick(final String command, final MCMMOCredits plugin) {
            this.handler = click -> plugin.executeCommand(click.viewer().player(), command);
            return this;
        }

        /**
         * Builds the MenuTransform.
         *
         * @return The built MenuTransform.
         */
        public MenuTransform build() {
            return new MenuTransform(this.item, TransformContext.of(this.priority, this.itemUpdate()));
        }

        /**
         * Common ClickHandler to close the viewer's inventory.
         *
         * @return The ClickHandler.
         */
        private ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer, ClickContext<ChestPane, InventoryClickEvent, PlayerViewer>> closeInventory() {
            return click -> click.viewer().player().closeInventory();
        }

        /**
         * Builds a Transform where the provided item is updated whenever the
         * Menu's underlying {@link ChestInterface} is updated.
         *
         * @return The Transform.
         */
        private Transform<ChestPane, PlayerViewer> itemUpdate() {
            return ((pane, view) -> {
                ItemStack menuItem = this.item.update(view.viewer().player(), this.resolver);
                return pane.element(ItemStackElement.of(menuItem, this.handler), this.item.getX(), this.item.getY());
            });
        }
    }
}

