package games.cultivate.mcmmocredits.config;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.List;

@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal, unused"})
public final class MenuConfig extends Config {
    @Comment("Change settings for all menus")
    private AllMenuSettings all = new AllMenuSettings();
    @Comment("Change settings for: /credits menu <messages/settings>")
    private EditConfigMenus editing = new EditConfigMenus();
    @Comment("Change settings for: /credits menu")
    private MainMenu main = new MainMenu();
    @Comment("Change settings for: /credits menu redeem")
    private RedeemMenu redeem = new RedeemMenu();

    MenuConfig() {
        super(MenuConfig.class, "menus.conf");
    }

    @ConfigSerializable
    static class AllMenuSettings {
        private boolean fill = false;
        private boolean navigation = false;
    }

    @ConfigSerializable
    static class EditConfigMenus {
        @Comment("Edit Messages Menu configuration")
        private ConfigMenu messages = new ConfigMenu(Material.WRITABLE_BOOK, "<dark_gray>Edit Messages", 45);
        @Comment("Edit Settings Menu configuration")
        private ConfigMenu settings = new ConfigMenu(Material.REDSTONE, "<dark_gray>Edit Settings", 27);
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
        private ConfigItem navigation = new ConfigItem(Material.COMPASS, "<red>Previous Menu", "<gray>Left Click to go back!", 26);
        @Comment("Change settings for in-game message editor shortcut.")
        private ConfigItem messages = new ConfigItem(Material.DIAMOND, "<#FF253C>Edit Messages", "<gray>Left Click to edit messages!", 11);
        @Comment("Change settings for in-game MCMMO Credit redemption shortcut.")
        private ConfigItem redeem = new ConfigItem(Material.EMERALD, "<green>Redeem MCMMO Credits!", "<gray>Left Click to redeem MCMMO Credits!", 13);
        @Comment("Change settings for in-game settings editor shortcut.")
        private ConfigItem settings = new ConfigItem(Material.IRON_INGOT, "<#FF253C>Edit Settings", "<gray>Left Click to edit settings!", 15);
    }

    @ConfigSerializable
    static class RedeemMenu {
        @Comment("Redeem Menu Size must be >=18 or larger to fit all skills.")
        private MenuInfo info = new MenuInfo("<dark_gray>Redeem Your Credits...", 36);
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
    record ConfigMenu(PartialConfigItem item, MenuInfo info) {
        ConfigMenu(final Material material, final String title, final int size) {
            this(new PartialConfigItem(material), new MenuInfo(title, size));
        }
    }

    @ConfigSerializable
    record ConfigItem(Material material, String name, List<String> lore, int amount, int slot, boolean glow) {
        ConfigItem(final Material material, final PrimarySkillType skill, final int slot) {
            this(material, "<yellow> " + WordUtils.capitalizeFully(skill.name()), List.of("<yellow><player>, click here to redeem!"), 1, slot, false);
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
