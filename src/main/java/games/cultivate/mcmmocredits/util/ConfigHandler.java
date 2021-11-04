package games.cultivate.mcmmocredits.util;

import games.cultivate.mcmmocredits.MCMMOCredits;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>This class is responsible for configuration management for the messages.conf and settings.conf file.
 * This class is also responsible for local placeholder parsing, and message sending.</p>
 *
 * @see ConfigHandler#createFile(String)
 * @see ConfigHandler#loadFile(String)
 * @see ConfigHandler#value(String)
 * @see ConfigHandler#message(String)
 * @see ConfigHandler#parse(OfflinePlayer, String)
 * @see ConfigHandler#parse(OfflinePlayer, String, int)
 * @see ConfigHandler#parse(OfflinePlayer, String, String, int, int)
 * @see ConfigHandler#sendMessage(CommandSender, String)
 * @see CreditsMessages
 * @see CreditsSettings
 */
public final class ConfigHandler {
    static final Pattern pattern = Pattern.compile("%(.*?)%");
    public static CommentedConfigurationNode messages_instance;
    public static CommentedConfigurationNode config_instance;

    /**
     * <p>This method is provided to easily access values in settings.conf
     * We are providing an {@link Object} so that we can access different types of value in the file in the future.</p>
     *
     * @param key String which represents what key we should access in settings.conf
     * @return {@link Object} which represents the provided key.
     */
    public static Object value(String key) {
        return config_instance.node(key).raw();
    }

    /**
     * <p>This method is provided to easily access values in messages.conf</p>
     *
     * @param messageKey String which represents what key we should access in messages.conf
     * @return {@link String} which represents the provided key.
     */
    public static String message(String messageKey) {
        return messages_instance.node(messageKey).getString();
    }

    /**
     * <p>This method is responsible for creating or skipping creation of our config files.</p>
     * <p>We create a File instance, then check if we need to make directories or create the file if it's missing.
     * Then, we send feedback based on the result of that check.</p>
     *
     * @param fileName String representing which file we are creating.
     * @return {@link File} object for the file we just created.
     */
    public static File createFile(String fileName) {
        File file = new File(MCMMOCredits.getInstance().getDataFolder().getAbsolutePath() + "\\" + fileName + ".conf");
        try {
            if (file.getParentFile().mkdirs() && file.createNewFile()) {
                Bukkit.getLogger().log(Level.INFO, "[MCMMOCredits] Created" + fileName + ".conf file!");
            } else {
                Bukkit.getLogger().log(Level.INFO, "[MCMMOCredits] " + fileName + ".conf already exists... skipping.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * <p>This method is responsible for attempting to load our config files, and set an accessible instance of the file.</p>
     * <p>
     * We are doing the following here:
     * <br>
     * 1. Create a {@link File} and {@link Path} object.
     * <br>
     * 2. Build a {@link HoconConfigurationLoader} to build our HOCON configurations.
     * <br>
     * 3. Create a {@link CommentedConfigurationNode} to interact with our loader.
     * <br>
     * 4. Load the file, and set it's contents using the appropriate {@link TypeSerializer}.
     * <br>
     * 5. Save the file and set the instance.
     *
     * @param fileType String representing which file we are trying to load.
     */
    public static void loadFile(String fileType) {
        File file = createFile(fileType);
        Path path = Paths.get(file.getPath());
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .prettyPrinting(true)
                .path(path)
                .build();
        CommentedConfigurationNode node;
        if (fileType.equalsIgnoreCase("settings")) {
            CreditsSettings config;
            try {
                node = loader.load();
                config = node.get(CreditsSettings.class);
                Objects.requireNonNull(node).set(CreditsSettings.class, config);
                loader.save(node);
                config_instance = node;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        if (fileType.equalsIgnoreCase("messages")) {
            CreditsMessages config;
            try {
                node = loader.load();
                config = node.get(CreditsMessages.class);
                Objects.requireNonNull(node).set(CreditsMessages.class, config);
                loader.save(node);
                messages_instance = node;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * <p>This method is responsible for parsing universal local, and external placeholders. This is compatible with every message
     * in the plugin. We separate out this parsing to avoid processing when possible.</p>
     * <p>Messages can be parsed for the following placeholders here:</p>
     * %credits%: MCMMO Credit balance for the provided {@link Player}
     * <br>
     * %player%: Username of the provided {@link Player}
     * <br>
     * {@link PlaceholderAPI}: We will parse PAPI messages in all places.
     * <br>
     *
     * @param offlinePlayer {@link OfflinePlayer} instance to parse placeholders for
     * @param text          {@link String} object to parse placeholders within.
     * @return {@link String} with parsed, local and external placeholders.
     * @see ConfigHandler#parse(OfflinePlayer, String, int)
     * @see ConfigHandler#parse(OfflinePlayer, String, String, int, int)
     */
    public static String parse(OfflinePlayer offlinePlayer, String text) {
        Matcher matcher = pattern.matcher(text);
        StringBuilder sb = new StringBuilder();
        String replacement = null;
        UUID uuid = offlinePlayer.getUniqueId();
        while (matcher.find()) {
            switch (matcher.group(0)) {
                case "%credits%" -> replacement = Database.getCredits(uuid) + "";
                case "%player%" -> replacement = offlinePlayer.getName();
            }
            if (replacement == null) {
                continue;
            }
            matcher.appendReplacement(sb, "");
            sb.append(replacement);
        }
        matcher.appendTail(sb);
        return PlaceholderAPI.setPlaceholders(offlinePlayer, sb.toString());
    }

    /**
     * <p>This method is responsible for parsing all placeholders in
     * {@link ConfigHandler#parse(OfflinePlayer, String)} along with amount values where relevant.</p>
     * <p>Messages can be parsed for the following placeholders here:</p>
     * %credits%: MCMMO Credit balance for the provided {@link Player}
     * <br>
     * %player%: Username of the provided {@link Player}
     * <br>
     * %amount%: Amount of MCMMO Credits being passed.
     * <br>
     * {@link PlaceholderAPI}: We will parse PAPI messages in all places.
     * <br>
     *
     * @param offlinePlayer {@link OfflinePlayer} instance to parse placeholders for
     * @param text          {@link String} object to parse placeholders within.
     * @return {@link String} with parsed, local and external placeholders.
     * @see ConfigHandler#parse(OfflinePlayer, String)
     * @see ConfigHandler#parse(OfflinePlayer, String, String, int, int)
     */
    public static String parse(OfflinePlayer offlinePlayer, String text, int amount) {
        Matcher matcher = pattern.matcher(text);
        StringBuilder sb = new StringBuilder();
        String replacement = null;
        while (matcher.find()) {
            switch (matcher.group(0)) {
                case "%credits%" -> replacement = Database.getCredits(offlinePlayer.getUniqueId()) + "";
                case "%player%" -> replacement = offlinePlayer.getName();
                case "%amount%" -> replacement = amount + "";
            }
            if (replacement == null) {
                continue;
            }
            matcher.appendReplacement(sb, "");
            sb.append(replacement);
        }
        matcher.appendTail(sb);
        return PlaceholderAPI.setPlaceholders(offlinePlayer, sb.toString());
    }

    /**
     * <p>This method is responsible for parsing all placeholders available.
     * This is compatible only in instances where all arguments can be passed.
     * We separate out this parsing to avoid processing when possible.</p>
     * <p>Messages can be parsed for the following placeholders here:</p>
     * %credits%: MCMMO Credit balance for the provided {@link Player}
     * <br>
     * %player%: Username of the provided {@link Player}
     * <br>
     * %amount%: Amount of MCMMO Credits being passed.
     * <br>
     * %skill%: Name of relevant MCMMO Skill
     * <br>
     * %cap%: Level Cap of previously reference MCMMO Skill.
     * <br>
     * {@link PlaceholderAPI}: We will parse PAPI messages in all places.
     * <br>
     *
     * @param offlinePlayer {@link OfflinePlayer} instance to parse placeholders for
     * @param text          {@link String} object to parse placeholders within.
     * @return {@link String} with parsed, local and external placeholders.
     * @see ConfigHandler#parse(OfflinePlayer, String, int)
     * @see ConfigHandler#parse(OfflinePlayer, String, String, int, int)
     */
    public static String parse(OfflinePlayer offlinePlayer, String text, String skill, int cap, int amount) {
        Matcher matcher = pattern.matcher(text);
        StringBuilder sb = new StringBuilder();
        String replacement = null;
        while (matcher.find()) {
            switch (matcher.group(0)) {
                case "%credits%" -> replacement = Database.getCredits(offlinePlayer.getUniqueId()) + "";
                case "%player%" -> replacement = offlinePlayer.getName();
                case "%amount%" -> replacement = amount + "";
                case "%skill%" -> replacement = skill;
                case "%cap%" -> replacement = cap + "";
            }
            if (replacement == null) {
                continue;
            }
            matcher.appendReplacement(sb, "");
            sb.append(replacement);
        }
        matcher.appendTail(sb);
        return PlaceholderAPI.setPlaceholders(offlinePlayer, sb.toString());
    }

    /**
     * <p>This method is responsible for sending messages to users.</p>
     *
     * @param commandSender {@link CommandSender} to send a message to.
     * @param text          {@link String} that is being converted to {@link Component} and sent to the user.
     */
    public static void sendMessage(CommandSender commandSender, String text) {
        commandSender.sendMessage(MCMMOCredits.getMM().parse(message("prefix") + text));
    }

    /**
     * <p>This class is used to help generate the messages.conf file. The values in this class are defaults for the plugin.</p>
     *
     * <p>YAML doesn't support comments in Configurate 4.2.0-SNAPSHOT because of SnakeYAML.
     * Until then, we will use HOCON. We can use Transformations to go back to YAML in a future version if required</p>
     *
     * @see <a href="https://github.com/SpongePowered/Configurate/pull/175" target="_top">Refer to this PR for more info.</a>
     */
    @ConfigSerializable
    static class CreditsMessages {
        @Comment("Prefix for all plugin messages.")
        private final String prefix = "<gold><bold>CREDITS</bold> ";

        @Comment("Shown to user when invalid arguments are used in a command.")
        private final String invalid_args = "<red>Invalid args!";

        @Comment("Shown to user when they use an invalid number.")
        private final String must_be_number = "<red>You need to specify a valid number.";

        @Comment("Shown to user when they do not have permission to execute a command!")
        private final String no_perms = "<red>You do not have permission to do this!";

        @Comment("Shown to user when they check their own MCMMO Credits amount with /credits.")
        private final String credits_check_self = "<green>You have %credits% MCMMO Credits!";

        @Comment("Shown to user when they check the MCMMO Credit balance of another user with /credits.")
        private final String credits_check_other = "<green>%player% has %credits% MCMMO Credits!";

        @Comment("Shown to user when they use the command system and populate it with a player that does not exist.")
        private final String player_does_not_exist = "<red>This player does not exist in our database!";

        @Comment("Shown to user when they successfully reload the Plugin.")
        private final String reload_successful = "<green>All configuration files have been reloaded!";

        @Comment("Shown to user when they try to redeem MCMMO Credits to go over a skill's level cap.")
        private final String redeem_skill_cap = "<red>You cannot redeem this many MCMMO Credits into %skill%, due to the Level Cap (%cap%).";

        @Comment("Shown to user when they try to redeem more MCMMO Credits than they have available.")
        private final String redeem_not_enough_credits = "<red>You do not have enough MCMMO Credits to do this!";

        @Comment("Shown to user when they successfully redeem MCMMO Credits into a skill.")
        private final String redeem_successful = "<green>Redemption Successful! You have redeemed %amount% Credits into %skill%. You have %credits% Credits remaining.";

        @Comment("Shown to user when they successfully redeem MCMMO Credits into a skill.")
        private final String redeem_successful_other = "<green>Redemption Successful! You have redeemed %amount% Credits into %skill% for %player%. They have %credits% Credits remaining.";

        @Comment("Shown to user on login if send_login_message is set to true in settings.conf")
        private final String login_message = "<hover:show_text:'<green>You have %credits% MCMMO Credits!'><yellow>Hover here to see how many MCMMO Credits you have!";

        @Comment("Shown to user when they successfully redeem MCMMO Credits into a skill. %credits% placeholder will show previous balance since we are updating credit balances asynchronously.")
        private final String modify_credits_add = "<green>You have given %amount% Credits to %player%";

        @Comment("Shown to user when they successfully redeem MCMMO Credits into a skill. %credits% placeholder will show previous balance since we are updating credit balances asynchronously.")
        private final String modify_credits_set = "<yellow>You have set %player%'s Credits to %amount%!";

        @Comment("Shown to user when they successfully redeem MCMMO Credits into a skill. %credits% placeholder will show previous balance since we are updating credit balances asynchronously.")
        private final String modify_credits_take = "<red>You have taken %amount% Credits from %player%";
    }

    /**
     * <p>This class is used to help generate the settings.conf file. The values in this class are defaults for the plugin.</p>
     *
     * <p>YAML doesn't support comments in Configurate 4.2.0-SNAPSHOT because of SnakeYAML. Until then, we will use HOCON.
     * We can use Transformations to go back to YAML in a future version if required.</p>
     *
     * @see <a href="https://github.com/SpongePowered/Configurate/pull/175" target="_top">Refer to this PR for more info.</a>
     */
    @ConfigSerializable
    static class CreditsSettings {
        @Comment("Perform offline player lookups with usercache. PAPER ONLY. Disable if you are having problems.")
        private final boolean use_usercache_lookup = false;

        @Comment("Toggles tab completion for Player based arguments. Useful if you have other plugins which hide staff.")
        private final boolean player_tab_completion = true;

        @Comment("Toggles sending a login message to the user indicating how many MCMMO Credits they have. Message can be configured in messages.conf.")
        private final boolean send_login_message = true;

        @Comment("Toggles console message when a user is added to the MCMMO Credits database")
        private final boolean database_add_message = true;
    }
}
