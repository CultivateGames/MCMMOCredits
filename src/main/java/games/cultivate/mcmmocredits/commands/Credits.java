package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.google.inject.Inject;
import games.cultivate.mcmmocredits.config.ConfigUtil;
import games.cultivate.mcmmocredits.config.MessagesConfig;
import games.cultivate.mcmmocredits.data.Database;
import games.cultivate.mcmmocredits.keys.ItemStackKey;
import games.cultivate.mcmmocredits.keys.StringKey;
import games.cultivate.mcmmocredits.menu.Button;
import games.cultivate.mcmmocredits.menu.MenuDirector;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.incendo.interfaces.paper.PlayerViewer;

/**
 * This class is responsible for handling of the /credits command.
 */
@CommandMethod("credits")
public class Credits {
    private final MessagesConfig messages;

    @Inject
    public Credits(MessagesConfig messages) {
        this.messages = messages;
    }

    @CommandDescription("Check your own MCMMO Credit balance.")
    @CommandMethod("")
    @CommandPermission("mcmmocredits.check.self")
    private void checkCredits(Player player) {
        TagResolver resolver = Resolver.fromPlayer(player);
        Text.fromPath(player, this.messages, "selfBalance", resolver).send();
        Text.fromKey(player, StringKey.CREDITS_BALANCE_SELF, resolver).send();
    }

    @CommandDescription("Check someone else's MCMMO Credit balance.")
    @CommandMethod("<username>")
    @CommandPermission("mcmmocredits.check.other")
    private void checkCreditsOther(CommandSender sender, @Argument(value = "username", suggestions = "customPlayer") String username) {
        Database database = Database.getDatabase();
        database.getUUID(username).whenCompleteAsync((i, t) -> {
            Text.Builder text = Text.builder().audience(sender);
            Resolver.Builder resolver = Resolver.builder().sender(sender);
            if (database.doesPlayerExist(i)) {
                text.key(StringKey.CREDITS_BALANCE_OTHER);
                text.resolver(resolver.player(username, i).build());
                text.build().send();
                return;
            }
            text.key(StringKey.PLAYER_DOES_NOT_EXIST);
            text.resolver(resolver.build()).build().send();
        });
    }

    @CommandDescription("Reload all configuration files provided by the plugin.")
    @CommandMethod("reload")
    @CommandPermission("mcmmocredits.admin.reload")
    private void reloadCredits(CommandSender sender) {
        ConfigUtil.loadAllConfigs();
        Text.fromKey(sender, StringKey.CREDITS_RELOAD_SUCCESSFUL).send();
    }

    @CommandDescription("Open a Menu that can be used to interface with this plugin.")
    @CommandMethod("menu")
    @CommandPermission("mcmmocredits.gui.basic")
    private void openMenu(Player player) {
        MenuDirector main = new MenuDirector(player);
        Button button = Button.of(ItemStackKey.MENU_REDEEM_ITEM, player);
        main.transferToInterface(button, ClickType.LEFT, main.build());
        if (player.hasPermission("mcmmocredits.gui.admin")) {

        }
        main.build().open(PlayerViewer.of(player));
    }

    @CommandDescription("Open the Edit Messages Menu")
    @CommandMethod("menu messages")
    @CommandPermission("mcmmocredits.gui.admin")
    private void openMessagesMenu(Player player) {
        MenuDirector md = new MenuDirector(player);
        md.config(ConfigUtil.MESSAGES);
        md.build().open(PlayerViewer.of(player));
    }

    @CommandDescription("Open the Edit Settings Menu")
    @CommandMethod("menu settings")
    @CommandPermission("mcmmocredits.gui.admin")
    private void openSettingsMenu(Player player) {
        MenuDirector md = new MenuDirector(player);
        md.config(ConfigUtil.SETTINGS);
        md.build().open(PlayerViewer.of(player));
    }

    @CommandDescription("Open the Credit Redemption Menu")
    @CommandMethod("menu redeem")
    @CommandPermission("mcmmocredits.gui.redeem")
    private void openRedeemMenu(Player player) {
        MenuDirector md = new MenuDirector(player);
        md.redemption();
        md.build().open(PlayerViewer.of(player));
    }
}
