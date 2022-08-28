package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.GeneralConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.placeholders.ResolverFactory;
import games.cultivate.mcmmocredits.util.InputStorage;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public final class MenuFactory {
    private final MenuConfig menus;
    private final InputStorage storage;
    private final GeneralConfig config;
    private final ResolverFactory resolverFactory;
    private final MCMMOCredits plugin;

    @Inject
    public MenuFactory(final MenuConfig menus, final ResolverFactory resolverFactory, final GeneralConfig config, final InputStorage storage, final MCMMOCredits plugin) {
        this.menus = menus;
        this.resolverFactory = resolverFactory;
        this.config = config;
        this.storage = storage;
        this.plugin = plugin;
    }

    public Menu createMainMenu(final Player player) {
        Menu menu = new MainMenu(this.menus, this.resolverFactory, player, this.plugin);
        menu.load();
        return menu;
    }

    public Menu createConfigMenu(final Player player) {
        Menu configMenu = new ConfigMenu(this.menus, this.resolverFactory, player, this.plugin, this.config, this.storage);
        configMenu.load();
        return configMenu;
    }

    public Menu createRedeemMenu(final Player player) {
        Menu redeemMenu = new RedeemMenu(this.menus, this.resolverFactory, player, this.plugin, this.config, this.storage);
        redeemMenu.load();
        return redeemMenu;
    }
}
