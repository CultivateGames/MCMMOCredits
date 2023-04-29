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

import games.cultivate.mcmmocredits.menu.ClickType;
import games.cultivate.mcmmocredits.menu.Item;
import games.cultivate.mcmmocredits.menu.Menu;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration used to adjust properties of all {@link Menu} instances.
 */
@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal, unused"})
public class MenuConfig extends Config {
    private static final Item FILLER_ITEM = Item.of(Material.BLACK_STAINED_GLASS_PANE);
    private static final Item NAVIGATION_ITEM = Util.createCommandItem(Material.COMPASS, "<red>Previous Menu", "<gray>Left Click to go back!", "credits menu main", 40);
    private ConfigMenu config = new ConfigMenu();
    private MainMenu main = new MainMenu();
    private RedeemMenu redeem = new RedeemMenu();

    /**
     * Constructs the configuration.
     */
    public MenuConfig() {
        super(MenuConfig.class, "menus.yml");
    }

    public MenuConfig(final Path path) {
        super(MenuConfig.class, "menus.yml", path);
    }

    /**
     * Config options that customize the Main Menu.
     */
    @ConfigSerializable
    static class MainMenu {
        private String title = "<#ff253c><bold>MCMMO Credits";
        private int slots = 54;
        private boolean fill = false;
        private boolean navigation = false;
        private Map<String, Item> items = new HashMap<>();

        protected MainMenu() {
            this.items.put("config", Util.createCommandItem(Material.DIAMOND, "<#FF253C>Edit Config", "<gray>Left Click to edit config!", "credits menu config", 11));
            this.items.put("redeem", Util.createCommandItem(Material.EMERALD, "<green>Redeem MCMMO Credits!", "<gray>Left Click to redeem Credits!", "credits menu redeem", 15));
            this.items.put("fill", FILLER_ITEM);
            this.items.put("navigation", NAVIGATION_ITEM);
        }
    }

    /**
     * Config options that customize the Configuration Menu.
     */
    @ConfigSerializable
    static class ConfigMenu {
        private String title = "<dark_gray>Edit Your Configuration...";
        private int slots = 54;
        private boolean fill = false;
        private boolean navigation = false;
        private Map<String, Item> items = new HashMap<>();

        protected ConfigMenu() {
            this.items.put("messages", Util.createConfigItem(Material.WRITABLE_BOOK, ClickType.EDIT_MESSAGE));
            this.items.put("settings", Util.createConfigItem(Material.REDSTONE, ClickType.EDIT_SETTING));
            this.items.put("fill", FILLER_ITEM);
            this.items.put("navigation", NAVIGATION_ITEM.withSlot(49));
        }
    }

    /**
     * Config options that customize the Redemption Menu.
     */
    @ConfigSerializable
    static class RedeemMenu {
        private String title = "<dark_gray>Redeem Your Credits...";
        private int slots = 45;
        private boolean fill = false;
        private boolean navigation = false;
        private Map<String, Item> items = new HashMap<>();

        protected RedeemMenu() {
            this.items.put("acrobatics", Util.createRedeemItem(Material.NETHERITE_BOOTS, "ACROBATICS", 10));
            this.items.put("alchemy", Util.createRedeemItem(Material.BREWING_STAND, "ALCHEMY", 11));
            this.items.put("archery", Util.createRedeemItem(Material.BOW, "ARCHERY", 12));
            this.items.put("axes", Util.createRedeemItem(Material.NETHERITE_AXE, "AXES", 13));
            this.items.put("excavation", Util.createRedeemItem(Material.NETHERITE_SHOVEL, "EXCAVATION", 14));
            this.items.put("fishing", Util.createRedeemItem(Material.FISHING_ROD, "FISHING", 15));
            this.items.put("herbalism", Util.createRedeemItem(Material.SUGAR_CANE, "HERBALISM", 16));
            this.items.put("mining", Util.createRedeemItem(Material.NETHERITE_PICKAXE, "MINING", 19));
            this.items.put("repair", Util.createRedeemItem(Material.ANVIL, "REPAIR", 20));
            this.items.put("swords", Util.createRedeemItem(Material.NETHERITE_SWORD, "SWORDS", 21));
            this.items.put("taming", Util.createRedeemItem(Material.LEAD, "TAMING", 23));
            this.items.put("unarmed", Util.createRedeemItem(Material.CARROT_ON_A_STICK, "UNARMED", 24));
            this.items.put("woodcutting", Util.createRedeemItem(Material.OAK_LOG, "WOODCUTTING", 25));
            this.items.put("fill", FILLER_ITEM);
            this.items.put("navigation", NAVIGATION_ITEM);
        }
    }
}
