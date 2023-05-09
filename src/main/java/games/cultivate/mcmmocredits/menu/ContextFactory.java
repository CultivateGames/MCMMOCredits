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
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.util.ChatQueue;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.transform.PaperTransform;

import javax.inject.Inject;

/**
 * Used to encapsulate building {@link ClickHandler} instances.
 */
public final class ContextFactory {
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
    public ContextFactory(final ChatQueue queue, final MainConfig config, final MCMMOCredits plugin) {
        this.queue = queue;
        this.config = config;
        this.plugin = plugin;
    }

    /**
     * Provides a TransformContext object for the item
     * where name and lore are updated, and a click handler is attached to it.
     *
     * @return The TransformContext.
     */
    public TransformContext<ChestPane, PlayerViewer> createContext(final User user, final Item item) {
        int slot = item.slot();
        return TransformContext.of(0, PaperTransform.chestItem(() -> ItemStackElement.of(item.parseUser(user), this.getClick(user, item)), slot % 9, slot / 9));
    }

    /**
     * Builds a ClickHandler that will redeem credits into the button specified skill.
     *
     * @param user  The user.
     * @param skill the skill name to be redeemed (if applicable).
     * @return The ClickHandler.
     */
    private ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer,
            ClickContext<ChestPane, InventoryClickEvent, PlayerViewer>> buildRedeemClick(final User user, final String skill) {
        return click -> {
            click.viewer().close();
            Resolver resolver = Resolver.ofUser(user);
            resolver.addSkill(skill);
            Text.fromString(user, this.config.getMessage("redeem-prompt"), resolver).send();
            this.queue.act(user.uuid(), i -> this.executeCommand(user, String.format("credits redeem %s %s", i, skill)));
        };
    }

    /**
     * Builds a ClickHandler that will edit a specified config node when the corresponding button is clicked.
     *
     * @param user The user.
     * @param path the path to the configuration setting or message to be edited
     * @return The ClickHandler.
     */
    private ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer,
            ClickContext<ChestPane, InventoryClickEvent, PlayerViewer>> buildConfigClick(final User user, final Object... path) {
        return click -> {
            click.viewer().close();
            Resolver resolver = Resolver.ofUser(user);
            resolver.addStringTag("setting", Util.joinString(".", path));
            Text.fromString(user, this.config.getMessage("edit-config-prompt"), resolver).send();
            this.queue.act(user.uuid(), i -> {
                resolver.addStringTag("change", i);
                boolean status = this.config.set(i, path);
                Text.fromString(user, this.config.getMessage(status ? "edit-config" : "edit-config-fail"), resolver).send();
            });
        };
    }

    /**
     * Executes commands using the main thread executor as a workaround to Cloud.
     *
     * @param user    The User who dispatches the command.
     * @param command The command.
     */
    private void executeCommand(final User user, final String command) {
        Bukkit.getScheduler().getMainThreadExecutor(this.plugin).execute(() -> Bukkit.dispatchCommand(user.player(), command));
    }

    /**
     * Builds ClickHandler based on click type of the provided item.
     *
     * @param user The user to parse against.
     * @param item The item.
     * @return The ClickHandler.
     */
    private ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer,
            ClickContext<ChestPane, InventoryClickEvent, PlayerViewer>> getClick(final User user, final Item item) {
        return switch (item.clickType()) {
            case EDIT_MESSAGE, EDIT_SETTING -> this.buildConfigClick(user, (Object[]) item.name().split("\\."));
            case REDEEM -> this.buildRedeemClick(user, item.data());
            case COMMAND -> click -> this.executeCommand(user, item.data());
            default -> ClickHandler.cancel();
        };
    }
}
