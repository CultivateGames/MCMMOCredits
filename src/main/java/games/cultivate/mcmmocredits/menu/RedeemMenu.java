package games.cultivate.mcmmocredits.menu;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.skills.SkillTools;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.GeneralConfig;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.util.InputStorage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.paper.element.ItemStackElement;

public final class RedeemMenu extends BaseMenu {
    private final InputStorage storage;
    private final GeneralConfig config;
    private final MCMMOCredits plugin;

    RedeemMenu(final MenuConfig menu, final Player player, final GeneralConfig config, final InputStorage storage, final MCMMOCredits plugin) {
        super(menu, player, "redeem");
        this.config = config;
        this.storage = storage;
        this.plugin = plugin;
    }

    @Override
    public void applySpecialItems() {
        this.transformations.add(TransformContext.of(0, (pane, view) -> {
            for (PrimarySkillType skill : PrimarySkillType.values()) {
                if (SkillTools.isChildSkill(skill)) {
                    continue;
                }
                String path = "redeem.items." + skill.name().toLowerCase();
                int slot = this.menu.slot(path);
                pane = pane.element(ItemStackElement.of(this.menu.item(path, this.player), click -> {
                    if (click.cause().isLeftClick()) {
                        this.close();
                        TagResolver resolver = Resolver.builder().player(this.player).skill(skill).build();
                        Text.fromString(this.player, this.config.string("menuRedeemPrompt"), resolver).send();
                        this.storage.act(this.player.getUniqueId(), i -> {
                            String command = String.format("redeem %s %d", skill.name(), Integer.parseInt(i));
                            Bukkit.getScheduler().callSyncMethod(this.plugin, () -> {
                                Bukkit.dispatchCommand(this.player, command);
                                return this;
                            });
                        });
                    }
                }), slot % 9, slot / 9);
            }
            return pane;
        }));
    }
}

