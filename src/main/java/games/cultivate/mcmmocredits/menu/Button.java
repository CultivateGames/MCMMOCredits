package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.ItemType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public record Button(ItemStack item, int slot, String command) {
    public Button(ItemStack item, int slot) {
        this(item, slot, "");
    }

    public int x() {
        return slot % 9;
    }

    public int y() {
        return slot / 9;
    }

    public static Button of(ItemStack item, int slot) {
        return new Button(item, slot);
    }

    public static Button of(Config config, ItemType type, Player player) {
        return config.button(type, player);
    }

    public static Button of(Config config, ItemType type, Player player, String command, int slot) {
        return new Button(config.item(type, player), slot, command);
    }

    public static Button of(Config config, ItemType type, Player player, String command) {
        return new Button(config.item(type, player), config.itemSlot(type), command);
    }

    public <T, Z> void addToPDC(PersistentDataType<T, Z> data, Z value) {
        item.editMeta(meta -> meta.getPersistentDataContainer().set(MCMMOCredits.NAMESPACED_KEY, data, value));
    }

    public <T, Z> Z valueFromPDC(PersistentDataType<T, Z> data) {
        return item.getItemMeta().getPersistentDataContainer().get(MCMMOCredits.NAMESPACED_KEY, data);
    }
}
