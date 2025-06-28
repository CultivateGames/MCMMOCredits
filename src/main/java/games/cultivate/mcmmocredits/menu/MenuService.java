//
// MIT License
//
// Copyright (c) 2024 Cultivate Games
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
package games.cultivate.mcmmocredits.menu;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.Captions;
import games.cultivate.mcmmocredits.messages.MessageService;
import games.cultivate.mcmmocredits.user.CommandSourceData;
import games.cultivate.mcmmocredits.user.UserService;
import jakarta.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public final class MenuService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MenuService.class);
    private final MCMMOCredits plugin;
    private final MenuProperties menuProperties;
    private final Map<UUID, Menu> menus;
    private final Map<UUID, CompletableFuture<String>> chatCache;
    private final MessageService messageService;
    private final UserService userService;

    @Inject
    public MenuService(final MCMMOCredits plugin, final MenuProperties menuProperties, final MessageService messageService, final UserService userService) {
        this.plugin = plugin;
        this.menuProperties = menuProperties;
        this.menus = new HashMap<>();
        this.chatCache = new HashMap<>();
        this.messageService = messageService;
        this.userService = userService;
    }

    /**
     * Opens an instance of the Menu for the provided player.
     *
     * @param player The player.
     */
    public void open(final Player player) {
        Menu menu = new Menu(this.menuProperties);
        menu.open(this.plugin, player);
        this.menus.put(player.getUniqueId(), menu);
    }

    /**
     * Closes the menu for the player at the specified id.
     *
     * @param uuid The uuid.
     */
    public void close(final UUID uuid) {
        Menu menu = this.menus.remove(uuid);
        if (menu != null) {
            menu.close();
        }
    }

    /**
     * Closes all instances of the menu that are currently tracked.
     */
    public void closeAll() {
        this.menus.values().forEach(Menu::close);
    }

    /**
     * If the user with the provided uuid is registered to the service.
     *
     * @param uuid The UUID of the user.
     * @return If they are registered.
     */
    public boolean isRegistered(final UUID uuid) {
        return this.chatCache.containsKey(uuid);
    }

    /**
     * Registers a user to be tracked by the service.
     *
     * @param uuid UUID of the user being tracked.
     */
    public void registerUser(final UUID uuid) {
        this.chatCache.put(uuid, new CompletableFuture<>());
    }

    /**
     * Unregisters a user to be tracked by the service.
     *
     * @param uuid UUID of the user no longer being tracked.
     */
    public void unregisterUser(final UUID uuid) {
        CompletableFuture<String> future = this.chatCache.remove(uuid);
        if (future != null) {
            future.complete(null);
        }
    }

    /**
     * Stores provided chat using the provided player for one message.
     *
     * @param player The player.
     * @param chat   The chat message to store.
     */
    public void completeChat(final Player player, final Component chat) {
        String message = PlainTextComponentSerializer.plainText().serialize(chat);
        UUID uuid = player.getUniqueId();
        if (message.equalsIgnoreCase("cancel")) {
            this.unregisterUser(uuid);
            this.messageService.send(Captions.CANCEL_TEXT_PROMPT, player);
            return;
        }
        this.chatCache.get(uuid).complete(message);
    }

    /**
     * Handles a click at the provided slot by the provided player.
     *
     * @param player The player who clicked.
     * @param slot   The slot clicked by the player.
     */
    public void handleClick(final Player player, final int slot) {
        String key = this.menus.get(player.getUniqueId()).itemKey(slot);
        switch (key) {
            case String s when s.equalsIgnoreCase("fill") -> {}
            case String s when s.equalsIgnoreCase("navigation") -> this.onCommandClick(player);
            case String s when Stream.of(PrimarySkillType.values()).anyMatch(e -> e.name().equalsIgnoreCase(key)) -> {
                PrimarySkillType skill = PrimarySkillType.valueOf(s);
                this.onRedeemClick(player, skill);
            }
            case null, default -> LOGGER.error("Invalid config key found in menu, not mapped to a valid item type! Menu item keys must be navigation, fill or a mcMMO skill type!", new IllegalArgumentException());
        }
    }

    private void onCommandClick(final Player player) {
        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        Bukkit.dispatchCommand(player, this.menuProperties.command());
    }

    private void onRedeemClick(final Player player, final PrimarySkillType skill) {
        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        CommandSourceData data = this.userService.getOrCreate(player);
        this.messageService.get(Captions.REDEEM_PROMPT).addSkill(skill).addUser(data).send(player);
        UUID uuid = player.getUniqueId();
        this.registerUser(uuid);
        String command = "credits redeem " + skill.name().toLowerCase(Locale.ROOT);
        this.chatCache.get(uuid)
                .thenAccept(i -> Bukkit.dispatchCommand(player, command + " " + i))
                .whenComplete((_, _) -> this.unregisterUser(uuid));
    }
}
