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
package games.cultivate.mcmmocredits.actions;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.user.User;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.incendo.interfaces.core.arguments.ArgumentKey;
import org.incendo.interfaces.core.arguments.InterfaceArguments;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * Action which executes a command.
 *
 * @param command The command.
 */
@ConfigSerializable
public record CommandAction(String command) implements Action {
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> ctx) {
        InterfaceArguments args = ctx.view().arguments();
        MCMMOCredits plugin = args.get(ArgumentKey.of("plugin", MCMMOCredits.class));
        User user = args.get(ArgumentKey.of("user", User.class));
        ctx.viewer().close();
        Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.dispatchCommand(user.player(), this.command), 1L);
    }
}
