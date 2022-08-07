package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import games.cultivate.mcmmocredits.config.GeneralConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.data.Database;
import games.cultivate.mcmmocredits.menu.MenuFactory;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

/**
 * This class is responsible for handling of the /credits command.
 */
@CommandMethod("credits")
public final class Credits {
    private final MenuConfig menus;
    private final GeneralConfig config;
    private final Database database;
    private final MenuFactory factory;

    @Inject
    public Credits(final GeneralConfig config, final MenuConfig menus, final Database database, final MenuFactory factory) {
        this.config = config;
        this.menus = menus;
        this.database = database;
        this.factory = factory;
    }

    @CommandDescription("Check your own MCMMO Credit balance.")
    @CommandMethod("")
    @CommandPermission("mcmmocredits.check.self")
    public void checkCredits(final Player player) {
        Text.fromString(player, this.config.string("selfBalance")).send();
    }

    @CommandDescription("Check someone else's MCMMO Credit balance.")
    @CommandMethod("<username>")
    @CommandPermission("mcmmocredits.check.other")
    public void checkCreditsOther(final CommandSender sender, final @Argument(suggestions = "user") String username) {
        this.database.getUUID(username).whenCompleteAsync((i, t) -> {
            if (this.database.doesPlayerExist(i)) {
                TagResolver tr = Resolver.builder().sender(sender).player(username).build();
                Text.fromString(sender, this.config.string("otherBalance"), tr).send();
                return;
            }
            Text.fromString(sender, this.config.string("playerDoesNotExist")).send();
        });
    }

    @CommandDescription("Reload all configuration files provided by the plugin.")
    @CommandMethod("reload")
    @CommandPermission("mcmmocredits.admin.reload")
    public void reloadCredits(final CommandSender sender) {
        this.config.load();
        this.menus.load();
        Text.fromString(sender, this.config.string("reloadSuccessful")).send();
    }

    @CommandDescription("Open the Main Menu")
    @CommandMethod("menu")
    @CommandPermission("mcmmocredits.gui.basic")
    private void openMenu(final Player player) {
        this.factory.createMainMenu(player).open();
    }

    @CommandDescription("Open the Edit Messages Menu")
    @CommandMethod("menu messages")
    @CommandPermission("mcmmocredits.menu.messages")
    private void openMessagesMenu(final Player player) {
        this.factory.createMessagesMenu(player).open();
    }

    @CommandDescription("Open the Edit Settings Menu")
    @CommandMethod("menu settings")
    @CommandPermission("mcmmocredits.menu.settings")
    private void openSettingsMenu(final Player player) {
        this.factory.createSettingsMenu(player).open();
    }

    @CommandDescription("Open the Credit Redemption Menu")
    @CommandMethod("menu redeem")
    @CommandPermission("mcmmocredits.menu.redeem")
    private void openRedeemMenu(final Player player) {
        this.factory.createRedeemMenu(player).open();
    }
}
