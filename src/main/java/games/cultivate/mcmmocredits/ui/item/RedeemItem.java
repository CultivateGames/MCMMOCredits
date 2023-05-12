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
package games.cultivate.mcmmocredits.ui.item;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.ui.ContextFactory;
import games.cultivate.mcmmocredits.user.User;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.pane.ChestPane;

import java.util.List;

/**
 * Represents an Item that starts a credit redemption when clicked.
 */
public final class RedeemItem extends BaseItem {
    private final PrimarySkillType skill;

    /**
     * Constructs the object.
     *
     * @param skill The skill to use in a redemption.
     * @param stack The representative ItemStack. Updated with refreshing name/lore.
     * @param name  Raw name of the item. Always parsed.
     * @param lore  Raw lore of the item. Always parsed.
     * @param slot  Location of item in a Menu.
     */
    private RedeemItem(final PrimarySkillType skill, final ItemStack stack, final String name, final List<String> lore, final int slot) {
        super(stack, name, lore, slot);
        this.skill = skill;
    }

    /**
     * Constructs the object from an existing Item.
     *
     * @param skill The skill to use in a redemption.
     * @param item  The item to be used for pass-through.
     */
    public static RedeemItem of(final PrimarySkillType skill, final Item item) {
        return new RedeemItem(skill, item.stack(), item.name(), item.lore(), item.slot());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeClick(final User user, final ContextFactory factory, final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> ctx) {
        ctx.viewer().close();
        factory.runRedeem(user, this.skill);
    }
}
