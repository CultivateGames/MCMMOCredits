package games.cultivate.mcmmocredits.user;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class User extends CommandExecutor {

   public User(final UUID uuid, final String username, final int credits, final int redeemed) {
        super(uuid, username, credits, redeemed);
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public boolean isConsole() {
        return false;
    }

    @Override
    public CommandSender sender() {
        return this.player();
    }

    @Override
    public Player player() {
        return Bukkit.getPlayer(this.uuid());
    }
}
