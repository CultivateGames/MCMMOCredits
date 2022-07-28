package games.cultivate.mcmmocredits.menu;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.skills.SkillTools;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.ItemType;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.config.MessagesConfig;
import games.cultivate.mcmmocredits.config.SettingsConfig;
import games.cultivate.mcmmocredits.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.incendo.interfaces.paper.type.ChestInterface;
import org.spongepowered.configurate.CommentedConfigurationNode;

import javax.inject.Inject;

public class MenuFactory {
    private final MenuConfig menus;

    @Inject
    public MenuFactory(MenuConfig menus) {
        this.menus = menus;
    }

    public Menu.Builder base(Player player, String title, int slots) {
        return Menu.builder().player(player).title(title).rows(slots / 9);
    }

    private Menu.Builder baseFromString(Player player, String path) {
        return this.base(player, this.menus.string(path + ".info.title"), this.menus.integer(path + ".info.size"));
    }

    public ChestInterface makeConfigMenu(Player player, Config config) {
        int slot = 0;
        String type = config.name().replace("Config", "");
        Menu.Builder builder = this.baseFromString(player, type.toLowerCase());
        for (CommentedConfigurationNode node : config.nodes()) {
            String path = config.joinedPath(node);
            if (path.contains("mysql") || path.contains("item")) {
                continue;
            }
            ItemStack item = this.menus.item(ItemType.valueOf("EDIT_" + type.toUpperCase() + "_ITEM"), player);
            item.setAmount(slot + 1);
            item.editMeta(meta -> {
                Component displayName = Text.removeItalics(Component.text(path.substring(path.lastIndexOf('.') + 1)));
                meta.displayName(displayName);
                meta.getPersistentDataContainer().set(MCMMOCredits.NAMESPACED_KEY, PersistentDataType.STRING, path);
            });
            builder = builder.configTransfer(new Button(item, slot), ClickType.LEFT, config);
            slot++;
        }
        return builder.build();
    }

    public ChestInterface makeRedemptionMenu(Player player) {
        Menu.Builder builder = this.baseFromString(player, "redeem");
        for (PrimarySkillType skill : PrimarySkillType.values()) {
            if (SkillTools.isChildSkill(skill)) {
                continue;
            }
            Button button = Button.of(this.menus, ItemType.fromSkill(skill), player, String.format("redeem %s ", skill.name()));
            button.addToPDC(PersistentDataType.STRING, skill.name());
            builder = builder.redeemTransfer(button, ClickType.LEFT);
        }
        return builder.build();
    }

    public ChestInterface makeMainMenu(Player player, MessagesConfig messages, SettingsConfig settings) {
        Menu.Builder builder = this.baseFromString(player, "main");
        builder = builder.interfaceTransfer(this.menus.button(ItemType.MAIN_REDEEM, player), ClickType.LEFT, this.makeRedemptionMenu(player));
        if (player.hasPermission("mcmmocredits.gui.admin")) {
            builder = builder.interfaceTransfer(this.menus.button(ItemType.MAIN_MESSAGES, player), ClickType.LEFT, this.makeConfigMenu(player, messages));
            builder = builder.interfaceTransfer(this.menus.button(ItemType.MAIN_SETTINGS, player), ClickType.LEFT, this.makeConfigMenu(player, settings));
        }
        return builder.build();
    }
}
