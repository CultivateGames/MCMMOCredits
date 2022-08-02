package games.cultivate.mcmmocredits.menu;

import org.bukkit.entity.Player;

public enum MenuType {
    MAIN, MESSAGES, SETTINGS, REDEEM;

    public boolean canOpen(Player player) {
        return player.hasPermission(this.permission());
    }

    public String permission() {
        return "mcmmocredits.menu." + this.name().toLowerCase();
    }
}
