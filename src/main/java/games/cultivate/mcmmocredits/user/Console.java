package games.cultivate.mcmmocredits.user;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class Console extends CommandExecutor {
    public static final Console INSTANCE = new Console();

    private Console() {
        super(new UUID(0, 0), "CONSOLE", 0, 0);
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public boolean isConsole() {
        return true;
    }

    @Override
    public CommandSender sender() {
        return Bukkit.getConsoleSender();
    }

    @Override
    public Player player() {
        throw new UnsupportedOperationException("Console is not a player!");
    }
}
