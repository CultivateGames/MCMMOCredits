package games.cultivate.mcmmocredits.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.type.ChestInterface;

import java.util.List;
import java.util.Objects;

public class Menu {
    private ChestInterface chestInterface;
    private PlayerViewer player;
    private Component title;
    private int rows;
    private boolean updates;
    private List<Button> buttons;
    private static final NamespacedKey NAMESPACED_KEY = Objects.requireNonNull(NamespacedKey.fromString("mcmmocredits"));

    public ChestInterface menu() {
        return chestInterface;
    }

    public void menu(ChestInterface menu) {
        this.chestInterface = menu;
    }

    public PlayerViewer player() {
        return player;
    }

    public void player(PlayerViewer player) {
        this.player = player;
    }

    public Component title() {
        return title;
    }

    public void title(Component title) {
        this.title = title;
    }

    public int rows() {
        return rows;
    }

    public void rows(int rows) {
        this.rows = rows;
    }

    public boolean update() {
        return updates;
    }

    public void update(boolean updates) {
        this.updates = updates;
    }

    public List<Button> buttons() {
        return buttons;
    }

    public void buttons(List<Button> buttons) {
        this.buttons = buttons;
    }

    public void open() {
        this.chestInterface.open(player);
    }
}
