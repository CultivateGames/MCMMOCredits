package games.cultivate.mcmmocredits.commands;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public final class Transaction {
    private final CommandSender sender;
    private final UUID recipient;
    private final int amount;
    private final PrimarySkillType skill;

    public Transaction(CommandSender sender, UUID recipient, int amount, PrimarySkillType skill) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.skill = skill;
    }

    Transaction(UUID recipient, int amount, PrimarySkillType skill) {
        this(Bukkit.getConsoleSender(), recipient, amount, skill);
    }

    Transaction(CommandSender sender, UUID recipient, int amount) {
        this(sender, recipient, amount, null);
    }

    public CommandSender sender() {
        return sender;
    }

    public UUID recipient() {
        return recipient;
    }

    public int amount() {
        return amount;
    }

    public PrimarySkillType skill() {
        return skill;
    }
}
