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

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.MainData;
import games.cultivate.mcmmocredits.events.CreditTransactionEvent;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.transaction.Transaction;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.util.ChatQueue;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.incendo.interfaces.core.arguments.ArgumentKey;
import org.incendo.interfaces.core.arguments.InterfaceArguments;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * Represents an action that is executed after an item is clicked.
 */
public interface Action extends ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer, ClickContext<ChestPane, InventoryClickEvent, PlayerViewer>> {

    /**
     * Returns an Action that does nothing.
     *
     * @return Action that does nothing.
     */
    static Action nothing() {
        return x -> {
        };
    }

    static Action redeem(final PrimarySkillType skill) {
        return ctx -> {
            InterfaceArguments args = ctx.view().arguments();
            Config<MainData> config = args.get(ArgumentKey.of("config", Config.class));
            ChatQueue queue = args.get(ArgumentKey.of("queue", ChatQueue.class));
            User user = args.get(ArgumentKey.of("user", User.class));
            MCMMOCredits plugin = args.get(ArgumentKey.of("plugin", MCMMOCredits.class));
            ctx.viewer().close();
            user.sendText(config.getMessage("redeem-prompt"), r -> r.addSkill(skill));
            queue.act(user.uuid(), i -> Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Transaction transaction = Transaction.builder().self(user).skill(skill).amount(Integer.parseInt(i)).build();
                Bukkit.getPluginManager().callEvent(new CreditTransactionEvent(transaction, true, false));
            }, 1L));
        };
    }

    static Action editConfig(final NodePath path) {
        return ctx -> {
            InterfaceArguments args = ctx.view().arguments();
            User user = args.get(ArgumentKey.of("user", User.class));
            Config<MainData> config = args.get(ArgumentKey.of("config", Config.class));
            ChatQueue queue = args.get(ArgumentKey.of("queue", ChatQueue.class));
            Resolver resolver = Resolver.ofUser(user).addTag("setting", Util.joinString(".", path.array()));
            ctx.viewer().close();
            user.sendText(config.getMessage("edit-config-prompt"), resolver);
            queue.act(user.uuid(), i -> {
                boolean status = config.set(i, path);
                user.sendText(config.getMessage(status ? "edit-config" : "edit-config-fail"), resolver.addTag("change", i));
            });
        };
    }

    /**
     * Action which executes a command.
     *
     * @param command The command.
     */
    @ConfigSerializable
    record Command(String command) implements Action {
        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> ctx) {
            InterfaceArguments args = ctx.view().arguments();
            MCMMOCredits plugin = args.get(ArgumentKey.of("plugin", MCMMOCredits.class));
            User user = args.get(ArgumentKey.of("user", User.class));
            ctx.viewer().close();
            Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.dispatchCommand(user.player(), this.command), 1L);
        }
    }
}
