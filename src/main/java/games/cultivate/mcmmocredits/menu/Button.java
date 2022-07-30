package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.ItemType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public record Button(ItemStack item, int slot, String command) {
    public Button(final ItemStack item, final int slot) {
        this(item, slot, "");
    }

    public int x() {
        return this.slot % 9;
    }

    public int y() {
        return this.slot / 9;
    }

    public static Button of(final ItemStack item, final int slot) {
        return new Button(item, slot);
    }

    public static Button of(final Config config, final ItemType type, final Player player) {
        return config.button(type, player);
    }

    public static Button of(final Config config, final ItemType type, final Player player, final String command, final int slot) {
        return new Button(config.item(type, player), slot, command);
    }

    public static Button of(final Config config, final ItemType type, final Player player, final String command) {
        return new Button(config.item(type, player), config.itemSlot(type), command);
    }

    public <T, Z> void addToPDC(final PersistentDataType<T, Z> data, final Z value) {
        this.item.editMeta(meta -> meta.getPersistentDataContainer().set(MCMMOCredits.NAMESPACED_KEY, data, value));
    }

    public <T, Z> Z valueFromPDC(final PersistentDataType<T, Z> data) {
        return this.item.getItemMeta().getPersistentDataContainer().get(MCMMOCredits.NAMESPACED_KEY, data);
    }
}
