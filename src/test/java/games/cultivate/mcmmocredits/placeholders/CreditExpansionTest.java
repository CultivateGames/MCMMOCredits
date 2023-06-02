package games.cultivate.mcmmocredits.placeholders;

import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserCache;
import games.cultivate.mcmmocredits.user.UserDAO;
import games.cultivate.mcmmocredits.user.UserService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.replacer.CharsReplacer;
import me.clip.placeholderapi.replacer.Replacer;
import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class CreditExpansionTest {
    private final Replacer replace = new CharsReplacer(Replacer.Closure.PERCENT);
    private User user;
    private UserCache cache;
    private UserService service;
    private Map<String, PlaceholderExpansion> map;

    @BeforeEach
    void setUp() {
        this.user = new User(new UUID(2, 2), "testUser", 1000, 500);
        this.cache = new UserCache();
        this.service = new UserService(mock(UserDAO.class), this.cache);
        this.map = new HashMap<>();
        this.map.put("mcmmocredits", new CreditsExpansion(this.service));
    }

    @Test
    void onRequest_ValidUser_ValidPlaceholders() {
        this.cache.add(this.user);
        String content = "%mcmmocredits_credits%, %mcmmocredits_redeemed%, %mcmmocredits_username%, %mcmmocredits_uuid%, %mcmmocredits_cached%";
        String expected = String.format("%s, %s, %s, %s, %s", this.user.credits(), this.user.redeemed(), this.user.username(), this.user.uuid().toString(), this.service.isCached(this.user));
        OfflinePlayer player = mock(OfflinePlayer.class);
        doReturn("testUser").when(player).getName();
        assertEquals(expected, this.replace.apply(content, player, this.map::get));
    }

    @Test
    void onRequest_InvalidUser_NoPlaceholders() {
        String content = "%mcmmocredits_credits%, %mcmmocredits_redeemed%, %mcmmocredits_username%, %mcmmocredits_uuid%, %mcmmocredits_cached%";
        String expected = MessageFormat.format("{0}, {0}, {0}, {0}, {0}", "0");
        OfflinePlayer player = mock(OfflinePlayer.class);
        doReturn("testUser").when(player).getName();
        assertEquals(expected, this.replace.apply(content, player, this.map::get));
    }
}
