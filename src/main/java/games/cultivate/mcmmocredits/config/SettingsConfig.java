package games.cultivate.mcmmocredits.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * This class is responsible for generating the default settings.conf file. All values here represent defaults.
 */
@ConfigSerializable
public class SettingsConfig {
    @Comment("Perform offline player lookups with usercache. PAPER ONLY. Disable if you are having problems.")
    private final boolean use_usercache_lookup = false;

    @Comment("Toggles tab completion for Player based arguments. Useful if you have other plugins which hide staff.")
    private final boolean player_tab_completion = true;

    @Comment("Toggles sending a login message to the user indicating how many MCMMO Credits they have. Message can be configured in messages.conf.")
    private final boolean send_login_message = true;

    @Comment("Toggles console message when a user is added to the MCMMO Credits database")
    private final boolean database_add_message = true;
}