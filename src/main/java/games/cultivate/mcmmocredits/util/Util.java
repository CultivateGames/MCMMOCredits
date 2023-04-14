//
// MIT License
//
// Copyright (c) 2023 Cultivate Games
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package games.cultivate.mcmmocredits.util;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import games.cultivate.mcmmocredits.menu.ClickTypes;
import games.cultivate.mcmmocredits.menu.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Utility class for methods with no clear association.
 */
public final class Util {
    private static Path pluginPath;
    //We are keeping a string list rather than calculating it to reduce complexity.
    private static final List<String> MCMMO_SKILLS = List.of("acrobatics", "alchemy", "archery", "axes", "excavation", "fishing", "herbalism", "mining", "repair", "swords", "taming", "unarmed", "woodcutting");

    private Util() {
        throw new AssertionError("Util cannot be instantiated!");
    }

    /**
     * Returns a list of non-child skill names from MCMMO, formatted in lowercase.
     *
     * @return A List of formatted non-child skill names.
     */
    public static List<String> getSkillNames() {
        return MCMMO_SKILLS;
    }

    /**
     * Returns a list of non-child skill names from MCMMO, joined by a delimiter.
     *
     * @return A List of formatted non-child skill names.
     */
    public static String getJoinedSkillNames() {
        return Util.joinString(",", MCMMO_SKILLS);
    }

    /**
     * Creates a file and associated directories if they do not exist, given the file name.
     *
     * @param fileName Name of the {@link File} to create.
     */
    public static void createFile(final String fileName) {
        createFile(pluginPath, fileName);
    }

    /**
     * Creates a file and associated directories if they do not exist, given the directory path and the file name.
     *
     * @param dir      The {@link Path} to check for creation.
     * @param fileName Name of the {@link File} to create.
     */
    public static void createFile(final Path dir, final String fileName) {
        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            Files.createFile(dir.resolve(fileName));
        } catch (FileAlreadyExistsException ignored) { //Ignore if file already exists.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns if a player will exceed skill cap on a skill if an amount is applied.
     *
     * @param profile PlayerProfile of the user.
     * @param skill   MCMMO Skill to check against.
     * @param amount  The amount of credits to theoretically apply.
     * @return If the cap will be exceeded.
     */
    public static boolean exceedsSkillCap(final PlayerProfile profile, final PrimarySkillType skill, final int amount) {
        return profile.getSkillLevel(skill) + amount > mcMMO.p.getGeneralConfig().getLevelCap(skill);
    }

    /**
     * Obtains a PlayerProfile from the provided UUID.
     *
     * @param uuid UUID of a user.
     * @return PlayerProfile, or empty optional if the profile is not loaded.
     */
    public static Optional<PlayerProfile> getMCMMOProfile(final UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        PlayerProfile profile = player == null ? mcMMO.getDatabaseManager().loadPlayerProfile(uuid) : UserManager.getPlayer(player).getProfile();
        return profile.isLoaded() ? Optional.of(profile) : Optional.empty();
    }

    /**
     * Returns the path of the plugin directory.
     *
     * @return Path of the plugin directory.
     */
    public static Path getPluginPath() {
        if (pluginPath == null) {
            pluginPath = new File(Bukkit.getPluginsFolder(), "MCMMOCredits").toPath();
        }
        return pluginPath;
    }

    /**
     * Capitalizes the first letter of the input string and sets the remaining characters to lowercase.
     *
     * @param string The input string to be capitalized.
     * @return The capitalized string.
     */
    public static String capitalizeWord(final String string) {
        if (string == null || string.isEmpty()) {
            return string;
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    /**
     * Utility method to create Config Menu items for the Menu Config.
     *
     * @param material type of the item.
     * @param type     type of the click.
     * @return Built item for Menu Config.
     */
    public static Item createConfigItem(final Material material, final ClickTypes type) {
        return Item.builder().item(new ItemStack(material, 1)).slot(-1).type(type).lore(List.of("<gray>Click here to edit this config option!")).build();
    }

    /**
     * Utility method to create items that execute commands for the Menu Config.
     *
     * @param material type of the item.
     * @param name     name of the item as a string.
     * @param lore     lore of the item as a List of string.
     * @param command  command to be executed as a string.
     * @param slot     location of the item in the menu. 0 based.
     * @return Built item for Menu Config.
     */
    public static Item createCommandItem(final Material material, final String name, final String lore, final String command, final int slot) {
        return Item.builder().item(new ItemStack(material, 1)).name(name).lore(List.of(lore)).slot(slot).type(ClickTypes.COMMAND).data(command).build();
    }

    /**
     * Utility method to create items that execute credit redemptions for the Menu Config.
     *
     * @param material type of the item.
     * @param skill    skill that will be redeemed into.
     * @param slot     location of the item in the menu. 0 based.
     * @return Built item for Menu Config.
     */
    public static Item createRedeemItem(final Material material, final PrimarySkillType skill, final int slot) {
        return Item.builder().item(new ItemStack(material, 1)).name("<yellow>" + Util.capitalizeWord(skill.name())).lore(List.of("<yellow><sender>, click here to redeem!")).type(ClickTypes.REDEEM).slot(slot).build();
    }

    /**
     * Utility method that will join a collection of strings with the provided delimiter.
     *
     * @param delimiter string-based delimited.
     * @param members   collection of object to delimit.
     * @param <T>       The object being delimited. Converted to string.
     * @return The combined string.
     */
    public static <T> String joinString(final String delimiter, final Iterable<T> members) {
        StringBuilder sb = new StringBuilder();
        for (T obj : members) {
            sb.append(obj).append(delimiter);
        }
        sb.deleteCharAt(sb.lastIndexOf(delimiter));
        return sb.toString();
    }

    /**
     * Utility method that will join an array of strings with the provided delimiter.
     *
     * @param delimiter string-based delimited.
     * @param array     array of object to delimit. Converted to iterable list.
     * @param <T>       The object being delimited. Converted to string.
     * @return The combined string.
     */
    public static <T> String joinString(final String delimiter, final T[] array) {
        return Util.joinString(delimiter, Arrays.asList(array));
    }
}
