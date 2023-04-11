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
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.util.ChatQueue;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.pane.ChestPane;

import javax.inject.Inject;

/**
 * Used to encapsulate building {@link ClickHandler} instances.
 */
public final class ClickFactory {
    private final ChatQueue queue;
    private final MainConfig config;
    private final MCMMOCredits plugin;

    /**
     * Constructs the object.
     *
     * @param config Instance of the MainConfig.
     * @param queue  Instance of the ChatQueue.
     * @param plugin Instance of the plugin.
     */
    @Inject
    public ClickFactory(final ChatQueue queue, final MainConfig config, final MCMMOCredits plugin) {
        this.queue = queue;
        this.config = config;
        this.plugin = plugin;
    }

    private ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer,
            ClickContext<ChestPane, InventoryClickEvent, PlayerViewer>> buildRedeemClick(final Resolver resolver, final String skill) {
        return this.closeInventory().andThen(click -> {
            resolver.addSkill(skill);
            Player player = click.viewer().player();
            Text.fromString(player, this.config.getMessage("redeem-prompt"), resolver).send();
            this.queue.act(player.getUniqueId(), i -> this.executeCommand(player, String.format("credits redeem %d %s", Integer.parseInt(i), skill.toLowerCase())));
        });
    }

    private ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer,
            ClickContext<ChestPane, InventoryClickEvent, PlayerViewer>> buildCommandClick(final String command) {
        return click -> this.executeCommand(click.viewer().player(), command);
    }

    private ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer,
            ClickContext<ChestPane, InventoryClickEvent, PlayerViewer>> buildConfigClick(final Resolver resolver, final Object... path) {
        return this.closeInventory().andThen(click -> {
            resolver.addStringTag("setting", Util.joinString(".", path));
            Player player = click.viewer().player();
            Text.fromString(player, this.config.getMessage("edit-config-prompt"), resolver).send();
            this.queue.act(player.getUniqueId(), i -> {
                boolean status = this.config.set(i, path);
                resolver.addStringTag("change", i);
                Text.fromString(player, this.config.getMessage(status ? "edit-config" : "edit-config-fail"), resolver).send();
            });
        });
    }

    /**
     * Common ClickHandler to close the viewer's inventory.
     *
     * @return The ClickHandler.
     */
    private ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer,
            ClickContext<ChestPane, InventoryClickEvent, PlayerViewer>> closeInventory() {
        return click -> click.viewer().player().closeInventory();
    }

    /**
     * Executes commands using the main thread executor as a workaround to Cloud.
     *
     * @param player  The player who dispatches the command.
     * @param command The command.
     */
    private void executeCommand(final Player player, final String command) {
        Bukkit.getScheduler().getMainThreadExecutor(this.plugin).execute(() -> Bukkit.dispatchCommand(player, command));
    }

    public ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer,
            ClickContext<ChestPane, InventoryClickEvent, PlayerViewer>> getClick(final ClickTypes type, final String data, final Resolver resolver) {
       return switch (type) {
            case FILL -> ClickHandler.cancel();
            case REDEEM -> this.buildRedeemClick(resolver, data);
            case COMMAND -> this.buildCommandClick(data);
            case EDIT_SETTING, EDIT_MESSAGE -> this.buildConfigClick(resolver, (Object[]) data.split("\\."));
        };
    }
}
