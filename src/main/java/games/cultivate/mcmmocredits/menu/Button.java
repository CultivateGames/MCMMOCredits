package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.keys.ItemStackKey;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public record Button(ItemStack item, int slot, String command) {
    private static final NamespacedKey NAMESPACED_KEY = Objects.requireNonNull(NamespacedKey.fromString("mcmmocredits"));

    public int x() {
        return slot % 9;
    }

    public int y() {
        return slot / 9;
    }

    public <T, Z> void addToPDC(PersistentDataType<T, Z> data, Z value) {
        item.editMeta(meta -> meta.getPersistentDataContainer().set(NAMESPACED_KEY, data, value));
    }

    public <T, Z> Z valueFromPDC(PersistentDataType<T, Z> data) {
        return item.getItemMeta().getPersistentDataContainer().get(NAMESPACED_KEY, data);
    }

    public static Button of(ItemStackKey key, Player player, String command) {
        return new Button(key.get(player), key.slot(), command);
    }

    public static Button of(ItemStackKey key, Player player) {
        return new Button(key.get(player), key.slot(), "");
    }
}
