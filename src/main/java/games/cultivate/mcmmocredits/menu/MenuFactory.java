package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.config.MessagesConfig;
import games.cultivate.mcmmocredits.config.SettingsConfig;
import games.cultivate.mcmmocredits.data.InputStorage;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class MenuFactory {
    private final MenuConfig menus;
    private final MCMMOCredits plugin;
    private final MessagesConfig messages;
    private final InputStorage storage;
    private final SettingsConfig settings;

    @Inject
    public MenuFactory(final MenuConfig menus, final MessagesConfig messages, final SettingsConfig settings, final InputStorage storage, final MCMMOCredits plugin) {
        this.menus = menus;
        this.plugin = plugin;
        this.messages = messages;
        this.storage = storage;
        this.settings = settings;
    }

    public MainMenu createMainMenu(final Player player) {
        return new MainMenu(this.menus, this.plugin, player);
    }

    public ConfigMenu createMessagesMenu(final Player player) {
        return new ConfigMenu(this.menus, this.messages, this.storage, player);
    }

    public ConfigMenu createSettingsMenu(final Player player, final SettingsConfig config) {
        return new ConfigMenu(this.menus, this.messages, this.settings, this.storage, player);
    }

    public RedeemMenu createRedeemMenu(final Player player) {
        //TODO method stub
        return new RedeemMenu();
    }
}
