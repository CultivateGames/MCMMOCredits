package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.util.ChatQueue;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.pane.ChestPane;

import javax.inject.Inject;

/**
 * Used to encapsulate building {@link ClickHandler} instances.
 */
public final class ClickFactory {
    private final ChatQueue queue;
    private final MainConfig config;
    private final MCMMOCredits plugin;

    /**
     * Constructs the object.
     *
     * @param config Instance of the MainConfig.
     * @param queue  Instance of the ChatQueue.
     * @param plugin Instance of the plugin.
     */
    @Inject
    public ClickFactory(final ChatQueue queue, final MainConfig config, final MCMMOCredits plugin) {
        this.queue = queue;
        this.config = config;
        this.plugin = plugin;
    }

    private ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer,
            ClickContext<ChestPane, InventoryClickEvent, PlayerViewer>> buildRedeemClick(final Resolver resolver, final String skill) {
        return this.closeInventory().andThen(click -> {
            resolver.addResolver("skill", WordUtils.capitalizeFully(skill));
            Player player = click.viewer().player();
            Text.fromString(player, this.config.string("redeem-prompt"), resolver).send();
            this.queue.act(player.getUniqueId(), i -> this.executeCommand(player, String.format("credits redeem %d %s", Integer.parseInt(i), skill.toLowerCase())));
        });
    }

    private ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer,
            ClickContext<ChestPane, InventoryClickEvent, PlayerViewer>> buildCommandClick(final String command) {
        return click -> this.executeCommand(click.viewer().player(), command);
    }

    private ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer,
            ClickContext<ChestPane, InventoryClickEvent, PlayerViewer>> buildConfigClick(final Resolver resolver, final Object... path) {
        return this.closeInventory().andThen(click -> {
            resolver.addResolver("setting", this.config.translateNode(path));
            Player player = click.viewer().player();
            Text.fromString(player, this.config.string("edit-config-prompt"), resolver).send();
            this.queue.act(player.getUniqueId(), i -> {
                boolean status = this.config.modify(i, path);
                resolver.addResolver("change", i);
                Text.fromString(player, this.config.string(status ? "edit-config" : "edit-config-fail"), resolver).send();
            });
        });
    }

    /**
     * Common ClickHandler to close the viewer's inventory.
     *
     * @return The ClickHandler.
     */
    private ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer,
            ClickContext<ChestPane, InventoryClickEvent, PlayerViewer>> closeInventory() {
        return click -> click.viewer().player().closeInventory();
    }

    /**
     * Executes commands using the main thread executor as a workaround to Cloud.
     *
     * @param player  The player who dispatches the command.
     * @param command The command.
     */
    private void executeCommand(final Player player, final String command) {
        Bukkit.getScheduler().getMainThreadExecutor(this.plugin).execute(() -> Bukkit.dispatchCommand(player, command));
    }

    public ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer,
            ClickContext<ChestPane, InventoryClickEvent, PlayerViewer>> getClick(final ClickTypes type, final String data, final Resolver resolver) {
       return switch (type) {
            case FILL -> ClickHandler.cancel();
            case REDEEM -> this.buildRedeemClick(resolver, data);
            case COMMAND -> this.buildCommandClick(data);
            case EDIT_SETTING, EDIT_MESSAGE -> this.buildConfigClick(resolver, (Object[]) data.split("\\."));
        };
    }
}
