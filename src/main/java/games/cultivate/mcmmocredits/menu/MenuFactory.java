package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.GeneralConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.util.InputStorage;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class MenuFactory {
    private final MenuConfig menus;
    private final MCMMOCredits plugin;
    private final InputStorage storage;
    private final GeneralConfig config;

    @Inject
    public MenuFactory(final MenuConfig menus, final GeneralConfig config, final InputStorage storage, final MCMMOCredits plugin) {
        this.menus = menus;
        this.config = config;
        this.plugin = plugin;
        this.storage = storage;
    }

    public MainMenu createMainMenu(final Player player) {
        return new MainMenu(this.menus, this.plugin, player);
    }

    public ConfigMenu createMessagesMenu(final Player player) {
        return new ConfigMenu(this.menus, this.config, this.storage, player);
    }

    public ConfigMenu createSettingsMenu(final Player player) {
        return new ConfigMenu(this.menus, this.config, this.storage, player);
    }

    public RedeemMenu createRedeemMenu(final Player player) {
        //TODO method stub
        return new RedeemMenu();
    }
}
