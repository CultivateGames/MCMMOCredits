package games.cultivate.mcmmocredits.menu;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.Config;
import games.cultivate.mcmmocredits.config.ItemType;
import games.cultivate.mcmmocredits.config.MenuConfig;
import games.cultivate.mcmmocredits.config.MessagesConfig;
import games.cultivate.mcmmocredits.data.InputStorage;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.core.transform.Transform;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.transform.PaperTransform;
import org.incendo.interfaces.paper.type.ChestInterface;

import javax.inject.Inject;
import java.util.UUID;

public class Menu {
    private Menu () {}

    public static Builder builder() {
        return new Menu.Builder();
    }

    public static class Builder {
        @Inject
        private static InputStorage storage;
        @Inject
        private static MessagesConfig messages;
        @Inject
        private static MenuConfig menus;

        private ChestInterface.Builder builder = ChestInterface.builder();
        private Player player;

        public Builder player(Player player) {
            this.player = player;
            return this;
        }

        public Builder rows(int rows) {
            this.builder = this.builder.rows(rows);
            return this;
        }

        public Builder title(Component title) {
            this.builder = this.builder.title(Text.parseComponent(title, player));
            return this;
        }

        public Builder title(String title) {
            this.builder = this.builder.title(Text.fromString(player, title).toComponent());
            return this;
        }

        public Builder interfaceTransfer(Button button, ClickType clickType, ChestInterface chestInterface) {
            this.builder = this.builder.addTransform(2, this.interfaceTransform(button, chestInterface, clickType));
            return this;
        }

        public Builder commandTransfer(Button button, ClickType clickType) {
            this.builder = this.builder.addTransform(3, (pane, view) -> pane.element(ItemStackElement.of(button.item(), clickHandler -> {
                if (clickHandler.cause().getClick().equals(clickType) && !button.command().isEmpty()) {
                    view.viewer().player().performCommand(button.command());
                }
            }), button.x(), button.y()));
            return this;
        }

        public Builder redeemTransfer(Button button, ClickType clickType) {
            this.builder = this.builder.addTransform(4, (pane, view) -> pane.element(ItemStackElement.of(button.item(), click -> {
                if (click.cause().getClick().equals(clickType)) {
                    view.viewer().close();
                    ItemStack stack = click.cause().getCurrentItem();
                    if (stack != null && stack.hasItemMeta()) {
                        PersistentDataContainer pdc = stack.getItemMeta().getPersistentDataContainer();
                        PrimarySkillType skill = PrimarySkillType.valueOf(pdc.get(MCMMOCredits.NAMESPACED_KEY, PersistentDataType.STRING));
                        Player player = view.viewer().player();
                        Resolver.Builder rb = Resolver.builder().player(player).skill(skill);
                        Text.fromString(player, messages.string("menuRedeemPrompt"), rb.build()).send();
                        UUID uuid = player.getUniqueId();
                        storage.remove(uuid);
                        storage.add(uuid);
                        storage.act(uuid, i -> player.performCommand(button.command() + Integer.parseInt(i)));
                    }
                }
            }), button.x(), button.y()));
            return this;
        }

            private Transform<ChestPane, PlayerViewer> interfaceTransform (Button button, ChestInterface chestInterface, ClickType clickType) {
                return (pane, view) -> pane.element(ItemStackElement.of(button.item(), clickHandler -> {
                    if (clickHandler.cause().getClick().equals(clickType)) {
                        chestInterface.open(view.viewer());
                    }
                }), button.x(), button.y());
            }

        private Transform<ChestPane, PlayerViewer> configTransform(Button button, ClickType clickType, Config config) {
            return (pane, view) -> pane.element(ItemStackElement.of(button.item(), click -> {
                if (click.cause().getClick().equals(clickType)) {
                    view.viewer().close();
                    ItemStack stack = click.cause().getCurrentItem();
                    if (stack != null && stack.hasItemMeta()) {
                        String path = stack.getItemMeta().getPersistentDataContainer().get(MCMMOCredits.NAMESPACED_KEY, PersistentDataType.STRING);
                        Player player = view.viewer().player();
                        Resolver.Builder rb = Resolver.builder().player(player).tag("setting", path);
                        Text.fromString(player, messages.string("menuEditingPrompt"), rb.build()).send();
                        UUID uuid = player.getUniqueId();
                        storage.remove(uuid);
                        storage.add(uuid);
                        storage.act(uuid, i -> {
                            boolean result = config.modify(path, i);
                            String content = "settingChange" + (result ? "Successful" : "Failure");
                            Text.fromString(player, messages.string(content), rb.tag("change", i).build()).send();
                        });
                    }
                }
            }), button.x(), button.y());
        }

        public ChestInterface build() {
            if (menus.bool("fill", false)) {
                ItemStackElement<ChestPane> item = ItemStackElement.of(menus.item(ItemType.MAIN_FILL, player));
                this.builder = this.builder.addTransform(0, PaperTransform.chestFill(item));
            }
            if (menus.bool("navigation", false)) {
                Button button = Button.of(menus, ItemType.MAIN_NAVIGATION, player, "credits menu");
                //We are going to fix this after commit.
                //this.builder = this.builder.addTransform(1, this.commandTransform(button, ClickType.LEFT));
            }
            this.builder = this.builder.updates(true, 10).clickHandler(ClickHandler.cancel());
            ChestInterface updates = this.builder.build();
            System.out.println(updates.updates() + " " + updates.updateDelay());
            return updates;
        }
    }
}
