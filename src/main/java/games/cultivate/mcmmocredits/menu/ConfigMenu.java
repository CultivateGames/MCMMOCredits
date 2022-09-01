package games.cultivate.mcmmocredits.menu;

import broccolai.corn.paper.item.PaperItemBuilder;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.GeneralConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.placeholders.ResolverFactory;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.util.InputStorage;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.spongepowered.configurate.CommentedConfigurationNode;

/**
 * {@link Menu} allowing users to edit the configuration and have the changes applied from in-game via chat queue.
 */
public final class ConfigMenu extends BaseMenu {
    private final InputStorage storage;
    private final GeneralConfig config;

    ConfigMenu(final MenuConfig menu, final ResolverFactory resolverFactory, final Player player, final MCMMOCredits plugin, final GeneralConfig config, final InputStorage storage) {
        super(menu, resolverFactory, player, plugin, "editing");
        this.config = config;
        this.storage = storage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applySpecialItems() {
        this.transformations.add(TransformContext.of(3, (pane, view) -> {
            int slot = 1;
            for (CommentedConfigurationNode node : this.config.nodes()) {
                String path = this.config.joinedPath(node);
                if (!path.contains("mysql") && !path.contains("item") && !path.startsWith("database")) {
                    String display = path.substring(0, path.indexOf('.'));
                    if (!display.startsWith("database")) {
                        pane = this.createConfigTransform(pane, path, display, slot);
                        slot++;
                    }
                }
            }
            return pane;
        }));
    }

    /**
     * Used to transform a {@link ChestPane} to allow users to edit the configuration via chat queue when added menu items are left-clicked.
     *
     * @param pane   The current {@link ChestPane}
     * @param path   The configuration path to be changed.
     * @param type   The configuration section to be changed.
     * @param amount number representing the quantity of the {@link ItemStack}. Number is changed to help differentiate between options.
     * @return The modified {@link ChestPane}
     */
    private ChestPane createConfigTransform(final ChestPane pane, final String path, final String type, final int amount) {
        ItemStack item = this.itemFromPath(path, type).amount(amount).build();
        int slot = item.getAmount() - 1;
        return pane.element(ItemStackElement.of(item, click -> {
            if (click.cause().isLeftClick()) {
                this.close();
                Resolver.Builder resolver = this.resolverFactory.builder().users(this.player).tag("setting", path);
                Text.fromString(this.player, this.config.string("menuEditingPrompt"), resolver.build()).send();
                this.storage.act(this.player.getUniqueId(), i -> {
                    //Modify config, set message content based on result.
                    String content = "settingChange" + (this.config.modify(path, i) ? "Successful" : "Failure");
                    Text.fromString(this.player, this.config.string(content), resolver.tag("change", i).build()).send();
                });
            }
        }), slot % 9, slot / 9);
    }

    private PaperItemBuilder itemFromPath(final String path, final String type) {
        return PaperItemBuilder.of(this.menu.item("editing." + type, this.player, this.resolverFactory))
                .name(Component.text(path.substring(path.lastIndexOf('.') + 1)));
    }
}
