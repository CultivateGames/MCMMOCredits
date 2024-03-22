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
package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.menu.Item;
import games.cultivate.mcmmocredits.menu.Menu;
import games.cultivate.mcmmocredits.menu.RedeemMenu;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"FieldMayBeFinal", "unused", "FieldCanBeLocal"})
@ConfigSerializable
public final class MenuSettings implements Data {
    private String title = "<dark_gray>Redeem Your Credits...";
    private int size = 45;
    private boolean fill = false;
    private boolean navigation = true;
    private String command = "credits menu";
    private Map<String, Item> items = this.createItems();

    public String command() {
        return this.command;
    }

    public String title() {
        return this.title;
    }

    public int size() {
        return this.size;
    }

    public boolean fill() {
        return this.fill;
    }

    public boolean navigation() {
        return this.navigation;
    }

    public Map<String, Item> items() {
        return this.items;
    }

    private Map<String, Item> createItems() {
        Item.Builder builder = Item.builder().lore(List.of("<yellow><sender>, click here to redeem!"));
        Map<String, Item> map = new HashMap<>();
        map.put("acrobatics", builder.name("<yellow>Acrobatics").material(Material.NETHERITE_BOOTS).slot(10).build());
        map.put("alchemy", builder.name("<yellow>Alchemy").material(Material.BREWING_STAND).slot(11).build());
        map.put("archery", builder.name("<yellow>Archery").material(Material.BOW).slot(12).build());
        map.put("axes", builder.name("<yellow>Axes").material(Material.NETHERITE_AXE).slot(13).build());
        map.put("excavation", builder.name("<yellow>Excavation").material(Material.NETHERITE_SHOVEL).slot(14).build());
        map.put("fishing", builder.name("<yellow>Fishing").material(Material.FISHING_ROD).slot(15).build());
        map.put("herbalism", builder.name("<yellow>Herbalism").material(Material.SUGAR_CANE).slot(16).build());
        map.put("mining", builder.name("<yellow>Mining").material(Material.NETHERITE_PICKAXE).slot(19).build());
        map.put("repair", builder.name("<yellow>Repair").material(Material.ANVIL).slot(20).build());
        map.put("swords", builder.name("<yellow>Swords").material(Material.NETHERITE_SWORD).slot(21).build());
        map.put("taming", builder.name("<yellow>Taming").material(Material.LEAD).slot(23).build());
        map.put("unarmed", builder.name("<yellow>Unarmed").material(Material.CARROT_ON_A_STICK).slot(24).build());
        map.put("woodcutting", builder.name("<yellow>Woodcutting").material(Material.OAK_LOG).slot(25).build());
        map.put("navigation", Item.builder().material(Material.COMPASS).name("<red>Previous Menu").lore(List.of("<gray>Left Click to go back!")).slot(40).build());
        map.put("fill", Item.builder().material(Material.BLACK_STAINED_GLASS_PANE).build());
        return map;
    }

    public Menu toMenu() {
        return RedeemMenu.of(this);
    }
}
