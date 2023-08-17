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

    /**
     * Constructs the object. Adds items to the default item map.
     */
    public MenuData() {
        List<String> lore = List.of("<yellow><sender>, click here to redeem!");
        this.createRedeemItem(Material.NETHERITE_BOOTS, PrimarySkillType.ACROBATICS, lore, 10);
        this.createRedeemItem(Material.BREWING_STAND, PrimarySkillType.ALCHEMY, lore, 11);
        this.createRedeemItem(Material.BOW, PrimarySkillType.ARCHERY, lore, 12);
        this.createRedeemItem(Material.NETHERITE_AXE, PrimarySkillType.AXES, lore, 13);
        this.createRedeemItem(Material.NETHERITE_SHOVEL, PrimarySkillType.EXCAVATION, lore, 14);
        this.createRedeemItem(Material.FISHING_ROD, PrimarySkillType.FISHING, lore, 15);
        this.createRedeemItem(Material.SUGAR_CANE, PrimarySkillType.HERBALISM, lore, 16);
        this.createRedeemItem(Material.NETHERITE_PICKAXE, PrimarySkillType.MINING, lore, 19);
        this.createRedeemItem(Material.ANVIL, PrimarySkillType.REPAIR, lore, 20);
        this.createRedeemItem(Material.NETHERITE_SWORD, PrimarySkillType.SWORDS, lore, 21);
        this.createRedeemItem(Material.LEAD, PrimarySkillType.TAMING, lore, 23);
        this.createRedeemItem(Material.CARROT_ON_A_STICK, PrimarySkillType.UNARMED, lore, 24);
        this.createRedeemItem(Material.OAK_LOG, PrimarySkillType.WOODCUTTING, lore, 25);
        this.items.put("fill", Item.of(Material.BLACK_STAINED_GLASS_PANE));
        this.items.put("navigation", new Item(new ItemStack(Material.COMPASS), "<red>Previous Menu", List.of("<gray>Left Click to go back!"), 40, ItemAction.COMMAND));
    }

    private void createRedeemItem(final Material material, final PrimarySkillType skill, final List<String> lore, final int slot) {
        this.items.put(skill.name().toLowerCase(), new Item(new ItemStack(material), "<yellow>" + Util.capitalizeWord(skill.name()), lore, slot, ItemAction.REDEEM));
    }
}
