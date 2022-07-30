package games.cultivate.mcmmocredits.menu;

import broccolai.corn.paper.item.PaperItemBuilder;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.skills.SkillTools;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.ItemType;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.config.MessagesConfig;
import games.cultivate.mcmmocredits.config.SettingsConfig;
import games.cultivate.mcmmocredits.data.InputStorage;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.persistence.PersistentDataType;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.type.ChestInterface;
import org.spongepowered.configurate.CommentedConfigurationNode;

import javax.inject.Inject;

public class MenuFactory {
    private final MenuConfig menus;
    private final MCMMOCredits plugin;
    private final MessagesConfig messages;
    private final InputStorage storage;

    @Inject
    public MenuFactory(MenuConfig menus, MessagesConfig messages, InputStorage storage, MCMMOCredits plugin) {
        this.menus = menus;
        this.plugin = plugin;
        this.messages = messages;
        this.storage = storage;
    }

    public Menu.Builder base(Player player, String title, int slots) {
        return Menu.builder().player(player).title(title).rows(slots / 9);
    }

    private Menu.Builder baseFromString(Player player, String path) {
        return this.base(player, this.menus.string(path + ".info.title"), this.menus.integer(path + ".info.size"));
    }

    public ChestInterface makeConfigMenu(Player player, Config config) {
        String type = config.getClass().getSimpleName().replace("Config", "").toLowerCase();
        ChestInterface.Builder ci = ChestInterface.builder()
                .title(Text.fromString(player, menus.string(type + ".info.title")).toComponent())
                .rows(menus.integer(type + ".info.size") / 9);
        int slot = 0;
        for (CommentedConfigurationNode node : config.nodes()) {
            String path = config.joinedPath(node);
            if (path.contains("mysql") || path.contains("item")) {
                continue;
            }
            PaperItemBuilder pib = PaperItemBuilder.of(menus.item(path, player))
                    .amount(slot + 1)
                    .name(Text.removeItalics(Component.text(path.substring(path.lastIndexOf('.') + 1))));
            ci = ci.addTransform(5, (p, v) -> p.element(ItemStackElement.of(pib.build(), c -> {
                if (c.click().leftClick()) {
                    Resolver.Builder resolver = Resolver.builder().player(player).tag("setting", path);
                    Text.fromString(player, messages.string("menuEditingPrompt"), resolver.build()).send();
                    storage.act(player.getUniqueId(), i -> {
                        boolean result = config.modify(path, i);
                        String content = "settingChange" + (result ? "Successful" : "Failure");
                        Text.fromString(player, messages.string(content), resolver.tag("change", i).build()).send();
                    });
                }
            }), (pib.amount() - 1) % 9, (pib.amount() - 1) / 9));
            slot++;
        }
        return ci.build();
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
