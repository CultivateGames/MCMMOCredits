package games.cultivate.mcmmocredits.menu;

import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.type.ChestInterface;

public interface Menus {

    default void open() {
        if (this.chest() == null) {
            this.load();
        }
        this.chest().open(this.viewer());
    }

    default void close() {
        this.viewer().close();
    }

    void load();

    ChestInterface chest();

    PlayerViewer viewer();
}
