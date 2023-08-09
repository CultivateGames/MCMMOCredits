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
package games.cultivate.mcmmocredits.config;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.menu.Item;
import games.cultivate.mcmmocredits.menu.ItemAction;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration used to adjust menu properties.
 */
@SuppressWarnings({"FieldMayBeFinal, unused"})
@ConfigSerializable
public final class MenuData implements Data {
    private String title = "<dark_gray>Redeem Your Credits...";
    private int slots = 45;
    private boolean fill = false;
    private boolean navigation = false;
    private Map<String, Item> items = new HashMap<>();

    public MenuData() {
        this.items.put("acrobatics", this.createRedeemItem(Material.NETHERITE_BOOTS, PrimarySkillType.ACROBATICS, 10));
        this.items.put("alchemy", this.createRedeemItem(Material.BREWING_STAND, PrimarySkillType.ALCHEMY, 11));
        this.items.put("archery", this.createRedeemItem(Material.BOW, PrimarySkillType.ARCHERY, 12));
        this.items.put("axes", this.createRedeemItem(Material.NETHERITE_AXE, PrimarySkillType.AXES, 13));
        this.items.put("excavation", this.createRedeemItem(Material.NETHERITE_SHOVEL, PrimarySkillType.EXCAVATION, 14));
        this.items.put("fishing", this.createRedeemItem(Material.FISHING_ROD, PrimarySkillType.FISHING, 15));
        this.items.put("herbalism", this.createRedeemItem(Material.SUGAR_CANE, PrimarySkillType.HERBALISM, 16));
        this.items.put("mining", this.createRedeemItem(Material.NETHERITE_PICKAXE, PrimarySkillType.MINING, 19));
        this.items.put("repair", this.createRedeemItem(Material.ANVIL, PrimarySkillType.REPAIR, 20));
        this.items.put("swords", this.createRedeemItem(Material.NETHERITE_SWORD, PrimarySkillType.SWORDS, 21));
        this.items.put("taming", this.createRedeemItem(Material.LEAD, PrimarySkillType.TAMING, 23));
        this.items.put("unarmed", this.createRedeemItem(Material.CARROT_ON_A_STICK, PrimarySkillType.UNARMED, 24));
        this.items.put("woodcutting", this.createRedeemItem(Material.OAK_LOG, PrimarySkillType.WOODCUTTING, 25));
        this.items.put("fill", Item.of(Material.BLACK_STAINED_GLASS_PANE));
        this.items.put("navigation", new Item(new ItemStack(Material.COMPASS), "<red>Previous Menu", List.of("<gray>Left Click to go back!"), 40, ItemAction.COMMAND));
    }

    private Item createRedeemItem(final Material material, final PrimarySkillType skill, final int slot) {
        List<String> lore = List.of("<yellow><sender>, click here to redeem!");
        return new Item(new ItemStack(material), "<yellow>" + Util.capitalizeWord(skill.name()), lore, slot, ItemAction.REDEEM);
    }
}
