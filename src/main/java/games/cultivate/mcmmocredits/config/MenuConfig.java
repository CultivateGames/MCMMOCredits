package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.keys.BooleanKey;
import games.cultivate.mcmmocredits.keys.IntegerKey;
import games.cultivate.mcmmocredits.keys.ItemStackKey;
import games.cultivate.mcmmocredits.keys.StringKey;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Arrays;
import java.util.List;

@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal, unused"})
public class MenuConfig extends Config<MenuConfig> {
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
        super(MenuConfig.class);
    }

    @Override
    public void setupKeys() {
        //Inventory Attributes
        this.addKey(IntegerKey.EDIT_MESSAGES_SIZE);
        this.addKey(IntegerKey.EDIT_SETTINGS_SIZE);
        this.addKey(IntegerKey.MENU_SIZE);
        this.addKey(IntegerKey.REDEEM_SIZE);
        this.addKey(BooleanKey.MENU_FILL);
        this.addKey(BooleanKey.MENU_NAVIGATION);
        this.addKey(StringKey.EDIT_MESSAGES_TITLE);
        this.addKey(StringKey.EDIT_SETTINGS_TITLE);
        this.addKey(StringKey.MENU_TITLE);
        this.addKey(StringKey.REDEEM_TITLE);
        //Item Stack Keys
        for (ItemStackKey isk : ItemStackKey.values()) {
            this.addKey(isk);
        }
    }

    @ConfigSerializable
    private static final class EditMenus {
        @Comment("Edit Messages Menu configuration")
        @Setting("messages")
        private MessagesMenu messagesMenu = new MessagesMenu();
        @Comment("Edit Settings Menu configuration")
        @Setting("settings")
        private SettingsMenu settingsMenu = new SettingsMenu();
    }

    @ConfigSerializable
    private static final class MessagesMenu {
        private String title = "<dark_gray>Edit Messages";
        @Comment("Must be at least 27")
        private int size = 45;
        @Comment("Change item appearance. Name, amount and inventory slot are not configurable.")
        private ConfigItem item = new ConfigItem();
    }

    @ConfigSerializable
    private static final class SettingsMenu {
        private String title = "<dark_gray>Edit Settings";
        private int size = 45;
        @Comment("Change item appearance. Name, amount and inventory slot are not configurable.")
        private ConfigItem item = new ConfigItem();
    }

    @ConfigSerializable
    private static final class MainMenu {
        private String title = "<#ff253c><bold>MCMMO Credits";
        private int size = 54;
        @Comment("This will apply to all menus.")
        private boolean fill = false;
        @Comment("This will apply to all menus.")
        private ShortcutItem fillItem = new ShortcutItem();
        @Comment("This will apply to all menus.")
        private boolean navigation = false;
        @Comment("This will apply to all menus.")
        private ShortcutItem navigationItem = new ShortcutItem();
        @Comment("Appearance of Edit Messages shortcut.")
        @Setting("messages")
        private ShortcutItem messagesItem = new ShortcutItem();
        @Comment("Appearance of Redeem shortcut.")
        @Setting("redeem")
        private ShortcutItem redeemItem = new ShortcutItem();
        @Comment("Appearance of Edit Settings shortcut.")
        @Setting("settings")
        private ShortcutItem settingsItem = new ShortcutItem();
    }

    @ConfigSerializable
    private static final class RedeemMenu {
        private String title = "<dark_gray>Redeem Your Credits...";
        @Comment("Must be at least 18")
        private int size = 45;
        @Comment("Credit redemption items")
        @Setting("items")
        private Items items = new Items();
    }

    @ConfigSerializable
    private static final class Items {
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
    private static final class ConfigItem {
        private String material = "STONE";
        private int durability = 0;
        private List<String> lore = Arrays.asList("<gray><player>, welcome to MCMMO Credits!", "<gradient:#666666:#FFFFFF>Configure this menu in config.conf!");
        private boolean glow = true;
    }

    @ConfigSerializable
    private static final class ShortcutItem {
        private String material = "STONE";
        private int amount = 1;
        private int durability = 0;
        private String name = "<gray>Menu Item!";
        private List<String> lore = Arrays.asList("<gray><player>, welcome to MCMMO Credits!", "<gradient:#666666:#FFFFFF>Configure this menu in config.conf!");
        private boolean glow = true;
        private int slot = 0;
    }
}
