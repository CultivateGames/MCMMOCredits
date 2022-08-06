package games.cultivate.mcmmocredits.menu;

import net.kyori.adventure.text.Component;

public interface Menus {
    Component title();
    int rows();
    boolean update();
    int updateFrequency();
    void open();
    void close();
    void applyFillItems();
}
