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
import games.cultivate.mcmmocredits.config.MenuSettings;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.util.Util;
import jakarta.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

//TODO: finalize with further refactoring.
final class DefaultMenuService implements MenuService {
    private final MCMMOCredits plugin;
    private final MenuSettings menuSettings;
    private final Map<UUID, Menu> menus;
    private final Map<UUID, CompletableFuture<String>> chatCache;

    @Inject
    public DefaultMenuService(final MCMMOCredits plugin, final MenuSettings menuSettings) {
        this.plugin = plugin;
        this.menuSettings = menuSettings;
        this.menus = new HashMap<>();
        this.chatCache = new HashMap<>();
    }

    @Override
    public void open(final User user) {
        Menu menu = RedeemMenu.of(this.menuSettings);
        this.menus.put(user.uuid(), menu);
        menu.open(this.plugin, user);
    }

    @Override
    public void close(final UUID uuid) {
        Menu menu = this.menus.remove(uuid);
        if (menu != null) {
            menu.close();
        }
    }

    @Override
    public void closeAll() {
        this.menus.values().forEach(Menu::close);
    }

    @Override
    public boolean isRegistered(final UUID uuid) {
        return this.chatCache.containsKey(uuid);
    }

    @Override
    public void registerUser(final UUID uuid) {
        this.chatCache.put(uuid, new CompletableFuture<>());
    }

    @Override
    public void unregisterUser(final UUID uuid) {
        CompletableFuture<String> future = this.chatCache.remove(uuid);
        if (future != null) {
            future.complete(null);
        }
    }

    @Override
    public void completeChat(final User user, final Component chat) {
        String message = PlainTextComponentSerializer.plainText().serialize(chat);
        UUID uuid = user.uuid();
        if (message.equalsIgnoreCase("cancel")) {
            this.unregisterUser(uuid);
            //user.sendText(this.captions.caption(Captions.CANCEL_TEXT_PROMPT));
            return;
        }
        this.chatCache.get(uuid).complete(message);
    }

    @Override
    public void handleClick(final User user, final int slot) {
        String key = this.menus.get(user.uuid()).getItemEntry(slot).getKey();
        if (key == null) {
            return;
        }
        if (key.equalsIgnoreCase("navigation")) {
            this.onCommandClick(user);
        }
        if (Util.getSkillNames().contains(key)) {
            this.onRedeemClick(user, PrimarySkillType.valueOf(key.toUpperCase()));
        }
    }

    private void onCommandClick(final User user) {
        if (user.sender() instanceof Player p) {
            p.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            Bukkit.dispatchCommand(p, this.menuSettings.command());
        }
    }

    private void onRedeemClick(final User user, final PrimarySkillType skill) {
        if (user.sender() instanceof Player player) {
            player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            //user.sendText(this.captions.caption(Captions.REDEEM_PROMPT), r -> r.addSkill(skill));
            UUID uuid = user.uuid();
            this.registerUser(uuid);
            this.chatCache.get(uuid)
                    .thenApply(Integer::parseInt)
                    //.thenAccept(amount -> this.transactionService.redeem(user, skill, amount, false))
                    .whenComplete((v, t) -> this.unregisterUser(uuid));
        }
    }
}
