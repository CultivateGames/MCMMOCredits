//
// MIT License
//
// Copyright (c) 2023 Cultivate Games
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package games.cultivate.mcmmocredits.util;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import games.cultivate.mcmmocredits.config.GeneralConfig;
import games.cultivate.mcmmocredits.data.UserDAO;
import games.cultivate.mcmmocredits.events.CreditRedemptionEvent;
import games.cultivate.mcmmocredits.events.CreditTransactionEvent;
import games.cultivate.mcmmocredits.placeholders.ResolverFactory;
import games.cultivate.mcmmocredits.text.Text;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

/**
 * Event Handlers used to manage the database, and input storage.
 */
public class Listeners implements Listener {
    private final InputStorage storage;
    private final UserDAO dao;
    private final GeneralConfig config;
    private final ResolverFactory resolverFactory;

    @Inject
    public Listeners(final GeneralConfig config, final InputStorage storage, final UserDAO dao, final ResolverFactory resolverFactory) {
        this.config = config;
        this.storage = storage;
        this.dao = dao;
        this.resolverFactory = resolverFactory;
    }

    /**
     * Add users to MCMMO Credits database, and updates username for existing records.
     *
     * @param e Instance of {@link AsyncPlayerPreLoginEvent}
     */
    @EventHandler
    public void onPlayerPreLogin(final AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            //fully qualified import due to conflict.
            com.destroystokyo.paper.profile.PlayerProfile profile = e.getPlayerProfile();
            UUID uuid = profile.getId();
            String username = profile.getName();
            Optional<User> user = this.dao.getUser(uuid);
            if (user.isPresent()) {
                this.dao.setUsername(uuid, username);
                return;
            }
            this.dao.addUser(new User(uuid, username, 0, 0));
            if (this.config.bool("addPlayerNotification")) {
                TagResolver resolver = this.resolverFactory.fromUsers(Bukkit.getConsoleSender(), username);
                String content = this.config.string("addPlayerMessage", false);
                Text.fromString(Bukkit.getConsoleSender(), content, resolver).send();
            }
        }
    }

    /**
     * Send login message to user if it is enabled.
     *
     * @param e Instance of the {@link PlayerJoinEvent}
     */
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        if (this.config.bool("sendLoginMessage")) {
            Player player = e.getPlayer();
            Text.fromString(player, this.config.string("loginMessage"), this.resolverFactory.fromUsers(player)).send();
        }
    }

    /**
     * Capture player's chat if the user is part of {@link InputStorage}
     *
     * @param e Instance of the {@link AsyncChatEvent}
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
     * Removes the user from the {@link InputStorage} if they are being tracked.
     *
     * @param e Instance of the {@link PlayerQuitEvent}.
     */
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        this.storage.remove(e.getPlayer().getUniqueId());
    }

    /**
     * Implements the credit transactions initiated by commands in the plugin.
     * If external plugin event calls are not taking effect, change the {@link EventPriority} of your call as needed.
     *
     * @param e Instance of the {@link CreditTransactionEvent}
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void performTransaction(final CreditTransactionEvent e) {
        //Get information about the event.
        UUID uuid = e.user().uuid();
        int amount = e.amount();
        CreditOperation operation = e.operation();
        //Determine transaction status based on Database transaction.
        boolean transactionStatus = switch (operation) {
            case ADD -> this.dao.addCredits(uuid, amount);
            case SET -> this.dao.setCredits(uuid, amount);
            case TAKE -> this.dao.takeCredits(uuid, amount);
        };
        CommandSender sender = e.sender();
        //If transaction is successful, make the resolver and send the message to sender.
        if (transactionStatus) {
            TagResolver resolver = this.resolverFactory.fromTransaction(e);
            Text.fromString(sender, this.config.string(operation + "Sender"), resolver).send();
            Player player = Bukkit.getPlayer(uuid);
            //If the player isn't null and transaction is NOT silent, send the receiver message.
            if (player != null && !e.isSilent()) {
                Text.fromString(player, this.config.string(operation + "Receiver"), resolver).send();
            }
            return;
        }
        Text.fromString(sender, this.config.string("notEnoughCredits"), this.resolverFactory.fromUsers(sender)).send();
    }

    /**
     * Implements the credit redemption process initiated by the commands in the plugin.
     * If external plugin event calls are not taking effect, change the {@link EventPriority} of your call as needed.
     *
     * @param e Instance of the {@link CreditRedemptionEvent}
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void performRedemption(final CreditRedemptionEvent e) {
        //Get information about the event.
        UUID uuid = e.user().uuid();
        int amount = e.amount();
        CommandSender sender = e.sender();
        TagResolver resolver = this.resolverFactory.fromRedemption(e);
        //Check if the user has enough credits to redeem the amount specified.
        if (this.dao.getCredits(uuid) < amount) {
            Text.fromString(sender, this.config.string("notEnoughCredits"), resolver).send();
            return;
        }
        Player player = Bukkit.getPlayer(uuid);
        PlayerProfile profile = player != null ? UserManager.getPlayer(player).getProfile() : mcMMO.getDatabaseManager().loadPlayerProfile(uuid);
        if (!profile.isLoaded()) {
            //send a message if loading mcmmo profile fails. This may need to be a new configuration message.
            Text.fromString(sender, this.config.string("playerDoesNotExist"), resolver).send();
            return;
        }
        PrimarySkillType skill = e.skill();
        int currentLevel = profile.getSkillLevel(skill);
        if (currentLevel + amount > mcMMO.p.getGeneralConfig().getLevelCap(skill)) {
            Text.fromString(sender, this.config.string("skillCap"), resolver).send();
            return;
        }
        //Take credits first in case of issue with code. Easier to restore credits than previous skill levels.
        if (this.dao.takeCredits(uuid, amount)) {
            //Modify skill level if credit withdrawal is successful.
            profile.modifySkill(skill, currentLevel + amount);
            profile.save(true);
            this.dao.addRedeemedCredits(uuid, amount);
            //Rebuild TagResolver as information has changed pertaining to users.
            resolver = this.resolverFactory.fromRedemption(e);
            //If the sender is equal to the recipient, send the message for self redemption and return.
            if (sender instanceof Player p && p.getUniqueId().equals(uuid)) {
                Text.fromString(sender, this.config.string("selfRedeem"), resolver).send();
                return;
            }
            Text.fromString(sender, this.config.string("otherRedeemSender"), resolver).send();
            if (!e.isSilent() && player != null) {
                Text.fromString(player, this.config.string("otherRedeemReceiver"), resolver).send();
            }
        }
    }
}
