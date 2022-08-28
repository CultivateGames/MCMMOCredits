package games.cultivate.mcmmocredits.menu;

import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.type.ChestInterface;

public interface Menu {

    default void open() {
        this.chest().open(this.viewer());
    }

    default void close() {
        this.viewer().close();
    }

    void load();

    ChestInterface chest();

    PlayerViewer viewer();
}
