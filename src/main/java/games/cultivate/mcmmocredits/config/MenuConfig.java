package games.cultivate.mcmmocredits.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Arrays;
import java.util.List;

@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal, unused"})
public class MenuConfig extends Config {
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
        private ConfigMenu messages = new ConfigMenu(new MenuInfo("<dark_gray>Edit Messages", 45), PartialConfigItem.forConfig("WRITABLE_BOOK"));
        @Comment("Edit Settings Menu configuration")
        private ConfigMenu settings = new ConfigMenu(new MenuInfo("<dark_gray>Edit Settings", 27), PartialConfigItem.forConfig("REDSTONE"));
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
        private ConfigItem fill = new ConfigItem("BLACK_STAINED_GLASS_PANE", "", List.of(), 1, 0, -1, false);
        @Comment("Change settings for menu navigation item. This will apply to all menus.")
        private ConfigItem navigation = ConfigItem.populate("COMPASS", "<red>Previous Menu", "<gray>Left Click to go back!", 26);
        @Comment("Change settings for in-game message editor shortcut.")
        private ConfigItem messages = ConfigItem.populate("DIAMOND", "<#FF253C>Edit Messages", "<gray>Left Click to edit messages!", 11);
        @Comment("Change settings for in-game MCMMO Credit redemption shortcut.")
        private ConfigItem redeem = ConfigItem.populate("EMERALD", "<green>Redeem MCMMO Credits!", "<gray>Left Click to redeem MCMMO Credits!", 13);
        @Comment("Change settings for in-game settings editor shortcut.")
        private ConfigItem settings = ConfigItem.populate("IRON_INGOT", "<#FF253C>Edit Settings", "<gray>Left Click to edit settings!", 15);
    }

    @ConfigSerializable
    static class RedeemMenu {
        @Comment("Redeem Menu Size must be >=18 or larger to fit all skills.")
        private MenuInfo info = new MenuInfo("<dark_gray>Redeem Your Credits...", 36);
        @Setting("items")
        private RedeemItems items = new RedeemItems();
    }

    @ConfigSerializable
    static class RedeemItems {
        private ConfigItem acrobatics = ConfigItem.forSkill("NETHERITE_BOOTS", "<yellow>Acrobatics", 10);
        private ConfigItem alchemy = ConfigItem.forSkill("BREWING_STAND", "<yellow>Alchemy", 11);
        private ConfigItem archery = ConfigItem.forSkill("BOW", "<yellow>Archery", 12);
        private ConfigItem axes = ConfigItem.forSkill("NETHERITE_AXE", "<yellow>Axes", 13);
        private ConfigItem excavation = ConfigItem.forSkill("NETHERITE_SHOVEL", "<yellow>Excavation", 14);
        private ConfigItem fishing = ConfigItem.forSkill("FISHING_ROD", "<yellow>Fishing", 15);
        private ConfigItem herbalism = ConfigItem.forSkill("SUGAR_CANE", "<yellow>Herbalism", 16);
        private ConfigItem mining = ConfigItem.forSkill("NETHERITE_PICKAXE", "<yellow>Mining", 19);
        private ConfigItem repair = ConfigItem.forSkill("ANVIL", "<yellow>Repair", 20);
        private ConfigItem swords = ConfigItem.forSkill("NETHERITE_SWORD", "<yellow>Swords", 21);
        private ConfigItem taming = ConfigItem.forSkill("LEAD", "<yellow>Taming", 23);
        private ConfigItem unarmed = ConfigItem.forSkill("CARROT_ON_A_STICK", "<yellow>Unarmed", 24);
        private ConfigItem woodcutting = ConfigItem.forSkill("OAK_LOG", "<yellow>Woodcutting", 25);
    }

    @ConfigSerializable
    record MenuInfo(String title, int size){}

    @ConfigSerializable
    record ConfigMenu(MenuInfo info, PartialConfigItem item) {}

    @ConfigSerializable
    record ConfigItem(String material, String name, List<String> lore, int amount, int durability, int slot, boolean glow) {
        static ConfigItem forSkill(String material, String name, int slot) {
            return new ConfigItem(material, name, Arrays.asList("<yellow><player>, click here to redeem!"), 1, 0, slot, false);
        }
        static ConfigItem populate(String material, String name, String lore, int slot) {
            return new ConfigItem(material, name, Arrays.asList(lore), 1, 0, slot, false);
        }
    }

    @ConfigSerializable
    record PartialConfigItem(String material, List<String> lore, int durability, boolean glow) {
        static PartialConfigItem forConfig(String material) {
            return new PartialConfigItem(material, Arrays.asList("<gray>Click here to edit this config option!"), 0, false);
        }
    }
}
