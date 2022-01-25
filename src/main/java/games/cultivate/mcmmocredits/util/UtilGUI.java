package games.cultivate.mcmmocredits.util;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.Keys;
import it.unimi.dsi.fastutil.Pair;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.core.util.Vector2;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.transform.PaperTransform;
import org.incendo.interfaces.paper.type.ChestInterface;

import java.util.List;
import java.util.Objects;

//TODO separate out item logic into methods
public class UtilGUI {
    public static ChestInterface.Builder mainInterface(Player player) {
        ChestInterface.Builder cb = ChestInterface.builder();
        cb = cb.addTransform(1, (pane, view) -> {
            Pair<ItemStack, Vector2> itemInfo = prepareItem(Keys.GUI_REDEMPTION.getItemStack(), player);
            return pane.element(ItemStackElement.of(itemInfo.left()), itemInfo.right().x(), itemInfo.right().y());
        });
        if (Keys.GUI_FILL_ITEM.getBoolean()) {
            cb = cb.addTransform(0, PaperTransform.chestFill(ItemStackElement.of(Keys.GUI_FILL_ITEM.getItemStack())));
        }
        if (player.hasPermission("mcmmocredits.gui.admin")) {
            cb = cb.addTransform(2, (pane, view) -> {
                Pair<ItemStack, Vector2> itemInfo = prepareItem(Keys.GUI_SETTING_CHANGE.getItemStack(), player);
                return pane.element(ItemStackElement.of(itemInfo.left()), itemInfo.right().x(), itemInfo.right().y());
            });

            cb = cb.addTransform((pane, view) -> {
                Pair<ItemStack, Vector2> itemInfo = prepareItem(Keys.GUI_MESSAGE_CHANGE.getItemStack(), player);
                return pane.element(ItemStackElement.of(itemInfo.left(), (clickHandler) -> {
                    if (!clickHandler.click().leftClick()) {
                        PlayerViewer.of(player).close();
                    } else {
                        messageInterface(player).build().open(PlayerViewer.of(player));
                    }
                }), itemInfo.right().x(), itemInfo.right().y());
            });
        }
        cb = cb.title(parse(player, MiniMessage.miniMessage().deserialize(Keys.GUI_TITLE.getString())));
        cb = cb.rows(Keys.GUI_SIZE.getInt() / 9);
        cb = cb.clickHandler(ClickHandler.cancel());
        return cb;
    }

    //Stub
    public static ChestInterface.Builder messageInterface(Player player) {
        return ChestInterface.builder();
    }

    /*
     * i = slot, x = horizontal location, z = width (9), y = vertical location
     * i = x + (z * y);
     * x = i % z;
     * y = i / z;
     */
    protected static Pair<ItemStack, Vector2> prepareMessageItems(Player player) {
        ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
        //Stub
        int slot;
        if (item.hasItemMeta()) {
            item.editMeta(meta -> {
                meta.displayName();
                meta.lore();
            });
            slot = item.getItemMeta().getPersistentDataContainer().getOrDefault(MCMMOCredits.key, PersistentDataType.INTEGER, 0);
            return Pair.of(item, Vector2.at(slot % 9, slot / 9));
        }
        return Pair.of(item, Vector2.at(0, 0));
    }

    protected static Pair<ItemStack, Vector2> prepareItem(ItemStack item, Player player) {
        int slot;
        if (item.hasItemMeta()) {
            item.editMeta(meta -> {
                meta.displayName(parse(player, meta.displayName()));
                meta.lore(parseList(player, Objects.requireNonNull(meta.lore())));
            });
            slot = item.getItemMeta().getPersistentDataContainer().getOrDefault(MCMMOCredits.key, PersistentDataType.INTEGER, 0);
            return Pair.of(item, Vector2.at(slot % 9, slot / 9));
        }
        return Pair.of(item, Vector2.at(0, 0));
    }

    //This is abysmal, but I don't see how to get around this.
    private static Component parse(Player player, Component comp) {
        return MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, MiniMessage.miniMessage().serialize(comp)), Util.quickResolver(player));
    }

    private static List<Component> parseList(Player player, List<Component> comp) {
        return comp.stream().map(i -> MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, MiniMessage.miniMessage().serialize(i)), Util.quickResolver(player))).toList();
    }
}
