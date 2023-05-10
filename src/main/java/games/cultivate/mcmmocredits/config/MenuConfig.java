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
import games.cultivate.mcmmocredits.ui.menu.Menu;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration used to adjust properties of all {@link Menu} instances.
 */
@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal, unused"})
public class MenuConfig extends Config {
    private ConfigMenu config = new ConfigMenu();
    private MainMenu main = new MainMenu();
    private RedeemMenu redeem = new RedeemMenu();

    /**
     * Constructs the configuration.
     */
    public MenuConfig() {
        super(MenuConfig.class, "menus.yml");
    }

    MenuConfig(final Path path) {
        super(MenuConfig.class, "menus.yml", path);
    }

    private static BaseItem createFill() {
        return BaseItem.of(Material.BLACK_STAINED_GLASS_PANE);
    }

    private static CommandItem createCompass(final int slot) {
        String command = "credits menu main";
        String name = "<red>Previous Menu";
        List<String> lore = List.of("<gray>Left Click to go back!");
        return CommandItem.of(command, BaseItem.of(new ItemStack(Material.COMPASS, 1), name, lore, slot));
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
            this.items.put("config", createConfigShortcut());
            this.items.put("redeem", createRedeemShortcut());
            this.items.put("fill", createFill());
            this.items.put("navigation", createCompass(40));
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
            this.items.put("messages", createConfigItem(Material.WRITABLE_BOOK));
            this.items.put("settings", createConfigItem(Material.REDSTONE));
            this.items.put("fill", createFill());
            this.items.put("navigation", createCompass(49));
        }
        private BaseItem createConfigItem(final Material material) {
            return BaseItem.of(new ItemStack(material, 1), "", List.of("<gray>Click here to edit this config option!"), -1);
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
            this.items.put("acrobatics", createRedeemItem(Material.NETHERITE_BOOTS, "ACROBATICS", 10));
            this.items.put("alchemy", createRedeemItem(Material.BREWING_STAND, "ALCHEMY", 11));
            this.items.put("archery", createRedeemItem(Material.BOW, "ARCHERY", 12));
            this.items.put("axes", createRedeemItem(Material.NETHERITE_AXE, "AXES", 13));
            this.items.put("excavation", createRedeemItem(Material.NETHERITE_SHOVEL, "EXCAVATION", 14));
            this.items.put("fishing", createRedeemItem(Material.FISHING_ROD, "FISHING", 15));
            this.items.put("herbalism", createRedeemItem(Material.SUGAR_CANE, "HERBALISM", 16));
            this.items.put("mining", createRedeemItem(Material.NETHERITE_PICKAXE, "MINING", 19));
            this.items.put("repair", createRedeemItem(Material.ANVIL, "REPAIR", 20));
            this.items.put("swords", createRedeemItem(Material.NETHERITE_SWORD, "SWORDS", 21));
            this.items.put("taming", createRedeemItem(Material.LEAD, "TAMING", 23));
            this.items.put("unarmed", createRedeemItem(Material.CARROT_ON_A_STICK, "UNARMED", 24));
            this.items.put("woodcutting", createRedeemItem(Material.OAK_LOG, "WOODCUTTING", 25));
            this.items.put("fill", createFill());
            this.items.put("navigation", createCompass(40));
        }

        private static RedeemItem createRedeemItem(final Material material, final String skill, final int slot) {
            List<String> lore = List.of("<yellow><sender>, click here to redeem!");
            Item item = BaseItem.of(new ItemStack(material, 1), "<yellow>" + Util.capitalizeWord(skill), lore, slot);
            return RedeemItem.of(PrimarySkillType.valueOf(skill), item);
        }
    }
}
