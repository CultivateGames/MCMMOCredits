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

import games.cultivate.mcmmocredits.config.MenuSettings;
import games.cultivate.mcmmocredits.messages.Text;
import games.cultivate.mcmmocredits.user.User;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Represents a Bukkit Inventory with populated item slots.
 */
@ConfigSerializable
public final class RedeemMenu implements Menu {
    private final String title;
    private final int size;
    private final Map<String, Item> items;
    private transient Inventory inventory;
    private transient ScheduledTask task;

    private RedeemMenu(final Map<String, Item> items, final String title, final int size, final boolean fillEnabled, final boolean navigationEnabled) {
        this.items = items;
        this.title = title;
        this.size = size;
        this.task = null;
        this.inventory = Bukkit.createInventory(this, this.size);
        //TODO: check
        if (!navigationEnabled) {
            items.remove("navigation");
        }
        Item filler = items.remove("fill");
        Set<Integer> slots = items.values().stream().map(Item::slot).collect(Collectors.toSet());
        if (fillEnabled) {
            IntStream.range(0, size).filter(i -> !slots.contains(i)).forEach(i -> items.put("fill" + i, filler.withSlot(i)));
        }
    }

    public static RedeemMenu of(final MenuSettings settings) {
        return new RedeemMenu(settings.items(), settings.title(), settings.size(), settings.fill(), settings.navigation());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public Map<String, Item> items() {
        return this.items;
    }

    @Override
    public void open(final JavaPlugin plugin, final User user) {
        this.inventory = Bukkit.createInventory(this, this.size, Text.forOneUser(user, this.title).toComponent());
        this.draw(user);
        Bukkit.getGlobalRegionScheduler().run(plugin, x -> ((Player) user.sender()).openInventory(this.inventory));
        //TODO: might need to use global region scheduler.
        this.task = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, x -> this.draw(user), 50, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void close() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }

    private void draw(final User user) {
        this.items.values().forEach(x -> this.inventory.setItem(x.slot(), x.getItemFor(user)));
    }
}
