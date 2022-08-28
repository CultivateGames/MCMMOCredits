package games.cultivate.mcmmocredits.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import games.cultivate.mcmmocredits.config.GeneralConfig;
import games.cultivate.mcmmocredits.data.Database;
import games.cultivate.mcmmocredits.placeholders.ResolverFactory;
import games.cultivate.mcmmocredits.text.Text;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Event Handlers used to manage the database, and input storage.
 */
public class Listeners implements Listener {
    private final InputStorage storage;
    private final Database database;
    private final GeneralConfig config;
    private final ResolverFactory resolverFactory;

    @Inject
    public Listeners(final GeneralConfig config, final InputStorage storage, final Database database, final ResolverFactory resolverFactory) {
        this.config = config;
        this.storage = storage;
        this.database = database;
        this.resolverFactory = resolverFactory;
    }

    /**
     * Add users to MCMMO Credits database.
     *
     * @param e Instance of AsyncPlayerPreLoginEvent
     */
    @EventHandler
    public void onPlayerPreLogin(final AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            PlayerProfile profile = e.getPlayerProfile();
            UUID uuid = profile.getId();
            String username = profile.getName();
            if (!this.database.doesPlayerExist(uuid)) {
                this.database.addPlayer(uuid, username, 0);
                if (this.config.bool("addPlayerNotification")) {
                    TagResolver resolver = this.resolverFactory.fromUsers(Bukkit.getConsoleSender(), username);
                    String content = this.config.string("addPlayerMessage", false);
                    Text.fromString(Bukkit.getConsoleSender(), content, resolver).send();
                }
            }
            //Set the username on every allowed login to keep track of last known username.
            this.database.setUsername(uuid, username);
        }
    }

    /**
     * Send login message to user if it is enabled.
     *
     * @param e Instance of the PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        if (this.config.bool("sendLoginMessage")) {
            Player player = e.getPlayer();
            Text.fromString(player, this.config.string("loginMessage"), this.resolverFactory.fromUsers(player)).send();
        }
    }

    /**
     * Capture player's chat if the user is part of InputStorage.
     *
     * @param e Instance of the AsyncChatEvent.
     */
    @EventHandler
    public void onPlayerChat(final AsyncChatEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (this.storage.contains(uuid)) {
            String completion = MiniMessage.miniMessage().serialize(e.message());
            if (completion.equalsIgnoreCase("cancel")) {
                this.storage.remove(uuid);
                Text.fromString(player, this.config.string("cancelPrompt"), this.resolverFactory.fromUsers(player)).send();
            }
            this.storage.complete(uuid, completion);
            e.setCancelled(true);
        }
    }

    /**
     * Terminate CompletableFuture from Chat Input map and remove the player.
     *
     * @param e Instance of the PlayerQuitEvent.
     */
    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent e) {
        this.storage.remove(e.getPlayer().getUniqueId());
    }
}
