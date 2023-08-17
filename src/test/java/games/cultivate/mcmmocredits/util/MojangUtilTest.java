package games.cultivate.mcmmocredits.util;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MojangUtilTest {

    @Test
    void fetchUsername_ValidInfo_ReturnsCorrectName() {
        UUID uuid = UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5");
        assertEquals("Notch", MojangUtil.fetchUsername(uuid));
    }
}
