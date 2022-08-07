package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.config.GeneralConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.util.InputStorage;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public final class MenuFactory {
    private final MenuConfig menus;
    private final InputStorage storage;
    private final GeneralConfig config;

    @Inject
    public MenuFactory(final MenuConfig menus, final GeneralConfig config, final InputStorage storage) {
        this.menus = menus;
        this.config = config;
        this.storage = storage;
    }

    public MainMenu createMainMenu(final Player player) {
        MainMenu menu = new MainMenu(this.menus, player);
        menu.create();
        return menu;
    }

    public ConfigMenu createMessagesMenu(final Player player) {
        ConfigMenu messages = new ConfigMenu(this.menus, player, this.config, this.storage, "messages");
        messages.create();
        return messages;
    }

    public ConfigMenu createSettingsMenu(final Player player) {
        ConfigMenu settings = new ConfigMenu(this.menus, player, this.config, this.storage, "settings");
        settings.create();
        return settings;
    }

    public RedeemMenu createRedeemMenu(final Player player) {
        RedeemMenu redeem = new RedeemMenu(this.menus, player, this.config, this.storage);
        redeem.create();
        return redeem;
    }
}
