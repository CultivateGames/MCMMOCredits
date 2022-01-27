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
import org.bukkit.persistence.PersistentDataType;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.core.util.Vector2;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.type.ChestInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class GUI {
    private static final Style defaultStyle = Style.style().decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY).build();
    public static Map<UUID, CompletableFuture<String>> inputMap = new HashMap<>();

    public static ChestInterface.Builder main(Player player) {
        ChestInterface.Builder cb = interfaceTraits(player, Keys.GUI_SIZE.getInt() / 9);
        //Manually fill the GUI each time to ensure item updates, and proper filling order.
        cb = cb.addTransform(1, (pane, view) -> {
            Pair<ItemStack, Vector2> itemInfo = prepareItem(Keys.GUI_REDEMPTION.getItemStack(player));
            return pane.element(ItemStackElement.of(itemInfo.left()), itemInfo.right().x(), itemInfo.right().y());
        });

        if (player.hasPermission("mcmmocredits.gui.admin")) {
            cb = cb.addTransform(1, (pane, view) -> {
                Pair<ItemStack, Vector2> itemInfo = prepareItem(Keys.GUI_SETTING_CHANGE.getItemStack(player));
                return pane.element(ItemStackElement.of(itemInfo.left(), (clickHandler) -> {
                    if (clickHandler.click().leftClick()) {
                        configInterface(player, Material.COMPASS, Keys.settingKeys).build().open(view.viewer());
                    } else {
                        view.viewer().close();
                    }
                }), itemInfo.right().x(), itemInfo.right().y());
            });

            cb = cb.addTransform(1, (pane, view) -> {
                Pair<ItemStack, Vector2> itemInfo = prepareItem(Keys.GUI_MESSAGE_CHANGE.getItemStack(player));
                return pane.element(ItemStackElement.of(itemInfo.left(), (clickHandler) -> {
                    if (clickHandler.click().leftClick()) {
                        configInterface(player, Material.WRITABLE_BOOK, Keys.messageKeys).build().open(view.viewer());
                    } else {
                        view.viewer().close();
                    }
                }), itemInfo.right().x(), itemInfo.right().y());
            });
        }
        return cb;
    }

    public static ChestInterface.Builder interfaceTraits(Player player, int rows) {
        ChestInterface.Builder cb = ChestInterface.builder();
        cb = cb.title(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, Keys.GUI_TITLE.getString()), Util.quickResolver(player)));
        cb = cb.rows(rows);
        cb = cb.clickHandler(ClickHandler.cancel());
        cb = cb.updates(true, 10);
        //Manually fill the GUI each time to ensure item updates, and proper filling order.
        if (Keys.GUI_FILL.getBoolean()) {
            for (int x = 0; x < 9; x++) {
                for (int y = 0; y < cb.rows(); y++) {
                    int posX = x;
                    int posY = y;
                    cb = cb.addTransform(0, (pane, view) -> pane.element(ItemStackElement.of(Keys.GUI_FILL_ITEM.getItemStack(player)), posX, posY));
                }
            }
        }
        return cb;
    }

    public static ChestInterface.Builder configInterface(Player player, Material mat, List<Keys> keys) {
        ChestInterface.Builder cb = interfaceTraits(player, 3);
        for (Map.Entry<ItemStack, Vector2> item : prepareConfigItems(mat, keys).entrySet()) {
            cb = cb.addTransform(1, (pane, view) -> pane.element(ItemStackElement.of(item.getKey(), (clickHandler) -> {
                if (clickHandler.click().leftClick()) {
                    view.viewer().close();
                    String pathString = Objects.requireNonNull(clickHandler.cause().getCurrentItem()).getItemMeta().getPersistentDataContainer().get(MCMMOCredits.key, PersistentDataType.STRING);
                    if (clickHandler.cause().getWhoClicked() instanceof Player clicker) {
                        inputMap.put(clicker.getUniqueId(), new CompletableFuture<>());
                        player.sendMessage(Component.newline());
                        player.sendMessage(Component.text("Enter a new value for: ").color(NamedTextColor.RED).append(Component.text(Objects.requireNonNull(pathString), defaultStyle).append(Component.text(" in chat."))));
                        player.sendMessage(Component.newline());

                        //Change Config
                        UUID uuid = player.getUniqueId();
                        GUI.inputMap.get(uuid).thenAcceptAsync((i) -> ConfigHandler.changeConfigInGame(StringUtils.split(pathString, "."), i));
                        GUI.inputMap.get(uuid).whenComplete((i, throwable) -> GUI.inputMap.remove(uuid));
                    }
                }
            }), item.getValue().getX(), item.getValue().getY()));
        }
        return cb;
    }

    /*
     * i = slot, x = horizontal location, z = width (9), y = vertical location
     * i = x + (z * y);
     * x = i % z;
     * y = i / z;
     */
    protected static Map<ItemStack, Vector2> prepareConfigItems(Material mat, List<Keys> keys) {
        Map<ItemStack, Vector2> configItems = new HashMap<>();
        String[] comments;
        List<Component> lore = new ArrayList<>();
        int slot = 0;
        if (keys.size() <= 54) {
            for (Keys key : keys.stream().filter(Keys::canChange).toList()) {
                ItemStack item = new ItemStack(mat);
                comments = StringUtils.split(key.node().comment(), "\n");
                lore.add(Component.text("Comment:", defaultStyle));
                if (comments != null) {
                    for (String string : comments) {
                        lore.add(Component.text(string, defaultStyle));
                    }
                } else {
                    lore.add(Component.text("No comments found!", defaultStyle));
                }
                item.setAmount(slot + 1);
                item.editMeta(meta -> {
                    String path = StringUtils.join(key.path(), ".");
                    meta.displayName(Component.text(path.substring(path.indexOf(".") + 1)));
                    meta.lore(lore);
                    meta.getPersistentDataContainer().set(MCMMOCredits.key, PersistentDataType.STRING, path);
                });
                configItems.put(item, Vector2.at(slot % 9, slot / 9));
                slot++;
                lore.clear();
            }
        }
        return configItems;
    }

    protected static Pair<ItemStack, Vector2> prepareItem(ItemStack item) {
        int slot = item.getItemMeta().getPersistentDataContainer().getOrDefault(MCMMOCredits.key, PersistentDataType.INTEGER, 0);
        return Pair.of(item, Vector2.at(slot % 9, slot / 9));
    }
}
