package games.cultivate.mcmmocredits.util;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.commands.Redeem;
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.Keys;
import it.unimi.dsi.fastutil.Pair;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang.WordUtils;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Menus {
    public static Map<UUID, CompletableFuture<String>> inputMap = new HashMap<>();

    public static ChestInterface.Builder constructInterface(Player player, String title, int slots) {
        ChestInterface.Builder cb = ChestInterface.builder().title(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, title), Util.basicBuilder(player).build())).rows(slots / 9).clickHandler(ClickHandler.cancel()).updates(true, 10);
        if (Keys.MENU_FILL.get()) {
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

    public static ChestInterface constructMainMenu(Player player, String title, int slots) {
        ChestInterface.Builder cb = constructInterface(player, title, slots);
        cb = transferLeftClick(cb, constructRedeemInterface(player), prepareItem(Keys.MENU_REDEEM_ITEM.getItemStack(player)));
        if (player.hasPermission("mcmmocredits.gui.admin")) {
            cb = transferLeftClick(cb, constructConfigInterface(player, Keys.MESSAGE_KEYS, Keys.EDIT_MESSAGES_TITLE.get(), Keys.EDIT_MESSAGES_SIZE.get()), prepareItem(Keys.MENU_MESSAGES_ITEM.getItemStack(player)));
            cb = transferLeftClick(cb, constructConfigInterface(player, Keys.SETTING_KEYS, Keys.EDIT_SETTINGS_TITLE.get(), Keys.EDIT_SETTINGS_SIZE.get()), prepareItem(Keys.MENU_SETTINGS_ITEM.getItemStack(player)));
        }
        return cb.build();
    }

    public static ChestInterface constructConfigInterface(Player player, List<Keys> keyList, String title, int slots) {
        ChestInterface.Builder cb = constructInterface(player, title, slots);
        Config<?> configuration = keyList.get(0).config();
        for (Map.Entry<ItemStack, Vector2> item : prepareConfigItems(keyList).entrySet()) {
            cb = cb.addTransform(1, (pane, view) -> pane.element(ItemStackElement.of(item.getKey(), (clickHandler) -> {
                if (clickHandler.click().leftClick()) {
                    view.viewer().close();
                    ItemStack stack = clickHandler.cause().getCurrentItem();
                    if (stack != null && stack.hasItemMeta()) {
                        //Change config
                        String pathString = stack.getItemMeta().getPersistentDataContainer().getOrDefault(MCMMOCredits.key, PersistentDataType.STRING, "");
                        UUID uuid = player.getUniqueId();
                        if (inputMap.containsKey(uuid)) {
                            inputMap.get(uuid).complete(null);
                            inputMap.remove(uuid);
                        }
                        Util.sendMessage(player, Keys.CREDITS_MENU_EDITING_PROMPT.get(), Util.settingsBuilder(player, pathString, "").build());
                        inputMap.put(uuid, new CompletableFuture<>());
                        Menus.inputMap.get(uuid).thenAcceptAsync((i) -> {
                            String str;
                            if(Util.changeConfigInGame(configuration, Arrays.asList(StringUtils.split(pathString, ".")), i)) {
                                str = Keys.CREDITS_SETTING_CHANGE_SUCCESSFUL.get();
                            } else {
                                str = Keys.CREDITS_SETTING_CHANGE_FAILURE.get();
                            }
                            Util.sendMessage(player, str, Util.settingsBuilder(player, pathString, i).build());
                        }).whenComplete((i, throwable) -> Menus.inputMap.remove(uuid));
                    }
                }
            }), item.getValue().x(), item.getValue().y()));
        }
        if (Keys.MENU_NAVIGATION.get()) {
            cb = transferLeftClick(cb, constructMainMenu(player, Keys.MENU_TITLE.get(), Keys.MENU_SIZE.get()), prepareItem(Keys.MENU_NAVIGATION.getItemStack(player)));
        }
        return cb.build();
    }

    public static ChestInterface constructRedeemInterface(Player player) {
        ChestInterface.Builder cb = constructInterface(player, Keys.REDEEM_TITLE.get(), Keys.REDEEM_SIZE.get());
        for (Map.Entry<ItemStack, Vector2> item : prepareRedeemItems(player).entrySet()) {
            cb = cb.addTransform(1, (pane, view) -> pane.element(ItemStackElement.of(item.getKey(), (clickHandler) -> {
                if (clickHandler.click().leftClick()) {
                    view.viewer().close();
                    ItemStack stack = clickHandler.cause().getCurrentItem();
                    if (stack != null && stack.hasItemMeta()) {
                        //Redeem Credits
                        PrimarySkillType skill = PrimarySkillType.valueOf(stack.getItemMeta().getPersistentDataContainer().getOrDefault(MCMMOCredits.key, PersistentDataType.STRING, ""));
                        UUID uuid = player.getUniqueId();
                        if (inputMap.containsKey(uuid)) {
                            inputMap.get(uuid).complete(null);
                            inputMap.remove(uuid);
                        }
                        Util.sendMessage(player, Keys.CREDITS_MENU_REDEEM_PROMPT.get(), Util.redeemPromptResolver(player, WordUtils.capitalizeFully(skill.name()), mcMMO.p.getSkillTools().getLevelCap(skill)));
                        inputMap.put(uuid, new CompletableFuture<>());
                        Menus.inputMap.get(uuid).thenAcceptAsync((i) -> {
                            Redeem.creditRedemption(player, uuid, skill, Integer.parseInt(i));
                            Util.sendMessage(player, Keys.REDEEM_SUCCESSFUL_SELF.get(), Util.redeemBuilder(Pair.of(null, player), WordUtils.capitalizeFully(skill.name()), mcMMO.p.getSkillTools().getLevelCap(skill), Integer.parseInt(i)).build());
                        }).whenCompleteAsync((i, throwable) -> Menus.inputMap.remove(uuid));
                    }
                }
            }), item.getValue().x(), item.getValue().y()));
        }
        if (Keys.MENU_NAVIGATION.get()) {
            cb = transferLeftClick(cb, constructMainMenu(player, Keys.MENU_TITLE.get(), Keys.MENU_SIZE.get()), prepareItem(Keys.MENU_NAVIGATION_ITEM.getItemStack(player)));
        }
        return cb.build();
    }

    protected static Map<ItemStack, Vector2> prepareConfigItems(List<Keys> configType) {
        Map<ItemStack, Vector2> configItems = new HashMap<>();
        ItemStack base = configType.get(0).config().equals(Config.MESSAGES) ? Keys.EDIT_MESSAGES_ITEM.partialItemStack() : Keys.EDIT_SETTINGS_ITEM.partialItemStack();
        int slot = 0;
        for (Keys key : configType) {
            base.setAmount(slot + 1);
            base.editMeta(meta -> {
                String path = StringUtils.join(key.path(), ".");
                meta.displayName(Component.text(key.path().get(key.path().size() - 1), Util.DEFAULT_STYLE));
                meta.getPersistentDataContainer().set(MCMMOCredits.key, PersistentDataType.STRING, path);
            });
            configItems.put(base.clone(), PaperUtils.slotToGrid(slot));
            slot++;
        }
        return configItems;
    }

    protected static Map<ItemStack, Vector2> prepareRedeemItems(Player player) {
        Map<ItemStack, Vector2> redeemItems = new HashMap<>();
        for (Keys key : Keys.MENU_KEYS) {
            List<String> keyPath = key.path();
            if (key.type().equals(ItemStack.class) && keyPath.get(0).equalsIgnoreCase("redeem")) {
                Pair<ItemStack, Vector2> pair = prepareItem(key.getItemStack(player));
                pair.left().editMeta(meta -> meta.getPersistentDataContainer().set(MCMMOCredits.key, PersistentDataType.STRING, keyPath.get(2).toUpperCase()));
                redeemItems.put(pair.left(), pair.right());
            }
        }
        return redeemItems;
    }

    protected static ChestInterface.Builder transferLeftClick(ChestInterface.Builder cb, ChestInterface transfer, Pair<ItemStack, Vector2> pair) {
        return cb.addTransform(1, (pane, view) -> pane.element(ItemStackElement.of(pair.left(), (clickHandler) -> {
            if (clickHandler.click().leftClick()) {
                transfer.open(view.viewer());
            }
        }), pair.right().x(), pair.right().y()));
    }

    public static void openMainMenu(Player player) {
        constructMainMenu(player, Keys.MENU_TITLE.get(), Keys.MENU_SIZE.get()).open(PlayerViewer.of(player));
    }

    public static void openMessagesMenu(Player player) {
        constructConfigInterface(player, Keys.MESSAGE_KEYS, Keys.EDIT_MESSAGES_TITLE.get(), Keys.EDIT_MESSAGES_SIZE.get()).open(PlayerViewer.of(player));
    }

    public static void openSettingsMenu(Player player) {
        constructConfigInterface(player, Keys.SETTING_KEYS, Keys.EDIT_SETTINGS_TITLE.get(), Keys.EDIT_SETTINGS_SIZE.get()).open(PlayerViewer.of(player));
    }

    public static void openRedeemMenu(Player player) {
        constructRedeemInterface(player).open(PlayerViewer.of(player));
    }

    protected static Vector2 slotToGrid(ItemStack item) {
        return PaperUtils.slotToGrid(item.getItemMeta().getPersistentDataContainer().getOrDefault(MCMMOCredits.key, PersistentDataType.INTEGER, 0));
    }

    protected static Pair<ItemStack, Vector2> prepareItem(ItemStack item) {
        return Pair.of(item, slotToGrid(item));
    }
}
