package games.cultivate.mcmmocredits.menu;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.ItemType;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.text.Text;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
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
        return this.base(player, this.menus.string(path + ".title"), this.menus.integer(path + ".size"));
    }

    public ChestInterface makeConfigMenu(Player player, Config config) {
        int slot = 0;
        String type = config.name().replace("Config", "");
        Menu.Builder builder = this.baseFromString(player, type.toLowerCase());
        for (CommentedConfigurationNode node : config.nodes()) {
            ItemStack item = this.menus.item(ItemType.valueOf("EDIT_" + type.toUpperCase() + "_ITEM"), player);
            String path = StringUtils.join(node.path().array(), ".");
            if (path.contains("database") || path.contains("item")) {
                continue;
            }
            item.setAmount(slot + 1);
            item.editMeta(meta -> {
                meta.displayName(Component.text(path.substring(path.lastIndexOf('.') + 1), Text.DEFAULT_STYLE));
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
            String skillName = skill.name();
            ItemType item = ItemType.valueOf(skillName + "_ITEM");
            Button button = Button.of(this.menus, item, player, "redeem " + skillName + " ");
            builder = builder.redeemTransfer(button, ClickType.LEFT);
        }
        return builder.build();
    }

    public ChestInterface makeMainMenu(Player player) {
        return this.base(player, "testing", 9).build();
    }
}
