package games.cultivate.mcmmocredits.data;

import games.cultivate.mcmmocredits.user.User;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.testing.junit5.JdbiExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDAOTest {
    private static final Queries QUERIES = new Queries();

    @RegisterExtension
    public JdbiExtension h2Extension = JdbiExtension.h2().withInitializer((ds, handle) -> handle.execute(QUERIES.query("CREATE-TABLE-MYSQL"))).withPlugin(new SqlObjectPlugin());
    private Jdbi jdbi;
    private User user;
    private UserDAO dao;

    @BeforeEach
    public void setUp() {
        this.jdbi = this.h2Extension.getJdbi();
        this.dao = this.jdbi.onDemand(UserDAO.class);
        this.user = new User(new UUID(1, 1), "Notch", 100, 1500);
        this.jdbi.useHandle(x -> x.execute("INSERT INTO MCMMOCredits(uuid, username, credits, redeemed) VALUES(?, ?, ?, ?)", this.user.uuid(), this.user.username(), this.user.credits(), this.user.redeemed()));
    }

    @AfterEach
    public void tearDown() {
        this.jdbi.useHandle(x -> x.execute("DELETE FROM MCMMOCredits"));
    }

    @Test
    void testAddUser() {
        User testUser = this.jdbi.withHandle(x -> x.select("SELECT * FROM MCMMOCredits WHERE uuid = ?;", this.user.uuid()).map(ConstructorMapper.of(User.class)).one());
        assertEquals(this.user.uuid(), testUser.uuid());
        assertEquals(this.user.username(), testUser.username());
        assertEquals(this.user.credits(), testUser.credits());
        assertEquals(this.user.redeemed(), testUser.redeemed());
    }

    @Test
    void testGetUserUUID() {
        Optional<User> testUser = this.dao.getUser(this.user.uuid());
        assertTrue(testUser.isPresent());
        assertEquals(this.user.uuid(), testUser.get().uuid());
    }

    @Test
    void testGetUserString() {
        Optional<User> testUser = this.dao.getUser(this.user.username());
        assertTrue(testUser.isPresent());
        assertEquals("Notch", testUser.get().username());
    }

    @Test
    void testSetUsername() {
        String username = "MinecraftMinecraft";
        this.dao.setUsername(new UUID(1, 1), username);
        String result = this.jdbi.withHandle(x -> x.select("SELECT username FROM MCMMOCREDITS WHERE username LIKE ?", username).mapTo(String.class).one());
        assertEquals(username, result);
    }

    @Test
    void testSetCredits() {
        int set = 10;
        boolean status = this.dao.setCredits(this.user.uuid(), set);
        int result = this.jdbi.withHandle(x -> x.select("SELECT credits FROM MCMMOCREDITS WHERE uuid = ?;", this.user.uuid()).mapTo(int.class).one());
        assertTrue(status);
        assertEquals(set, result);
    }

    @Test
    void testSetNegativeCredits() {
        int set = -1;
        boolean status = false;
        try {
            this.dao.setCredits(this.user.uuid(), set);
        } catch (UnableToExecuteStatementException e) {
            status = true;
        }
        int result = this.jdbi.withHandle(x -> x.select("SELECT credits FROM MCMMOCREDITS WHERE uuid = ?;", this.user.uuid()).mapTo(int.class).one());
        assertTrue(status);
        assertEquals(this.user.credits(), result);
    }

    @Test
    void testGetCredits() {
        assertEquals(this.user.credits(), this.dao.getCredits(this.user.uuid()));
    }

    @Test
    void testAddCredits() {
        int credits = this.user.credits();
        int add = 10;
        boolean status = this.dao.addCredits(this.user.uuid(), add);
        int result = this.jdbi.withHandle(x -> x.select("SELECT credits FROM MCMMOCREDITS WHERE uuid = ?;", this.user.uuid()).mapTo(int.class).one());
        assertTrue(status);
        assertEquals(credits + add, result);
    }

    @Test
    void testGetRedeemedCredits() {
        User testUser = this.jdbi.withHandle(x -> x.select("SELECT * FROM MCMMOCredits WHERE uuid = ?;", this.user.uuid()).map(ConstructorMapper.of(User.class)).one());
        assertEquals(this.user.redeemed(), testUser.redeemed());
    }

    @Test
    void testAddRedeemedCredits() {
        int redeemed = this.user.redeemed();
        int oldCredits = this.user.credits();
        int redeem = 50;
        this.dao.redeemCredits(this.user.uuid(), redeem);
        int newCredits = this.jdbi.withHandle(x -> x.select("SELECT credits FROM MCMMOCREDITS WHERE uuid = ?;", this.user.uuid()).mapTo(int.class).one());
        int newRedeemed = this.jdbi.withHandle(x -> x.select("SELECT redeemed FROM MCMMOCREDITS WHERE uuid = ?;", this.user.uuid()).mapTo(int.class).one());
        assertEquals(oldCredits - redeem, newCredits);
        assertEquals(redeemed + redeem, newRedeemed);
    }
}
