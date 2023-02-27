package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public record ItemProperties(String name, List<String> lore) {
    public static ItemProperties empty() {
        return new ItemProperties("", List.of(""));
    }

    public ItemStack apply(final ItemStack item, final Player player, final Resolver resolver) {
        Component display = Text.fromString(player, this.name, resolver).toComponent();
        List<Component> displayLore = this.lore.stream().map(x -> Text.fromString(player, x, resolver).toComponent()).toList();
        ItemStack stackCopy = new ItemStack(item);
        stackCopy.editMeta(meta -> {
            if (!this.name.isEmpty()) {
                meta.displayName(display);
            }
            if (this.lore.stream().noneMatch(String::isEmpty)) {
                meta.lore(displayLore);
            }
        });
        return stackCopy;
    }
}
