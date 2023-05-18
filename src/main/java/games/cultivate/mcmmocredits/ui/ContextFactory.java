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
package games.cultivate.mcmmocredits.ui;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.ui.item.Item;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.util.ChatQueue;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Bukkit;
import org.incendo.interfaces.core.transform.Transform;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.transform.PaperTransform;

import javax.inject.Inject;

/**
 * Handles creation of click actions when Items are clicked inside of Menus.
 */
public final class ContextFactory {
    private final ChatQueue queue;
    private final MainConfig config;
    private final MCMMOCredits plugin;

    /**
     * Constructs the object.
     *
     * @param config Config instance, used to run config modification process.
     * @param queue  ChatQueue, used to execute redeem and config modification processes.
     * @param plugin Plugin instance, used to execute processes on the main thread if needed.
     */
    @Inject
    public ContextFactory(final ChatQueue queue, final MainConfig config, final MCMMOCredits plugin) {
        this.queue = queue;
        this.config = config;
        this.plugin = plugin;
    }

    /**
     * Creates a TransformContext for an item where display details are updated, and a ClickHandler is attached to it.
     *
     * @param user The user who will be viewing the item.
     * @param item The item to parse.
     * @return The TransformContext.
     */
    public Transform<ChestPane, PlayerViewer> createContext(final User user, final Item item) {
        int slot = item.slot();
        return PaperTransform.chestItem(() -> ItemStackElement.of(item.parseUser(user), ctx -> item.executeClick(user, this, ctx)), slot % 9, slot / 9);
    }

    /**
     * Redeem credits into the provided skill.
     *
     * @param user  The menu viewer.
     * @param skill The affected skill.
     */
    public void runRedeem(final User user, final PrimarySkillType skill) {
        Resolver resolver = Resolver.ofUser(user);
        resolver.addSkill(skill);
        Text.fromString(user, this.config.getMessage("redeem-prompt"), resolver).send();
        this.queue.act(user.uuid(), i -> this.runCommand(user, String.format("credits redeem %s %s", i, skill)));
    }

    /**
     * Edits a specified config node when the corresponding item is clicked.
     *
     * @param user The menu viewer.
     * @param path The config node to be modified.
     */
    public void runEditConfig(final User user, final Object... path) {
        Resolver resolver = Resolver.ofUser(user);
        resolver.addTag("setting", Util.joinString(".", path));
        Text.fromString(user, this.config.getMessage("edit-config-prompt"), resolver).send();
        this.queue.act(user.uuid(), i -> {
            resolver.addTag("change", i);
            boolean status = this.config.set(i, path);
            Text.fromString(user, this.config.getMessage(status ? "edit-config" : "edit-config-fail"), resolver).send();
        });
    }

    /**
     * Executes commands using the main thread executor.
     *
     * @param user    The menu viewer.
     * @param command The command to be executed.
     */
    public void runCommand(final User user, final String command) {
        Bukkit.getScheduler().getMainThreadExecutor(this.plugin).execute(() -> Bukkit.dispatchCommand(user.player(), command));
    }
}
