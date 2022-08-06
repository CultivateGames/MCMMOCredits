package games.cultivate.mcmmocredits.menu;

import broccolai.corn.paper.item.PaperItemBuilder;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.GeneralConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.util.InputStorage;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.core.transform.Transform;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.type.ChestInterface;
import org.spongepowered.configurate.CommentedConfigurationNode;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class ConfigMenu extends Menu {
    private final MenuConfig menu;
    private final GeneralConfig config;
    private final InputStorage storage;
    private final Player player;

    @Inject
    public ConfigMenu(final MenuConfig menu, final GeneralConfig config, final InputStorage storage, final Player player) {
        this.menu = menu;
        this.config = config;
        this.storage = storage;
        this.player = player;
        this.viewer = PlayerViewer.of(player);
    }

    @Override
    public Transform<ChestPane, PlayerViewer> itemTransform() {
        return (pane, view) -> {
            int slot = 0;
            for (CommentedConfigurationNode node : this.config.nodes()) {
                String path = this.config.joinedPath(node);
                if (path.contains("mysql") || path.contains("item")) {
                    continue;
                }
                pane = this.createConfigTransform(pane, this.itemFromPath(path).amount(slot + 1).build());
                slot++;
            }
            return pane;
        };
    }

    @Override
    public ChestInterface create() {
        String type = this.config.getClass().getSimpleName().replace("BaseConfig", "").toLowerCase();
        Component title = Text.fromString(this.player, this.menu.string(type + ".info.title")).toComponent();
        int rows = this.menu.integer(type + ".info.size") / 9;
        List<TransformContext<ChestPane, PlayerViewer>> transforms = new ArrayList<>();
        transforms.add(TransformContext.of(0, this.itemTransform()));
        if (this.menu.bool("all.fill")) {
            transforms.add(TransformContext.of(0, this.fillTransform(this.menu, this.player)));
        }
        return new ChestInterface(rows, title, transforms, List.of(), true, 10, ClickHandler.cancel());
    }

    private ChestPane createConfigTransform(final ChestPane pane, final ItemStack item) {
        String path = item.getItemMeta().getPersistentDataContainer().get(MCMMOCredits.NAMESPACED_KEY, PersistentDataType.STRING);
        int slot = item.getAmount() - 1;
        return pane.element(ItemStackElement.of(item, click -> {
            if (click.cause().isLeftClick()) {
                Resolver.Builder resolver = Resolver.builder().player(this.player).tag("setting", path);
                Text.fromString(this.player, this.config.string("menuEditingPrompt"), resolver.build()).send();
                this.storage.act(this.player.getUniqueId(), i -> {
                    boolean result = this.config.modify(path, i);
                    String content = result ? "settingChangeSuccessful" : "settingChangeFailure";
                    Text.fromString(this.player, this.config.string(content), resolver.tag("change", i).build()).send();
                });
            }
        }), slot % 9, slot / 9);
    }

    private PaperItemBuilder itemFromPath(final String path) {
        return PaperItemBuilder.of(this.menu.item(path, this.player))
                .name(Component.text(path.substring(path.lastIndexOf('.') + 1)))
                .setData(MCMMOCredits.NAMESPACED_KEY, PersistentDataType.STRING, path);
    }
}
