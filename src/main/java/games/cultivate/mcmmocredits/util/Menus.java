package games.cultivate.mcmmocredits.util;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Keys;
import it.unimi.dsi.fastutil.Pair;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.core.util.Vector2;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.type.ChestInterface;
import org.incendo.interfaces.paper.utils.PaperUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Menus {
    public static Map<UUID, CompletableFuture<String>> inputMap = new HashMap<>();

    public static ChestInterface.Builder constructInterface(Player player, Keys title, Keys slots) {
        ChestInterface.Builder cb = ChestInterface.builder()
                .title(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, title.getString()), Util.basicBuilder(player).build()))
                .rows(slots.getInt() / 9)
                .clickHandler(ClickHandler.cancel())
                .updates(true, 10);
        if (Keys.MENU_FILL.getBoolean()) {
            for (int x = 0; x < 9; x++) {
                for (int y = 0; y < cb.rows(); y++) {
                    int posX = x;
                    int posY = y;
                    cb = cb.addTransform(0, (pane, view) -> pane.element(ItemStackElement.of(Keys.MENU_FILL_ITEM.getItemStack(player)), posX, posY));
                }
            }
        }
        return cb;
    }

    protected static ChestInterface.Builder basicTransform(ChestInterface.Builder cb, Pair<ItemStack, Vector2> pair) {
        return cb.addTransform(1, (pane, view) -> pane.element(ItemStackElement.of(pair.left()), pair.right().x(), pair.right().y()));
    }

    protected static ChestInterface.Builder transferLeftClick(ChestInterface.Builder cb, ChestInterface transfer, Pair<ItemStack, Vector2> pair) {
        return cb.addTransform(1, (pane, view) -> pane.element(ItemStackElement.of(pair.left(), (clickHandler) -> {
            if (clickHandler.click().leftClick()) {
               transfer.open(view.viewer());
            }
        }), pair.right().x(), pair.right().y()));
    }

    /**
     * public static ChestInterface.Builder redeemMenu(Player player) {}
     */

    public static void openMainMenu(Player player) {
        constructMainMenu(player, Keys.MENU_TITLE, Keys.MENU_SIZE).open(PlayerViewer.of(player));
    }

    public static void openMessagesMenu(Player player) {
        constructConfigInterface(player, Keys.EDIT_MESSAGES_ITEM, Keys.EDIT_MESSAGES_TITLE, Keys.EDIT_MESSAGES_SIZE).open(PlayerViewer.of(player));
    }

    public static void openSettingsMenu(Player player) {
        constructConfigInterface(player, Keys.EDIT_SETTINGS_ITEM, Keys.EDIT_MESSAGES_TITLE, Keys.EDIT_MESSAGES_SIZE).open(PlayerViewer.of(player));
    }

    public static ChestInterface constructMainMenu(Player player, Keys title, Keys rows) {
        ChestInterface.Builder cb = constructInterface(player, title, rows);
        cb = basicTransform(cb, prepareItem(Keys.MENU_REDEEM_ITEM.getItemStack(player)));
        if (player.hasPermission("mcmmocredits.gui.admin")) {
            cb = transferLeftClick(cb, constructConfigInterface(player, Keys.EDIT_MESSAGES_ITEM, Keys.EDIT_MESSAGES_TITLE, Keys.EDIT_MESSAGES_SIZE), prepareItem(Keys.MENU_MESSAGES_ITEM.getItemStack(player)));
            cb = transferLeftClick(cb, constructConfigInterface(player, Keys.EDIT_SETTINGS_ITEM, Keys.EDIT_SETTINGS_TITLE, Keys.EDIT_SETTINGS_SIZE), prepareItem(Keys.MENU_SETTINGS_ITEM.getItemStack(player)));
        }
        return cb.build();
    }

    private static void sendConfigMessage(Player player, String string) {
        player.sendMessage(Component.newline().toBuilder().append(Component.text("Enter a new value for: ").color(NamedTextColor.RED), Component.text(string, Util.defaultStyle), Component.text(" in chat."), Component.newline()));
    }

    public static ChestInterface constructConfigInterface(Player player, Keys itemStack, Keys title, Keys rows) {
        ChestInterface.Builder cb = constructInterface(player, title, rows);
        for (Map.Entry<ItemStack, Vector2> item : prepareConfigItems(itemStack).entrySet()) {
            cb = cb.addTransform(1, (pane, view) -> pane.element(ItemStackElement.of(item.getKey(), (clickHandler) -> {
                if (clickHandler.click().leftClick()) {
                    view.viewer().close();
                    ItemStack stack = clickHandler.cause().getCurrentItem();
                    if (stack != null && stack.hasItemMeta()) {
                        String pathString = stack.getItemMeta().getPersistentDataContainer().get(MCMMOCredits.key, PersistentDataType.STRING);
                        if (clickHandler.cause().getWhoClicked() instanceof Player clicker) {
                            inputMap.put(clicker.getUniqueId(), new CompletableFuture<>());
                            sendConfigMessage(player, pathString);
                            //Change Config
                            UUID uuid = player.getUniqueId();
                            Menus.inputMap.get(uuid).thenAcceptAsync((i) -> ConfigHandler.changeConfigInGame(StringUtils.split(pathString, "."), i)).whenComplete((i, throwable) -> Menus.inputMap.remove(uuid));
                        }
                    }
                }
            }), item.getValue().x(), item.getValue().getY()));
        }
        if (Keys.MENU_NAVIGATION.getBoolean()) {
            cb = transferLeftClick(cb, constructMainMenu(player, Keys.MENU_TITLE, Keys.MENU_SIZE), prepareItem(Keys.MENU_NAVIGATION.getItemStack(player)));
        }
        return cb.build();
    }

    protected static Map<ItemStack, Vector2> prepareConfigItems(Keys keys) {
        Map<ItemStack, Vector2> configItems = new HashMap<>();
        ItemStack item = keys.partialItemStack();
        int slot = 0;
        for (Keys key : keys.name().contains("messages") ? Keys.messageKeys : Keys.settingKeys.stream().filter(Keys::canChange).toList()) {
            item.setAmount(slot + 1);
            item.editMeta(meta -> {
                String path = StringUtils.join(key.path(), ".");
                meta.displayName(Component.text(path, Util.defaultStyle));
                meta.getPersistentDataContainer().set(MCMMOCredits.key, PersistentDataType.STRING, path);
            });
            configItems.put(item, PaperUtils.slotToGrid(slot));
            slot++;
        }
        return configItems;
    }

    protected static Pair<ItemStack, Vector2> prepareItem(ItemStack item) {
        return Pair.of(item, PaperUtils.slotToGrid(item.getItemMeta().getPersistentDataContainer().getOrDefault(MCMMOCredits.key, PersistentDataType.INTEGER, 0)));
    }
}
