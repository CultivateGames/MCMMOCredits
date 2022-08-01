package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.config.MessagesConfig;
import games.cultivate.mcmmocredits.config.SettingsConfig;
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
    private final MessagesConfig messages;
    private final MenuConfig menus;
    private final SettingsConfig settings;
    private final Database database;
    private final MenuFactory factory;

    @Inject
    public Credits(final MessagesConfig messages, final SettingsConfig settings, final MenuConfig menus, final Database database, final MenuFactory factory) {
        this.messages = messages;
        this.settings = settings;
        this.menus = menus;
        this.database = database;
        this.factory = factory;
    }

    @CommandDescription("Check your own MCMMO Credit balance.")
    @CommandMethod("")
    @CommandPermission("mcmmocredits.check.self")
    public void checkCredits(final Player player) {
        Text.fromString(player, this.messages.string("selfBalance")).send();
    }

    @CommandDescription("Check someone else's MCMMO Credit balance.")
    @CommandMethod("<username>")
    @CommandPermission("mcmmocredits.check.other")
    public void checkCreditsOther(final CommandSender sender, final @Argument(suggestions = "user") String username) {
        this.database.getUUID(username).whenCompleteAsync((i, t) -> {
            if (this.database.doesPlayerExist(i)) {
                TagResolver tr = Resolver.builder().sender(sender).player(username).build();
                Text.fromString(sender, this.messages.string("otherBalance"), tr).send();
                return;
            }
            Text.fromString(sender, this.messages.string("playerDoesNotExist")).send();
        });
    }

    @CommandDescription("Reload all configuration files provided by the plugin.")
    @CommandMethod("reload")
    @CommandPermission("mcmmocredits.admin.reload")
    public void reloadCredits(final CommandSender sender) {
        //TODO close all inventories.
        this.menus.load();
        this.settings.load();
        this.messages.load();
        Text.fromString(sender, this.messages.string("reloadSuccessful")).send();
    }

    @CommandDescription("Open a Menu that can be used to interface with this plugin.")
    @CommandMethod("menu")
    @CommandPermission("mcmmocredits.menu.basic")
    public void openMenu(final Player player) {
        this.factory.createMainMenu(player).open();
    }

    @CommandDescription("Open the Edit Messages Menu")
    @CommandMethod("menu messages")
    @CommandPermission("mcmmocredits.menu.admin")
    public void openMessagesMenu(final Player player) {
        this.factory.createMessagesMenu(player).open();
    }

    @CommandDescription("Open the Edit Settings Menu")
    @CommandMethod("menu settings")
    @CommandPermission("mcmmocredits.menu.admin")
    public void openSettingsMenu(final Player player) {
       this.factory.createSettingsMenu(player).open();
    }

    @CommandDescription("Open the Credit Redemption Menu")
    @CommandMethod("menu redeem")
    @CommandPermission("mcmmocredits.menu.redeem")
    public void openRedeemMenu(final Player player) {
        this.factory.createRedeemMenu(player).open();
    }
}
