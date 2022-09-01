//
// MIT License
//
// Copyright (c) 2022 Cultivate Games
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
import com.gmail.nossr50.util.skills.SkillTools;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.GeneralConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.placeholders.ResolverFactory;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.util.InputStorage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.paper.element.ItemStackElement;

/**
 * {@link Menu} instance where users will exchange Credits for levels applied towards {@link PrimarySkillType}.
 */
public final class RedeemMenu extends BaseMenu {
    private final InputStorage storage;
    private final GeneralConfig config;

    RedeemMenu(final MenuConfig menu, final ResolverFactory resolverFactory, final Player player, final MCMMOCredits plugin, final GeneralConfig config, final InputStorage storage) {
        super(menu, resolverFactory, player, plugin, "redeem");
        this.config = config;
        this.storage = storage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applySpecialItems() {
        this.transformations.add(TransformContext.of(3, (pane, view) -> {
            for (PrimarySkillType skill : PrimarySkillType.values()) {
                if (SkillTools.isChildSkill(skill)) {
                    continue;
                }
                String path = "redeem.items." + skill.name().toLowerCase();
                ItemStack item = this.menu.item(path, this.player, this.resolverFactory);
                int slot = this.menu.slot(path);
                pane = pane.element(ItemStackElement.of(item, click -> {
                    if (click.cause().isLeftClick()) {
                        this.close();
                        TagResolver resolver = this.resolverFactory.builder().users(this.player).skill(skill).build();
                        Text.fromString(this.player, this.config.string("menuRedeemPrompt"), resolver).send();
                        this.storage.act(this.player.getUniqueId(), i -> {
                            String command = "credits redeem %d %s";
                            this.runSyncCommand(command.formatted(Integer.parseInt(i), path.substring(path.lastIndexOf('.') + 1)));
                        });
                    }
                }), slot % 9, slot / 9);
            }
            return pane;
        }));
    }
}

