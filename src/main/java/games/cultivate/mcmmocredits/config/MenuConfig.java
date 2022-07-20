package games.cultivate.mcmmocredits.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Arrays;
import java.util.List;

@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal, unused"})
public class MenuConfig extends Config {
    @Comment("Editing Config menus: /credits menu <messages/settings>")
    @Setting("editing")
    private EditMenus editMenus = new EditMenus();
    @Comment("Main menu: /credits menu")
    @Setting("main")
    private MainMenu mainMenu = new MainMenu();
    @Comment("Redeem Menu from /credits menu redeem")
    @Setting("redeem")
    private RedeemMenu redeemMenu = new RedeemMenu();

    MenuConfig() {
        super(MenuConfig.class, "menus.conf");
    }

    @ConfigSerializable
    static class EditMenus {
        @Comment("Edit Messages Menu configuration")
        @Setting("messages")
        private MessagesMenu messagesMenu = new MessagesMenu();
        @Comment("Edit Settings Menu configuration")
        @Setting("settings")
        private SettingsMenu settingsMenu = new SettingsMenu();
    }

    @ConfigSerializable
    static class MessagesMenu {
        @Setting("title")
        private String title = "<dark_gray>Edit Messages";
        @Comment("Must be at least 27")
        @Setting("size")
        private int size = 45;
        @Comment("Change item appearance. Name, amount and inventory slot are not configurable.")
        @Setting("item")
        private ConfigItem item = new ConfigItem();
    }

    @ConfigSerializable
    static class SettingsMenu {
        @Setting("title")
        private String title = "<dark_gray>Edit Settings";
        @Setting("size")
        private int size = 45;
        @Comment("Change item appearance. Name, amount and inventory slot are not configurable.")
        @Setting("item")
        private ConfigItem item = new ConfigItem();
    }

    @ConfigSerializable
    static class MainMenu {
        @Setting("title")
        private String title = "<#ff253c><bold>MCMMO Credits";
        @Setting("size")
        private int size = 54;
        @Comment("This will apply to all menus.")
        @Setting("fill")
        private boolean fill = false;
        @Comment("This will apply to all menus.")
        @Setting("fillItem")
        private ShortcutItem fillItem = new ShortcutItem();
        @Comment("This will apply to all menus.")
        @Setting("navigation")
        private boolean navigation = false;
        @Comment("This will apply to all menus.")
        @Setting("navigationItem")
        private ShortcutItem navigationItem = new ShortcutItem();
        @Comment("Appearance of Edit Messages shortcut.")
        @Setting("messagesItem")
        private ShortcutItem messagesItem = new ShortcutItem();
        @Comment("Appearance of Redeem shortcut.")
        @Setting("redeemItem")
        private ShortcutItem redeemItem = new ShortcutItem();
        @Comment("Appearance of Edit Settings shortcut.")
        @Setting("settingsItem")
        private ShortcutItem settingsItem = new ShortcutItem();
    }

    @ConfigSerializable
    static class RedeemMenu {
        @Setting("title")
        private String title = "<dark_gray>Redeem Your Credits...";
        @Comment("Must be at least 18")
        @Setting("size")
        private int size = 45;
        @Comment("Credit redemption items")
        @Setting("items")
        private Items items = new Items();
    }

    @ConfigSerializable
    static class Items {
        @Setting("acrobatics")
        private ShortcutItem acrobatics = new ShortcutItem();
        @Setting("alchemy")
        private ShortcutItem alchemy = new ShortcutItem();
        @Setting("archery")
        private ShortcutItem archery = new ShortcutItem();
        @Setting("axes")
        private ShortcutItem axes = new ShortcutItem();
        @Setting("excavation")
        private ShortcutItem excavation = new ShortcutItem();
        @Setting("fishing")
        private ShortcutItem fishing = new ShortcutItem();
        @Setting("herbalism")
        private ShortcutItem herbalism = new ShortcutItem();
        @Setting("mining")
        private ShortcutItem mining = new ShortcutItem();
        @Setting("repair")
        private ShortcutItem repair = new ShortcutItem();
        @Setting("swords")
        private ShortcutItem swords = new ShortcutItem();
        @Setting("taming")
        private ShortcutItem taming = new ShortcutItem();
        @Setting("unarmed")
        private ShortcutItem unarmed = new ShortcutItem();
        @Setting("woodcutting")
        private ShortcutItem woodcutting = new ShortcutItem();
    }

    @ConfigSerializable
    static class ConfigItem {
        @Setting("material")
        private String material = "STONE";
        @Setting("durability")
        private int durability = 0;
        @Setting("lore")
        private List<String> lore = Arrays.asList("<gray><player>, welcome to MCMMO Credits!", "<gradient:#666666:#FFFFFF>Configure this menu in config.conf!");
        @Setting("glow")
        private boolean glow = true;
    }

    @ConfigSerializable
    static class ShortcutItem {
        @Setting("material")
        private String material = "STONE";
        @Setting("amount")
        private int amount = 1;
        @Setting("durability")
        private int durability = 0;
        @Setting("name")
        private String name = "<gray>Menu Item!";
        @Setting("lore")
        private List<String> lore = Arrays.asList("<gray><player>, welcome to MCMMO Credits!", "<gradient:#666666:#FFFFFF>Configure this menu in config.conf!");
        @Setting("glow")
        private boolean glow = true;
        @Setting("slot")
        private int slot = 0;
    }
}
