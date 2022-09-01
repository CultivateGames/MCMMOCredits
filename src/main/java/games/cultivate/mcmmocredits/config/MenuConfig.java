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
package games.cultivate.mcmmocredits.config;

import broccolai.corn.paper.item.PaperItemBuilder;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.placeholders.ResolverFactory;
import games.cultivate.mcmmocredits.serializers.ItemSerializer;
import games.cultivate.mcmmocredits.text.Text;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;

/**
 * Object that represents a configuration used to modify the plugin's UI.
 */
@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal, unused"})
public final class MenuConfig extends BaseConfig {
    @Comment("Change settings for all menus")
    private AllMenuSettings all = new AllMenuSettings();
    @Comment("Change settings for: /credits menu <messages/settings>")
    private EditConfigMenu editing = new EditConfigMenu();
    @Comment("Change settings for: /credits menu")
    private MainMenu main = new MainMenu();
    @Comment("Change settings for: /credits menu redeem")
    private RedeemMenu redeem = new RedeemMenu();

    MenuConfig() {
        super(MenuConfig.class, "menus.conf");
    }

    /**
     * Generates an item from the provided path within this configuration.
     *
     * @param path            path to derive ItemStack from.
     * @param player          player to deserialize the ItemStack against.
     * @param resolverFactory Resolver Factory to parse Components against.
     * @return parsed ItemStack derived from the configuration.
     */
    public ItemStack item(final String path, final Player player, final ResolverFactory resolverFactory) {
        CommentedConfigurationNode node = this.rootNode().node(NodePath.of(path.split("\\.")));
        try {
            return PaperItemBuilder.of(ItemSerializer.INSTANCE.deserialize(ItemStack.class, node))
                    .name(Text.parseComponent(player, Component.text(node.node("name").getString("")), resolverFactory))
                    .loreModifier(i -> i.replaceAll(x -> Text.parseComponent(player, x, resolverFactory))).build();
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        return new ItemStack(Material.AIR);
    }

    /**
     * Get inventory slot for an ItemStack from provided path.
     *
     * @param path path to derive inventory slot from.
     * @return inventory slot derived from the path.
     */
    public int slot(final String path) {
        return this.integer(path + ".slot");
    }

    @ConfigSerializable
    static class AllMenuSettings {
        private boolean fill = false;
        private boolean navigation = false;
    }

    @ConfigSerializable
    static class EditConfigMenu {
        @Comment("Menu size must be >= 27 to fit all options.")
        private MenuInfo info = new MenuInfo("<dark_gray>Modify Configuration", 45);
        @Comment("Item settings for message nodes.")
        private PartialConfigItem messages = new PartialConfigItem(Material.WRITABLE_BOOK);
        @Comment("Item settings for setting nodes.")
        private PartialConfigItem settings = new PartialConfigItem(Material.REDSTONE);
    }

    @ConfigSerializable
    static class MainMenu {
        @Comment("Information for the Main Menu")
        private MenuInfo info = new MenuInfo("<#ff253c><bold>MCMMO Credits", 54);
        @Comment("Items used in the Main Menu")
        private MainMenuItems items = new MainMenuItems();
    }

    @ConfigSerializable
    static class MainMenuItems {
        @Comment("Change settings for menu filler item. This will apply to all menus. Slot option does nothing.")
        private ConfigItem fill = new ConfigItem(Material.BLACK_STAINED_GLASS_PANE, "", List.of(), 1, -1, false);
        @Comment("Change settings for menu navigation item. This will apply to all menus.")
        private ConfigItem navigation = new ConfigItem(Material.COMPASS, "<red>Previous Menu", "<gray>Left Click to go back!", 40);
        @Comment("Change settings for in-game message editor shortcut.")
        private ConfigItem config = new ConfigItem(Material.DIAMOND, "<#FF253C>Edit Config", "<gray>Left Click to edit config!", 11);
        @Comment("Change settings for in-game MCMMO Credit redemption shortcut.")
        private ConfigItem redeem = new ConfigItem(Material.EMERALD, "<green>Redeem MCMMO Credits!", "<gray>Left Click to redeem MCMMO Credits!", 15);
    }

    @ConfigSerializable
    static class RedeemMenu {
        @Comment("Menu Size must be >=18 or larger to fit all options.")
        private MenuInfo info = new MenuInfo("<dark_gray>Redeem Your Credits...", 45);
        private RedeemItems items = new RedeemItems();
    }

    @ConfigSerializable
    static class RedeemItems {
        private ConfigItem acrobatics = new ConfigItem(Material.NETHERITE_BOOTS, PrimarySkillType.ACROBATICS, 10);
        private ConfigItem alchemy = new ConfigItem(Material.BREWING_STAND, PrimarySkillType.ALCHEMY, 11);
        private ConfigItem archery = new ConfigItem(Material.BOW, PrimarySkillType.ARCHERY, 12);
        private ConfigItem axes = new ConfigItem(Material.NETHERITE_AXE, PrimarySkillType.AXES, 13);
        private ConfigItem excavation = new ConfigItem(Material.NETHERITE_SHOVEL, PrimarySkillType.EXCAVATION, 14);
        private ConfigItem fishing = new ConfigItem(Material.FISHING_ROD, PrimarySkillType.FISHING, 15);
        private ConfigItem herbalism = new ConfigItem(Material.SUGAR_CANE, PrimarySkillType.HERBALISM, 16);
        private ConfigItem mining = new ConfigItem(Material.NETHERITE_PICKAXE, PrimarySkillType.MINING, 19);
        private ConfigItem repair = new ConfigItem(Material.ANVIL, PrimarySkillType.REPAIR, 20);
        private ConfigItem swords = new ConfigItem(Material.NETHERITE_SWORD, PrimarySkillType.SWORDS, 21);
        private ConfigItem taming = new ConfigItem(Material.LEAD, PrimarySkillType.TAMING, 23);
        private ConfigItem unarmed = new ConfigItem(Material.CARROT_ON_A_STICK, PrimarySkillType.UNARMED, 24);
        private ConfigItem woodcutting = new ConfigItem(Material.OAK_LOG, PrimarySkillType.WOODCUTTING, 25);
    }

    @ConfigSerializable
    record MenuInfo(String title, int size) {

    }

    @ConfigSerializable
    record ConfigItem(Material material, String name, List<String> lore, int amount, int slot, boolean glow) {
        ConfigItem(final Material material, final PrimarySkillType skill, final int slot) {
            this(material, "<yellow>" + WordUtils.capitalizeFully(skill.name()), List.of("<yellow><target>, click here to redeem!"), 1, slot, false);
        }

        ConfigItem(final Material material, final String name, final String lore, final int slot) {
            this(material, name, List.of(lore), 1, slot, false);
        }
    }

    @ConfigSerializable
    record PartialConfigItem(Material material, List<String> lore, boolean glow) {
        PartialConfigItem(final Material material) {
            this(material, List.of("<gray>Click here to edit this config option!"), false);
        }
    }
}
