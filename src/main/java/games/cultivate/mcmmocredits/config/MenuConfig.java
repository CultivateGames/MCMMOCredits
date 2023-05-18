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
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration used to adjust menu properties.
 */
@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal, unused"})
public class MenuConfig extends Config {
    private ConfigMenuSettings config = new ConfigMenuSettings();
    private MainMenuSettings main = new MainMenuSettings();
    private RedeemMenuSettings redeem = new RedeemMenuSettings();

    /**
     * Constructs the object with sane defaults.
     */
    public MenuConfig() {
        super(MenuConfig.class, "menus.yml");
    }

    /**
     * Constructs the object with a custom file path.
     *
     * @param path A customized file path.
     */
    MenuConfig(final Path path) {
        super(MenuConfig.class, "menus.yml", path);
    }

    /**
     * Creates default fill item.
     *
     * @return The created item.
     */
    private static BaseItem createFill() {
        return BaseItem.of(Material.BLACK_STAINED_GLASS_PANE);
    }

    /**
     * Creates default navigation item.
     *
     * @param slot The slot location of the created item.
     * @return The created item.
     */
    private static CommandItem createCompass(final int slot) {
        String command = "credits menu main";
        String name = "<red>Previous Menu";
        List<String> lore = List.of("<gray>Left Click to go back!");
        return CommandItem.of(command, BaseItem.of(new ItemStack(Material.COMPASS, 1), name, lore, slot));
    }

    /**
     * Settings used to modify the Main Menu (/credits menu main).
     */
    @ConfigSerializable
    static final class MainMenuSettings {
        private String title = "<#ff253c><bold>MCMMO Credits";
        private int slots = 54;
        private boolean fill = false;
        private boolean navigation = false;
        private Map<String, Item> items = new HashMap<>();

        private MainMenuSettings() {
            this.items.put("config", this.createConfigShortcut());
            this.items.put("redeem", this.createRedeemShortcut());
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
     * Settings used to modify the Config Menu (/credits menu config).
     */
    @ConfigSerializable
    static final class ConfigMenuSettings {
        private String title = "<dark_gray>Edit Your Configuration...";
        private int slots = 54;
        private boolean fill = false;
        private boolean navigation = false;
        private Map<String, Item> items = new HashMap<>();

        private ConfigMenuSettings() {
            this.items.put("messages", this.createConfigItem(Material.WRITABLE_BOOK));
            this.items.put("settings", this.createConfigItem(Material.REDSTONE));
            this.items.put("fill", createFill());
            this.items.put("navigation", createCompass(49));
        }

        private BaseItem createConfigItem(final Material material) {
            return BaseItem.of(new ItemStack(material, 1), "", List.of("<gray>Click here to edit this config option!"), -1);
        }
    }

    /**
     * Settings used to modify the Redeem Menu (/credits menu redeem).
     */
    @ConfigSerializable
    static final class RedeemMenuSettings {
        private String title = "<dark_gray>Redeem Your Credits...";
        private int slots = 45;
        private boolean fill = false;
        private boolean navigation = false;
        private Map<String, Item> items = new HashMap<>();

        private RedeemMenuSettings() {
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
