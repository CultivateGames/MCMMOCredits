package games.cultivate.mcmmocredits.item;

public class MenuItem extends Item {
    private String command;
    private int slot;

    public int x() {
        return slot % 9;
    }

    public int y() {
        return slot / 9;
    }
}
