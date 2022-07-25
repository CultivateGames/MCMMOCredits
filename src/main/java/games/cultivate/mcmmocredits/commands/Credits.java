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
import org.incendo.interfaces.paper.PlayerViewer;

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
    public Credits(MessagesConfig messages, SettingsConfig settings, MenuConfig menus, Database database, MenuFactory factory) {
        this.messages = messages;
        this.settings = settings;
        this.menus = menus;
        this.database = database;
        this.factory = factory;
    }

    @CommandDescription("Check your own MCMMO Credit balance.")
    @CommandMethod("")
    @CommandPermission("mcmmocredits.check.self")
    public void checkCredits(Player player) {
        Text.fromString(player, this.messages.string("selfBalance")).send();
    }

    @CommandDescription("Check someone else's MCMMO Credit balance.")
    @CommandMethod("<username>")
    @CommandPermission("mcmmocredits.check.other")
    public void checkCreditsOther(CommandSender sender, @Argument(suggestions = "user") String username) {
        this.database.getUUID(username).whenCompleteAsync((i, t) -> {
            if (this.database.doesPlayerExist(i)) {
                TagResolver tr = Resolver.builder().sender(sender).player(username, i).build();
                Text.fromString(sender, this.messages.string("otherBalance"), tr).send();
                return;
            }
            Text.fromString(sender, this.messages.string("playerDoesNotExist")).send();
        });
    }

    @CommandDescription("Reload all configuration files provided by the plugin.")
    @CommandMethod("reload")
    @CommandPermission("mcmmocredits.admin.reload")
    public void reloadCredits(CommandSender sender) {
        this.menus.load();
        this.settings.load();
        this.messages.load();
        Text.fromString(sender, this.messages.string("reloadSuccessful")).send();
    }

    @CommandDescription("Open a Menu that can be used to interface with this plugin.")
    @CommandMethod("menu")
    @CommandPermission("mcmmocredits.gui.basic")
    public void openMenu(Player player) {
        this.factory.makeMainMenu(player).open(PlayerViewer.of(player));
    }

    @CommandDescription("Open the Edit Messages Menu")
    @CommandMethod("menu messages")
    @CommandPermission("mcmmocredits.gui.admin")
    public void openMessagesMenu(Player player) {
        this.factory.makeConfigMenu(player, this.messages).open(PlayerViewer.of(player));
    }

    @CommandDescription("Open the Edit Settings Menu")
    @CommandMethod("menu settings")
    @CommandPermission("mcmmocredits.gui.admin")
    public void openSettingsMenu(Player player) {
        this.factory.makeConfigMenu(player, this.settings).open(PlayerViewer.of(player));
    }

    @CommandDescription("Open the Credit Redemption Menu")
    @CommandMethod("menu redeem")
    @CommandPermission("mcmmocredits.gui.redeem")
    public void openRedeemMenu(Player player) {
        this.factory.makeRedemptionMenu(player).open(PlayerViewer.of(player));
    }
}
