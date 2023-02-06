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
import games.cultivate.mcmmocredits.menu.ItemType;
import games.cultivate.mcmmocredits.menu.Menu;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

/**
 * Configuration used to adjust properties of all {@link Menu} instances.
 */
@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal, unused"})
public class MenuConfig extends Config {
    private static final String HEADER = """
            Repository: https://github.com/CultivateGames/MCMMOCredits
            Item Name and Lore can parse MiniMessage and PlaceholderAPI. Info: https://docs.adventure.kyori.net/minimessage/index.html
                        
            Properties:
            title: Title of the inventory.
            slot: Number of slots in the menu. This can be 9, 18, 27, 36, 45, or 54. Must also be large enough to fit all items.
            fill: Whether to fill empty spots in menu with filler item stacks.
            navigation: Whether to add a navigation button in menu. Goes back to main menu (/credits menu main).
                        
            Items:
            fill: Customizes the filler item. Only shows if enabled. Slot is unused.
            navigation: Customizes the navigation item. Only shows if enabled.
            main.config: Customizes item that takes you to Edit Config GUI.
            main.redemption: Customizes item that takes you to Redemption GUI.
            config.messages: Customizes item that shows in Edit Config GUI for editable messages. Amount + Slot are unused.
            config.settings: Customizes item that shows in Edit Config GUI for editable settings. Amount + Slot are unused.
            redeem: Customizes item that shows for each skill in the Redemption GUI.
            """;
    private static final Item FILLER_ITEM = Item.of(Material.BLACK_STAINED_GLASS_PANE);
    private static final Item NAVIGATION_ITEM = commandItem(Material.COMPASS, "<red>Previous Menu", "<gray>Left Click to go back!", ItemType.MAIN_MENU, 40);
    private ConfigMenu config = new ConfigMenu();
    private MainMenu main = new MainMenu();
    private RedeemMenu redeem = new RedeemMenu();

    /**
     * Constructs the configuration.
     */
    MenuConfig() {
        super(MenuConfig.class, "menus.yml", HEADER);
    }

    private static Item commandItem(final Material material, final String name, final String lore, final ItemType type, final int slot) {
        return Item.builder()
                .item(new ItemStack(material, 1))
                .name(name)
                .lore(List.of(lore))
                .slot(slot)
                .type(type)
                .build();
    }

    /**
     * Config options that customize the Main Menu.
     */
    @ConfigSerializable
    static class MainMenu {
        private MenuProperties properties = new MenuProperties("<#ff253c><bold>MCMMO Credits", 54, false, false);
        private Items items = new Items();

        @ConfigSerializable
        static class Items {
            private Item config = commandItem(Material.DIAMOND, "<#FF253C>Edit Config", "<gray>Left Click to edit config!", ItemType.CONFIG_MENU, 11);
            private Item redemption = commandItem(Material.EMERALD, "<green>Redeem MCMMO Credits!", "<gray>Left Click to redeem Credits!", ItemType.REDEEM_MENU, 15);
            private Item fill = FILLER_ITEM;
            private Item navigation = NAVIGATION_ITEM;
        }
    }

    /**
     * Config options that customize the Configuration Menu.
     */
    @ConfigSerializable
    static class ConfigMenu {
        private MenuProperties properties = new MenuProperties("<dark_gray>Edit Your Configuration...", 54, false, false);
        private Items items = new Items();

        @ConfigSerializable
        static class Items {
            private Item messages = this.configItem(Material.WRITABLE_BOOK, ItemType.EDIT_MESSAGE);
            private Item settings = this.configItem(Material.REDSTONE, ItemType.EDIT_SETTING);
            private Item fill = FILLER_ITEM;
            private Item navigation = NAVIGATION_ITEM.toBuilder().slot(49).build();

            private Item configItem(final Material material, final ItemType type) {
                List<String> lore = List.of("<gray>Click here to edit this config option!");
                return Item.builder().item(new ItemStack(material, 1)).slot(-1).type(type).lore(lore).build();
            }
        }
    }

    /**
     * Config options that customize the Redemption Menu.
     */
    @ConfigSerializable
    static class RedeemMenu {
        private MenuProperties properties = new MenuProperties("<dark_gray>Redeem Your Credits...", 45, false, false);
        private Items items = new Items();

        @ConfigSerializable
        static class Items {
            private Item acrobatics = this.redeemItem(Material.NETHERITE_BOOTS, PrimarySkillType.ACROBATICS, 10);
            private Item alchemy = this.redeemItem(Material.BREWING_STAND, PrimarySkillType.ALCHEMY, 11);
            private Item archery = this.redeemItem(Material.BOW, PrimarySkillType.ARCHERY, 12);
            private Item axes = this.redeemItem(Material.NETHERITE_AXE, PrimarySkillType.AXES, 13);
            private Item excavation = this.redeemItem(Material.NETHERITE_SHOVEL, PrimarySkillType.EXCAVATION, 14);
            private Item fishing = this.redeemItem(Material.FISHING_ROD, PrimarySkillType.FISHING, 15);
            private Item herbalism = this.redeemItem(Material.SUGAR_CANE, PrimarySkillType.HERBALISM, 16);
            private Item mining = this.redeemItem(Material.NETHERITE_PICKAXE, PrimarySkillType.MINING, 19);
            private Item repair = this.redeemItem(Material.ANVIL, PrimarySkillType.REPAIR, 20);
            private Item swords = this.redeemItem(Material.NETHERITE_SWORD, PrimarySkillType.SWORDS, 21);
            private Item taming = this.redeemItem(Material.LEAD, PrimarySkillType.TAMING, 23);
            private Item unarmed = this.redeemItem(Material.CARROT_ON_A_STICK, PrimarySkillType.UNARMED, 24);
            private Item woodcutting = this.redeemItem(Material.OAK_LOG, PrimarySkillType.WOODCUTTING, 25);
            private Item fill = FILLER_ITEM;
            private Item navigation = NAVIGATION_ITEM;

            private Item redeemItem(final Material material, final PrimarySkillType skill, final int slot) {
                ItemStack item = new ItemStack(material, 1);
                String name = "<yellow>" + WordUtils.capitalizeFully(skill.name());
                List<String> lore = List.of("<yellow><sender>, click here to redeem!");
                return Item.builder().item(item).name(name).lore(lore).type(ItemType.REDEEM).slot(slot).build();
            }
        }
    }

    /**
     * Properties that are common among all Menus.
     *
     * @param title      Title of the menu. Does not update.
     * @param slots      Size of the menu.
     * @param fill       Whether the Menu's empty spaces should be filled.
     * @param navigation If navigation item should be added to menu.
     */
    @ConfigSerializable
    public record MenuProperties(@NotNull String title, int slots, boolean fill, boolean navigation) {
    }
}
