package games.cultivate.mcmmocredits.util;

import games.cultivate.mcmmocredits.MCMMOCredits;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

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
 * This class is responsible for all configuration management, and message parsing/sending.
 */
public final class ConfigHandler {
    private static final Pattern pattern = Pattern.compile("%(.*?)%");
    private static CommentedConfigurationNode messages_instance;
    private static CommentedConfigurationNode settings_instance;

    /**
     * This is used to access values from settings.conf as an Object.
     * TODO: Enumerate config settings.
     */
    public static Object value(String key) {
        return settings_instance.node(key).raw();
    }

    /**
     * This is used to access values from messages.conf as a String.
     * TODO: Enumerate messages.
     */
    public static String message(String messageKey) {
        return messages_instance.node(messageKey).getString();
    }

    /**
     * This is responsible for creating configuration files if we believe they do not exist.
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
     * This is responsible for attempting to load configuration files, and then creating an instance of the result.
     * TODO: de-duplicate
     */
    public static void loadFile(String fileType) {
        File file = createFile(fileType);
        Path path = Paths.get(file.getPath());
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().prettyPrinting(true).path(path).build();
        CommentedConfigurationNode node;
        if (fileType.equalsIgnoreCase("settings")) {
            CreditsSettings config;
            try {
                node = loader.load();
                config = node.get(CreditsSettings.class);
                Objects.requireNonNull(node).set(CreditsSettings.class, config);
                loader.save(node);
                settings_instance = node;
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
     * This is responsible for parsing out placeholders within our messages.
     * <p>
     * This is the basic placeholder parser, which will only parse information we will always have.
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
     * This is responsible for parsing out placeholders within our messages.
     * <p>
     * This is a contextual placeholder parser, which will only work if we are
     * within a transaction which modifies Credit balance.
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
     * This is responsible for parsing out placeholders within our messages.
     * <p>
     * This is a contextual placeholder parser, which will only work if we are
     * within a transaction which is part of redeeming MCMMO Credits into a skill.
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
     * This is responsible for actually sending the message to a user. All messages sent out are prepended with a prefix.
     * TODO: Enumerate all config values.
     */
    public static void sendMessage(CommandSender commandSender, String text) {
        commandSender.sendMessage(MCMMOCredits.getMM().parse(message("prefix") + text));
    }

    /**
     * This class is responsible for generating the default messages.conf file. All values here represent defaults.
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
        private final String reload_successful = "<green>The relevant configuration files have been reloaded!";

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

        @Comment("Shown to user when a user adds MCMMO Credits to another user. %credits% placeholder will show previous balance since we are updating credit balances asynchronously.")
        private final String modify_credits_add = "<green>You have given %amount% Credits to %player%";

        @Comment("Shown to user when a user sets another user's balance to an amount. %credits% placeholder will show previous balance since we are updating credit balances asynchronously.")
        private final String modify_credits_set = "<yellow>You have set %player%'s Credits to %amount%!";

        @Comment("Shown to user when a user takes MCMMO Credits from another user. %credits% placeholder will show previous balance since we are updating credit balances asynchronously.")
        private final String modify_credits_take = "<red>You have taken %amount% Credits from %player%";
    }

    /**
     * This class is responsible for generating the default settings.conf file. All values here represent defaults.
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
