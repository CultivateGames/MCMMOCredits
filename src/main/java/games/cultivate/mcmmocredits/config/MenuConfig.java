package games.cultivate.mcmmocredits.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Arrays;
import java.util.List;

@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal, unused"})
public class MenuConfig {
    @Comment("Editing Config menus: /credits menu <messages/settings>")
    @Setting("editing")
    private EditMenus editMenus = new EditMenus();
    @Comment("Main menu: /credits menu")
    @Setting("main")
    private MainMenu mainMenu = new MainMenu();
    @Comment("Redeem Menu from /credits menu redeem")
    @Setting("redeem")
    private RedeemMenu redeemMenu = new RedeemMenu();

    @ConfigSerializable
    protected static final class EditMenus {
        @Comment("Edit Messages Menu configuration")
        @Setting("messages")
        private MessagesMenu messagesMenu = new MessagesMenu();
        @Comment("Edit Settings Menu configuration")
        @Setting("settings")
        private SettingsMenu settingsMenu = new SettingsMenu();
    }

    @ConfigSerializable
    protected static final class MessagesMenu {
        private String inventory_title = "<dark_gray>Edit Messages";
        @Comment("Must be at least 27")
        private int inventory_size = 45;
        @Comment("Change item appearance. Name, amount and inventory slot are not configurable.")
        private ConfigItem item = new ConfigItem();
    }

    @ConfigSerializable
    protected static final class SettingsMenu {
        private String inventory_title = "<dark_gray>Edit Settings";
        private int inventory_size = 45;
        @Comment("Change item appearance. Name, amount and inventory slot are not configurable.")
        private ConfigItem item = new ConfigItem();
    }

    @ConfigSerializable
    protected static final class MainMenu {
        private String inventory_title = "<#ff253c><bold>MCMMO Credits";
        private int inventory_size = 54;
        @Comment("This will apply to all menus.")
        private boolean fill = false;
        @Comment("This will apply to all menus.")
        private ShortcutItem fill_item = new ShortcutItem();
        @Comment("This will apply to all menus.")
        private boolean navigation = false;
        @Comment("This will apply to all menus.")
        private ShortcutItem navigation_item = new ShortcutItem();
        @Comment("Appearance of Edit Messages shortcut.")
        @Setting("messages")
        private ShortcutItem messages_item = new ShortcutItem();
        @Comment("Appearance of Redeem shortcut.")
        @Setting("redeem")
        private ShortcutItem redeem_item = new ShortcutItem();
        @Comment("Appearance of Edit Settings shortcut.")
        @Setting("settings")
        private ShortcutItem settings_item = new ShortcutItem();
    }

    @ConfigSerializable
    protected static final class RedeemMenu {
        private String inventory_title = "<dark_gray>Redeem Your Credits...";
        @Comment("Must be at least 18")
        private int inventory_size = 45;
        @Comment("Credit redemption items")
        @Setting("items")
        private Items items = new Items();
    }

    @ConfigSerializable
    protected static final class Items {
        private ShortcutItem acrobatics = new ShortcutItem();
        private ShortcutItem alchemy = new ShortcutItem();
        private ShortcutItem archery = new ShortcutItem();
        private ShortcutItem axes = new ShortcutItem();
        private ShortcutItem excavation = new ShortcutItem();
        private ShortcutItem fishing = new ShortcutItem();
        private ShortcutItem herbalism = new ShortcutItem();
        private ShortcutItem mining = new ShortcutItem();
        private ShortcutItem repair = new ShortcutItem();
        private ShortcutItem swords = new ShortcutItem();
        private ShortcutItem taming = new ShortcutItem();
        private ShortcutItem unarmed = new ShortcutItem();
        private ShortcutItem woodcutting = new ShortcutItem();
    }

    @ConfigSerializable
    protected  static final class ConfigItem {
        private String material = "STONE";
        private int durability = 0;
        private List<String> lore = Arrays.asList("<gray><player>, welcome to MCMMO Credits!", "<gradient:#666666:#FFFFFF>Configure this menu in config.conf!");
        private boolean glow = true;
    }

    @ConfigSerializable
    protected static final class ShortcutItem {
        private String material = "STONE";
        private int amount = 1;
        private int durability = 0;
        private String name = "<gray>Menu Item!";
        private List<String> lore = Arrays.asList("<gray><player>, welcome to MCMMO Credits!", "<gradient:#666666:#FFFFFF>Configure this menu in config.conf!");
        private boolean glow = true;
        private int inventory_slot;
    }
}
