package games.cultivate.mcmmocredits.menu;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.ConfigUtil;
import games.cultivate.mcmmocredits.data.InputStorage;
import games.cultivate.mcmmocredits.keys.BooleanKey;
import games.cultivate.mcmmocredits.keys.IntegerKey;
import games.cultivate.mcmmocredits.keys.ItemStackKey;
import games.cultivate.mcmmocredits.keys.Key;
import games.cultivate.mcmmocredits.keys.StringKey;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.util.Util;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.transform.PaperTransform;
import org.incendo.interfaces.paper.type.ChestInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MenuDirector {
    private ChestInterface.Builder builder;
    private Player player;
    private static final NamespacedKey NAMESPACED_KEY = Objects.requireNonNull(NamespacedKey.fromString("mcmmocredits"));

    public MenuDirector(Player player) {
        this.builder = ChestInterface.builder();
        this.player = player;
    }

    public void updates(boolean update, int delay) {
        this.builder = this.builder.updates(update, delay);
    }

    public void player(Player player) {
        this.player = player;
    }

    public void rows(int rows) {
        this.builder = this.builder.rows(rows);
    }

    public void title(Component title) {
        this.builder = this.builder.title(Util.parse(title, player));
    }

    public void title(String title) {
        title = PlaceholderAPI.setPlaceholders(player, title);
        this.builder = this.builder.title(MiniMessage.miniMessage().deserialize(title, Resolver.fromPlayer(player)));
    }

    public void transferToInterface(Button button, ClickType clickType, ChestInterface chestInterface) {
        this.builder = this.builder.addTransform(1, (pane, view) -> pane.element(ItemStackElement.of(button.item(), clickHandler -> {
            if (clickHandler.cause().getClick().equals(clickType)) {
                chestInterface.open(view.viewer());
            }
        }), button.x(), button.y()));
    }

    public void transferToCommand(Button button, ClickType clickType) {
        this.builder = this.builder.addTransform(1, (pane, view) -> pane.element(ItemStackElement.of(button.item(), clickHandler -> {
            if (clickHandler.cause().getClick().equals(clickType) && !button.command().isEmpty()) {
                view.viewer().player().performCommand(button.command());
            }
        }), button.x(), button.y()));
    }

    public void transferToRedemption(Button button, ClickType clickType) {
        this.builder = this.builder.addTransform(1, (pane, view) -> pane.element(ItemStackElement.of(button.item(), click -> {
            if (click.cause().getClick().equals(clickType)) {
                view.viewer().close();
                ItemStack stack = click.cause().getCurrentItem();
                if (stack != null && stack.hasItemMeta()) {
                    PersistentDataContainer pdc = stack.getItemMeta().getPersistentDataContainer();
                    PrimarySkillType skill = PrimarySkillType.valueOf(pdc.get(NAMESPACED_KEY, PersistentDataType.STRING));
                    Resolver.Builder rb = Resolver.builder().player(player).skill(skill);
                    Text.fromKey(player, StringKey.CREDITS_MENU_REDEEM_PROMPT, rb.build()).send();
                    UUID uuid = player.getUniqueId();
                    InputStorage storage = InputStorage.getInstance();
                    storage.remove(uuid);
                    storage.add(uuid);
                    storage.act(uuid, i -> {
                        int amount = Integer.parseInt(i);
                        //TODO add transaction
                            rb.transaction(amount);
                            Text.fromKey(player, StringKey.REDEEM_SUCCESSFUL_SELF, rb.build()).send();
                    });
                }
            }
        }), button.x(), button.y()));
    }

    public void transferToConfig(Button button, ClickType clickType) {
        this.builder = this.builder.addTransform(1, (pane, view) -> pane.element(ItemStackElement.of(button.item(), click -> {
            if (click.cause().getClick().equals(clickType)) {
                view.viewer().close();
                ItemStack stack = click.cause().getCurrentItem();
                if (stack != null && stack.hasItemMeta()) {
                    String path = stack.getItemMeta().getPersistentDataContainer().get(NAMESPACED_KEY, PersistentDataType.STRING);
                    Resolver.Builder rb = Resolver.builder().sender(player).tags("setting", path);
                    Text.fromKey(player, StringKey.CREDITS_MENU_EDITING_PROMPT, rb.build()).send();
                    InputStorage storage = InputStorage.getInstance();
                    UUID uuid = player.getUniqueId();
                    storage.remove(uuid);
                    storage.add(uuid);
                    storage.act(uuid, i -> {
                        boolean result = Util.modifyConfig(path,i);
                        StringKey key = StringKey.valueOf("CREDITS_SETTING_CHANGE_" + (result ? "SUCCESSFUL" : "FAILURE"));
                        Text.fromKey(player, key, rb.tags("change", i).build()).send();
                    });
                }
            }
        }), button.x(), button.y()));
    }

    protected Button createRedeemItem(ItemStackKey key) {
        Button button = Button.of(key, player);
        button.addToPDC(PersistentDataType.STRING, key.path().split("\\.")[2].toUpperCase());
        return button;
    }

    protected List<Button> createConfigItems(Config<?> config) {
        List<Button> configItems = new ArrayList<>();
        int slot = 0;
        String type = config.equals(ConfigUtil.MESSAGES) ? "MESSAGES" : "SETTINGS";
        ItemStack item = ItemStackKey.valueOf("EDIT_" + type + "_ITEM").get(player);
        for (Key<?> key : config.keys()) {
            String path = key.path();
            //Database options are not supported for live reload.
            if (path.contains("database") || path.contains("item")) {
                continue;
            }
            ItemStack base = item.clone();
            base.setAmount(slot + 1);
            base.editMeta(meta -> {
                meta.displayName(Component.text(path.substring(path.lastIndexOf('.')), Text.DEFAULT_STYLE));
                meta.getPersistentDataContainer().set(NAMESPACED_KEY, PersistentDataType.STRING, path);
                if (meta.hasLore()) {
                    meta.lore(meta.lore().stream().map(i -> Util.parse(i, player)).toList());
                }
            });
            configItems.add(new Button(base, slot, ""));
            slot++;
        }
        return configItems;
    }

    public ChestInterface build() {
        if (BooleanKey.MENU_FILL.get()) {
            this.builder = this.builder.addTransform(0, PaperTransform.chestFill(ItemStackKey.MENU_FILL_ITEM.element(player)));
        }
        if (BooleanKey.MENU_NAVIGATION.get()) {
            ItemStackKey navigation = ItemStackKey.MENU_NAVIGATION_ITEM;
            Button button = new Button(navigation.get(player), navigation.slot(), "credits menu");
            this.transferToCommand(button, ClickType.LEFT);
        }
        return this.builder.updates(true, 10).build();
    }

    public void base(Player player, String title, int slots) {
        this.player(player);
        this.title(title);
        this.rows(slots / 9);
    }

    public void redemption() {
        this.base(player, StringKey.REDEEM_TITLE.get(), IntegerKey.REDEEM_SIZE.get() / 9);
        for (ItemStackKey key : ItemStackKey.values()) {
            if (key.path().startsWith("redeem")) {
                this.transferToRedemption(this.createRedeemItem(key), ClickType.LEFT);
            }
        }
    }

    public void config(Config<?> config) {
        String type = config.equals(ConfigUtil.MESSAGES) ? "MESSAGES" : "SETTINGS";
        this.base(player, StringKey.valueOf("EDIT_" + type + "_TITLE").get(), IntegerKey.valueOf("EDIT_" + type + "_SIZE").get() / 9);
        for (Button button : this.createConfigItems(config)) {
            this.transferToConfig(button, ClickType.LEFT);
        }
    }
}
