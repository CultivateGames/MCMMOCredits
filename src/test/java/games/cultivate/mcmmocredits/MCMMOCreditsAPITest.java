package games.cultivate.mcmmocredits;

import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserCache;
import games.cultivate.mcmmocredits.user.UserDAO;
import games.cultivate.mcmmocredits.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MCMMOCreditsAPITest {
    private final UUID uuid = new UUID(2, 2);
    private final int credits = 100;
    private MCMMOCreditsAPI api;
    private User user;
    private UserCache cache;
    @Mock
    private UserDAO dao;

    @BeforeEach
    void setUp() {
        this.cache = new UserCache();
        this.api = new MCMMOCreditsAPI(new UserService(this.dao, this.cache));
        this.user = new User(this.uuid, "TestUser", this.credits, 0);
    }

    @Test
    void getCredits_ValidUser_ReturnsCredits() {
        this.cache.add(this.user);
        assertEquals(this.credits, this.api.getCredits(this.uuid));
    }

    @Test
    void getCredits_InvalidUser_ReturnsZero() {
        assertEquals(0, this.api.getCredits(UUID.randomUUID()));
    }

    @Test
    void addCredits_ValidUser_ReturnsUpdatedCredits() {
        this.cache.add(this.user);
        when(this.dao.setCredits(this.uuid, this.credits + 200)).thenReturn(true);
        assertTrue(this.api.addCredits(this.uuid, 200));
        assertEquals(this.credits + 200, this.api.getCredits(this.uuid));
    }

    @Test
    void addCredits_InvalidUser_ReturnsFalse() {
        UUID ruuid = UUID.randomUUID();
        when(this.dao.setCredits(ruuid, 2030)).thenReturn(false);
        assertFalse(this.api.addCredits(ruuid, 2030));
        assertEquals(0, this.api.getCredits(ruuid));
    }

    @Test
    void setCredits_ValidUser_ReturnsUpdatedCredits() {
        this.cache.add(this.user);
        when(this.dao.setCredits(this.uuid, 250)).thenReturn(true);
        assertTrue(this.api.setCredits(this.uuid, 250));
        assertEquals(250, this.api.getCredits(this.uuid));
    }

    @Test
    void setCredits_InvalidUser_ReturnsFalse() {
        UUID ruuid = UUID.randomUUID();
        when(this.dao.setCredits(ruuid, 2030)).thenReturn(false);
        assertFalse(this.api.setCredits(ruuid, 2030));
        assertEquals(0, this.api.getCredits(ruuid));
    }

    @Test
    void takeCredits_ValidUser_ReturnsUpdatedCredits() {
        this.cache.add(this.user);
        when(this.dao.setCredits(this.uuid, this.credits - 20)).thenReturn(true);
        assertTrue(this.api.takeCredits(this.uuid, 20));
        assertEquals(this.credits - 20, this.api.getCredits(this.uuid));
    }

    @Test
    void takeCredits_InvalidUser_ReturnsFalse() {
        UUID ruuid = UUID.randomUUID();
        assertFalse(this.api.takeCredits(ruuid, 10));
        assertEquals(0, this.api.getCredits(ruuid));
    }

}
