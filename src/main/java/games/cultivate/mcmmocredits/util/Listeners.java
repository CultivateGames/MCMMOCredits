package games.cultivate.mcmmocredits.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Keys;
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

    /**
     * Add users to MCMMO Credits database.
     * @param e Instance of AsyncPlayerPreLoginEvent
     */
    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        if (!MCMMOCredits.getAdapter().doesPlayerExist(e.getPlayerProfile().getId()) && e.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            PlayerProfile profile = e.getPlayerProfile();
            MCMMOCredits.getAdapter().addPlayer(profile.getId(), profile.getName(), 0);
            if (Keys.ADD_NOTIFICATION.getBoolean()) {
                ConfigHandler.sendMessage(Bukkit.getConsoleSender(), Keys.DATABASE_CONSOLE_MESSAGE, PlaceholderResolver.placeholders(Placeholder.miniMessage("player", Objects.requireNonNull(profile.getName()))));
            }
        }
    }

    /**
     * Update username if necessary and send login message if it is enabled.
     * @param e Instance of the PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        MCMMOCredits.getAdapter().setUsername(e.getPlayer().getUniqueId(), e.getPlayer().getName());
        if (Keys.SEND_LOGIN_MESSAGE.getBoolean()) {
            ConfigHandler.sendMessage(e.getPlayer(), Keys.LOGIN_MESSAGE, Util.basicBuilder(e.getPlayer()).build());
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
            Menus.inputMap.get(uuid).complete(MiniMessage.miniMessage().serialize(e.message()));
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
