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

import broccolai.corn.paper.item.PaperItemBuilder;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.GeneralConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.placeholders.ResolverFactory;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.util.InputStorage;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;

import java.util.stream.Stream;

/**
 * {@link Menu} allowing users to edit the configuration and have the changes applied from in-game via chat queue.
 */
public final class ConfigMenu extends BaseMenu {
    private final InputStorage storage;
    private final GeneralConfig config;

    ConfigMenu(final MenuConfig menu, final ResolverFactory resolverFactory, final Player player, final MCMMOCredits plugin, final GeneralConfig config, final InputStorage storage) {
        super(menu, resolverFactory, player, plugin, "editing");
        this.config = config;
        this.storage = storage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applySpecialItems() {
        this.transformations.add(TransformContext.of(3, (pane, view) -> {
            int slot = 1;
            for (String path : this.config.nodes().keySet().stream().sorted().toList()) {
                if (Stream.of("mysql", "item", "database").noneMatch(path::contains)) {
                    String display = path.substring(0, path.indexOf('.'));
                    pane = this.createConfigTransform(pane, path, display, slot);
                    slot++;
                }
            }
            return pane;
        }));
    }

    /**
     * Used to transform a {@link ChestPane} to allow users to edit the configuration via chat queue when added menu items are left-clicked.
     *
     * @param pane   The current {@link ChestPane}
     * @param path   The configuration path to be changed.
     * @param type   The configuration section to be changed.
     * @param amount number representing the quantity of the {@link ItemStack}. Number is changed to help differentiate between options.
     * @return The modified {@link ChestPane}
     */
    private ChestPane createConfigTransform(final ChestPane pane, final String path, final String type, final int amount) {
        ItemStack item = this.itemFromPath(path, type).amount(amount).build();
        int slot = item.getAmount() - 1;
        return pane.element(ItemStackElement.of(item, click -> {
            if (click.cause().isLeftClick()) {
                this.close();
                Resolver.Builder resolver = this.resolverFactory.builder().users(this.player).tag("setting", path);
                Text.fromString(this.player, this.config.string("menuEditingPrompt"), resolver.build()).send();
                this.storage.act(this.player.getUniqueId(), i -> {
                    //Modify config, set message content based on result.
                    String content = "settingChange" + (this.config.modify(path, i) ? "Successful" : "Failure");
                    Text.fromString(this.player, this.config.string(content), resolver.tag("change", i).build()).send();
                });
            }
        }), slot % 9, slot / 9);
    }

    private PaperItemBuilder itemFromPath(final String path, final String type) {
        return PaperItemBuilder.of(this.menu.item("editing." + type, this.player, this.resolverFactory))
                .name(Component.text(path.substring(path.lastIndexOf('.') + 1)));
    }
}
