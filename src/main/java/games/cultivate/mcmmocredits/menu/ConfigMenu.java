package games.cultivate.mcmmocredits.menu;

import broccolai.corn.paper.item.PaperItemBuilder;
import games.cultivate.mcmmocredits.config.GeneralConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.util.InputStorage;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.spongepowered.configurate.CommentedConfigurationNode;

public final class ConfigMenu extends BaseMenu {
    private final InputStorage storage;
    private final GeneralConfig config;
    private final String type;

    ConfigMenu(final MenuConfig menu, final Player player, final GeneralConfig config, final InputStorage storage, final String type) {
        super(menu, player, "editing");
        this.config = config;
        this.storage = storage;
        this.type = type;
    }

    @Override
    public void applySpecialItems() {
        this.transformations.add(TransformContext.of(0, (pane, view) -> {
            int slot = 1;
            for (CommentedConfigurationNode node : this.config.nodes()) {
                String path = this.config.joinedPath(node);
                if (!path.contains("mysql") && !path.contains("item") && path.startsWith(this.type)) {
                    pane = this.createConfigTransform(pane, path, slot);
                }
                slot++;
            }
            return pane;
        }));
    }

    private ChestPane createConfigTransform(final ChestPane pane, final String path, final int amount) {
        ItemStack item = this.itemFromPath(path).amount(amount).build();
        int slot = item.getAmount() - 1;
        return pane.element(ItemStackElement.of(item, click -> {
            if (click.cause().isLeftClick()) {
                this.close();
                Resolver.Builder resolver = Resolver.builder().player(this.player).tag("setting", path);
                Text.fromString(this.player, this.config.string("menuEditingPrompt"), resolver.build()).send();
                this.storage.act(this.player.getUniqueId(), i -> {
                    //Modify config, set message content based on result.
                    String content = "settingChange" + (this.config.modify(path, i) ? "Successful" : "Failure");
                    Text.fromString(this.player, this.config.string(content), resolver.tag("change", i).build()).send();
                });
            }
        }), slot % 9, slot / 9);
    }

    private PaperItemBuilder itemFromPath(final String path) {
        return PaperItemBuilder.of(this.menu.item(path, this.player))
                .name(Component.text(path.substring(path.lastIndexOf('.') + 1)));
    }
}
