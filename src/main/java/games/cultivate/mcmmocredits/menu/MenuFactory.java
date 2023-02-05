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
import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.util.InputStorage;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.type.ChestInterface;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Factory which is used to create {@link Menu} instances.
 */
public final class MenuFactory {
    private static final int CONFIG_PRIORITY = 0;
    private static final int EMPTY_PRIORITY = 3;
    private static final int REDEEM_PRIORITY = 1;
    private static final int COMMAND_PRIORITY = 2;
    private final MenuConfig menus;
    private final InputStorage storage;
    private final MainConfig config;
    private final MCMMOCredits plugin;

    @Inject
    public MenuFactory(final MenuConfig menus, final MainConfig config, final InputStorage storage, final MCMMOCredits plugin) {
        this.menus = menus;
        this.config = config;
        this.storage = storage;
        this.plugin = plugin;
    }

    public ChestInterface buildMenu(final User user, final String path) {
        Menu menu = this.menus.getMenu(path.toLowerCase());
        if (path.contains("config")) {
            this.addConfigItems(menu);
        }
        this.applyFill(menu, path);
        List<TransformContext<ChestPane, PlayerViewer>> transforms = menu.items().stream().map(x -> this.getTransform(x, user.resolver()).context()).toList();
        return menu.createInterface(transforms, user);
    }

    private MenuTransform getTransform(final Item item, final Resolver resolver) {
        return switch (item.type()) {
            case FILL -> this.emptyTransform(item, resolver);
            case REDEEM -> this.redeemTransform(item, resolver);
            case MAIN_MENU, CONFIG_MENU, REDEEM_MENU -> this.commandTransform(item, resolver);
            case EDIT_SETTING, EDIT_MESSAGE -> this.configTransform(item, resolver);
        };
    }

    private void applyFill(final Menu menu, final String path) {
        Set<Integer> allSlots = IntStream.range(0, menu.slots()).boxed().collect(Collectors.toSet());
        Set<Integer> itemSlots = menu.items().stream().map(Item::slot).collect(Collectors.toSet());
        allSlots.removeAll(itemSlots);
        Item fill = this.menus.getItem(path, "items", "fill");
        allSlots.forEach(x -> menu.items().add(fill.toBuilder().slot(x).build()));
    }

    private void addConfigItems(final Menu menu) {
        List<String> keys = this.config.filterKeys("mysql", "database");
        menu.items().removeIf(x -> x.slot() >= 0 && x.slot() <= keys.size());
        for (int i = 0; i < keys.size(); i++) {
            String string = keys.get(i);
            ItemType type = string.contains(".") ? ItemType.EDIT_SETTING : ItemType.EDIT_MESSAGE;
            Item item = menu.findFirstOfType(type);
            menu.items().add(item.toBuilder().name(string).slot(i).build());
        }
    }

    private MenuTransform configTransform(final Item item, final Resolver resolver) {
        return MenuTransform.builder()
                .item(item)
                .resolver(resolver)
                .priority(CONFIG_PRIORITY)
                .configClick(this.config, this.storage, (Object[]) item.name().split("\\."))
                .build();
    }

    private MenuTransform emptyTransform(final Item item, final Resolver resolver) {
        return MenuTransform.builder()
                .item(item)
                .resolver(resolver)
                .priority(EMPTY_PRIORITY)
                .build();
    }

    private MenuTransform redeemTransform(final Item item, final Resolver resolver) {
        return MenuTransform.builder()
                .item(item)
                .resolver(resolver)
                .priority(REDEEM_PRIORITY)
                .redeemClick(this.storage, this.config.string("redeem-prompt"), this.plugin)
                .build();
    }

    private MenuTransform commandTransform(final Item item, final Resolver resolver) {
        String command = "credits menu " + item.type().toString().split("_")[0].toLowerCase();
        return MenuTransform.builder()
                .item(item)
                .resolver(resolver)
                .priority(COMMAND_PRIORITY)
                .commandClick(command, this.plugin)
                .build();
    }
}
