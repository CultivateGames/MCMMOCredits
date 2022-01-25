package games.cultivate.mcmmocredits.util;

import cloud.commandframework.types.tuples.Triplet;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.ConfigHandler;
import games.cultivate.mcmmocredits.config.Keys;
import games.cultivate.mcmmocredits.database.Database;
import it.unimi.dsi.fastutil.Pair;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.placeholder.Placeholder;
import net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.type.ChestInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * This class is responsible for holding various methods which need to be accessible and have no sensible location elsewhere.
 */
public class Util {
    private static final String[] EMPTY_ARRAY = new String[0];

    public static ChestInterface.Builder mainInterface(Player player) {
        ChestInterface.Builder cb = ChestInterface.builder();
        cb = cb.addTransform(1, (pane, view) -> {
            Triplet<ItemStack, Integer, Integer> itemInfo = Objects.requireNonNull(prepareItem(Keys.GUI_REDEMPTION.getItemStack(), player));
            return pane.element(ItemStackElement.of(itemInfo.getFirst()), itemInfo.getSecond(), itemInfo.getThird());
        });
        cb = cb.title(parse(player, MiniMessage.miniMessage().deserialize(Keys.GUI_TITLE.getString())));
        cb = cb.rows(Keys.GUI_SIZE.getInt() / 9);
        cb = cb.clickHandler(ClickHandler.cancel());
        return cb;
    }

    protected static Triplet<ItemStack, Integer, Integer> prepareItem(ItemStack item, Player player) {
        int slot;
        if (item.hasItemMeta()) {
            item.editMeta(meta -> {
                meta.displayName(parse(player, meta.displayName()));
                meta.lore(parseList(player, Objects.requireNonNull(meta.lore())));
            });
            slot = item.getItemMeta().getPersistentDataContainer().getOrDefault(MCMMOCredits.key, PersistentDataType.INTEGER, 0);
            /*
             * i = slot, x = horizontal location, z = width (9), y = vertical location
             * i = x + (z * y);
             * x = i % z;    // % is the "modulo operator", the remainder of i / width;
             * y = i / z;    // where "/" is an integer division
             */
            return Triplet.of(item, slot % 9, slot / 9);
        }
        return Triplet.of(item,0, 0);
    }

    /**
    private static Integer[] calculateXYPosition(ItemStack item) {
        int slot = 0;
        if (item.hasItemMeta()) {
            slot = item.getItemMeta().getPersistentDataContainer().getOrDefault(MCMMOCredits.key, PersistentDataType.INTEGER, 0);
        }
        return new Integer[]{Math.max(0, slot - 1), slot % 8};
    }

    protected static ItemStack parseItemStackPlaceholders(ItemStack item, Player player) {
        if (!item.hasItemMeta()) {
            return item;
        }
        item.editMeta(meta -> {
            meta.displayName(parse(player, meta.displayName()));
            meta.lore(parseList(player, Objects.requireNonNull(meta.lore())));
        });
        return item;
    }
     **/

    //This is abysmal, but I don't see how to get around this.
    private static Component parse(Player player, Component comp) {
        return MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, MiniMessage.miniMessage().serialize(comp)), Util.quickResolver(player));
    }

    private static List<Component> parseList(Player player, List<Component> comp) {
        return comp.stream().map(i -> MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, MiniMessage.miniMessage().serialize(i)), Util.quickResolver(player))).toList();
    }

    public static String[] getPathFromSuffix(String suffix) {
        for (Keys key : Keys.all) {
            if (key.path()[key.path().length - 1].endsWith(suffix)) {
                return key.path();
            }
        }
        return EMPTY_ARRAY;
    }

    @SuppressWarnings("deprecation")
    public static Optional<UUID> shouldProcessUUID(@NotNull CommandSender sender, @NotNull String playerName) {
        if (Bukkit.getPlayer(playerName) != null) {
            return Optional.of(Objects.requireNonNull(Bukkit.getPlayer(playerName)).getUniqueId());
        }
        if (Keys.USERCACHE_LOOKUP.getBoolean() && MCMMOCredits.isPaper() && Bukkit.getOfflinePlayerIfCached(playerName) != null) {
            return Optional.of(Objects.requireNonNull(Bukkit.getOfflinePlayerIfCached(playerName)).getUniqueId());
        }
        if (Keys.UNSAFE_LOOKUP.getBoolean() && Bukkit.getOfflinePlayer(playerName).hasPlayedBefore()) {
            return Optional.of(Objects.requireNonNull(Bukkit.getPlayerUniqueId(playerName)));
        }
        ConfigHandler.sendMessage(sender, Keys.PLAYER_DOES_NOT_EXIST, quickResolver(sender));
        return Optional.empty();
    }

    /**
     * PlaceholderResolver for Config messages.
     * player = Player's username
     * credits = Player's credits
     */
    public static PlaceholderResolver.Builder basicBuilder(Player player) {
        return PlaceholderResolver.builder().placeholders(createPlaceholders("player", player.getName(), "credits", Database.getCredits(player.getUniqueId()) + ""));
    }

    public static PlaceholderResolver.Builder settingsBuilder(CommandSender sender, String setting, String change) {
        List<Placeholder<?>> phList = createPlaceholders("setting", setting, "change", change);
        return sender instanceof Player player ? basicBuilder(player).placeholders(phList) : PlaceholderResolver.builder().placeholders(phList);
    }

    /**
     * amount = Amount of Credits in transaction
     * sender = CommandSender's name (must be player)
     * sender_credits = CommandSender's MCMMO Credits (must be player)
     * + basicBuilder
     */
    public static PlaceholderResolver.Builder transactionBuilder(Pair<@Nullable CommandSender, @NotNull Player> pair, int amount) {
        PlaceholderResolver.Builder right = basicBuilder(pair.right()).placeholder(createPlaceholder("amount", amount + ""));
        if (pair.left() != null && pair.left() instanceof Player leftPlayer) {
            return right.placeholders(createPlaceholders("sender", leftPlayer.getName(), "sender_credits", Database.getCredits(leftPlayer.getUniqueId()) + ""));
        }
        return right;
    }

    /**
     * skill = skill redeemed into
     * cap = skill cap of relevant skill
     * + transactionBuilder
     */
    public static PlaceholderResolver.Builder redeemBuilder(Pair<@Nullable CommandSender, @NotNull Player> pair, String skill, int cap, int amount) {
        return transactionBuilder(pair, amount).placeholders(createPlaceholders("skill", skill, "cap", cap + ""));
    }

    public static @NotNull PlaceholderResolver quickResolver(@NotNull CommandSender sender) {
        return sender instanceof Player senderPlayer ? basicBuilder(senderPlayer).build() : PlaceholderResolver.placeholders(createPlaceholder("sender", sender.getName()));
    }

    public static List<Placeholder<?>> createPlaceholders(String... s) {
        List<Placeholder<?>> phList = new ArrayList<>();
        for (int i = 0; i < s.length - 1; i += 2) {
            phList.add(Placeholder.miniMessage(s[i], s[i + 1]));
        }
        return phList;
    }

    public static Placeholder<?> createPlaceholder(String... s) {
        return Placeholder.miniMessage(s[0], s[1]);
    }
}
