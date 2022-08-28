package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.placeholders.ResolverFactory;
import games.cultivate.mcmmocredits.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.core.util.Vector2;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.type.ChestInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseMenu implements Menu {
    protected final List<TransformContext<ChestPane, PlayerViewer>> transformations;
    protected final MenuConfig menu;
    protected final Player player;
    protected final ResolverFactory resolverFactory;
    protected final MCMMOCredits plugin;
    private final Component title;
    private final int rows;
    private ChestInterface chest;

    BaseMenu(final MenuConfig menu, final ResolverFactory resolverFactory, final Player player, final MCMMOCredits plugin, final String path) {
        this.transformations = new ArrayList<>();
        this.menu = menu;
        this.resolverFactory = resolverFactory;
        this.player = player;
        this.plugin = plugin;
        this.title = Text.fromString(player, menu.string(path + ".info.title"), resolverFactory.fromUsers(player)).toComponent();
        this.rows = menu.integer(path + ".info.size") / 9;
    }

    public abstract void applySpecialItems();

    @Override
    public ChestInterface chest() {
        if (this.chest == null) {
            this.load();
        }
        return this.chest;
    }

    @Override
    public PlayerViewer viewer() {
        return PlayerViewer.of(this.player);
    }

    public void applyFillItems() {
        if (this.menu.bool("all.navigation")) {
            String path = "main.items.navigation";
            this.transformations.add(TransformContext.of(2, (pane, view) -> {
                pane = pane.element(ItemStackElement.of(this.menu.item(path, this.player, this.resolverFactory), click -> {
                    if (click.click().leftClick()) {
                        Bukkit.getScheduler().callSyncMethod(this.plugin, () -> {
                            Bukkit.dispatchCommand(this.player, "credits menu main");
                            return this;
                        });
                    }
                }), this.menu.slot(path) % 9, this.menu.slot(path) / 9);
                return pane;
            }));
        }
        if (this.menu.bool("all.fill")) {
            ItemStackElement<ChestPane> filler = ItemStackElement.of(this.menu.item("main.items.fill", this.player, this.resolverFactory));
            this.transformations.add(TransformContext.of(1, (pane, view) -> {
                for (Map.Entry<Vector2, ItemStackElement<ChestPane>> ele : pane.chestElements().entrySet()) {
                    if (ele.getValue().equals(ItemStackElement.empty())) {
                        Vector2 vector = ele.getKey();
                        pane = pane.element(filler, vector.x(), vector.y());
                    }
                }
                return pane;
            }));
        }
    }

    @Override
    public void load() {
        this.applySpecialItems();
        this.applyFillItems();
        this.chest = new ChestInterface(this.rows, this.title, this.transformations, List.of(), true, 10, ClickHandler.cancel());
    }
}
