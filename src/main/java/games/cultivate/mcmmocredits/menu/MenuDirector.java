package games.cultivate.mcmmocredits.menu;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.ItemType;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.config.MessagesConfig;
import games.cultivate.mcmmocredits.data.InputStorage;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.transform.PaperTransform;
import org.incendo.interfaces.paper.type.ChestInterface;
import org.spongepowered.configurate.CommentedConfigurationNode;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

//This class will be ripped out soon tm.
public class MenuDirector {
    private static final NamespacedKey NAMESPACED_KEY = Objects.requireNonNull(NamespacedKey.fromString("mcmmocredits"));
    private final MessagesConfig messages;
    private final MenuConfig menus;
    private final InputStorage storage;
    private ChestInterface.Builder builder;
    private Player player;

    @Inject
    public MenuDirector(MessagesConfig messages, MenuConfig menus, InputStorage storage) {
        this.builder = ChestInterface.builder();
        this.messages = messages;
        this.menus = menus;
        this.storage = storage;
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
        this.builder = this.builder.title(Text.parseComponent(title, player));
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
                    Text.fromString(player, this.messages.string("menuRedeemPrompt"), rb.build()).send();
                    UUID uuid = player.getUniqueId();
                    this.storage.remove(uuid);
                    this.storage.add(uuid);
                    this.storage.act(uuid, i -> {
                        int amount = Integer.parseInt(i);
                        //TODO add transaction
                        rb.transaction(amount);
                        Text.fromString(player, "selfRedeem", rb.build()).send();
                    });
                }
            }
        }), button.x(), button.y()));
    }

    public void transferToConfig(Button button, ClickType clickType, Config<?> config) {
        this.builder = this.builder.addTransform(1, (pane, view) -> pane.element(ItemStackElement.of(button.item(), click -> {
            if (click.cause().getClick().equals(clickType)) {
                view.viewer().close();
                ItemStack stack = click.cause().getCurrentItem();
                if (stack != null && stack.hasItemMeta()) {
                    String path = stack.getItemMeta().getPersistentDataContainer().get(NAMESPACED_KEY, PersistentDataType.STRING);
                    Resolver.Builder rb = Resolver.builder().sender(player).tags("setting", path);
                    Text.fromString(player, this.messages.string("editRedeemPrompt"), rb.build()).send();
                    UUID uuid = player.getUniqueId();
                    this.storage.remove(uuid);
                    this.storage.add(uuid);
                    this.storage.act(uuid, i -> {
                        boolean result = config.modify(path, i);
                        String content = "settingChange" + (result ? "Successful" : "Failure");
                        Text.fromString(player, this.messages.string(content), rb.tags("change", i).build()).send();
                    });
                }
            }
        }), button.x(), button.y()));
    }

    protected Button createRedeemItem(ItemType type) {
        new Button(this.menus.item(type, player), this.menus.itemSlot(type), "");
        Button button = new Button(this.menus.item(type, player), 0, "");
        button.addToPDC(PersistentDataType.STRING, type.path().get(2).toUpperCase());
        return button;
    }

    protected List<Button> createConfigItems(Config<?> config) {
        List<Button> configItems = new ArrayList<>();
        int slot = 0;
        String type = config.config() instanceof MessagesConfig ? "MESSAGES" : "SETTINGS";

        for (CommentedConfigurationNode node : config.baseNode().childrenMap().values()) {
            String path = StringUtils.join(node.path().array(), ".");
            //Database and item options are not supported for in-game modification.
            if (path.contains("database") || path.contains("item")) {
                continue;
            }
            ItemStack item = this.menus.item(ItemType.valueOf("EDIT_" + type + "_ITEM"), player);
            item.setAmount(slot + 1);
            item.editMeta(meta -> {
                meta.displayName(Component.text(path.substring(path.lastIndexOf('.')), Text.DEFAULT_STYLE));
                meta.getPersistentDataContainer().set(NAMESPACED_KEY, PersistentDataType.STRING, path);
                if (meta.hasLore()) {
                    meta.lore(meta.lore().stream().map(i -> Text.parseComponent(i, player)).toList());
                }
            });
            configItems.add(new Button(item, slot, ""));
            slot++;
        }
        return configItems;
    }

    public ChestInterface build() {
        if (this.menus.bool("fill", false)) {
            ItemStackElement<ChestPane> item = ItemStackElement.of(this.menus.item(ItemType.MENU_FILL_ITEM, player));
            this.builder = this.builder.addTransform(0, PaperTransform.chestFill(item));
        }
        if (this.menus.bool("navigation", false)) {
            ItemStack navigation = this.menus.item(ItemType.MENU_NAVIGATION_ITEM, player);
            Button button = new Button(navigation, this.menus.integer("navigationItem.slot", 0), "credits menu");
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
        this.base(player, this.menus.string("redeem.title"), this.menus.integer("redeem.size", 0) / 9);
        for (ItemType type : ItemType.values()) {
            if (type.path().get(0).equals("redeem")) {
                this.transferToRedemption(this.createRedeemItem(type), ClickType.LEFT);
            }
        }
    }

    public void config(Config<?> config) {
        String type = config.config() instanceof MessagesConfig ? "MESSAGES" : "SETTINGS";
        this.base(player, this.menus.string(type + ".title"), this.menus.integer(type + ".size", 0));
        for (Button button : this.createConfigItems(config)) {
            this.transferToConfig(button, ClickType.LEFT, config);
        }
    }
}
