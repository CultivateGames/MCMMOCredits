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
import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.data.UserDAO;
import games.cultivate.mcmmocredits.events.CreditRedemptionEvent;
import games.cultivate.mcmmocredits.events.CreditTransactionEvent;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.Console;
import games.cultivate.mcmmocredits.user.User;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
    private final ChatQueue storage;
    private final UserDAO dao;
    private final MainConfig config;

    @Inject
    public Listeners(final MainConfig config, final ChatQueue storage, final UserDAO dao) {
        this.config = config;
        this.storage = storage;
        this.dao = dao;
    }

    /**
     * Add users to MCMMO Credits database, and updates username for existing records.
     *
     * @param e The event.
     */
    @EventHandler
    public void onPlayerPreLogin(final AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            var profile = e.getPlayerProfile();
            UUID uuid = profile.getId();
            String username = profile.getName();
            Optional<User> user = this.dao.getUser(uuid);
            if (user.isPresent()) {
                this.dao.setUsername(uuid, username);
                return;
            }
            this.dao.addUser(new User(uuid, username, 0, 0));
            if (this.config.bool("settings", "add-user-message")) {
                Text.forOneUser(Console.INSTANCE, this.config.string("add-user")).send();
            }
        }
    }

    /**
     * Sends login message to user if enable in configuration.
     *
     * @param e The event.
     */
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        if (this.config.bool("settings", "send-login-message")) {
            User user = this.dao.forceUser(e.getPlayer().getUniqueId());
            Text.forOneUser(user, this.config.string("login-message")).send();
        }
    }

    /**
     * Captures chat of a user if their {@link UUID} is in the {@link ChatQueue}.
     *
     * @param e The event.
     */
    @EventHandler
    public void onPlayerChat(final AsyncChatEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (this.storage.contains(uuid)) {
            String completion = PlainTextComponentSerializer.plainText().serialize(e.message());
            if (completion.equalsIgnoreCase("cancel")) {
                this.storage.remove(uuid);
                User user = this.dao.forceUser(uuid);
                Text.forOneUser(user, this.config.string("cancel-prompt")).send();
            }
            this.storage.complete(uuid, completion);
            e.setCancelled(true);
        }
    }

    /**
     * Removes all users from the {@link ChatQueue} if present.
     *
     * @param e The event.
     */
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        this.storage.remove(e.getPlayer().getUniqueId());
    }

    /**
     * Performs Credit Transaction with information derived from the event.
     *
     * @param e The event.
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void performTransaction(final CreditTransactionEvent e) {
        UUID uuid = e.uuid();
        int amount = e.amount();
        CreditOperation operation = e.operation();
        boolean transactionStatus = switch (operation) {
            case ADD -> this.dao.addCredits(uuid, amount);
            case SET -> this.dao.setCredits(uuid, amount);
            case TAKE -> this.dao.takeCredits(uuid, amount);
        };
        CommandSender sender = e.sender();
        Resolver resolver = this.updateTransactionResolver(sender, uuid, amount);
        if (!transactionStatus) {
            Text.fromString(sender, this.config.string("not-enough-credits"), resolver).send();
            return;
        }
        String content = "credits-" + operation;
        Text.fromString(sender, this.config.string(content), resolver).send();
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && !e.isSilent()) {
            Text.fromString(player, this.config.string(content + "-user"), resolver).send();
        }
    }

    /**
     * Performs Credit Redemption with information derived from the event.
     *
     * @param e The event.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void performRedemption(final CreditRedemptionEvent e) {
        UUID uuid = e.uuid();
        int amount = e.amount();
        CommandSender sender = e.sender();
        PrimarySkillType skill = e.skill();
        Resolver resolver = this.updateRedeemResolver(sender, uuid, skill, amount);
        if (this.dao.getCredits(uuid) < amount) {
            Text.fromString(sender, this.config.string("not-enough-credits"), resolver).send();
            return;
        }
        Player player = Bukkit.getPlayer(uuid);
        PlayerProfile profile = player != null ? UserManager.getPlayer(player).getProfile() : mcMMO.getDatabaseManager().loadPlayerProfile(uuid);
        if (!profile.isLoaded()) {
            Text.fromString(sender, this.config.string("mcmmo-profile-fail"), resolver).send();
            return;
        }
        int currentLevel = profile.getSkillLevel(skill);
        if (currentLevel + amount > mcMMO.p.getGeneralConfig().getLevelCap(skill)) {
            Text.fromString(sender, this.config.string("mcmmo-skill-cap"), resolver).send();
            return;
        }
        this.dao.redeemCredits(uuid, amount);
        profile.modifySkill(skill, currentLevel + amount);
        profile.save(true);
        resolver = this.updateRedeemResolver(sender, uuid, skill, amount);
        if (sender instanceof Player p && p.getUniqueId().equals(uuid)) {
            Text.fromString(sender, this.config.string("redeem"), resolver).send();
            return;
        }
        Text.fromString(sender, this.config.string("redeem-sudo"), resolver).send();
        if (!e.isSilent() && player != null) {
            Text.fromString(player, this.config.string("redeem-sudo-user"), resolver).send();
        }
    }

    /**
     * Updates the Resolver used for message parsing as the information changes during event processing.
     *
     * @param sender CommandSender from event.
     * @param uuid   UUID from event.
     * @param skill  MCMMO skill from event.
     * @param amount amount of credits from event.
     * @return The updated resolver.
     */
    private Resolver updateRedeemResolver(final CommandSender sender, final UUID uuid, final PrimarySkillType skill, final int amount) {
        CommandExecutor executor = this.dao.fromSender(sender);
        return Resolver.fromRedemption(executor, this.dao.forceUser(uuid), skill, amount);
    }

    /**
     * Updates the Resolver used for message parsing as the information changes during event processing.
     *
     * @param sender CommandSender from event.
     * @param uuid   UUID from event.
     * @param amount amount of credits from event.
     * @return The updated resolver.
     */
    private Resolver updateTransactionResolver(final CommandSender sender, final UUID uuid, final int amount) {
        CommandExecutor executor = this.dao.fromSender(sender);
        User target = this.dao.getUser(uuid).orElseThrow();
        return Resolver.builder().transaction(amount).users(executor, target).build();
    }
}
