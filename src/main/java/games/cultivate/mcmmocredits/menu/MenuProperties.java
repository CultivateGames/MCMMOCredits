//
// MIT License
//
// Copyright (c) 2024 Cultivate Games
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
import games.cultivate.mcmmocredits.config.Data;
import games.cultivate.mcmmocredits.messages.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@ConfigSerializable
public record MenuProperties(Map<String, Item> items,
                             String title,
                             String command,
                             int size,
                             boolean fill,
                             boolean navigation) implements Data {
    private static final List<String> SKILL_ITEM_LORE = List.of("<yellow><sender>, click here to redeem!");

    public MenuProperties {
        if (size % 9 != 0) {
            throw new IllegalArgumentException("size must be a multiple of 9!");
        }
    }

    public MenuProperties() {
        this(DefaultItems.items(), "Default Menu", "credits menu", 54, true, true);
    }

    public Component parseTitle(final Player player) {
        return Message.of(this.title).parseFor(player);
    }

    private enum DefaultItems {
        ACROBATICS(PrimarySkillType.ACROBATICS, Material.NETHERITE_BOOTS, 10),
        ALCHEMY(PrimarySkillType.ALCHEMY, Material.BREWING_STAND, 11),
        ARCHERY(PrimarySkillType.ARCHERY, Material.BOW, 12),
        AXES(PrimarySkillType.AXES, Material.NETHERITE_AXE, 13),
        EXCAVATION(PrimarySkillType.EXCAVATION, Material.NETHERITE_SHOVEL, 14),
        FISHING(PrimarySkillType.FISHING, Material.FISHING_ROD, 15),
        HERBALISM(PrimarySkillType.HERBALISM, Material.SUGAR_CANE, 16),
        MINING(PrimarySkillType.MINING, Material.NETHERITE_PICKAXE, 19),
        REPAIR(PrimarySkillType.REPAIR, Material.ANVIL, 20),
        SWORDS(PrimarySkillType.SWORDS, Material.NETHERITE_SWORD, 21),
        TAMING(PrimarySkillType.TAMING, Material.LEAD, 23),
        UNARMED(PrimarySkillType.UNARMED, Material.CARROT_ON_A_STICK, 24),
        WOODCUTTING(PrimarySkillType.WOODCUTTING, Material.OAK_LOG, 25),
        NAVIGATION("Navigation", Material.COMPASS, 40),
        FILL("Fill", Material.BLACK_STAINED_GLASS_PANE, -1);

        private final String name;
        private final Material material;
        private final int slot;
        private final PrimarySkillType skill;

        DefaultItems(final String name, final Material material, final int slot) {
            this.name = name;
            this.material = material;
            this.slot = slot;
            this.skill = null;
        }

        DefaultItems(final PrimarySkillType skill, final Material material, final int slot) {
            this.skill = skill;
            this.name = skill.name().substring(0, 1).toUpperCase(Locale.ROOT) + skill.name()
                    .substring(1)
                    .toLowerCase(Locale.ROOT);
            this.material = material;
            this.slot = slot;
        }

        private static Map<String, Item> items() {
            return Arrays.stream(DefaultItems.values())
                    .map(DefaultItems::toDefaultItem)
                    .collect(Collectors.toMap(DefaultItem::name, DefaultItem::toItem));
        }


        public DefaultItem toDefaultItem() {
            return new DefaultItem(this.name, this.material, this.slot, this.skill);
        }
    }

    private record DefaultItem(String name, Material material, int slot, PrimarySkillType skill) {
        public Item toItem() {
            return Item.builder()
                    .lore(SKILL_ITEM_LORE)
                    .name("<yellow>" + this.name)
                    .material(this.material)
                    .slot(this.slot)
                    .build();
        }
    }
}
