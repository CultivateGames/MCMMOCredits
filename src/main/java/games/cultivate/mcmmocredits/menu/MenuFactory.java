package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.GeneralConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.util.InputStorage;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public final class MenuFactory {
    private final MenuConfig menus;
    private final InputStorage storage;
    private final GeneralConfig config;
    private final MCMMOCredits plugin;

    @Inject
    public MenuFactory(final MenuConfig menus, final GeneralConfig config, final InputStorage storage, final MCMMOCredits plugin) {
        this.menus = menus;
        this.config = config;
        this.storage = storage;
        this.plugin = plugin;
    }

    public MainMenu createMainMenu(final Player player) {
        MainMenu menu = new MainMenu(this.menus, player);
        menu.load();
        return menu;
    }

    public ConfigMenu createConfigMenu(final Player player) {
        ConfigMenu configMenu = new ConfigMenu(this.menus, player, this.config, this.storage);
        configMenu.load();
        return configMenu;
    }

    public RedeemMenu createRedeemMenu(final Player player) {
        RedeemMenu redeem = new RedeemMenu(this.menus, player, this.config, this.storage, this.plugin);
        redeem.load();
        return redeem;
    }
}
