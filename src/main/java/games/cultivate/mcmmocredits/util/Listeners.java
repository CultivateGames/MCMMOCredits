package games.cultivate.mcmmocredits.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.database.Database;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.placeholder.Placeholder;
import net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;
import java.util.UUID;

/**
 * This class is responsible for any necessary Event Listeners that we may need.
 */
public class Listeners implements Listener {
    private Database database;

    public Listeners(Database database) {
        this.database = database;
    }
    
    /**
     * Add users to MCMMO Credits database.
     * @param e Instance of AsyncPlayerPreLoginEvent
     */
    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        PlayerProfile profile = e.getPlayerProfile();
        UUID uuid = profile.getId();
        String username = profile.getName();
        if (!this.database.doesPlayerExist(profile.getId()) && e.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            this.database.addPlayer(uuid, username, 0);
            if (Keys.ADD_NOTIFICATION.get()) {
                Util.sendMessage(Bukkit.getConsoleSender(), Keys.ADD_PLAYER_MESSAGE.get(), PlaceholderResolver.placeholders(Placeholder.miniMessage("player", Objects.requireNonNull(profile.getName()))));
            }
            return;
        }
        this.database.setUsername(uuid, username);
    }

    /**
     * Update username if necessary and send login message if it is enabled.
     * @param e Instance of the PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (Keys.SEND_LOGIN_MESSAGE.get()) {
            Util.sendMessage(e.getPlayer(), Keys.LOGIN_MESSAGE.get(), Util.basicBuilder(e.getPlayer()).build());
        }
    }

    /**
     * Capture player's chat if the user is part of the Chat Input map.
     * @param e Instance of the AsyncChatEvent.
     */
    @EventHandler
    public void onPlayerChat(AsyncChatEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (Menus.inputMap.containsKey(uuid)) {
            String completion = MiniMessage.miniMessage().serialize(e.message());
            if (completion.equalsIgnoreCase("cancel")) {
                Menus.inputMap.get(uuid).complete(null);
                Menus.inputMap.remove(uuid);
                Util.sendMessage(e.getPlayer(), Keys.CANCEL_PROMPT.get(), Util.quickResolver(e.getPlayer()));
            } else {
                Menus.inputMap.get(uuid).complete(completion);
            }
            e.setCancelled(true);
        }
    }

    /**
     * Terminate CompletableFuture from Chat Input map and remove the player.
     * @param e Instance of the PlayerQuitEvent.
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (Menus.inputMap.containsKey(uuid)) {
            Menus.inputMap.get(uuid).complete(null);
            Menus.inputMap.remove(uuid);
        }
    }
}
