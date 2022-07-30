package games.cultivate.mcmmocredits.config;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public enum ItemType {
    EDIT_MESSAGES_ITEM("editing.messages.item"),
    EDIT_SETTINGS_ITEM("editing.settings.item"),
    MAIN_FILL("main.items.fill"),
    MAIN_MESSAGES("main.items.messages"),
    MAIN_NAVIGATION("main.items.navigation"),
    MAIN_REDEEM("main.items.redeem"),
    MAIN_SETTINGS("main.items.settings"),
    ACROBATICS_ITEM("redeem.items.acrobatics"),
    ALCHEMY_ITEM("redeem.items.alchemy"),
    ARCHERY_ITEM("redeem.items.archery"),
    AXES_ITEM("redeem.items.axes"),
    EXCAVATION_ITEM("redeem.items.excavation"),
    FISHING_ITEM("redeem.items.fishing"),
    HERBALISM_ITEM("redeem.items.herbalism"),
    MINING_ITEM("redeem.items.mining"),
    REPAIR_ITEM("redeem.items.repair"),
    SWORDS_ITEM("redeem.items.swords"),
    TAMING_ITEM("redeem.items.taming"),
    UNARMED_ITEM("redeem.items.unarmed"),
    WOODCUTTING_ITEM("redeem.items.woodcutting");

    private final List<String> path;

    ItemType(final String path) {
        this.path = Arrays.asList(path.split("\\."));
    }

    public @NotNull List<String> path() {
        return this.path;
    }

    public static ItemType fromSkill(final PrimarySkillType skill) {
        for (ItemType it : ItemType.values()) {
            if (it.path().contains(skill.name().toLowerCase())) {
                return it;
            }
        }
        return null;
    }
}
