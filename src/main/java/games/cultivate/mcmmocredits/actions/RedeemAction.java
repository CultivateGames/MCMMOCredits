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

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.events.CreditTransactionEvent;
import games.cultivate.mcmmocredits.transaction.Transaction;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.util.ChatQueue;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.incendo.interfaces.core.arguments.ArgumentKey;
import org.incendo.interfaces.core.arguments.InterfaceArguments;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * Action which redeems credits into a skill via chat input.
 */
@ConfigSerializable
public record RedeemAction(PrimarySkillType skill) implements Action {
    /**
     * Builds a RedeemAction using a string to parse the skill type.
     *
     * @param skill The skill type.
     * @return The action.
     */
    public static RedeemAction of(final String skill) {
        return new RedeemAction(PrimarySkillType.valueOf(skill.toUpperCase()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> ctx) {
        InterfaceArguments args = ctx.view().arguments();
        MainConfig config = args.get(ArgumentKey.of("config", MainConfig.class));
        ChatQueue queue = args.get(ArgumentKey.of("queue", ChatQueue.class));
        User user = args.get(ArgumentKey.of("user", User.class));
        ctx.viewer().close();
        user.sendText(config.getMessage("redeem-prompt"), r -> r.addSkill(this.skill));
        queue.act(user.uuid(), i -> {
            Transaction transaction = Transaction.builder().self(user).skill(this.skill).amount(Integer.parseInt(i)).build();
            Bukkit.getPluginManager().callEvent(new CreditTransactionEvent(transaction, true, false));
        });
    }
}
