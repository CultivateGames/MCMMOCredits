package games.cultivate.mcmmocredits.item;

import dev.dbassett.skullcreator.SkullCreator;
import games.cultivate.mcmmocredits.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class Item {
    private Material material;
    private Component name;
    private List<Component> lore;
    private int amount;
    private short durability;
    private boolean glow;
    private Map<Enchantment, Integer> enchantments;
    private @Nullable String skull;

    public Item() {
    }

    @SuppressWarnings("deprecation")
    public Item(ItemStack itemStack) {
        this.material = itemStack.getType();
        this.amount = itemStack.getAmount();
        this.durability = itemStack.getDurability();
        ItemMeta meta = itemStack.getItemMeta();
        this.name = meta.displayName();
        this.lore = meta.lore();
        Map<Enchantment, Integer> map = meta.getEnchants();
        this.enchantments = map;
        this.glow = map.isEmpty();
        this.skull = null;
    }

    @SuppressWarnings("deprecation")
    public ItemStack toStack() {
        ItemStack result = new ItemStack(this.material, this.amount);
        result.setDurability(this.durability);
        result.editMeta(meta -> {
            meta.displayName(this.name);
            meta.lore(this.lore);
            if (this.glow) {
                meta.addEnchant(Enchantment.ARROW_INFINITE, 10, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        });
        if (this.skull != null) {
            result = SkullCreator.itemWithBase64(result, this.skull);
        }
        return result;
    }

    public ItemStack toStack(Player player) {
        this.parseDisplay(player);
        return this.toStack();
    }

    public static Item fromStack(ItemStack itemStack) {
        return new Item(itemStack);
    }



    public void parseDisplay(Player player) {
        this.name = Text.parseComponent(this.name, player);
        this.lore = this.lore.stream().map(i -> Text.parseComponent(i, player)).toList();
    }

    public void material(Material material) {
        this.material = material;
    }

    public void name(Component name) {
        this.name = name;
    }

    public void lore(List<Component> lore) {
        this.lore = lore;
    }

    public void amount(int amount) {
        this.amount = amount;
    }

    public void durability(int durability) {
        this.durability = (short) durability;
    }

    public void glow(boolean glow) {
        this.glow = glow;
        if (glow) {
            this.addEnchant(Enchantment.ARROW_INFINITE, 10);
        }
    }

    public void addEnchant(Enchantment enchantment, Integer level) {
        this.enchantments.putIfAbsent(enchantment, level);
    }

    public void enchantments(Map<Enchantment, Integer> enchantments) {
        if (!this.glow) {
            throw new IllegalStateException("Adding enchantments when item should not glow!");
        }
        this.enchantments = enchantments;
    }

    public void skull(String skull) {
        this.skull = skull;
    }
}