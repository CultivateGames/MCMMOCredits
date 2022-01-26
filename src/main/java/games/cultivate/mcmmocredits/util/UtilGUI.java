package games.cultivate.mcmmocredits.util;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Keys;
import it.unimi.dsi.fastutil.Pair;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.core.util.Vector2;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.transform.PaperTransform;
import org.incendo.interfaces.paper.type.ChestInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class UtilGUI {
    private static final Style defaultStyle = Style.style().decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY).build();
    public static CompletableFuture<String> chatInputMessage = new CompletableFuture<>();

    public static ChestInterface.Builder main(Player player) {
        ChestInterface.Builder cb = interfaceTraits(player);
        cb = cb.addTransform(1, (pane, view) -> {
            Pair<ItemStack, Vector2> itemInfo = prepareItem(Keys.GUI_REDEMPTION.getItemStack(), player);
            return pane.element(ItemStackElement.of(itemInfo.left()), itemInfo.right().x(), itemInfo.right().y());
        });
        if (player.hasPermission("mcmmocredits.gui.admin")) {
            cb = cb.addTransform(2, (pane, view) -> {
                Pair<ItemStack, Vector2> itemInfo = prepareItem(Keys.GUI_SETTING_CHANGE.getItemStack(), player);
                return pane.element(ItemStackElement.of(itemInfo.left()), itemInfo.right().x(), itemInfo.right().y());
            });

            cb = cb.addTransform((pane, view) -> {
                Pair<ItemStack, Vector2> itemInfo = prepareItem(Keys.GUI_MESSAGE_CHANGE.getItemStack(), player);
                return pane.element(ItemStackElement.of(itemInfo.left(), (clickHandler) -> {
                    if (clickHandler.click().leftClick()) {
                        messageInterface(player).build().open(view.viewer());
                    } else {
                        view.viewer().close();
                    }
                }), itemInfo.right().x(), itemInfo.right().y());
            });
        }
        return cb;
    }

    public static ChestInterface.Builder interfaceTraits(Player player) {
        ChestInterface.Builder cb = ChestInterface.builder();
        if (Keys.GUI_FILL.getBoolean()) {
            cb = cb.addTransform(0, PaperTransform.chestFill(ItemStackElement.of(prepareFillItem(player))));
        }
        cb = cb.title(parse(player, MiniMessage.miniMessage().deserialize(Keys.GUI_TITLE.getString())));
        cb = cb.rows(Keys.GUI_SIZE.getInt() / 9);
        cb = cb.clickHandler(ClickHandler.cancel());
        cb = cb.updates(true, 10);
        return cb;
    }

    //Stub
    @SuppressWarnings("unused")
    public static ChestInterface.Builder settingInterface(Player player) {
        return ChestInterface.builder();
    }

    //Stub
    public static ChestInterface.Builder messageInterface(Player player) {
        ChestInterface.Builder cb = interfaceTraits(player);
        for (Map.Entry<ItemStack, Vector2> item : prepareMessageItems().entrySet()) {
            cb = cb.addTransform((pane, view) -> pane.element(ItemStackElement.of(item.getKey(), (clickHandler) -> {
                if (clickHandler.click().leftClick()) {
                    view.viewer().close();
                    String pathString = Objects.requireNonNull(clickHandler.cause().getCurrentItem()).getItemMeta().getPersistentDataContainer().get(MCMMOCredits.key, PersistentDataType.STRING);
                    Listeners.chatInputPlayers.add((Player) clickHandler.cause().getWhoClicked());
                    player.sendMessage(Component.newline());
                    assert pathString != null;
                    player.sendMessage(Component.text("Enter a new value for: ").color(NamedTextColor.RED).append(Component.text(pathString, defaultStyle).append(Component.text(" in chat."))));
                    player.sendMessage(Component.newline());
                    chatInputMessage.whenComplete((i, future) -> ConfigHandler.changeConfigInGame(StringUtils.split(pathString, "."), i));
                }
            }), item.getValue().getX(), item.getValue().getY()));
        }
        return cb.rows(3);
    }

    /*
     * i = slot, x = horizontal location, z = width (9), y = vertical location
     * i = x + (z * y);
     * x = i % z;
     * y = i / z;
     */
    protected static Map<ItemStack, Vector2> prepareMessageItems() {
        Map<ItemStack, Vector2> messageItems = new HashMap<>();
        String[] comments;
        List<Component> lore = new ArrayList<>();
        int slot = 0;
        for (Keys key : Keys.messageKeys) {
            ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
            if (slot >= 54) {
                break;
            }
            comments = StringUtils.split(key.node().comment(), "\n");

            lore.add(Component.text("Comment:", defaultStyle));
            if (comments != null) {
                for (String string : comments) {
                    lore.add(Component.text(string, defaultStyle));
                }
            } else {
                lore.add(Component.text("No comments found!", defaultStyle));
            }
            item.setType(Material.WRITABLE_BOOK);
            item.setAmount(slot + 1);
            item.editMeta(meta -> {
                meta.displayName(Component.text(StringUtils.join(key.path(), ".")));
                meta.lore(lore);
                meta.getPersistentDataContainer().set(MCMMOCredits.key, PersistentDataType.STRING, StringUtils.join(key.path(), "."));
            });
            messageItems.put(item, Vector2.at(slot % 9, slot / 9));

            slot++;
            lore.clear();
        }
        return messageItems;
    }

    protected static Pair<ItemStack, Vector2> prepareItem(ItemStack item, Player player) {
        int slot;
        item.editMeta(meta -> {
            meta.displayName(parse(player, meta.displayName()));
            meta.lore(parseList(player, Objects.requireNonNull(meta.lore())));
        });
        slot = item.getItemMeta().getPersistentDataContainer().getOrDefault(MCMMOCredits.key, PersistentDataType.INTEGER, 0);
        return Pair.of(item, Vector2.at(slot % 9, slot / 9));
    }

    protected static ItemStack prepareFillItem(Player player) {
        ItemStack item = Keys.GUI_FILL_ITEM.getItemStack();
        if (item.getItemMeta() != null) {
          ItemMeta meta = item.getItemMeta();
            meta.displayName(parse(player, meta.displayName()));
            meta.lore(parseList(player, Objects.requireNonNull(meta.lore())));
            item.setItemMeta(meta);
        }
        return item;
    }

    //This is abysmal, but I don't see how to get around this.
    private static Component parse(Player player, Component comp) {
        return MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, MiniMessage.miniMessage().serialize(comp)), Util.quickResolver(player));
    }

    private static List<Component> parseList(Player player, List<Component> comp) {
        return comp.stream().map(i -> MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, MiniMessage.miniMessage().serialize(i)), Util.quickResolver(player))).toList();
    }
}
