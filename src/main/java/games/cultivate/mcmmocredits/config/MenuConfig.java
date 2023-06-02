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
import games.cultivate.mcmmocredits.ui.item.BaseItem;
import games.cultivate.mcmmocredits.ui.item.CommandItem;
import games.cultivate.mcmmocredits.ui.item.Item;
import games.cultivate.mcmmocredits.ui.item.RedeemItem;
import games.cultivate.mcmmocredits.ui.menu.BaseMenu;
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
public class MenuConfig extends BaseConfig {
    private BaseMenu main = BaseMenu.of(this.createMainItems(), "<#ff253c><bold>MCMMO Credits", 54, false, false);
    private BaseMenu config = BaseMenu.of(this.createConfigItems(), "<dark_gray>Edit Your Configuration...", 54, false, false);
    private BaseMenu redeem = BaseMenu.of(this.createRedeemItems(), "<dark_gray>Redeem Your Credits...", 45, false, false);

    private Map<String, Item> createMainItems() {
        Map<String, Item> items = new HashMap<>();
        items.put("config", this.createConfigShortcut());
        items.put("redeem", this.createRedeemShortcut());
        items.put("fill", this.createFill());
        items.put("navigation", this.createCompass(49));
        return items;
    }

    private Map<String, Item> createConfigItems() {
        List<String> lore = List.of("<gray>Click here to edit this config option!");
        Map<String, Item> items = new HashMap<>();
        items.put("messages", BaseItem.of(new ItemStack(Material.WRITABLE_BOOK, 1), "", lore, -1));
        items.put("settings", BaseItem.of(new ItemStack(Material.REDSTONE, 1), "", lore, -1));
        items.put("fill", this.createFill());
        items.put("navigation", this.createCompass(49));
        return items;
    }

    private Map<String, Item> createRedeemItems() {
        Map<String, Item> items = new HashMap<>();
        items.put("acrobatics", this.createRedeemItem(Material.NETHERITE_BOOTS, PrimarySkillType.ACROBATICS, 10));
        items.put("alchemy", this.createRedeemItem(Material.BREWING_STAND, PrimarySkillType.ALCHEMY, 11));
        items.put("archery", this.createRedeemItem(Material.BOW, PrimarySkillType.ARCHERY, 12));
        items.put("axes", this.createRedeemItem(Material.NETHERITE_AXE, PrimarySkillType.AXES, 13));
        items.put("excavation", this.createRedeemItem(Material.NETHERITE_SHOVEL, PrimarySkillType.EXCAVATION, 14));
        items.put("fishing", this.createRedeemItem(Material.FISHING_ROD, PrimarySkillType.FISHING, 15));
        items.put("herbalism", this.createRedeemItem(Material.SUGAR_CANE, PrimarySkillType.HERBALISM, 16));
        items.put("mining", this.createRedeemItem(Material.NETHERITE_PICKAXE, PrimarySkillType.MINING, 19));
        items.put("repair", this.createRedeemItem(Material.ANVIL, PrimarySkillType.REPAIR, 20));
        items.put("swords", this.createRedeemItem(Material.NETHERITE_SWORD, PrimarySkillType.SWORDS, 21));
        items.put("taming", this.createRedeemItem(Material.LEAD, PrimarySkillType.TAMING, 23));
        items.put("unarmed", this.createRedeemItem(Material.CARROT_ON_A_STICK, PrimarySkillType.UNARMED, 24));
        items.put("woodcutting", this.createRedeemItem(Material.OAK_LOG, PrimarySkillType.WOODCUTTING, 25));
        items.put("fill", this.createFill());
        items.put("navigation", this.createCompass(40));
        return items;
    }

    /**
     * Creates default fill item.
     *
     * @return The created item.
     */
    private BaseItem createFill() {
        return BaseItem.of(Material.BLACK_STAINED_GLASS_PANE);
    }

    /**
     * Creates default navigation item.
     *
     * @param slot The slot location of the created item.
     * @return The created item.
     */
    private CommandItem createCompass(final int slot) {
        String command = "credits menu main";
        String name = "<red>Previous Menu";
        List<String> lore = List.of("<gray>Left Click to go back!");
        return CommandItem.of(command, BaseItem.of(new ItemStack(Material.COMPASS, 1), name, lore, slot));
    }

    private CommandItem createConfigShortcut() {
        String command = "credits menu config";
        String name = "<#FF253C>Edit Config";
        List<String> lore = List.of("<gray>Left Click to edit config!");
        return CommandItem.of(command, BaseItem.of(new ItemStack(Material.DIAMOND, 1), name, lore, 11));
    }

    private CommandItem createRedeemShortcut() {
        String command = "credits menu redeem";
        String name = "<green>Redeem MCMMO Credits!";
        List<String> lore = List.of("<gray>Left Click to redeem Credits!");
        return CommandItem.of(command, BaseItem.of(new ItemStack(Material.EMERALD, 1), name, lore, 15));
    }

    private RedeemItem createRedeemItem(final Material material, final PrimarySkillType skill, final int slot) {
        List<String> lore = List.of("<yellow><sender>, click here to redeem!");
        Item item = BaseItem.of(new ItemStack(material, 1), "<yellow>" + Util.capitalizeWord(skill.name()), lore, slot);
        return RedeemItem.of(skill, item);
    }
}
