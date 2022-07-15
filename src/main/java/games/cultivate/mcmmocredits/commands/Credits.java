package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.config.MessagesConfig;
import games.cultivate.mcmmocredits.config.SettingsConfig;
import games.cultivate.mcmmocredits.data.Database;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
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

    @Inject
    public Credits(MessagesConfig messages, SettingsConfig settings, MenuConfig menus, Database database) {
        this.messages = messages;
        this.settings = settings;
        this.menus = menus;
        this.database = database;
    }

    @CommandDescription("Check your own MCMMO Credit balance.")
    @CommandMethod("")
    @CommandPermission("mcmmocredits.check.self")
    private void checkCredits(Player player) {
        Text.fromString(player, this.messages.string("selfBalance")).send();
    }

    @CommandDescription("Check someone else's MCMMO Credit balance.")
    @CommandMethod("<username>")
    @CommandPermission("mcmmocredits.check.other")
    private void checkCreditsOther(CommandSender sender, @Argument(value = "username", suggestions = "customPlayer") String username) {
        this.database.getUUID(username).whenCompleteAsync((i, t) -> {
            Text.Builder text = Text.builder().audience(sender);
            Resolver.Builder resolver = Resolver.builder().sender(sender);
            if (this.database.doesPlayerExist(i)) {
                text.content(this.messages.string("otherBalance"))
                .resolver(resolver.player(username, i).build())
                .build().send();
                return;
            }
            text = text.content(this.messages.string("playerDoesNotExist"));
            text.resolver(resolver.build()).build().send();
        });
    }

    @CommandDescription("Reload all configuration files provided by the plugin.")
    @CommandMethod("reload")
    @CommandPermission("mcmmocredits.admin.reload")
    private void reloadCredits(CommandSender sender) {
        this.messages.load();
        this.settings.load();
        this.menus.load();
        Text.fromString(sender, this.messages.string("reloadSuccessful")).send();
    }

    @CommandDescription("Open a Menu that can be used to interface with this plugin.")
    @CommandMethod("menu")
    @CommandPermission("mcmmocredits.gui.basic")
    private void openMenu(Player player) {
        //open main menu
    }

    @CommandDescription("Open the Edit Messages Menu")
    @CommandMethod("menu messages")
    @CommandPermission("mcmmocredits.gui.admin")
    private void openMessagesMenu(Player player) {
        //open messages menu
    }

    @CommandDescription("Open the Edit Settings Menu")
    @CommandMethod("menu settings")
    @CommandPermission("mcmmocredits.gui.admin")
    private void openSettingsMenu(Player player) {
        //open settings menu
    }

    @CommandDescription("Open the Credit Redemption Menu")
    @CommandMethod("menu redeem")
    @CommandPermission("mcmmocredits.gui.redeem")
    private void openRedeemMenu(Player player) {
        //open redeem menu
    }
}
