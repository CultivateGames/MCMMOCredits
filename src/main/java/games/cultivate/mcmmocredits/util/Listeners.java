package games.cultivate.mcmmocredits.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import games.cultivate.mcmmocredits.data.Database;
import games.cultivate.mcmmocredits.data.InputStorage;
import games.cultivate.mcmmocredits.keys.BooleanKey;
import games.cultivate.mcmmocredits.keys.StringKey;
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

import java.util.UUID;

/**
 * Event Handlers used to manage the database, and input storage.
 */
public class Listeners implements Listener {
    /**
     * Add users to MCMMO Credits database.
     *
     * @param e Instance of AsyncPlayerPreLoginEvent
     */
    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            PlayerProfile profile = e.getPlayerProfile();
            UUID uuid = profile.getId();
            String username = profile.getName();
            Database database = Database.getDatabase();
            if (!database.doesPlayerExist(uuid)) {
                database.addPlayer(uuid, username, 0);
                if (BooleanKey.ADD_NOTIFICATION.get()) {
                    TagResolver tr = Resolver.builder().player(username, uuid).build();
                    Text.fromKey(Bukkit.getConsoleSender(), StringKey.ADD_PLAYER_MESSAGE, tr).send();
                }
            }
            //Set the username on every allowed login to keep track of last known username.
            database.setUsername(uuid, username);
        }
    }

    /**
     * Update username if necessary and send login message if it is enabled.
     *
     * @param e Instance of the PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (BooleanKey.SEND_LOGIN_MESSAGE.get()) {
            Text.fromKey(e.getPlayer(), StringKey.LOGIN_MESSAGE).send();
        }
    }

    /**
     * Capture player's chat if the user is part of the Chat Input map.
     *
     * @param e Instance of the AsyncChatEvent.
     */
    @EventHandler
    public void onPlayerChat(AsyncChatEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        InputStorage storage = InputStorage.getInstance();
        if (storage.contains(uuid)) {
            String completion = MiniMessage.miniMessage().serialize(e.message());
            if (completion.equalsIgnoreCase("cancel")) {
                storage.remove(uuid);
                Text.fromKey(player, StringKey.CANCEL_PROMPT).send();
            }
            storage.complete(uuid, completion);
            e.setCancelled(true);
        }
    }

    /**
     * Terminate CompletableFuture from Chat Input map and remove the player.
     *
     * @param e Instance of the PlayerQuitEvent.
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        InputStorage.getInstance().remove(e.getPlayer().getUniqueId());
    }
}
