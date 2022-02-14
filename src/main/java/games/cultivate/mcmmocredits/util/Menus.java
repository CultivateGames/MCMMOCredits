package games.cultivate.mcmmocredits.util;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
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
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.transform.PaperTransform;
import org.incendo.interfaces.paper.type.ChestInterface;
import org.incendo.interfaces.paper.utils.PaperUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Menus {
    public static final Menus INSTANCE = new Menus();
    public static Map<UUID, CompletableFuture<String>> inputMap = new HashMap<>();

    protected ChestInterface.Builder constructInterface(Player player, String title, int slots) {
        ChestInterface.Builder cb = ChestInterface.builder().title(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, title), Util.player(player))).rows(slots / 9).clickHandler(ClickHandler.cancel()).updates(true, 10);
        ItemStackElement<ChestPane> ise = ItemStackElement.of(Keys.MENU_FILL_ITEM.getItemStack(player));
        if (Keys.MENU_FILL.get()) {
            cb = cb.addTransform(0, PaperTransform.chestFill(ise));
        }
        return cb;
    }

    protected ChestInterface constructMainMenu(Player player, String title, int slots) {
        ChestInterface.Builder cb = constructInterface(player, title, slots);
        cb = transferLeftClick(cb, constructRedeemInterface(player), prepareItem(Keys.MENU_REDEEM_ITEM.getItemStack(player)));
        if (player.hasPermission("mcmmocredits.gui.admin")) {
            cb = transferLeftClick(cb, constructConfigInterface(player, Keys.MESSAGE_KEYS, Keys.EDIT_MESSAGES_TITLE.get(), Keys.EDIT_MESSAGES_SIZE.get()), prepareItem(Keys.MENU_MESSAGES_ITEM.getItemStack(player)));
            cb = transferLeftClick(cb, constructConfigInterface(player, Keys.SETTING_KEYS, Keys.EDIT_SETTINGS_TITLE.get(), Keys.EDIT_SETTINGS_SIZE.get()), prepareItem(Keys.MENU_SETTINGS_ITEM.getItemStack(player)));
        }
        return cb.build();
    }

    protected ChestInterface constructConfigInterface(Player player, List<Keys> keyList, String title, int slots) {
        ChestInterface.Builder cb = constructInterface(player, title, slots);
        Config<?> configuration = keyList.get(0).config();
        for (Map.Entry<ItemStack, Vector2> item : prepareConfigItems(keyList, player).entrySet()) {
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
                        Util.sendMessage(player, Keys.CREDITS_MENU_EDITING_PROMPT.get(), Util.settings(player, pathString, ""));
                        inputMap.put(uuid, new CompletableFuture<>());
                        Menus.inputMap.get(uuid).thenAcceptAsync((i) -> {
                            String str;
                            if (Util.changeConfigInGame(configuration, Arrays.asList(StringUtils.split(pathString, ".")), i)) {
                                str = Keys.CREDITS_SETTING_CHANGE_SUCCESSFUL.get();
                            } else {
                                str = Keys.CREDITS_SETTING_CHANGE_FAILURE.get();
                            }
                            Util.sendMessage(player, str, Util.settings(player, pathString, i));
                        }).whenComplete((i, throwable) -> Menus.inputMap.remove(uuid));
                    }
                }
            }), item.getValue().x(), item.getValue().y()));
        }
        return addNavigation(player, cb);
    }

    @NotNull
    protected ChestInterface addNavigation(Player player, ChestInterface.Builder cb) {
        if (Keys.MENU_NAVIGATION.get()) {
            Pair<ItemStack, Vector2> pair = prepareItem(Keys.MENU_NAVIGATION_ITEM.getItemStack(player));
            cb = cb.addTransform(1, (pane, view) -> pane.element(ItemStackElement.of(pair.left(), (clickHandler) -> {
                if (clickHandler.click().leftClick()) {
                   player.performCommand("credits menu");
                }
            }), pair.right().x(), pair.right().y()));
        }
        return cb.build();
    }

    protected ChestInterface constructRedeemInterface(Player player) {
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
                        Util.sendMessage(player, Keys.CREDITS_MENU_REDEEM_PROMPT.get(), Util.resolverBuilder().redeem(WordUtils.capitalizeFully(skill.name()), Util.SKILL_TOOLS.getLevelCap(skill)).build());
                        inputMap.put(uuid, new CompletableFuture<>());
                        Menus.inputMap.get(uuid).thenAcceptAsync((i) -> {
                            int amount = Integer.parseInt(i);
                            if (Redeem.creditRedemption(player, uuid, skill, amount)) {
                                Util.sendMessage(player, Keys.REDEEM_SUCCESSFUL_SELF.get(), Util.fullRedeem(null, player, WordUtils.capitalizeFully(skill.name()), Util.SKILL_TOOLS.getLevelCap(skill), amount));
                            }
                        }).whenCompleteAsync((i, throwable) -> Menus.inputMap.remove(uuid));
                    }
                }
            }), item.getValue().x(), item.getValue().y()));
        }
        return addNavigation(player, cb);
    }

    protected Map<ItemStack, Vector2> prepareConfigItems(List<Keys> configType, Player player) {
        Map<ItemStack, Vector2> configItems = new HashMap<>();
        int slot = 0;
        for (Keys key : configType) {
            ItemStack base = configType.get(0).config().equals(Config.MESSAGES) ? Keys.EDIT_MESSAGES_ITEM.getItemStack(player) : Keys.EDIT_SETTINGS_ITEM.getItemStack(player);
            base.setAmount(slot + 1);
            base.editMeta(meta -> {
                String path = StringUtils.join(key.path(), ".");
                meta.displayName(Component.text(key.path().get(key.path().size() - 1), Util.DEFAULT_STYLE));
                meta.getPersistentDataContainer().set(MCMMOCredits.key, PersistentDataType.STRING, path);
                if (meta.hasLore()) {
                    meta.lore(Objects.requireNonNull(meta.lore()).stream().map(i -> Util.parse(i, player)).toList());
                }
            });
            configItems.put(base, PaperUtils.slotToGrid(slot));
            slot++;
        }
        return configItems;
    }

    protected Map<ItemStack, Vector2> prepareRedeemItems(Player player) {
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

    protected ChestInterface.Builder transferLeftClick(ChestInterface.Builder cb, ChestInterface transfer, Pair<ItemStack, Vector2> pair) {
        return cb.addTransform(1, (pane, view) -> pane.element(ItemStackElement.of(pair.left(), (clickHandler) -> {
            if (clickHandler.click().leftClick()) {
                transfer.open(view.viewer());
            }
        }), pair.right().x(), pair.right().y()));
    }

    public void openMainMenu(Player player) {
        constructMainMenu(player, Keys.MENU_TITLE.get(), Keys.MENU_SIZE.get()).open(PlayerViewer.of(player));
    }

    public void openMessagesMenu(Player player) {
        constructConfigInterface(player, Keys.MESSAGE_KEYS, Keys.EDIT_MESSAGES_TITLE.get(), Keys.EDIT_MESSAGES_SIZE.get()).open(PlayerViewer.of(player));
    }

    public void openSettingsMenu(Player player) {
        constructConfigInterface(player, Keys.SETTING_KEYS, Keys.EDIT_SETTINGS_TITLE.get(), Keys.EDIT_SETTINGS_SIZE.get()).open(PlayerViewer.of(player));
    }

    public void openRedeemMenu(Player player) {
        constructRedeemInterface(player).open(PlayerViewer.of(player));
    }

    protected Vector2 slotToGrid(ItemStack item) {
        return PaperUtils.slotToGrid(item.getItemMeta().getPersistentDataContainer().getOrDefault(MCMMOCredits.key, PersistentDataType.INTEGER, 0));
    }

    protected Pair<ItemStack, Vector2> prepareItem(ItemStack item) {
        return Pair.of(item, slotToGrid(item));
    }
}
