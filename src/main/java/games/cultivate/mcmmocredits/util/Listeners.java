package games.cultivate.mcmmocredits.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import games.cultivate.mcmmocredits.config.GeneralConfig;
import games.cultivate.mcmmocredits.data.Database;
import games.cultivate.mcmmocredits.placeholders.Resolver;
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

    @Inject
    public Listeners(final GeneralConfig config, final InputStorage storage, final Database database) {
        this.config = config;
        this.storage = storage;
        this.database = database;
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
                    TagResolver resolver = Resolver.builder().player(username).build();
                    String content = this.config.string("addPlayerMessage", false);
                    Text.fromString(Bukkit.getConsoleSender(), content, resolver).send();
                }
            }
            //Set the username on every allowed login to keep track of last known username.
            this.database.setUsername(uuid, username);
        }
    }

    /**
     * Update username if necessary and send login message if it is enabled.
     *
     * @param e Instance of the PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        if (this.config.bool("sendLoginMessage")) {
            Text.fromString(e.getPlayer(), this.config.string("loginMessage")).send();
        }
    }

    /**
     * Capture player's chat if the user is part of the Chat Input map.
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
                Text.fromString(player, this.config.string("cancelPrompt")).send();
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
